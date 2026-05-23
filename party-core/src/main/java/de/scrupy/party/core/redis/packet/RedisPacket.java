package de.scrupy.party.core.redis.packet;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

public abstract class RedisPacket implements Serializable {

    @NotNull
    private final UUID packetId;

    public RedisPacket(@NotNull UUID packetId) {
        this.packetId = packetId;
    }

    public @NotNull UUID getPacketId() {
        return packetId;
    }
}
