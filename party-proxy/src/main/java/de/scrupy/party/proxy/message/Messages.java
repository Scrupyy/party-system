package de.scrupy.party.proxy.message;

import de.scrupy.party.core.config.MessageProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Messages {

    @NotNull
    private final Map<String, String> messages;
    @NotNull
    private final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    @NotNull
    private final String prefix;

    public Messages(@Nullable MessageProvider messageProvider) {
        if (messageProvider == null) {
            this.messages = new HashMap<>();
        } else {
            this.messages = messageProvider.getMessages();
        }
        this.prefix = messages.getOrDefault("party.prefix", "");
    }

    @NotNull
    public Component getComponent(String key, TagResolver... replacements) {
        String message = prefix + get(key);
        return MINI_MESSAGE.deserialize(message, replacements);
    }

    @NotNull
    public Component getComponent(String key) {
        String message = prefix + get(key);
        return MINI_MESSAGE.deserialize(message);
    }

    @NotNull
    public String get(String key) {
        return messages.getOrDefault(key, "<red>message for key: " + key + " not found</red>");
    }
}