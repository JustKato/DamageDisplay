package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class HologramService {

    private HologramService() {
    }

    public static Collection<Player> getPermittedNearbyPlayers(Location location, double range) {
        Collection<Entity> nearby;
        if (range > 0) {
            nearby = location.getWorld().getNearbyEntities(location, range, range, range, entity -> entity instanceof Player);
        } else {
            nearby = new ArrayList<>(location.getWorld().getPlayers());
        }

        List<Player> players = new ArrayList<>();
        for (Entity entity : nearby) {
            Player player = (Player) entity;
            if (player.hasPermission("damagedisplay.enabled")) {
                players.add(player);
            }
        }
        return players;
    }

    public static TextDisplay spawn(Location at, String legacyText, Collection<Player> viewers, PluginSettings settings) {
        return spawn(at, legacyText, viewers, settings, false);
    }

    public static TextDisplay spawn(Location at, String legacyText, Collection<Player> viewers, PluginSettings settings, boolean forceViewerScoped) {
        DisplayTracker tracker = DamageDisplay.me.getDisplayTracker();
        if (!tracker.canSpawn(at.getChunk(), settings)) {
            return null;
        }
        if (viewers.isEmpty()) {
            return null;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        Location loc = at.clone().add(new Vector(
                0.05 * (rand.nextBoolean() ? -1 : 0),
                1.5,
                0.05 * (rand.nextBoolean() ? -1 : 0)
        ));

        TextDisplay textDisplay = loc.getWorld().spawn(loc, TextDisplay.class);
        textDisplay.setText(legacyText);
        tracker.applyStyle(textDisplay, settings);

        if (forceViewerScoped || settings.isViewerScoped()) {
            textDisplay.setVisibleByDefault(false);
            for (Player viewer : viewers) {
                viewer.showEntity(DamageDisplay.me, textDisplay);
            }
        }

        tracker.trackAndScheduleRemoval(textDisplay, settings);
        return textDisplay;
    }
}
