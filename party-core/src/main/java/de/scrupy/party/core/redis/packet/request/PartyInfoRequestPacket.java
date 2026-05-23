package de.scrupy.party.core.redis.packet.request;

import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyInfoRequestPacket extends RedisPacket {

    @NotNull
    private final UUID requester;

    public PartyInfoRequestPacket(@NotNull UUID packetId, @NotNull UUID requester) {
        super(packetId);
        this.requester = requester;
    }

    public @NotNull UUID getRequester() {
        return requester;
    }
}
