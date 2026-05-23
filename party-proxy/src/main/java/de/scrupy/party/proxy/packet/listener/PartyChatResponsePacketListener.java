package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.response.PartyChatResponsePacket;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PartyChatResponsePacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyChatResponsePacketListener(
            @NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onPartyChat(PartyChatResponsePacket packet) {
        Optional<Party> optionalParty = packet.getParty();
        PartyPlayer sender = packet.getSender();

        if (optionalParty.isEmpty()) {
            UUID uuid = sender.uuid();
            Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
            if (optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                player.sendMessage(messages.getComponent("player.no.party"));
            }
            return;
        }

        Party party = optionalParty.get();
        List<PartyMember> members = party.getMembers();

        String name = sender.name();
        String message = packet.getMessage();
        Component messageToPlayer =
                messages.getComponent(
                        "party.chat.message",
                        Placeholder.parsed("sender", name),
                        Placeholder.unparsed("message", message));

        for (PartyMember member : members) {
            UUID uuid = member.uuid();
            Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
            if (optionalPlayer.isEmpty()) continue;

            Player player = optionalPlayer.get();
            player.sendMessage(messageToPlayer);
        }
    }
}
