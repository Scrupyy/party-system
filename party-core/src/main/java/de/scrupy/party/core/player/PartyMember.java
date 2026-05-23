package de.scrupy.party.core.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PartyMember(@NotNull UUID uuid, @NotNull String name) {
}
