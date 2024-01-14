package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements DDCommand {
    @Override
    public String getCommandLabel() {
        return "reload";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( !sender.hasPermission("damagedisplay.command.reload") ) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
        DamageDisplay.me.reloadConfig();

        return true;
    }

    @Override
    public String getDescription() {
        return "Reload the configuration from the config file into memory.";
    }
}
