package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.RedisManager;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.core.redis.packet.request.PartyAcceptRequestPacket;
import de.scrupy.party.core.redis.packet.response.PartyAcceptResponsePacket;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PartyAcceptRequestListener implements RedisPacketListener {

    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final RedisManager redisManager;
    @NotNull
    private final RedisPacketManager redisPacketManager;
    @NotNull
    private final ExecutorService executor;

    public PartyAcceptRequestListener(@NotNull PartyRepository partyRepository,
                                      @NotNull RedisManager redisManager,
                                      @NotNull RedisPacketManager redisPacketManager,
                                      @NotNull ExecutorService executor) {
        this.partyRepository = partyRepository;
        this.redisManager = redisManager;
        this.redisPacketManager = redisPacketManager;
        this.executor = executor;
    }

    @PacketListener
    public void onPartyAcceptRequest(PartyAcceptRequestPacket packet) {
        UUID packetId = packet.getPacketId();
        PartyPlayer playerToJoin = packet.getPartyPlayer();
        String invitorName = packet.getName();

        UUID uuid = playerToJoin.uuid();
        Optional<Party> optionalCurrentParty = partyRepository.getParty(uuid);
        if (optionalCurrentParty.isPresent()) {
            PartyAcceptResponsePacket result = new PartyAcceptResponsePacket(packetId,
                    null,
                    playerToJoin,
                    PartyAcceptResponsePacket.ResultType.ALREADY_JOINED);
            redisPacketManager.sendPacket(result);
            return;
        }

        String name = playerToJoin.name();
        redisManager.isInviteTokenSet(name, invitorName).thenAccept(invited -> {
            if (!invited) {
                PartyAcceptResponsePacket partyAcceptResultPacket = new PartyAcceptResponsePacket(packetId, null, playerToJoin,
                        PartyAcceptResponsePacket.ResultType.NOT_INVITED);
                redisPacketManager.sendPacket(partyAcceptResultPacket);
                return;
            }

            redisManager.removeInviteToken(name, invitorName).thenAccept(result -> {
                executor.execute(() -> {
                    Optional<Party> optionalParty = partyRepository.getParty(invitorName);
                    if (optionalParty.isEmpty()) {
                        PartyAcceptResponsePacket partyAcceptResultPacket =
                                new PartyAcceptResponsePacket(
                                        packetId,
                                        null,
                                        playerToJoin,
                                        PartyAcceptResponsePacket.ResultType.PARTY_NOT_FOUND);
                        redisPacketManager.sendPacket(partyAcceptResultPacket);
                        return;
                    }

                    Party party = optionalParty.get();
                    partyRepository.addPlayerToParty(party, playerToJoin);

                    PartyAcceptResponsePacket partyAcceptResultPacket =
                            new PartyAcceptResponsePacket(
                                    packetId,
                                    party,
                                    playerToJoin,
                                    PartyAcceptResponsePacket.ResultType.SUCCESS);
                    redisPacketManager.sendPacket(partyAcceptResultPacket);
                });
            });
        }).exceptionally(throwable -> {
            PartyAcceptResponsePacket partyAcceptResultPacket =
                    new PartyAcceptResponsePacket(
                            packetId, null, playerToJoin, PartyAcceptResponsePacket.ResultType.ERROR);
            redisPacketManager.sendPacket(partyAcceptResultPacket);
            return null;
        });
    }
}
