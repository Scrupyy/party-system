package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.redis.packet.request.PartyInfoRequestPacket;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyInfoSubCommand extends PlayerSubCommand {

    @NotNull
    private final ProxyRedisPacketHandler proxyRedisPacketHandler;

    public PartyInfoSubCommand(@NotNull String commandName, @NotNull ProxyRedisPacketHandler proxyRedisPacketHandler) {
        super(commandName);
        this.proxyRedisPacketHandler = proxyRedisPacketHandler;
    }

    @Override
    public void execute(@NotNull String[] arguments, @NotNull Player player) {
        UUID packetId = proxyRedisPacketHandler.getRandomPacketId();
        UUID uniqueId = player.getUniqueId();
        PartyInfoRequestPacket packet = new PartyInfoRequestPacket(packetId, uniqueId);
        proxyRedisPacketHandler.sendRequestPacket(player, packet);
    }
}