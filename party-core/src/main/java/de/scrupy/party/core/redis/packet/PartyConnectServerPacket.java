package de.scrupy.party.core.redis.packet;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PartyConnectServerPacket extends RedisPacket {

    @NotNull
    public final String host;
    public final int port;
    @NotNull
    private final String serverName;
    @NotNull
    private final PartyPlayer partyPlayer;
    @Nullable
    private final Party party;

    public PartyConnectServerPacket(@NotNull UUID packetId,
                                    @NotNull String host,
                                    int port,
                                    @NotNull String serverName,
                                    @NotNull PartyPlayer partyPlayer,
                                    @Nullable Party party) {
        super(packetId);
        this.host = host;
        this.port = port;
        this.serverName = serverName;
        this.partyPlayer = partyPlayer;
        this.party = party;
    }

    public @NotNull String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public @NotNull String getServerName() {
        return serverName;
    }

    public @NotNull PartyPlayer getPartyPlayer() {
        return partyPlayer;
    }

    public @NotNull Optional<Party> getParty() {
        return Optional.ofNullable(party);
    }
}
