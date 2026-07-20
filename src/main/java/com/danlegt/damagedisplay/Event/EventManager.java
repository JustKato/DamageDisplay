package com.danlegt.damagedisplay.Event;

import com.danlegt.damagedisplay.DamageDisplay;
import com.danlegt.damagedisplay.Event.EventListeners.OnDamageHandler;
import com.danlegt.damagedisplay.Event.EventListeners.OnHealHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class EventManager {
    private final DamageDisplay plugin;
    private final List<Listener> enabledListeners = List.of(
        new OnDamageHandler(),
        new OnHealHandler()
    );

    public EventManager(DamageDisplay plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        for (var ev : enabledListeners) {
            plugin.getServer().getPluginManager().registerEvents(ev, plugin);
        }
    }
}
