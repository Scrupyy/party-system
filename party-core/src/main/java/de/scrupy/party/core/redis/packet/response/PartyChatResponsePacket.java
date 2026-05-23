package de.scrupy.party.core.redis.packet.response;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PartyChatResponsePacket extends RedisPacket {

    @NotNull
    private final PartyPlayer sender;
    @Nullable
    private final Party party;
    @NotNull
    private final String message;

    public PartyChatResponsePacket(@NotNull UUID packetId,
                                   @NotNull PartyPlayer sender,
                                   @Nullable Party party,
                                   @NotNull String message) {
        super(packetId);
        this.sender = sender;
        this.party = party;
        this.message = message;
    }

    public @NotNull PartyPlayer getSender() {
        return sender;
    }

    public @NotNull String getMessage() {
        return message;
    }

    public @NotNull Optional<Party> getParty() {
        return Optional.ofNullable(party);
    }
}
