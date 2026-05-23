package de.scrupy.party.core.redis;

import de.scrupy.party.core.redis.packet.RedisPacket;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RedisPacketHandler {

    void registerPacketListener(@NotNull RedisPacketListener redisPacketListener);

    void sendPacket(@NotNull RedisPacket redisPacket);

    <T extends RedisPacket> void handlePacket(@NotNull T redisPacket);

    @NotNull
    UUID getRandomPacketId();
}
