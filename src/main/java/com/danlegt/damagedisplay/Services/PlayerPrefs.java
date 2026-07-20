package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerPrefs {

    private static final String PREFS_FILE = "player-prefs.yml";
    private static final String LEGACY_CACHE = "cache.dat";

    private final Map<UUID, Boolean> actionBarEnabled = new HashMap<>();
    private final Map<UUID, Boolean> healsEnabled = new HashMap<>();

    public boolean isActionBarEnabled(UUID playerId) {
        return actionBarEnabled.getOrDefault(playerId, true);
    }

    public boolean isHealsEnabled(UUID playerId) {
        return healsEnabled.getOrDefault(playerId, true);
    }

    public boolean toggleActionBar(UUID playerId) {
        boolean next = !isActionBarEnabled(playerId);
        actionBarEnabled.put(playerId, next);
        save();
        return next;
    }

    public boolean toggleHeals(UUID playerId) {
        boolean next = !isHealsEnabled(playerId);
        healsEnabled.put(playerId, next);
        save();
        return next;
    }

    public void load() {
        actionBarEnabled.clear();
        healsEnabled.clear();

        File dataFolder = DamageDisplay.me.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        migrateLegacyCache(dataFolder);

        File prefsFile = new File(dataFolder, PREFS_FILE);
        if (!prefsFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(prefsFile);
        ConfigurationSection players = config.getConfigurationSection("players");
        if (players == null) {
            return;
        }

        for (String key : players.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                ConfigurationSection section = players.getConfigurationSection(key);
                if (section == null) {
                    continue;
                }
                if (section.contains("actionbar")) {
                    actionBarEnabled.put(id, section.getBoolean("actionbar"));
                }
                if (section.contains("heals")) {
                    healsEnabled.put(id, section.getBoolean("heals"));
                }
            } catch (IllegalArgumentException ignored) {
                DamageDisplay.me.getLogger().log(Level.WARNING, "Invalid UUID in player-prefs.yml: " + key);
            }
        }
    }

    public void save() {
        File dataFolder = DamageDisplay.me.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        YamlConfiguration config = new YamlConfiguration();
        Set<UUID> ids = new HashSet<>();
        ids.addAll(actionBarEnabled.keySet());
        ids.addAll(healsEnabled.keySet());

        for (UUID id : ids) {
            String path = "players." + id;
            if (actionBarEnabled.containsKey(id)) {
                config.set(path + ".actionbar", actionBarEnabled.get(id));
            }
            if (healsEnabled.containsKey(id)) {
                config.set(path + ".heals", healsEnabled.get(id));
            }
        }

        try {
            config.save(new File(dataFolder, PREFS_FILE));
        } catch (IOException e) {
            DamageDisplay.me.getLogger().log(Level.WARNING, "Could not save player-prefs.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void migrateLegacyCache(File dataFolder) {
        File legacy = new File(dataFolder, LEGACY_CACHE);
        if (!legacy.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(legacy))) {
            List<UUID> disabled = (List<UUID>) ois.readObject();
            for (UUID id : disabled) {
                actionBarEnabled.put(id, false);
            }
            save();

            File backup = new File(dataFolder, LEGACY_CACHE + ".bak");
            if (!legacy.renameTo(backup)) {
                DamageDisplay.me.getLogger().log(Level.WARNING, "Migrated cache.dat but could not rename it to cache.dat.bak");
            } else {
                DamageDisplay.me.getLogger().info("Migrated cache.dat to player-prefs.yml");
            }
        } catch (IOException | ClassNotFoundException e) {
            DamageDisplay.me.getLogger().log(Level.WARNING, "Could not migrate cache.dat: " + e.getMessage());
        }
    }
}
