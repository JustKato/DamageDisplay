package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandManager implements CommandExecutor {

    public static List<DDCommand> registeredCommands = List.of(
        new InfoCommand(),
        new ToggleCommand()
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /dd <info|help|toggle|reload>");
            return true;
        }

        for ( var cmd: registeredCommands ) {
            if ( cmd.getCommandLabel().equalsIgnoreCase(args[0]) ) {
                return cmd.handleCommand(sender, command, label, args);
            }
        }

        return false;
    }

}
