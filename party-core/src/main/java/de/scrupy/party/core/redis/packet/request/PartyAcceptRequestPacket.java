package de.scrupy.party.core.redis.packet.request;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyAcceptRequestPacket extends RedisPacket {

    @NotNull
    private final PartyPlayer partyPlayer;
    @NotNull
    private final String name;

    public PartyAcceptRequestPacket(@NotNull UUID packetId, @NotNull PartyPlayer partyPlayer, @NotNull String name) {
        super(packetId);
        this.partyPlayer = partyPlayer;
        this.name = name;
    }

    public @NotNull PartyPlayer getPartyPlayer() {
        return partyPlayer;
    }

    public @NotNull String getName() {
        return name;
    }
}
