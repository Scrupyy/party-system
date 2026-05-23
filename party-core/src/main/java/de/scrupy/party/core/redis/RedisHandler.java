package de.scrupy.party.core.redis;

import de.scrupy.party.core.player.PartyPlayer;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface RedisHandler {

    @NotNull
    CompletionStage<Boolean> isInviteTokenSet(@NotNull String targetName, @NotNull String senderName);

    @NotNull
    CompletionStage<Long> removeInviteToken(@NotNull String targetName, @NotNull String senderName);

    @NotNull
    CompletionStage<Optional<PartyPlayer>> getPartyPlayer(@NotNull String name);

    @NotNull
    CompletionStage<Optional<UUID>> getUniqueId(@NotNull String playerName);

    void setInviteToken(@NotNull String targetName, @NotNull String senderName);

    void savePartyPlayer(@NotNull PartyPlayer partyPlayer);

    void removePartyPlayer(@NotNull PartyPlayer partyPlayer);

    void registerChannelListener(@NotNull RedisPubSubListener<String, String> listener);

    void subscribeChannel(@NotNull String channel);

    void publishMessage(@NotNull String channel, @NotNull String message);
}
