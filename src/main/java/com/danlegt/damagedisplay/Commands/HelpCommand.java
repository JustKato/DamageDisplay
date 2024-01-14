package com.danlegt.damagedisplay.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand implements DDCommand {
    @Override
    public String getCommandLabel() {
        return "help";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( !sender.hasPermission("damagedisplay.command.help") ) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "====================");
        CommandManager.registeredCommands.forEach( cmd -> {
            sender.sendMessage(ChatColor.GOLD + "/dd " + cmd.getCommandLabel() + ChatColor.GRAY + " - " + ChatColor.RESET + cmd.getDescription());
        });
        sender.sendMessage(ChatColor.GRAY + "====================");

        return true;
    }

    @Override
    public String getDescription() {
        return "Show the help screen with all the available commands";
    }
}
