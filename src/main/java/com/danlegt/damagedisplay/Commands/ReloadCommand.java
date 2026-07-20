package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements DDCommand {
    @Override
    public String getCommandLabel() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "damagedisplay.command.reload";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.toLegacy("&cYou do not have permission to use this command."));
            return true;
        }

        DamageDisplay.me.reloadConfig();
        DamageDisplay.me.getSettings().reload();
        DamageDisplay.me.getPlayerPrefs().load();

        sender.sendMessage(Messages.toLegacy("&aConfiguration reloaded."));
        return true;
    }

    @Override
    public String getDescription() {
        return "Reload the configuration from the config file into memory.";
    }
}
