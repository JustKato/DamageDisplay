package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /dd <info|toggle|reload>");
            return true;
        }

        var registeredCommands = List.of(new InfoCommand());

        for ( var cmd: registeredCommands ) {
            if ( cmd.getCommandLabel().equalsIgnoreCase(args[0]) ) {
                return cmd.handleCommand(sender, command, label, args);
            }
        }


        if (args[0].equalsIgnoreCase("info")) {

        } else if (args[0].equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission("damagedisplay.command.toggle")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            // Toggle functionality here
            sender.sendMessage(ChatColor.GREEN + "Toggled setting for " + player.getName());
        }

        return false;
    }

}
