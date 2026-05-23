package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.packet.request.PartyInviteRequestPacket;
import de.scrupy.party.proxy.message.Messages;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyInviteSubCommand extends PlayerSubCommand {

    @NotNull
    private final Messages messages;
    @NotNull
    private final PlayerCache<Player> playerCache;
    @NotNull
    private final ProxyRedisPacketHandler redisPacketHandler;

    public PartyInviteSubCommand(@NotNull String commandName,
                                 @NotNull Messages messages,
                                 @NotNull PlayerCache<Player> playerCache,
                                 @NotNull ProxyRedisPacketHandler redisPacketHandler) {
        super(commandName, "<Player-Name>");

        this.messages = messages;
        this.playerCache = playerCache;
        this.redisPacketHandler = redisPacketHandler;
    }

    @Override
    public void execute(@NotNull String[] arguments, @NotNull Player player) {
        if (arguments.length == 0) {
            player.sendMessage(messages.getComponent("party.invite.help"));
            return;
        }

        String playerName = arguments[0];

        String username = player.getUsername();
        if (playerName.equalsIgnoreCase(username)) {
            player.sendMessage(messages.getComponent("party.self.invite"));
            return;
        }

        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);

        UUID randomPacketId = redisPacketHandler.getRandomPacketId();
        PartyInviteRequestPacket partyInviteRequestPacket = new PartyInviteRequestPacket(randomPacketId, partyPlayer, playerName);
        redisPacketHandler.sendRequestPacket(player, partyInviteRequestPacket);
    }
}