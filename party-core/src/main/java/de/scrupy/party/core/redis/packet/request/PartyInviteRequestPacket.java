package de.scrupy.party.core.redis.packet.request;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyInviteRequestPacket extends RedisPacket {

    @NotNull
    private final PartyPlayer sender;
    @NotNull
    private final String targetName;

    public PartyInviteRequestPacket(@NotNull UUID packetId, @NotNull PartyPlayer sender, @NotNull String targetName) {
        super(packetId);
        this.sender = sender;
        this.targetName = targetName;
    }

    public @NotNull PartyPlayer getSender() {
        return sender;
    }

    public @NotNull String getTargetName() {
        return targetName;
    }
}
