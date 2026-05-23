package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.response.PartyAcceptResponsePacket;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyAcceptResponsePacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyAcceptResponsePacketListener(@NotNull ProxyServer proxyServer,
                                             @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onPartyAccept(PartyAcceptResponsePacket packet) {
        Optional<Party> optionalParty = packet.getParty();
        PartyPlayer partyPlayer = packet.getPartyPlayer();
        PartyAcceptResponsePacket.ResultType resultType = packet.getResultType();

        UUID partyPlayerUuid = partyPlayer.uuid();
        Optional<Player> optionalPlayer = proxyServer.getPlayer(partyPlayerUuid);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();

            handlePartyAcceptResult(resultType, player);
        }

        if (resultType == PartyAcceptResponsePacket.ResultType.SUCCESS) {
            if (optionalParty.isEmpty()) return;

            Party party = optionalParty.get();
            List<PartyMember> members = party.getMembers();
            for (PartyMember member : members) {
                UUID uuid = member.uuid();

                Optional<Player> optionalMemberPlayer = proxyServer.getPlayer(uuid);
                if (optionalMemberPlayer.isEmpty()) continue;

                Player player = optionalMemberPlayer.get();
                TagResolver.Single parsed = Placeholder.parsed("target", partyPlayer.name());
                Component message = messages.getComponent("player.joined.party", parsed);
                player.sendMessage(message);
            }
        }
    }

    private void handlePartyAcceptResult(@NotNull PartyAcceptResponsePacket.ResultType resultType, @NotNull Player player) {
        switch (resultType) {
            case NOT_INVITED -> player.sendMessage(messages.getComponent("party.not.invited"));
            case PARTY_NOT_FOUND -> player.sendMessage(messages.getComponent("party.not.found"));
            case ALREADY_JOINED -> player.sendMessage(messages.getComponent("party.already.joined"));
            case ERROR -> player.sendMessage(messages.getComponent("party.error.message"));
        }
    }
}