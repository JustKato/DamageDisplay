package com.danlegt.damagedisplay.Event.EventListeners;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.ActionBar;
import com.danlegt.damagedisplay.Services.HologramService;
import com.danlegt.damagedisplay.Services.Messages;
import com.danlegt.damagedisplay.Services.PlayerPrefs;
import com.danlegt.damagedisplay.Services.PluginSettings;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Map;

public class OnDamageHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        PluginSettings settings = DamageDisplay.me.getSettings();
        PlayerPrefs prefs = DamageDisplay.me.getPlayerPrefs();
        if (!settings.isActionBarDamageDealtEnabled()) {
            return;
        }

        double damage = e.getFinalDamage();
        String formatted = Messages.format(settings.getActionBarDamageDealtFormat(), Map.of(
                "damage", Messages.roundDamage(damage)
        ));

        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getDamager();
            if (!prefs.isActionBarEnabled(p.getUniqueId())) {
                return;
            }
            if (!p.hasPermission("damagedisplay.enabled")) {
                return;
            }
            ActionBar.sendToPlayer(p, formatted);
        }

        if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player p) {
            if (!p.hasPermission("damagedisplay.enabled")) {
                return;
            }
            if (!prefs.isActionBarEnabled(p.getUniqueId())) {
                return;
            }
            ActionBar.sendToPlayer(p, formatted);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnvironmentDamage(EntityDamageEvent e) {
        PluginSettings settings = DamageDisplay.me.getSettings();
        PlayerPrefs prefs = DamageDisplay.me.getPlayerPrefs();
        double damage = e.getFinalDamage();
        String damageText = Messages.roundDamage(damage);

        if (e.getEntity().getType() == EntityType.PLAYER && settings.isActionBarDamageReceivedEnabled()) {
            Player p = (Player) e.getEntity();
            if (p.hasPermission("damagedisplay.enabled") && prefs.isActionBarEnabled(p.getUniqueId())) {
                String formatted = Messages.format(settings.getActionBarDamageReceivedFormat(), Map.of(
                        "damage", damageText,
                        "cause", ActionBar.parseStringToPretty(e.getCause())
                ));
                ActionBar.sendToPlayer(p, formatted);
            }
        }

        if (!settings.isDisplayTextEnabled()) {
            return;
        }

        if (settings.isOnlyFromPlayers() && !isPlayerCaused(e)) {
            return;
        }

        Location entityLoc = e.getEntity().getLocation();
        Collection<Player> viewers = HologramService.getPermittedNearbyPlayers(entityLoc, settings.getNearbyRange());
        if (viewers.isEmpty()) {
            return;
        }

        boolean crit = false;
        if (e instanceof EntityDamageByEntityEvent damageByEntityEvent
                && damageByEntityEvent.getDamager() instanceof Player damager) {
            crit = damager.getFallDistance() > 0.0f
                    && !damager.isOnGround()
                    && !damager.isInWater()
                    && !damager.isInsideVehicle()
                    && !damager.hasPotionEffect(PotionEffectType.BLINDNESS);
        }

        String template = crit ? settings.getCritFormat() : settings.getDisplayFormat();
        String legacy = Messages.formatLegacy(template, Map.of("damage", damageText));
        HologramService.spawn(entityLoc, legacy, viewers, settings);
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
