package com.danlegt.damagedisplay.Event.EventListeners;

import com.danlegt.damagedisplay.Commands.ToggleCommand;
import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.ActionBar;
import com.danlegt.damagedisplay.Services.DisplayTracker;
import com.danlegt.damagedisplay.Services.PluginSettings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class OnDamageHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;

        PluginSettings settings = DamageDisplay.me.getSettings();
        if (!settings.isActionBarDamageDealtEnabled()) return;

        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getDamager();

            if (ToggleCommand.disabledPlayers.contains(p.getUniqueId())) return;

            if (!p.hasPermission("damagedisplay.enabled"))
                return;

            ActionBar.sendToPlayer(p, "★ Damage Dealt: " + ChatColor.YELLOW + Math.round(e.getDamage() * 100.0) / 100.0);
        }

        if (e.getDamager() instanceof Projectile a) {
            if (a.getShooter() instanceof Player p) {
                if (!p.hasPermission("damagedisplay.enabled"))
                    return;

                if (ToggleCommand.disabledPlayers.contains(p.getUniqueId())) return;

                ActionBar.sendToPlayer(p, "★ Damage Dealt: " + ChatColor.YELLOW + Math.round(e.getDamage() * 100.0) / 100.0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnvironmentDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;

        PluginSettings settings = DamageDisplay.me.getSettings();
        var receivedDamage = (Math.round(e.getDamage() * 100.0) / 100.0);

        if (e.getEntity().getType() == EntityType.PLAYER && settings.isActionBarDamageReceivedEnabled()) {
            Player p = (Player) e.getEntity();
            if (!p.hasPermission("damagedisplay.enabled"))
                return;

            if (ToggleCommand.disabledPlayers.contains(p.getUniqueId())) return;

            ActionBar.sendToPlayer(p, "☠ Damage Taken: " + ChatColor.RED + receivedDamage + ChatColor.GRAY + " | " + ActionBar.parseStringToPretty(e.getCause()));
        }

        if (!settings.isDisplayTextEnabled()) {
            return;
        }

        Location entityLoc = e.getEntity().getLocation();
        DisplayTracker tracker = DamageDisplay.me.getDisplayTracker();
        if (!tracker.canSpawn(entityLoc.getChunk(), settings)) {
            return;
        }

        if (settings.isOnlyFromPlayers() && !isPlayerCaused(e)) {
            return;
        }

        double nearbyRange = settings.getNearbyRange();
        Collection<Player> nearbyPlayers = Collections.emptyList();
        if (nearbyRange > 0) {
            nearbyPlayers = getNearbyPlayers(entityLoc, nearbyRange);
            if (nearbyPlayers.isEmpty()) {
                return;
            }
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        String damagePrefix = ChatColor.RED.toString();

        if (e instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (damageByEntityEvent.getDamager() instanceof Player damager) {
                if (damager.getFallDistance() > 0.0f && !damager.isOnGround() && !damager.isInWater() && !damager.isInsideVehicle() && !damager.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    damagePrefix = ChatColor.GOLD + "✴";
                }
            }
        }

        Location loc = entityLoc.clone();
        loc.add(new Vector(.05 * (rand.nextBoolean() ? -1 : 0), 1.5f, .05 * (rand.nextBoolean() ? -1 : 0)));

        TextDisplay textDisplay = e.getEntity().getWorld().spawn(loc, TextDisplay.class);
        textDisplay.setText(damagePrefix + receivedDamage);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);

        if (settings.isViewerScoped()) {
            textDisplay.setVisibleByDefault(false);
            Collection<Player> viewers = nearbyRange > 0
                    ? nearbyPlayers
                    : getNearbyPlayers(loc, 16);
            for (Player viewer : viewers) {
                viewer.showEntity(DamageDisplay.me, textDisplay);
            }
        }

        tracker.trackAndScheduleRemoval(textDisplay, settings.getLingerTime());
    }

    private static Collection<Player> getNearbyPlayers(Location location, double range) {
        Collection<Entity> nearby = location.getWorld().getNearbyEntities(location, range, range, range, entity -> entity instanceof Player);
        Collection<Player> players = new ArrayList<>(nearby.size());
        for (Entity entity : nearby) {
            players.add((Player) entity);
        }
        return players;
    }

    private static boolean isPlayerCaused(EntityDamageEvent e) {
        if (!(e instanceof EntityDamageByEntityEvent damageByEntityEvent)) {
            return false;
        }

        Entity damager = damageByEntityEvent.getDamager();
        if (damager instanceof Player) {
            return true;
        }
        return damager instanceof Projectile projectile && projectile.getShooter() instanceof Player;
    }
}
