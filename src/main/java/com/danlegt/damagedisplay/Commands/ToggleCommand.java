package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ToggleCommand implements DDCommand {

    public static List<Player> disabledPlayers = new ArrayList<>();

    @Override
    public String getCommandLabel() {
        return "toggle";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if ( !sender.hasPermission("damagedisplay.command.toggle") ) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if ( !(sender instanceof Player player) ) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if ( ToggleCommand.disabledPlayers.contains(player) ) {
            sender.sendMessage("Action bar messages toggled " + ChatColor.GREEN + "ON");
            ToggleCommand.disabledPlayers.remove(player);
        } else {
            sender.sendMessage("Action bar messages toggled " + ChatColor.RED + "OFF");
            ToggleCommand.disabledPlayers.add(player);
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Toggle the actionbar message you receive whenever dealing/taking damage";
    }
}
