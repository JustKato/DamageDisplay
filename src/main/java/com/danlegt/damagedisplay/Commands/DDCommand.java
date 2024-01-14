package com.danlegt.damagedisplay.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface DDCommand {

    public String getCommandLabel();

    public boolean handleCommand(CommandSender sender, Command command, String label, String[] args);

    public String getDescription();

}
