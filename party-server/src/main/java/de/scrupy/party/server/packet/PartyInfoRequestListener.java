package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.request.PartyInfoRequestPacket;
import de.scrupy.party.core.redis.packet.response.PartyInfoResponsePacket;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PartyInfoRequestListener implements RedisPacketListener {

    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final ServerRedisPacketManager redisPacketManager;

    public PartyInfoRequestListener(
            @NotNull PartyRepository partyRepository,
            @NotNull ServerRedisPacketManager redisPacketManager) {
        this.partyRepository = partyRepository;
        this.redisPacketManager = redisPacketManager;
    }

    @PacketListener
    public void onPartyInfoRequest(PartyInfoRequestPacket packet) {
        UUID packetId = packet.getPacketId();
        UUID requester = packet.getRequester();

        PartyInfoResponsePacket result;

        Optional<Party> optionalParty = partyRepository.getParty(requester);
        if (optionalParty.isEmpty()) {
            result = new PartyInfoResponsePacket(packetId, null, requester);
        } else {
            result = new PartyInfoResponsePacket(packetId, optionalParty.get(), requester);
        }

        redisPacketManager.sendPacket(result);
    }
}
