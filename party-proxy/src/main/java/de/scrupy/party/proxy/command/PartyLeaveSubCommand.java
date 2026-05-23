package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.packet.request.PartyLeaveRequestPacket;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyLeaveSubCommand extends PlayerSubCommand {

    @NotNull
    private final ProxyRedisPacketHandler packetHandler;
    @NotNull
    private final PlayerCache<Player> playerCache;

    public PartyLeaveSubCommand(@NotNull String commandName,
                                @NotNull ProxyRedisPacketHandler packetHandler,
                                @NotNull PlayerCache<Player> playerCache) {
        super(commandName);
        this.packetHandler = packetHandler;
        this.playerCache = playerCache;
    }

    @Override
    public void execute(@NotNull String[] arguments, @NotNull Player player) {
        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);

        UUID packetId = packetHandler.getRandomPacketId();
        PartyLeaveRequestPacket packet = new PartyLeaveRequestPacket(packetId, partyPlayer);
        packetHandler.sendRequestPacket(player, packet);
    }
}