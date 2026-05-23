package de.scrupy.party.proxy.packet;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PendingRequest(@NotNull UUID packetId, @NotNull Player player, @NotNull Long time) {
}
