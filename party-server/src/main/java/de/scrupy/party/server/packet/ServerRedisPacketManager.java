package de.scrupy.party.server.packet;

import de.scrupy.party.core.redis.RedisHandler;
import de.scrupy.party.core.redis.RedisManager;
import de.scrupy.party.core.redis.packet.RedisPacket;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

public class ServerRedisPacketManager extends RedisPacketManager {

    @NotNull
    private final ExecutorService executor;

    public ServerRedisPacketManager(@NotNull RedisHandler redisHandler, @NotNull ExecutorService executor) {
        super(redisHandler, RedisManager.PARTY_REQUEST_CHANNEL, RedisManager.PARTY_RESPONSE_CHANNEL);
        this.executor = executor;
    }

    @Override
    public <T extends RedisPacket> void handlePacket(@NotNull T redisPacket) {
        executor.execute(() -> super.handlePacket(redisPacket));
    }
}
