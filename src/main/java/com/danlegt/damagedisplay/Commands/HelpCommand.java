package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.Services.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand implements DDCommand {
    @Override
    public String getCommandLabel() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "damagedisplay.command.help";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.toLegacy("&cYou do not have permission to use this command."));
            return true;
        }

        sender.sendMessage(Messages.toLegacy("&8===================="));
        CommandManager.registeredCommands.forEach(cmd -> {
            if (sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage(Messages.toLegacy("&6/dd " + cmd.getCommandLabel() + " &7- &f" + cmd.getDescription()));
            }
        });
        sender.sendMessage(Messages.toLegacy("&8===================="));

        return true;
    }

    @Override
    public String getDescription() {
        return "Show the help screen with all the available commands";
    }
}
