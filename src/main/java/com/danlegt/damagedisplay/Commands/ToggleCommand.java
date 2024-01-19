package com.danlegt.damagedisplay.Commands;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ToggleCommand implements DDCommand {

    public static List<UUID> disabledPlayers = new ArrayList<>();
    // The file that we store all the player toggle command statuses to
    private static final String CACHE_FILE = "cache.dat";

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

        UUID playerUUID = player.getUniqueId();
        if ( ToggleCommand.disabledPlayers.contains(playerUUID) ) {
            sender.sendMessage("Action bar messages toggled " + ChatColor.GREEN + "ON");
            ToggleCommand.disabledPlayers.remove(playerUUID);
        } else {
            sender.sendMessage("Action bar messages toggled " + ChatColor.RED + "OFF");
            ToggleCommand.disabledPlayers.add(playerUUID);
        }

        // Update the toggles list
        saveToggleCache();

        return true;
    }

    @Override
    public String getDescription() {
        return "Toggle the actionbar message you receive whenever dealing/taking damage";
    }

    private static void saveToggleCache() {
        File dataFolder = DamageDisplay.me.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File file = new File(dataFolder, CACHE_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(disabledPlayers));
        } catch (IOException e) {
            e.printStackTrace();
            DamageDisplay.me.getLogger().log(Level.WARNING, "Could not save player toggle command cache: " + e.getMessage());
        }
    }

    public static void loadToggleCache() {
        File file = new File(DamageDisplay.me.getDataFolder(), CACHE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                disabledPlayers = new ArrayList<>((List<UUID>) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                DamageDisplay.me.getLogger().log(Level.WARNING, "Could not load player toggle command cache: " + e.getMessage());
            }
        }
    }

}
