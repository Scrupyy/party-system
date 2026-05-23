package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.PartyUpdateLeaderPacket;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyUpdateLeaderPacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyUpdateLeaderPacketListener(@NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onLeaderUpdate(PartyUpdateLeaderPacket packet) {
        Party party = packet.getParty();
        List<PartyMember> members = party.getMembers();

        for (PartyMember member : members) {
            UUID uuid = member.uuid();
            Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
            if (optionalPlayer.isEmpty()) continue;

            Player player = optionalPlayer.get();
            PartyMember partyLeader = party.getPartyLeader();
            String leaderName = partyLeader.name();
            Component message = messages.getComponent("party.updated.leader", Placeholder.parsed("player", leaderName));
            player.sendMessage(message);
        }
    }
}
