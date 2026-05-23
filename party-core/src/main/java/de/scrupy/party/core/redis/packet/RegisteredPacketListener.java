package de.scrupy.party.core.redis.packet;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public record RegisteredPacketListener(@NotNull Object object, @NotNull Method method) {
}
