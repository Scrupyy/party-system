package de.scrupy.party.proxy.command;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerSubCommand {

    @NotNull
    private final String commandName;
    @NotNull
    private final String usage;

    public PlayerSubCommand(@NotNull String commandName) {
        this.commandName = commandName;
        this.usage = "";
    }

    public PlayerSubCommand(@NotNull String commandName, @NotNull String usage) {
        this.commandName = commandName;
        this.usage = usage;
    }

    public abstract void execute(@NotNull String[] arguments, @NotNull Player player);

    public @NotNull String getCommandName() {
        return commandName;
    }

    public @NotNull String getUsage() {
        return usage;
    }
}
