package de.scrupy.party.core.redis.packet.response;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyLeaveResponsePacket extends RedisPacket {

    @NotNull
    private final PartyPlayer partyPlayer;
    @NotNull
    private final ResultType resultType;

    public PartyLeaveResponsePacket(@NotNull UUID packetId, @NotNull ResultType resultType, @NotNull PartyPlayer partyPlayer) {
        super(packetId);
        this.resultType = resultType;
        this.partyPlayer = partyPlayer;
    }

    public @NotNull PartyPlayer getPartyPlayer() {
        return partyPlayer;
    }

    public @NotNull ResultType getResultType() {
        return resultType;
    }

    public enum ResultType {
        NO_PARTY,
        PARTY_DELETED,
        SUCCESS
    }
}
