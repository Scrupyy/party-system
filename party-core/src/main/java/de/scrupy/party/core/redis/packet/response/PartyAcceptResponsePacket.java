package de.scrupy.party.core.redis.packet.response;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PartyAcceptResponsePacket extends RedisPacket {

    @Nullable
    private final Party party;
    @NotNull
    private final PartyPlayer partyPlayer;
    @NotNull
    private final ResultType resultType;

    public PartyAcceptResponsePacket(@NotNull UUID packetId,
                                     @Nullable Party party,
                                     @NotNull PartyPlayer partyPlayer,
                                     @NotNull ResultType resultType) {
        super(packetId);
        this.party = party;
        this.partyPlayer = partyPlayer;
        this.resultType = resultType;
    }

    public @NotNull Optional<Party> getParty() {
        return Optional.ofNullable(party);
    }

    public @NotNull PartyPlayer getPartyPlayer() {
        return partyPlayer;
    }

    public @NotNull ResultType getResultType() {
        return resultType;
    }

    public enum ResultType {
        PARTY_NOT_FOUND,
        NOT_INVITED,
        ALREADY_JOINED,
        SUCCESS,
        ERROR
    }
}
