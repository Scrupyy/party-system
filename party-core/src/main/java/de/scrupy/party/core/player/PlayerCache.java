package de.scrupy.party.core.player;

import de.scrupy.party.core.redis.RedisHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerCache<T> {

    @NotNull
    private final RedisHandler redisHandler;
    @NotNull
    private final Map<T, PartyPlayer> partyPlayers;

    public PlayerCache(@NotNull RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
        partyPlayers = new HashMap<>();
    }

    public void addPlayer(@NotNull T player) {
        PartyPlayer partyPlayer = getPartyPlayer(player);

        partyPlayers.put(player, partyPlayer);
        redisHandler.savePartyPlayer(partyPlayer);
    }

    @NotNull
    public PartyPlayer getPartyPlayer(@NotNull T player) {
        return partyPlayers.computeIfAbsent(player, this::createPartyPlayer);
    }

    public abstract PartyPlayer createPartyPlayer(@NotNull T player);

    public void removePlayer(@NotNull T player) {
        PartyPlayer partyPlayer = partyPlayers.get(player);
        if (partyPlayer != null) redisHandler.removePartyPlayer(partyPlayer);
        partyPlayers.remove(player);
    }
}
