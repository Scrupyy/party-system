package de.scrupy.party.core.config;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MessageConfig implements Config, MessageProvider {

    @NotNull
    public static final String CONFIG_NAME = "messages.json";
    @NotNull
    private final Map<String, String> messages;

    public MessageConfig() {
        this.messages = new HashMap<>();
        initializeMessages();
    }

    private void initializeMessages() {
        messages.put("party.prefix", "<gradient:#00ff87:#60efff>PartySystem</gradient> <dark_gray>-</dark_gray> ");
        messages.put("party.command.help", "<grey>Befehle:</grey><commands>");
        messages.put("party.error.message", "<red>Es ist ein Fehler aufgetreten!</red>");
        messages.put("party.response.timeout", "<red>Das Party System ist aktuell nicht verfügbar!</red>");
        messages.put("party.invite.help", "<grey>Verwende /party invite <Name></grey>");
        messages.put("party.already.joined", "<red>Du bist bereits in einer Party.</red>");
        messages.put("player.not.found", "<red>Der Spieler ist nicht online!</red>");
        messages.put("player.invite.success", "<green>Der Spieler <target> wurde zur Party eingeladen.</green>");
        messages.put("player.no.party", "<red>Du bist in keiner Party!</red>");
        messages.put("player.party.invite", "<grey><sender> lädt dich in eine Party ein. Nutze /party accept <sender> um der Party beizutreten.");
        messages.put("party.self.invite", "<red>Du kannst dich nicht selbst einladen.</red>");
        messages.put("party.already.invited", "<red>Du hast den Spieler bereits eingeladen. Warte noch etwas ab.</red>");
        messages.put("party.not.invited", "<red>Du wurdest von diesem Spieler nicht eingeladen.</red>");
        messages.put("party.not.found", "<red>Die Party wurde nicht gefunden.</red>");
        messages.put("player.in.party", "<red>Der Spieler ist bereits in einer Party.</red>");
        messages.put("party.join.success", "<green>Du bist der Party beigetreten.</green>");
        messages.put("player.joined.party", "<grey>Der Spieler <target> ist der Party beigetreten!</grey>");
        messages.put("party.updated.leader", "<grey>Der Spieler <yellow><player></yellow> ist jetzt der Party-Leiter</grey>");
        messages.put("party.deleted", "<red>Die Party wurde aufgelöst.</red>");
        messages.put("party.leaved", "<red>Du hast die Party verlassen!</red>");
        messages.put("party.no.permission", "<red>Dazu hast du keine Rechte!</red>");
        messages.put("party.info.message", "<grey>Deine aktuelle Party:</grey>" +
                "<newline><grey>Party-ID:</grey><yellow><id></yellow>" +
                "<newline><grey>Member:</grey><members>" +
                "<newline><grey>Leader:</grey><yellow><leader></yellow>");
        messages.put("party.chat.message", "Party Message - <sender>: <message>");
        messages.put("party.member.format", "<newline><yellow><member></yellow>");
        messages.put("party.joining.server", "<grey>Die Party betritt den Server: <yellow><server></yellow>.</grey>");
    }

    @Override
    public @NotNull Map<String, String> getMessages() {
        return messages;
    }
}
