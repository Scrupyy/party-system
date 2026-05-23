package de.scrupy.party.server;

import de.scrupy.party.core.config.ConfigManager;
import de.scrupy.party.core.config.RedisConfig;
import de.scrupy.party.core.redis.RedisManager;
import de.scrupy.party.server.packet.*;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartyServer {

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyServer.class.getSimpleName());
    public static boolean running = true;
    @NotNull
    private final ExecutorService executor;
    @NotNull
    private final PartyRepository partyRepository;

    public PartyServer() {
        this.executor = Executors.newSingleThreadExecutor();
        this.partyRepository = new PartyRepository();
        startInputScanner();
    }

    private void startInputScanner() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext() && running) {
                String next = scanner.next();

                if (next.equalsIgnoreCase("stop")) {
                    running = false;
                    stop();
                    return;
                }
            }
        }, "Console-Reader");

        consoleThread.start();
    }

    public void stop() {
        LOGGER.info("shutting down...");
        this.executor.close();
    }

    public void start() {
        long currentTime = System.currentTimeMillis();

        ConfigManager configManager = new ConfigManager();
        RedisConfig redisConfig = configManager.loadConfig(RedisConfig.CONFIG_NAME, RedisConfig.class, new RedisConfig());
        RedisManager redisManager = new RedisManager(redisConfig);
        ServerRedisPacketManager redisPacketManager = new ServerRedisPacketManager(redisManager, executor);

        PartyInviteRequestListener redisPacketListener = new PartyInviteRequestListener(redisManager, partyRepository, redisPacketManager, executor);
        redisPacketManager.registerPacketListener(redisPacketListener);

        PartyAcceptRequestListener partyAcceptRequestListener = new PartyAcceptRequestListener(partyRepository, redisManager, redisPacketManager, executor);
        redisPacketManager.registerPacketListener(partyAcceptRequestListener);

        PartyInfoRequestListener partyInfoRequestListener = new PartyInfoRequestListener(partyRepository, redisPacketManager);
        redisPacketManager.registerPacketListener(partyInfoRequestListener);

        PartyLeaveRequestListener partyLeaveRequestListener = new PartyLeaveRequestListener(partyRepository, redisPacketManager);
        redisPacketManager.registerPacketListener(partyLeaveRequestListener);

        PartyConnectServerRequestListener partyConnectServerListener = new PartyConnectServerRequestListener(partyRepository, redisPacketManager);
        redisPacketManager.registerPacketListener(partyConnectServerListener);

        PartyChatRequestListener partyChatRequestListener = new PartyChatRequestListener(partyRepository, redisPacketManager);
        redisPacketManager.registerPacketListener(partyChatRequestListener);

        long startingTime = System.currentTimeMillis() - currentTime;
        LOGGER.info("party server successfully started after " + startingTime + " ms.");
    }
}