package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.PartyConnectServerPacket;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyConnectServerPacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyConnectServerPacketListener(@NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onServerConnect(PartyConnectServerPacket packet) {
        Optional<Party> optionalParty = packet.getParty();

        String host = packet.getHost();
        InetSocketAddress address = new InetSocketAddress(host, packet.getPort());
        String serverName = packet.getServerName();

        ServerInfo serverInfo = new ServerInfo(serverName, address);

        RegisteredServer server = proxyServer.getServer(serverName)
                .orElseGet(() -> proxyServer.registerServer(serverInfo));

        if (optionalParty.isEmpty()) return;

        Party party = optionalParty.get();
        List<PartyMember> members = party.getMembers();

        for (PartyMember member : members) {
            UUID uuid = member.uuid();
            Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
            if (optionalPlayer.isEmpty()) continue;

            Player player = optionalPlayer.get();
            Component message = messages.getComponent("party.joining.server",
                    Placeholder.unparsed("server", serverName));
            player.sendMessage(message);
            player.createConnectionRequest(server).connect();
        }
    }
}