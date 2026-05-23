package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.RedisHandler;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.core.redis.packet.request.PartyInviteRequestPacket;
import de.scrupy.party.core.redis.packet.response.PartyInviteResponsePacket;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class PartyInviteRequestListener implements RedisPacketListener {

    @NotNull
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PartyInviteRequestListener.class.getSimpleName());

    @NotNull
    private final RedisHandler redisHandler;
    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final RedisPacketManager redisPacketManager;
    @NotNull
    private final ExecutorService executor;

    public PartyInviteRequestListener(
            @NotNull RedisHandler redisHandler,
            @NotNull PartyRepository partyRepository,
            @NotNull RedisPacketManager redisPacketManager,
            @NotNull ExecutorService executor) {
        this.redisHandler = redisHandler;
        this.partyRepository = partyRepository;
        this.redisPacketManager = redisPacketManager;
        this.executor = executor;
    }

    @PacketListener
    public void onPartyInviteRequestPacket(PartyInviteRequestPacket packet) {
        PartyPlayer sender = packet.getSender();
        UUID packetId = packet.getPacketId();
        UUID senderUuid = sender.uuid();
        Optional<Party> optionalParty = partyRepository.getParty(senderUuid);
        String targetName = packet.getTargetName();

        if (optionalParty.isPresent()) {
            Party party = optionalParty.get();

            if (!party.isPartyLeader(sender)) {
                PartyInviteResponsePacket result =
                        new PartyInviteResponsePacket(
                                packetId, sender, null, PartyInviteResponsePacket.ResultType.NO_PERMISSION);
                redisPacketManager.sendPacket(result);
                return;
            }

            if (party.isMember(targetName)) {
                PartyInviteResponsePacket result =
                        new PartyInviteResponsePacket(
                                packetId, sender, null, PartyInviteResponsePacket.ResultType.ALREADY_IN_PARTY);
                redisPacketManager.sendPacket(result);
                return;
            }
        }

        Optional<Party> optionalTargetParty = partyRepository.getParty(targetName);
        if (optionalTargetParty.isPresent()) {
            PartyInviteResponsePacket result =
                    new PartyInviteResponsePacket(
                            packetId, sender, null, PartyInviteResponsePacket.ResultType.ALREADY_IN_PARTY);
            redisPacketManager.sendPacket(result);
            return;
        }

        redisHandler.getPartyPlayer(targetName).thenCompose(optionalPartyPlayer -> {
            if (optionalPartyPlayer.isEmpty()) {
                return CompletableFuture.completedFuture(
                        new PartyInviteResponsePacket(
                                packetId,
                                sender,
                                null,
                                PartyInviteResponsePacket.ResultType.PLAYER_NOT_FOUND));
            }

            executor.execute(
                    () -> {
                        partyRepository.createPartyIfNotExists(sender);
                    });

            PartyPlayer target = optionalPartyPlayer.get();

            return redisHandler.isInviteTokenSet(targetName, sender.name()).thenApply(invited -> {
                if (invited) {
                    return new PartyInviteResponsePacket(
                            packetId,
                            sender,
                            target,
                            PartyInviteResponsePacket.ResultType.ALREADY_INVITED);
                }

                redisHandler.setInviteToken(targetName, sender.name());
                return new PartyInviteResponsePacket(
                        packetId, sender, target, PartyInviteResponsePacket.ResultType.SUCCESS);
            });
        }).thenAccept(resultPacket -> {
            if (resultPacket != null) {
                redisPacketManager.sendPacket(resultPacket);
            }
        }).exceptionally(throwable -> {
            LOGGER.warn("Failed while inviting player to party", throwable);
            PartyInviteResponsePacket result =
                    new PartyInviteResponsePacket(
                            packetId, sender, null, PartyInviteResponsePacket.ResultType.ERROR);
            redisPacketManager.sendPacket(result);
            return null;
        });
    }
}
