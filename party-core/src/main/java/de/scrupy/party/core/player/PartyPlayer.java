package de.scrupy.party.core.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PartyPlayer(@NotNull UUID uuid, @NotNull String name) {
}
