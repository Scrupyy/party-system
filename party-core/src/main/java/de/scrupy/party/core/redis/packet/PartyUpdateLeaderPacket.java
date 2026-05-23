package de.scrupy.party.core.redis.packet;

import de.scrupy.party.core.player.Party;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyUpdateLeaderPacket extends RedisPacket {

    @NotNull
    private final Party party;

    public PartyUpdateLeaderPacket(@NotNull UUID packetId, @NotNull Party party) {
        super(packetId);
        this.party = party;
    }

    public @NotNull Party getParty() {
        return party;
    }
}
