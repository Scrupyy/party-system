package de.scrupy.party.core.redis.packet.request;

import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.RedisPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyLeaveRequestPacket extends RedisPacket {

    @NotNull
    private final PartyPlayer player;

    public PartyLeaveRequestPacket(@NotNull UUID packetId, @NotNull PartyPlayer player) {
        super(packetId);
        this.player = player;
    }

    public @NotNull PartyPlayer getPlayer() {
        return player;
    }
}
