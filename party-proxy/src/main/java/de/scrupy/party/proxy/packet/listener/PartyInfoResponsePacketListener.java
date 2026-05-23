package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.response.PartyInfoResponsePacket;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyInfoResponsePacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyInfoResponsePacketListener(
            @NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onPartyInfoResult(PartyInfoResponsePacket packet) {
        Optional<Party> optionalParty = packet.getParty();
        UUID requester = packet.getRequester();

        Optional<Player> optionalPlayer = proxyServer.getPlayer(requester);
        if (optionalPlayer.isEmpty()) return;

        Player player = optionalPlayer.get();

        if (optionalParty.isEmpty()) {
            player.sendMessage(messages.getComponent("player.no.party"));
            return;
        }

        Party party = optionalParty.get();
        int id = party.getId();
        PartyMember partyLeader = party.getPartyLeader();
        List<PartyMember> members = party.getMembers();

        String memberFormat = messages.get("party.member.format");
        List<String> memberNameList = new ArrayList<>();
        for (PartyMember member : members) {
            String replace = memberFormat.replace("<member>", member.name());
            memberNameList.add(replace);
        }

        String membersString = String.join("", memberNameList);
        TagResolver.Single membersPlaceholder = Placeholder.parsed("members", membersString);
        TagResolver.Single partyLeaderPlaceholder = Placeholder.unparsed("leader", partyLeader.name());
        TagResolver.Single idPlaceholder = Placeholder.unparsed("id", String.valueOf(id));

        Component message = messages.getComponent("party.info.message",
                membersPlaceholder,
                partyLeaderPlaceholder,
                idPlaceholder);
        player.sendMessage(message);
    }
}
