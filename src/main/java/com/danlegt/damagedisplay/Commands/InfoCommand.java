package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public class InfoCommand implements DDCommand{

    @Override
    public String getCommandLabel() {
        return "info";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( !sender.hasPermission("damagedisplay.command.info") ) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        FileConfiguration config = DamageDisplay.me.getConfig();
        sender.sendMessage(ChatColor.GREEN + "DamageDisplay Configuration:");
        for (String key : config.getKeys(true)) {
            sender.sendMessage(ChatColor.AQUA + key + ": " + Objects.requireNonNull(config.get(key)));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Retrieve information about the plugin";
    }

}
