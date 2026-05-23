package de.scrupy.party.server.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import de.scrupy.party.core.redis.packet.PacketListener;
import de.scrupy.party.core.redis.packet.PartyConnectServerPacket;
import de.scrupy.party.core.redis.packet.RedisPacketListener;
import de.scrupy.party.core.redis.packet.RedisPacketManager;
import de.scrupy.party.server.party.PartyRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PartyConnectServerRequestListener implements RedisPacketListener {

    @NotNull
    private final PartyRepository partyRepository;
    @NotNull
    private final RedisPacketManager redisPacketManager;

    public PartyConnectServerRequestListener(@NotNull PartyRepository partyRepository, @NotNull RedisPacketManager redisPacketManager) {
        this.partyRepository = partyRepository;
        this.redisPacketManager = redisPacketManager;
    }

    @PacketListener
    public void onServerConnect(PartyConnectServerPacket packet) {
        UUID packetId = packet.getPacketId();
        String host = packet.getHost();
        int port = packet.getPort();
        String serverName = packet.getServerName();
        PartyPlayer partyPlayer = packet.getPartyPlayer();

        UUID uuid = partyPlayer.uuid();
        Optional<Party> optionalParty = partyRepository.getParty(uuid);
        if (optionalParty.isEmpty()) return;

        Party party = optionalParty.get();
        if (!party.isPartyLeader(partyPlayer)) return;

        PartyConnectServerPacket partyConnectServerPacket = new PartyConnectServerPacket(packetId,
                host,
                port,
                serverName,
                partyPlayer,
                party);
        redisPacketManager.sendPacket(partyConnectServerPacket);
    }
}
