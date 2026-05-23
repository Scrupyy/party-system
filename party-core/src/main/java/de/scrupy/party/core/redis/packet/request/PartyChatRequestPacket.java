package de.scrupy.party.core.redis.packet.request;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyChatRequestPacket extends RedisPacket {

    @NotNull
    private final PartyPlayer partyPlayer;
    @NotNull
    private final String message;

    public PartyChatRequestPacket(@NotNull UUID packetId, @NotNull PartyPlayer partyPlayer, @NotNull String message) {
        super(packetId);
        this.partyPlayer = partyPlayer;
        this.message = message;
    }

    public @NotNull PartyPlayer getPartyPlayer() {
        return partyPlayer;
    }

    public @NotNull String getMessage() {
        return message;
    }
}
