package de.scrupy.party.proxy.packet;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.redis.RedisHandler;
import de.scrupy.party.core.redis.RedisManager;
import de.scrupy.party.core.redis.packet.RedisPacket;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.proxy.TaskExecutor;
import de.scrupy.party.proxy.message.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyRedisPacketHandler extends RedisPacketManager {

    @NotNull
    private static final Logger LOGGER = Logger.getLogger(ProxyRedisPacketHandler.class.getSimpleName());
    private static final long TIMEOUT_TIME = 700L;
    @NotNull
    private final Map<UUID, PendingRequest> pendingRequests;
    @NotNull
    private final ScheduledExecutorService scheduler;
    @NotNull
    private final Messages messages;
    @NotNull
    private final TaskExecutor taskExecutor;

    public ProxyRedisPacketHandler(@NotNull RedisHandler redisHandler, @NotNull Messages messages, @NotNull TaskExecutor taskExecutor) {
        super(redisHandler, RedisManager.PARTY_RESPONSE_CHANNEL, RedisManager.PARTY_REQUEST_CHANNEL);
        this.messages = messages;
        this.taskExecutor = taskExecutor;
        this.pendingRequests = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        startScheduler();
    }

    private void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                for (PendingRequest pendingRequest : pendingRequests.values()) {
                    if (System.currentTimeMillis() >= pendingRequest.time()) {
                        UUID packetId = pendingRequest.packetId();
                        pendingRequests.remove(packetId);

                        Player player = pendingRequest.player();
                        player.sendMessage(messages.getComponent("party.response.timeout"));
                    }
                }
            } catch (Exception exception) {
                LOGGER.log(Level.WARNING, "exception while checking pending requests", exception);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T extends RedisPacket> void handlePacket(@NotNull T redisPacket) {
        UUID packetId = redisPacket.getPacketId();
        if (pendingRequests.containsKey(packetId)) {
            removePendingRequest(packetId);
        }

        taskExecutor.execute(() -> {
            super.handlePacket(redisPacket);
        });
    }

    public void removePendingRequest(@NotNull UUID packetId) {
        pendingRequests.remove(packetId);
    }

    public void sendRequestPacket(@NotNull Player player, @NotNull RedisPacket redisPacket) {
        UUID packetId = redisPacket.getPacketId();

        PendingRequest pendingRequest = new PendingRequest(packetId, player, System.currentTimeMillis() + TIMEOUT_TIME);
        pendingRequests.put(packetId, pendingRequest);
        sendPacket(redisPacket);
    }
}