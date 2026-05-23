package de.scrupy.party.proxy.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.scrupy.party.proxy.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PartyCommand implements SimpleCommand {

    @NotNull
    private final Map<String, PlayerSubCommand> subCommands;
    @NotNull
    private final Messages messages;
    private String commandHelp;

    public PartyCommand(@NotNull Messages messages) {
        this.messages = messages;
        this.subCommands = new HashMap<>();
        this.commandHelp = "";
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("This command can only be executed by a player.", NamedTextColor.RED));
            return;
        }

        String[] arguments = invocation.arguments();
        if (arguments.length < 1) {
            sendHelp(player);
            return;
        }

        String argument = arguments[0];
        PlayerSubCommand subCommand = subCommands.get(argument);
        if (subCommand == null) {
            sendHelp(player);
            return;
        }

        String[] subCommandArguments = Arrays.copyOfRange(arguments, 1, arguments.length);
        subCommand.execute(subCommandArguments, player);
    }

    private void updateCommandHelp() {
        Collection<PlayerSubCommand> commands = subCommands.values();
        StringBuilder stringBuilder = new StringBuilder();

        for (PlayerSubCommand command : commands) {
            String commandName = command.getCommandName();
            String usage = command.getUsage();

            stringBuilder.append("<newline>")
                    .append("<yellow>")
                    .append("/party ")
                    .append(commandName)
                    .append(" ")
                    .append(usage)
                    .append("</yellow>");
        }

        commandHelp = stringBuilder.toString();
    }

    private void sendHelp(@NotNull Player player) {
        Component message = messages.getComponent("party.command.help",
                Placeholder.parsed("commands", commandHelp));
        player.sendMessage(message);
    }

    public void registerSubCommand(@NotNull PlayerSubCommand subCommand) {
        String commandName = subCommand.getCommandName();
        subCommands.put(commandName, subCommand);
        updateCommandHelp();
    }
}
