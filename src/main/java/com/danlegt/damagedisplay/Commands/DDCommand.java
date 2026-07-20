package com.danlegt.damagedisplay.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface DDCommand {

    String getCommandLabel();

    String getPermission();

    boolean handleCommand(CommandSender sender, Command command, String label, String[] args);

    String getDescription();

}
