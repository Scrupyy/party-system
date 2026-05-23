package de.scrupy.party.proxy.packet.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.response.PartyLeaveResponsePacket;
import de.scrupy.party.proxy.message.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PartyLeaveResponsePacketListener implements RedisPacketListener {

    @NotNull
    private final ProxyServer proxyServer;
    @NotNull
    private final Messages messages;

    public PartyLeaveResponsePacketListener(
            @NotNull ProxyServer proxyServer, @NotNull Messages messages) {
        this.proxyServer = proxyServer;
        this.messages = messages;
    }

    @PacketListener
    public void onPartyLeave(PartyLeaveResponsePacket packet) {
        PartyPlayer partyPlayer = packet.getPartyPlayer();
        PartyLeaveResponsePacket.ResultType resultType = packet.getResultType();

        UUID uuid = partyPlayer.uuid();
        Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
        if (optionalPlayer.isEmpty()) return;

        Player player = optionalPlayer.get();
        switch (resultType) {
            case NO_PARTY -> player.sendMessage(messages.getComponent("player.no.party"));
            case PARTY_DELETED -> player.sendMessage(messages.getComponent("party.deleted"));
            case SUCCESS -> player.sendMessage(messages.getComponent("party.leaved"));
        }
    }
}
