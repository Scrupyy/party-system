package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.packet.request.PartyAcceptRequestPacket;
import de.scrupy.party.proxy.message.Messages;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyAcceptSubCommand extends PlayerSubCommand {

    @NotNull
    private final PlayerCache<Player> playerCache;
    @NotNull
    private final ProxyRedisPacketHandler redisPacketHandler;
    @NotNull
    private final Messages messages;

    public PartyAcceptSubCommand(@NotNull String commandName,
                                 @NotNull PlayerCache<Player> playerCache,
                                 @NotNull ProxyRedisPacketHandler proxyRedisPacketHandler,
                                 @NotNull Messages messages) {
        super(commandName, "<Player-Name>");
        this.playerCache = playerCache;
        this.redisPacketHandler = proxyRedisPacketHandler;
        this.messages = messages;
    }

    @Override
    public void execute(@NotNull String[] arguments, @NotNull Player player) {
        if (arguments.length == 0) {
            player.sendMessage(messages.getComponent("command.chat.help"));
            return;
        }

        String senderName = arguments[0];
        String username = player.getUsername();

        if (senderName.equals(username)) {
            player.sendMessage(messages.getComponent("party.self.accept"));
            return;
        }

        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);

        UUID packetId = redisPacketHandler.getRandomPacketId();
        PartyAcceptRequestPacket partyAcceptRequestPacket = new PartyAcceptRequestPacket(packetId, partyPlayer, senderName);
        redisPacketHandler.sendRequestPacket(player, partyAcceptRequestPacket);
    }
}