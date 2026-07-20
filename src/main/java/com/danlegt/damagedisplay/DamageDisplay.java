package com.danlegt.damagedisplay;

import com.danlegt.damagedisplay.Commands.CommandManager;
import com.danlegt.damagedisplay.Event.EventManager;
import com.danlegt.damagedisplay.Services.DisplayTracker;
import com.danlegt.damagedisplay.Services.PlayerPrefs;
import com.danlegt.damagedisplay.Services.PluginSettings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;

public final class DamageDisplay extends JavaPlugin {

    public static DamageDisplay me;
    public static final int bStatsID = 20685;

    private final PluginSettings settings = new PluginSettings();
    private final DisplayTracker displayTracker = new DisplayTracker();
    private final PlayerPrefs playerPrefs = new PlayerPrefs();

    @Override
    public void onEnable() {
        me = this;

        new EventManager(me).registerEvents();

        this.saveDefaultConfig();
        this.settings.reload();
        this.playerPrefs.load();

        CommandManager commandManager = new CommandManager();
        this.getCommand("dd").setExecutor(commandManager);
        this.getCommand("dd").setTabCompleter(commandManager);

        registerMetrics();
    }

    @Override
    public void onDisable() {
        displayTracker.removeAll();
    }

    public PluginSettings getSettings() {
        return settings;
    }

    public DisplayTracker getDisplayTracker() {
        return displayTracker;
    }

    public PlayerPrefs getPlayerPrefs() {
        return playerPrefs;
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, bStatsID);
        metrics.addCustomChart(new SimplePie("displayText_enabled", () -> Boolean.toString(settings.isDisplayTextEnabled())));
        metrics.addCustomChart(new SimplePie("heals_enabled", () -> Boolean.toString(settings.isHealsEnabled())));
        metrics.addCustomChart(new SimplePie("viewerScoped", () -> Boolean.toString(settings.isViewerScoped())));
        metrics.addCustomChart(new SimplePie("motion_enabled", () -> Boolean.toString(settings.isMotionEnabled())));
        metrics.addCustomChart(new SimplePie("onlyFromPlayers", () -> Boolean.toString(settings.isOnlyFromPlayers())));
    }
}
