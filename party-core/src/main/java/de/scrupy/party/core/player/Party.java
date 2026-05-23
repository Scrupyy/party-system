package de.scrupy.party.core.player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    @NotNull
    private final List<PartyMember> members;
    private final int id;
    @NotNull
    private PartyMember partyLeader;

    public Party(int id, @NotNull PartyMember partyLeader) {
        this.id = id;
        this.partyLeader = partyLeader;
        this.members = new ArrayList<>();
        members.add(partyLeader);
    }

    public boolean isMember(@NotNull String name) {
        for (PartyMember member : members) {
            if (member.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public void addMember(@NotNull PartyMember member) {
        members.add(member);
    }

    public void removeMember(@NotNull UUID uuid) {
        for (PartyMember member : members) {
            if (member.uuid().equals(uuid)) {
                removeMember(member);
                return;
            }
        }
    }

    public void removeMember(@NotNull PartyMember partyMember) {
        members.remove(partyMember);
    }

    public boolean isPartyLeader(@NotNull PartyPlayer partyPlayer) {
        return partyLeader.uuid().equals(partyPlayer.uuid());
    }

    public int getId() {
        return id;
    }

    public @NotNull List<PartyMember> getMembers() {
        return members;
    }

    @NotNull
    public PartyMember getPartyLeader() {
        return partyLeader;
    }

    public void setPartyLeader(@NotNull PartyMember partyLeader) {
        this.partyLeader = partyLeader;
    }
}
