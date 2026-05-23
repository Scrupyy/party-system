package de.scrupy.party.server.party;

import de.scrupy.party.core.player.Party;
import de.scrupy.party.core.player.PartyMember;
import de.scrupy.party.core.player.PartyPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PartyRepository {

    private static final Random RANDOM = new Random();
    private final Map<UUID, Party> partiesByPlayerId;
    private final Map<String, Party> partiesByPlayerName;
    private final Map<Integer, Party> partiesById;

    public PartyRepository() {
        partiesById = new HashMap<>();
        partiesByPlayerId = new HashMap<>();
        partiesByPlayerName = new HashMap<>();
    }

    @NotNull
    public Optional<Party> getParty(@NotNull String name) {
        Party party = partiesByPlayerName.get(name.toLowerCase());
        return Optional.ofNullable(party);
    }

    @NotNull
    public Optional<Party> getParty(@NotNull UUID uuid) {
        Party party = partiesByPlayerId.get(uuid);
        return Optional.ofNullable(party);
    }

    @NotNull
    public Party createPartyIfNotExists(@NotNull PartyPlayer partyPlayer) {
        UUID uuid = partyPlayer.uuid();
        Party currentParty = partiesByPlayerId.get(uuid);
        if (currentParty != null) return currentParty;

        int randomPartyId = getRandomPartyId();
        String name = partyPlayer.name();

        PartyMember partyMember = new PartyMember(uuid, name);

        Party party = new Party(randomPartyId, partyMember);
        partiesByPlayerId.put(uuid, party);
        partiesById.put(randomPartyId, party);
        partiesByPlayerName.put(name.toLowerCase(), party);
        return party;
    }

    private int getRandomPartyId() {
        int partyId = 0;

        while (partyId == 0) {
            int id = RANDOM.nextInt(999999);
            if (partiesById.containsKey(id)) continue;

            partyId = id;
        }

        return partyId;
    }

    public void addPlayerToParty(@NotNull Party party, @NotNull PartyPlayer playerToJoin) {
        UUID uuid = playerToJoin.uuid();
        String name = playerToJoin.name();
        int id = party.getId();
        PartyMember partyMember = new PartyMember(uuid, name);

        party.addMember(partyMember);
        partiesByPlayerName.put(name.toLowerCase(), party);
        partiesById.put(id, party);
        partiesByPlayerId.put(uuid, party);
    }

    public void removePlayerFromParty(@NotNull Party party, @NotNull PartyPlayer partyPlayer) {
        UUID uuid = partyPlayer.uuid();
        String name = partyPlayer.name();

        party.removeMember(uuid);
        partiesByPlayerId.remove(uuid);
        partiesByPlayerName.remove(name.toLowerCase());
    }

    public void deleteParty(@NotNull Party party) {
        int id = party.getId();
        partiesById.remove(id);

        PartyMember partyLeader = party.getPartyLeader();
        partiesByPlayerId.remove(partyLeader.uuid());

        String partyLeaderName = partyLeader.name();
        partiesByPlayerName.remove(partyLeaderName.toLowerCase());
    }

    public Map<Integer, Party> getPartiesById() {
        return partiesById;
    }

    public Map<String, Party> getPartiesByPlayerName() {
        return partiesByPlayerName;
    }

    public Map<UUID, Party> getPartiesByPlayerId() {
        return partiesByPlayerId;
    }
}
