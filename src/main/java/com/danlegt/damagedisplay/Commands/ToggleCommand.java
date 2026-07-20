package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.Messages;
import com.danlegt.damagedisplay.Services.PlayerPrefs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCommand implements DDCommand {

    @Override
    public String getCommandLabel() {
        return "toggle";
    }

    @Override
    public String getPermission() {
        return "damagedisplay.command.toggle";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.toLegacy("&cYou do not have permission to use this command."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.toLegacy("&cOnly players can use this command."));
            return true;
        }

        String target = args.length >= 2 ? args[1].toLowerCase() : "actionbar";
        PlayerPrefs prefs = DamageDisplay.me.getPlayerPrefs();

        switch (target) {
            case "actionbar" -> {
                boolean enabled = prefs.toggleActionBar(player.getUniqueId());
                sender.sendMessage(Messages.toLegacy(
                        enabled ? "&7Action bar messages toggled &aON" : "&7Action bar messages toggled &cOFF"
                ));
            }
            case "heals" -> {
                boolean enabled = prefs.toggleHeals(player.getUniqueId());
                sender.sendMessage(Messages.toLegacy(
                        enabled ? "&7Heal displays toggled &aON" : "&7Heal displays toggled &cOFF"
                ));
            }
            default -> sender.sendMessage(Messages.toLegacy("&cUsage: /dd toggle [actionbar|heals]"));
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Toggle action bar messages or heal displays (/dd toggle [actionbar|heals])";
    }
}
