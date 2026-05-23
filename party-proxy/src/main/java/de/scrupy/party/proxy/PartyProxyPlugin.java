package de.scrupy.party.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.config.ConfigManager;
import de.scrupy.party.core.config.MessageConfig;
import de.scrupy.party.core.config.RedisConfig;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.RedisManager;
import de.scrupy.party.proxy.command.*;
import de.scrupy.party.proxy.listener.ServerConnectedListener;
import de.scrupy.party.proxy.message.Messages;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import de.scrupy.party.proxy.packet.listener.*;
import de.scrupy.party.proxy.player.DefaultPlayerCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(
        id = "party-system",
        name = "PartySystem",
        version = "1.0-SNAPSHOT"
)
public class PartyProxyPlugin {

    @NotNull
    private final ProxyServer proxy;
    @NotNull
    private final Logger logger;
    @Nullable
    private RedisManager redisManager;

    @Inject
    public PartyProxyPlugin(@NotNull ProxyServer server, @NotNull Logger logger) {
        this.proxy = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        TaskExecutor taskExecutor = new TaskExecutor(proxy, this);

        ConfigManager configManager = new ConfigManager();

        MessageConfig messageConfig = configManager.loadConfig(MessageConfig.CONFIG_NAME, MessageConfig.class, new MessageConfig());
        RedisConfig redisConfig = configManager.loadConfig(RedisConfig.CONFIG_NAME, RedisConfig.class, new RedisConfig());
        Messages messages = new Messages(messageConfig);

        try {
            redisManager = new RedisManager(redisConfig);
            logger.info("Successfully connected to Redis.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "failed to connect to redis database. Stopped loading of party-system.", e);
            return;
        }

        ProxyRedisPacketHandler redisPacketManager = new ProxyRedisPacketHandler(redisManager, messages, taskExecutor);
        PlayerCache<Player> playerCache = new DefaultPlayerCache(redisManager);

        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta partyCommandMeta = commandManager.metaBuilder("party")
                .plugin(this)
                .build();

        PartyAcceptResponsePacketListener partyAcceptPacketListener = new PartyAcceptResponsePacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyAcceptPacketListener);
        PartyInviteResponsePacketListener partyInviteResponsePacketListener = new PartyInviteResponsePacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyInviteResponsePacketListener);
        PartyInfoResponsePacketListener partyInfoResponsePacketListener = new PartyInfoResponsePacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyInfoResponsePacketListener);
        PartyLeaveResponsePacketListener partyLeaveResponsePacketListener = new PartyLeaveResponsePacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyLeaveResponsePacketListener);
        PartyUpdateLeaderPacketListener partyUpdateLeaderPacketListener = new PartyUpdateLeaderPacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyUpdateLeaderPacketListener);
        PartyConnectServerPacketListener partyConnectServerPacketListener = new PartyConnectServerPacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyConnectServerPacketListener);
        PartyChatResponsePacketListener partyChatResponsePacketListener = new PartyChatResponsePacketListener(proxy, messages);
        redisPacketManager.registerPacketListener(partyChatResponsePacketListener);

        PartyCommand partyCommand = new PartyCommand(messages);
        partyCommand.registerSubCommand(new PartyInviteSubCommand("invite", messages, playerCache, redisPacketManager));
        partyCommand.registerSubCommand(new PartyAcceptSubCommand("accept", playerCache, redisPacketManager, messages));
        partyCommand.registerSubCommand(new PartyInfoSubCommand("info", redisPacketManager));
        partyCommand.registerSubCommand(new PartyLeaveSubCommand("leave", redisPacketManager, playerCache));
        partyCommand.registerSubCommand(new PartyChatSubCommand("chat", playerCache, redisPacketManager, messages));
        commandManager.register(partyCommandMeta, partyCommand);

        EventManager eventManager = proxy.getEventManager();
        ServerConnectedListener serverConnectedListener = new ServerConnectedListener(playerCache, redisPacketManager);
        eventManager.register(this, serverConnectedListener);

        logger.info("Plugin successfully started.");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (redisManager != null) {
            redisManager.shutdown();
        }
    }
}