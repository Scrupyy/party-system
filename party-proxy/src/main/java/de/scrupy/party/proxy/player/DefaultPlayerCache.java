package de.scrupy.party.proxy.player;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.RedisHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultPlayerCache extends PlayerCache<Player> {

    public DefaultPlayerCache(@NotNull RedisHandler redisHandler) {
        super(redisHandler);
    }

    @Override
    public PartyPlayer createPartyPlayer(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();
        String username = player.getUsername();
        return new PartyPlayer(uniqueId, username);
    }
}