package com.danlegt.damagedisplay.Event.EventListeners;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.HologramService;
import com.danlegt.damagedisplay.Services.Messages;
import com.danlegt.damagedisplay.Services.PlayerPrefs;
import com.danlegt.damagedisplay.Services.PluginSettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class OnHealHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeal(EntityRegainHealthEvent e) {
        PluginSettings settings = DamageDisplay.me.getSettings();
        if (!settings.isHealsEnabled()) {
            return;
        }

        double amount = e.getAmount();
        if (amount <= 0) {
            return;
        }

        Location loc = e.getEntity().getLocation();
        Collection<Player> nearby = HologramService.getPermittedNearbyPlayers(loc, settings.getNearbyRange());
        PlayerPrefs prefs = DamageDisplay.me.getPlayerPrefs();

        Collection<Player> viewers = new ArrayList<>();
        for (Player player : nearby) {
            if (prefs.isHealsEnabled(player.getUniqueId())) {
                viewers.add(player);
            }
        }
        if (viewers.isEmpty()) {
            return;
        }

        String legacy = Messages.formatLegacy(settings.getHealFormat(), Map.of(
                "amount", Messages.roundDamage(amount)
        ));
        HologramService.spawn(loc, legacy, viewers, settings, true);
    }
}
