package de.scrupy.party.core.config;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MessageProvider {

    @NotNull
    Map<String, String> getMessages();
}
