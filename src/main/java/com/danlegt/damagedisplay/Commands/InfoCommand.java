package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class InfoCommand implements DDCommand {

    @Override
    public String getCommandLabel() {
        return "info";
    }

    @Override
    public String getPermission() {
        return "damagedisplay.command.info";
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.toLegacy("&cYou do not have permission to use this command."));
            return true;
        }

        FileConfiguration config = DamageDisplay.me.getConfig();
        sender.sendMessage(Messages.toLegacy(
                "&aDamageDisplay (&b" + DamageDisplay.me.getDescription().getVersion() + "&a)"
        ));
        for (String key : config.getKeys(true)) {
            Object val = config.get(key);
            if (val == null) {
                continue;
            }

            if (val instanceof Boolean || val instanceof String || val instanceof Number) {
                sender.sendMessage(Messages.toLegacy("&6" + key + "&7: &f" + val));
            }
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Retrieve information about the plugin";
    }
}
