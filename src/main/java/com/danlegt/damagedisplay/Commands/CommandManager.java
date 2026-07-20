package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.Services.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    public static final List<DDCommand> registeredCommands = List.of(
            new InfoCommand(),
            new ToggleCommand(),
            new HelpCommand(),
            new ReloadCommand()
    );

    private static final List<String> TOGGLE_OPTIONS = List.of("actionbar", "heals");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Messages.toLegacy("&cUsage: /dd <info|help|toggle|reload>"));
            return true;
        }

        for (var cmd : registeredCommands) {
            if (cmd.getCommandLabel().equalsIgnoreCase(args[0])) {
                return cmd.handleCommand(sender, command, label, args);
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();
            for (DDCommand cmd : registeredCommands) {
                String cmdLabel = cmd.getCommandLabel();
                if (cmdLabel.startsWith(prefix) && sender.hasPermission(cmd.getPermission())) {
                    completions.add(cmdLabel);
                }
            }
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")
                && sender.hasPermission("damagedisplay.command.toggle")) {
            String prefix = args[1].toLowerCase();
            List<String> completions = new ArrayList<>();
            for (String option : TOGGLE_OPTIONS) {
                if (option.startsWith(prefix)) {
                    completions.add(option);
                }
            }
            return completions;
        }

        return List.of();
    }
}
