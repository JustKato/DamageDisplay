package com.danlegt.damagedisplay;

import com.danlegt.damagedisplay.Event.EventManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DamageDisplay extends JavaPlugin {

    public static DamageDisplay me;

    @Override
    public void onEnable() {
        // Singleton for Services
        me = this;

        // Register Listeners
        new EventManager(me).registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
