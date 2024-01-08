package com.danlegt.damagedisplay.Event.EventListeners;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Services.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class OnDamageHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        // Check if the event has already been cancelled
        if ( e.isCancelled() ) return;

        // Check if the player has dealt the damage
        if ( e.getDamager().getType().equals(EntityType.PLAYER) ) {
            // Yoink a ref to the player
            Player p = (Player) e.getDamager();
            // Permission check
            if ( !p.hasPermission("damagedisplay.enabled") )
                return;

            // Send out a Damage indicator to the player
            ActionBar.sendToPlayer(p, "★ Damage Dealt: " + ChatColor.YELLOW + Math.round(e.getDamage() * 100.0) / 100.0);
        }

        // Check if this is a projectile send out by the player
        if ( e.getDamager() instanceof Projectile a) {
            // Check if the player has dealt the damage
            if ( a.getShooter() instanceof Player p ) {
                // Permission check
                if ( !p.hasPermission("damagedisplay.enabled") )
                    return;

                // Send out a Damage indicator to the player
                ActionBar.sendToPlayer(p, "★ Damage Dealt: " + ChatColor.YELLOW + Math.round(e.getDamage() * 100.0) / 100.0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnvironmentDamage(EntityDamageEvent e) {
        // Check if the event has already been cancelled
        if ( e.isCancelled() ) return;

        var receivedDamage = (Math.round(e.getDamage() * 100.0) / 100.0);

        // Check if the player has received the damage
        if ( e.getEntity().getType().equals(EntityType.PLAYER) ) {
            Player p = (Player) e.getEntity();
            // Permission check
            if ( !p.hasPermission("damagedisplay.enabled") )
                return;

            // Send out the Damage indicator to the player
            ActionBar.sendToPlayer(p, "☠ Damage Taken: " + ChatColor.RED + receivedDamage + ChatColor.GRAY + " | " + ActionBar.parseStringToPretty(e.getCause().toString()) );
        }

        var rand = new Random();

        Location loc = e.getEntity().getLocation().clone();
        loc.add(new Vector(.05 * (rand.nextBoolean() ? -1 : 0), 1.5f, .05 * (rand.nextBoolean() ? -1 : 0)));

        TextDisplay textDisplay = e.getEntity().getWorld().spawn(loc, TextDisplay.class);
        textDisplay.setText(ChatColor.RED.toString() + receivedDamage);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);

        new BukkitRunnable() {
            @Override
            public void run() {
                textDisplay.remove();
            }
        }.runTaskLater(DamageDisplay.me, 10);

    }

}
