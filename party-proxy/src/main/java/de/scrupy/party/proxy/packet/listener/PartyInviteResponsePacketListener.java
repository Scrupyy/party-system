package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.response.PartyInviteResponsePacket;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PartyInviteResponsePacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyInviteResponsePacketListener(@NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onPartyInviteResultPacket(PartyInviteResponsePacket packet) {
        PartyPlayer sender = packet.getSender();
        Optional<PartyPlayer> target = packet.getTarget();
        PartyInviteResponsePacket.ResultType resultType = packet.getResultType();

        Optional<Player> optionalSenderPlayer = proxyServer.getPlayer(sender.uuid());
        if (optionalSenderPlayer.isPresent()) {
            Player player = optionalSenderPlayer.get();

            switch (resultType) {
                case ALREADY_IN_PARTY -> player.sendMessage(messages.getComponent("player.in.party"));
                case PLAYER_NOT_FOUND -> player.sendMessage(messages.getComponent("player.not.found"));
                case ALREADY_INVITED -> player.sendMessage(messages.getComponent("party.already.invited"));
                case ERROR -> player.sendMessage(messages.getComponent("party.error.message"));
                case SUCCESS -> {
                    if (target.isEmpty()) {
                        player.sendMessage(messages.getComponent("party.error.message"));
                        return;
                    }

                    PartyPlayer partyPlayer = target.get();
                    String name = partyPlayer.name();
                    Component message = messages.getComponent("player.invite.success", Placeholder.parsed("target", name));
                    player.sendMessage(message);
                }
                case NO_PERMISSION -> player.sendMessage(messages.getComponent("party.no.permission"));
            }
        }

        if (target.isPresent() && resultType.equals(PartyInviteResponsePacket.ResultType.SUCCESS)) {
            PartyPlayer targetPartyPlayer = target.get();
            UUID uuid = targetPartyPlayer.uuid();
            Optional<Player> optionalTargetPlayer = proxyServer.getPlayer(uuid);
            if (optionalTargetPlayer.isEmpty()) return;

            Player targetPlayer = optionalTargetPlayer.get();
            String name = sender.name();
            Component message = messages.getComponent("player.party.invite", Placeholder.parsed("sender", name));
            targetPlayer.sendMessage(message);
        }
    }
}
