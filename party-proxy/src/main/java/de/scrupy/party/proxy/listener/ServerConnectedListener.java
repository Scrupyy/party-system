package de.scrupy.party.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.packet.PartyConnectServerPacket;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.core.redis.packet.request.PartyLeaveRequestPacket;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ServerConnectedListener {

    @NotNull
    private final PlayerCache<Player> playerCache;
    @NotNull
    private final RedisPacketManager redisPacketManager;

    public ServerConnectedListener(@NotNull PlayerCache<Player> playerCache, @NotNull RedisPacketManager redisPacketManager) {
        this.playerCache = playerCache;
        this.redisPacketManager = redisPacketManager;
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        RegisteredServer server = event.getServer();
        ServerInfo serverInfo = server.getServerInfo();
        InetSocketAddress address = serverInfo.getAddress();
        String hostName = address.getHostName();
        int port = address.getPort();
        String name = serverInfo.getName();

        Player player = event.getPlayer();
        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);

        UUID packetId = redisPacketManager.getRandomPacketId();
        PartyConnectServerPacket packet = new PartyConnectServerPacket(packetId, hostName, port, name, partyPlayer, null);
        redisPacketManager.sendPacket(packet);
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        playerCache.addPlayer(player);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);

        UUID packetId = redisPacketManager.getRandomPacketId();
        PartyLeaveRequestPacket packet = new PartyLeaveRequestPacket(packetId, partyPlayer);
        redisPacketManager.sendPacket(packet);

        playerCache.removePlayer(player);
    }
}