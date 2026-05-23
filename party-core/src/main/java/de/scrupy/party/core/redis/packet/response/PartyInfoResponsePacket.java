package de.scrupy.party.core.redis.packet.response;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PartyInfoResponsePacket extends RedisPacket {

    @Nullable
    private final Party party;
    @NotNull
    private final UUID requester;

    public PartyInfoResponsePacket(@NotNull UUID packetId, @Nullable Party party, @NotNull UUID requester) {
        super(packetId);
        this.party = party;
        this.requester = requester;
    }

    public @NotNull Optional<Party> getParty() {
        return Optional.ofNullable(party);
    }

    public @NotNull UUID getRequester() {
        return requester;
    }
}
