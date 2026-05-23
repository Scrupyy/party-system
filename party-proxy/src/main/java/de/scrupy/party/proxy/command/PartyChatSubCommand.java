package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.player.PlayerCache;
import de.scrupy.party.core.redis.packet.request.PartyChatRequestPacket;
import de.scrupy.party.proxy.message.Messages;
import de.scrupy.party.proxy.packet.ProxyRedisPacketHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyChatSubCommand extends PlayerSubCommand {

    @NotNull
    private final PlayerCache<Player> playerCache;
    @NotNull
    private final ProxyRedisPacketHandler packetHandler;
    @NotNull
    private final Messages messages;

    public PartyChatSubCommand(@NotNull String commandName,
                               @NotNull PlayerCache<Player> playerCache,
                               @NotNull ProxyRedisPacketHandler packetHandler, @NotNull Messages messages) {
        super(commandName, "<Message>");
        this.playerCache = playerCache;
        this.packetHandler = packetHandler;
        this.messages = messages;
    }

    @Override
    public void execute(@NotNull String[] arguments, @NotNull Player player) {
        if (arguments.length == 0) {
            player.sendMessage(messages.getComponent("command.chat.help"));
            return;
        }

        PartyPlayer partyPlayer = playerCache.getPartyPlayer(player);
        String message = String.join(" ", arguments);

        UUID packetId = packetHandler.getRandomPacketId();
        PartyChatRequestPacket packet = new PartyChatRequestPacket(packetId, partyPlayer, message);
        packetHandler.sendRequestPacket(player, packet);
    }
}
