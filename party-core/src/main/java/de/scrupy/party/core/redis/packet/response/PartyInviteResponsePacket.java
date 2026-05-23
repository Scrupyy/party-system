package de.scrupy.party.core.redis.packet.response;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PartyInviteResponsePacket extends RedisPacket {

    @NotNull
    private final PartyPlayer sender;
    @Nullable
    private final PartyPlayer target;
    @NotNull
    private final ResultType resultType;

    public PartyInviteResponsePacket(@NotNull UUID packetId,
                                     @NotNull PartyPlayer sender,
                                     @Nullable PartyPlayer target,
                                     @NotNull PartyInviteResponsePacket.ResultType resultType) {
        super(packetId);
        this.sender = sender;
        this.target = target;
        this.resultType = resultType;
    }

    public @NotNull PartyPlayer getSender() {
        return sender;
    }

    public @NotNull Optional<PartyPlayer> getTarget() {
        return Optional.ofNullable(target);
    }

    public @NotNull ResultType getResultType() {
        return resultType;
    }

    public enum ResultType {
        ALREADY_INVITED,
        SUCCESS,
        PLAYER_NOT_FOUND,
        ERROR,
        ALREADY_IN_PARTY,
        NO_PERMISSION
    }
}
