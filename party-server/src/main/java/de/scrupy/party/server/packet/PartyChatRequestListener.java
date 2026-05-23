package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.core.redis.packet.request.PartyChatRequestPacket;
import de.scrupy.party.core.redis.packet.response.PartyChatResponsePacket;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PartyChatRequestListener implements RedisPacketListener {

    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final RedisPacketManager redisPacketManager;

    public PartyChatRequestListener(
            @NotNull PartyRepository partyRepository, @NotNull RedisPacketManager redisPacketManager) {
        this.partyRepository = partyRepository;
        this.redisPacketManager = redisPacketManager;
    }

    @PacketListener
    public void onPartyChat(PartyChatRequestPacket packet) {
        UUID packetId = packet.getPacketId();
        PartyPlayer partyPlayer = packet.getPartyPlayer();
        UUID uuid = partyPlayer.uuid();

        Optional<Party> optionalParty = partyRepository.getParty(uuid);
        if (optionalParty.isEmpty()) {
            PartyChatResponsePacket partyChatResponsePacket =
                    new PartyChatResponsePacket(packetId, partyPlayer, null, "");
            redisPacketManager.sendPacket(partyChatResponsePacket);
            return;
        }

        String message = packet.getMessage();
        Party party = optionalParty.get();
        PartyChatResponsePacket partyChatResponsePacket =
                new PartyChatResponsePacket(packetId, partyPlayer, party, message);
        redisPacketManager.sendPacket(partyChatResponsePacket);
    }
}
