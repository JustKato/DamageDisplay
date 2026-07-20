package com.danlegt.damagedisplay.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

    public static List<DDCommand> registeredCommands = List.of(
        new InfoCommand(),
        new ToggleCommand(),
        new HelpCommand(),
        new ReloadCommand()
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /dd <info|help|toggle|reload>");
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
        if (args.length != 1) {
            return List.of();
        }

        String prefix = args[0].toLowerCase();
        List<String> completions = new ArrayList<>();
        for (DDCommand cmd : registeredCommands) {
            String label = cmd.getCommandLabel();
            if (label.startsWith(prefix) && sender.hasPermission(cmd.getPermission())) {
                completions.add(label);
            }
        }
        return completions;
    }
}
