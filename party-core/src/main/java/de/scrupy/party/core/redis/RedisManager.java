package de.scrupy.party.core.redis;

import com.google.gson.Gson;
import de.scrupy.party.core.Constants;
import de.scrupy.party.core.config.RedisConfig;
import de.scrupy.party.core.player.PartyPlayer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class RedisManager implements RedisHandler {

    @NotNull
    public static final String PARTY_REQUEST_CHANNEL = "party:request";
    @NotNull
    public static final String PARTY_RESPONSE_CHANNEL = "party:response";

    @NotNull
    private static final Logger LOGGER = Logger.getLogger(RedisManager.class.getSimpleName());

    @NotNull
    private static final String SESSION_KEY_PREFIX = "player:session:";
    @NotNull
    private static final String NAME_KEY_PREFIX = "player:name:";
    @NotNull
    private static final String PARTY_INVITE_TOKEN_KEY_PREFIX = "party:invite:token:";

    @NotNull
    private final RedisClient redisClient;
    @NotNull
    private final StatefulRedisConnection<String, String> connection;
    @NotNull
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    @NotNull
    private final RedisAsyncCommands<String, String> asyncCommands;

    public RedisManager(@NotNull RedisConfig redisConfig) {
        String address = redisConfig.getAddress();
        int port = redisConfig.getPort();
        String username = redisConfig.getUsername();
        String password = redisConfig.getPassword();

        RedisURI redisURI = RedisURI.builder()
                .withHost(address)
                .withPort(port)
                .withAuthentication(username, password.toCharArray())
                .withTimeout(RedisURI.DEFAULT_TIMEOUT_DURATION)
                .build();

        this.redisClient = RedisClient.create(redisURI);
        this.connection = redisClient.connect();
        this.asyncCommands = connection.async();
        this.pubSubConnection = redisClient.connectPubSub();
    }

    public void shutdown() {
        connection.close();
        redisClient.shutdown();
    }

    @Override
    public @NotNull CompletionStage<Boolean> isInviteTokenSet(
            @NotNull String targetName, @NotNull String senderName) {
        String partyInviteTokenKey = getPartyInviteTokenKey(targetName, senderName);
        return asyncCommands.exists(partyInviteTokenKey).thenApply(exists -> exists > 0);
    }

    @Override
    @NotNull
    public CompletionStage<Long> removeInviteToken(
            @NotNull String targetName, @NotNull String senderName) {
        String partyInviteTokenKey = getPartyInviteTokenKey(targetName, senderName);
        return asyncCommands.del(partyInviteTokenKey);
    }

    @Override
    @NotNull
    public CompletionStage<Optional<PartyPlayer>> getPartyPlayer(@NotNull String playerName) {
        return getUniqueId(playerName)
                .thenCompose(
                        optionalUniqueId -> {
                            if (optionalUniqueId.isEmpty())
                                return CompletableFuture.completedStage(Optional.empty());

                            UUID uuid = optionalUniqueId.get();
                            String sessionKey = getSessionKey(uuid);
                            return asyncCommands
                                    .get(sessionKey)
                                    .thenApply(
                                            uuidString -> {
                                                if (uuidString == null) return Optional.empty();

                                                Gson gson = Constants.GSON;
                                                PartyPlayer partyPlayer = gson.fromJson(uuidString, PartyPlayer.class);
                                                return Optional.of(partyPlayer);
                                            });
                        });
    }

    @Override
    @NotNull
    public CompletionStage<Optional<UUID>> getUniqueId(@NotNull String playerName) {
        return asyncCommands
                .get(NAME_KEY_PREFIX + playerName.toLowerCase())
                .thenApply(
                        uuidString -> {
                            if (uuidString == null) return Optional.empty();

                            return Optional.of(UUID.fromString(uuidString));
                        });
    }

    @Override
    public void setInviteToken(@NotNull String targetName, @NotNull String senderName) {
        String partyInviteTokenKey = getPartyInviteTokenKey(targetName, senderName);
        asyncCommands.setex(partyInviteTokenKey, 60, senderName);
    }

    @Override
    public void savePartyPlayer(@NotNull PartyPlayer partyPlayer) {
        Gson gson = Constants.GSON;
        String partyPlayerJson = gson.toJson(partyPlayer);

        UUID uuid = partyPlayer.uuid();
        String name = partyPlayer.name().toLowerCase();

        String sessionKey = getSessionKey(uuid);
        String nameKey = getNameKey(name);
        String uuidString = uuid.toString();
        asyncCommands.set(nameKey, uuidString);
        asyncCommands.set(sessionKey, partyPlayerJson);
    }

    @Override
    public void removePartyPlayer(@NotNull PartyPlayer partyPlayer) {
        UUID uuid = partyPlayer.uuid();
        String sessionKey = getSessionKey(uuid);

        String name = partyPlayer.name().toLowerCase();
        String nameKey = getNameKey(name);

        asyncCommands.del(nameKey);
        asyncCommands.del(sessionKey);
    }

    @Override
    public void registerChannelListener(@NotNull RedisPubSubListener<String, String> listener) {
        pubSubConnection.addListener(listener);
    }

    @Override
    public void subscribeChannel(@NotNull String channel) {
        RedisPubSubAsyncCommands<String, String> async = pubSubConnection.async();
        async.subscribe(channel);

        LOGGER.info("subscribed to redis channel: " + channel);
    }

    @Override
    public void publishMessage(@NotNull String channel, @NotNull String message) {
        RedisPubSubAsyncCommands<String, String> async = pubSubConnection.async();
        async.publish(channel, message);
    }

    @NotNull
    private String getNameKey(@NotNull String name) {
        return NAME_KEY_PREFIX + name;
    }

    @NotNull
    private String getSessionKey(@NotNull UUID uuid) {
        String uniqueIdString = uuid.toString();
        return SESSION_KEY_PREFIX + uniqueIdString;
    }

    @NotNull
    private String getPartyInviteTokenKey(@NotNull String targetName, @NotNull String senderName) {
        return PARTY_INVITE_TOKEN_KEY_PREFIX
                + targetName.toLowerCase()
                + ":"
                + senderName.toLowerCase();
    }
}
