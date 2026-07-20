package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginSettings {

    private boolean actionBarDamageDealtEnabled = true;
    private boolean actionBarDamageReceivedEnabled = true;
    private boolean displayTextEnabled = true;
    private int lingerTime = 10;
    private double nearbyRange = 16;
    private boolean onlyFromPlayers = false;
    private int maxTotal = 64;
    private int maxPerChunk = 8;
    private boolean viewerScoped = true;

    public void reload() {
        reload(DamageDisplay.me.getConfig(), DamageDisplay.me.getLogger());
    }

    public void reload(FileConfiguration config, Logger logger) {
        actionBarDamageDealtEnabled = config.getBoolean("actionbar.damageDealt.enabled", true);
        actionBarDamageReceivedEnabled = config.getBoolean("actionbar.damageReceived.enabled", true);
        displayTextEnabled = config.getBoolean("displayText.enabled", true);
        lingerTime = config.getInt("displayText.lingertime", 10);
        nearbyRange = config.getDouble("displayText.nearbyRange", 16);
        onlyFromPlayers = config.getBoolean("displayText.onlyFromPlayers", false);
        maxTotal = Math.max(0, config.getInt("displayText.maxTotal", 64));
        maxPerChunk = Math.max(0, config.getInt("displayText.maxPerChunk", 8));
        viewerScoped = config.getBoolean("displayText.viewerScoped", true);

        if (lingerTime <= 0) {
            if (logger != null) {
                logger.log(Level.WARNING,
                        "The configured linger time has been set to be equal or lower than 0, please add at least 1 tick to the linger time. If your intention was to disable the displayText please use displayText.enabled = false");
            }
            lingerTime = 1;
        }
    }

    public boolean isActionBarDamageDealtEnabled() {
        return actionBarDamageDealtEnabled;
    }

    public boolean isActionBarDamageReceivedEnabled() {
        return actionBarDamageReceivedEnabled;
    }

    public boolean isDisplayTextEnabled() {
        return displayTextEnabled;
    }

    public int getLingerTime() {
        return lingerTime;
    }

    public double getNearbyRange() {
        return nearbyRange;
    }

    public boolean isOnlyFromPlayers() {
        return onlyFromPlayers;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxPerChunk() {
        return maxPerChunk;
    }

    public boolean isViewerScoped() {
        return viewerScoped;
    }
}
