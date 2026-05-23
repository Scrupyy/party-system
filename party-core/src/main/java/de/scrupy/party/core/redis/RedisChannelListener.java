package de.scrupy.party.core.redis;

import com.google.gson.Gson;
import de.scrupy.party.core.Constants;
import de.scrupy.party.core.redis.packet.RedisPacket;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisChannelListener implements RedisPubSubListener<String, String> {

    @NotNull
    private final Logger LOGGER = Logger.getLogger(RedisChannelListener.class.getSimpleName());

    @NotNull
    private final RedisPacketHandler redisPacketHandler;

    public RedisChannelListener(@NotNull RedisPacketManager redisPacketHandler) {
        this.redisPacketHandler = redisPacketHandler;
    }

    @Override
    public void message(String channel, String message) {
        String[] split = message.split(":", 2);
        String packetTypeName = split[0];
        String packetData = split[1];

        try {
            Class<?> packetTypeClass = Class.forName(packetTypeName);
            if (RedisPacket.class.isAssignableFrom(packetTypeClass)) {
                Class<? extends RedisPacket> packetClass = packetTypeClass.asSubclass(RedisPacket.class);
                Gson gson = Constants.GSON;

                RedisPacket redisPacket = gson.fromJson(packetData, packetClass);
                redisPacketHandler.handlePacket(redisPacket);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(
                    Level.WARNING, "unknown packet (" + packetTypeName + ") received on channel " + channel);
        }
    }

    @Override
    public void message(String s, String k1, String s2) {
    }

    @Override
    public void subscribed(String s, long l) {
    }

    @Override
    public void psubscribed(String s, long l) {
    }

    @Override
    public void unsubscribed(String s, long l) {
    }

    @Override
    public void punsubscribed(String s, long l) {
    }
}
