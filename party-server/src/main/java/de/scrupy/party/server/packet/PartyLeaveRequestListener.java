package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.PartyUpdateLeaderPacket;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.core.redis.packet.request.PartyLeaveRequestPacket;
import de.scrupy.party.core.redis.packet.response.PartyLeaveResponsePacket;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class PartyLeaveRequestListener implements RedisPacketListener {

    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final RedisPacketManager redisPacketManager;

    public PartyLeaveRequestListener(@NotNull PartyRepository partyRepository, @NotNull RedisPacketManager redisPacketManager) {
        this.partyRepository = partyRepository;
        this.redisPacketManager = redisPacketManager;
    }

    @PacketListener
    public void onPartyLeave(PartyLeaveRequestPacket packet) {
        UUID packetId = packet.getPacketId();
        PartyPlayer player = packet.getPlayer();

        UUID uuid = player.uuid();
        Optional<Party> optionalParty = partyRepository.getParty(uuid);
        Consumer<PartyLeaveResponsePacket.ResultType> resultType =
                result -> {
                    PartyLeaveResponsePacket partyLeaveResponsePacket =
                            new PartyLeaveResponsePacket(packetId, result, player);
                    redisPacketManager.sendPacket(partyLeaveResponsePacket);
                };

        if (optionalParty.isEmpty()) {
            resultType.accept(PartyLeaveResponsePacket.ResultType.NO_PARTY);
            return;
        }

        Party party = optionalParty.get();
        List<PartyMember> members = party.getMembers();
        if (party.isPartyLeader(player)) {
            int memberAmount = members.size();
            if (memberAmount == 1) {
                partyRepository.deleteParty(party);

                resultType.accept(PartyLeaveResponsePacket.ResultType.PARTY_DELETED);
                return;
            }

            party.removeMember(uuid);
            PartyMember partyMember = members.getFirst();
            party.setPartyLeader(partyMember);
            UUID randomPacketId = redisPacketManager.getRandomPacketId();
            PartyUpdateLeaderPacket partyUpdateLeaderPacket =
                    new PartyUpdateLeaderPacket(randomPacketId, party);
            redisPacketManager.sendPacket(partyUpdateLeaderPacket);
        }

        partyRepository.removePlayerFromParty(party, player);
        resultType.accept(PartyLeaveResponsePacket.ResultType.SUCCESS);
    }
}
