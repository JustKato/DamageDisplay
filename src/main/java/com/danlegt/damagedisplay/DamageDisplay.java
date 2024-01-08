package com.danlegt.damagedisplay;

import com.danlegt.damagedisplay.Event.EventManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class DamageDisplay extends JavaPlugin {

    public static DamageDisplay me;
    public static final int bStatsID = 20685;

    @Override
    public void onEnable() {
        // Singleton for Services
        me = this;

        // Register Listeners
        new EventManager(me).registerEvents();

        // bStats Metrics
        RegisterMetrics();

        // Save the default settings if they are not saved
        DamageDisplay.me.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void RegisterMetrics() {
        new Metrics(this, bStatsID);
    }

}
