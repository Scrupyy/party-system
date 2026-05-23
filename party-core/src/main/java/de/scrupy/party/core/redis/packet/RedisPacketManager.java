package de.scrupy.party.core.redis.packet;

import com.google.gson.Gson;
import de.scrupy.party.core.Constants;
import de.scrupy.party.core.redis.RedisChannelListener;
import de.scrupy.party.core.redis.RedisHandler;
import de.scrupy.party.core.redis.RedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RedisPacketManager implements RedisPacketHandler {

    @NotNull
    private static final Logger LOGGER = Logger.getLogger(RedisPacketManager.class.getSimpleName());

    @NotNull
    private final RedisHandler redisHandler;
    @NotNull
    private final String publishChannel;

    @NotNull
    private final Map<Class<? extends RedisPacket>, List<RegisteredPacketListener>> packetListener;

    public RedisPacketManager(
            @NotNull RedisHandler redisHandler,
            @NotNull String subscribeChannel,
            @NotNull String publishChannel) {
        this.redisHandler = redisHandler;
        this.packetListener = new HashMap<>();
        this.publishChannel = publishChannel;
        RedisChannelListener listener = new RedisChannelListener(this);
        redisHandler.registerChannelListener(listener);
        redisHandler.subscribeChannel(subscribeChannel);
    }

    @Override
    public void registerPacketListener(@NotNull RedisPacketListener redisPacketListener) {
        Class<? extends @NotNull RedisPacketListener> clazz = redisPacketListener.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (!method.isAnnotationPresent(PacketListener.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> parameterType = method.getParameterTypes()[0];

            if (RedisPacket.class.isAssignableFrom(parameterType)) {
                Class<? extends RedisPacket> packetClass = parameterType.asSubclass(RedisPacket.class);

                method.setAccessible(true);

                RegisteredPacketListener registeredPacketListener =
                        new RegisteredPacketListener(redisPacketListener, method);
                List<RegisteredPacketListener> methodList =
                        packetListener.computeIfAbsent(packetClass, k -> new ArrayList<>());
                methodList.add(registeredPacketListener);
            }
        }
    }

    @Override
    public void sendPacket(@NotNull RedisPacket redisPacket) {
        Gson gson = Constants.GSON;
        String json = gson.toJson(redisPacket);

        String packetName = redisPacket.getClass().getName();

        String packetString = packetName + ":" + json;

        redisHandler.publishMessage(publishChannel, packetString);
    }

    @Override
    public <T extends RedisPacket> void handlePacket(@NotNull T redisPacket) {
        Class<? extends RedisPacket> packetType = redisPacket.getClass();
        List<RegisteredPacketListener> registeredPacketListeners =
                packetListener.get(redisPacket.getClass());
        if (registeredPacketListeners == null) return;

        for (RegisteredPacketListener registeredPacketListener : registeredPacketListeners) {
            Method method = registeredPacketListener.method();
            Object object = registeredPacketListener.object();

            try {
                method.invoke(object, redisPacket);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(
                        Level.WARNING, "failed to handle packet for type " + packetType.getSimpleName(), e);
            }
        }
    }

    @Override
    @NotNull
    public UUID getRandomPacketId() {
        return UUID.randomUUID();
    }
}
