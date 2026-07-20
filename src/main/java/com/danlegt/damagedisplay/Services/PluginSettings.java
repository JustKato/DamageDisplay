package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Display;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginSettings {

    private boolean actionBarDamageDealtEnabled = true;
    private boolean actionBarDamageReceivedEnabled = true;
    private String actionBarDamageDealtFormat = "&e★ Damage Dealt: &e{damage}";
    private String actionBarDamageReceivedFormat = "&c☠ Damage Taken: &c{damage}&7 | &7{cause}";

    private boolean displayTextEnabled = true;
    private int lingerTime = 10;
    private double nearbyRange = 16;
    private boolean onlyFromPlayers = false;
    private int maxTotal = 64;
    private int maxPerChunk = 8;
    private boolean viewerScoped = true;
    private String displayFormat = "&c{damage}";
    private String critFormat = "&6✴{damage}";

    private Display.Billboard billboard = Display.Billboard.VERTICAL;
    private boolean shadow = true;
    private boolean seeThrough = false;
    private boolean background = true;
    private byte textOpacity = (byte) 255;
    private float scale = 1.0f;

    private boolean motionEnabled = true;
    private double riseBlocks = 0.6;

    private boolean healsEnabled = false;
    private String healFormat = "&a+{amount}";

    public void reload() {
        reload(DamageDisplay.me.getConfig(), DamageDisplay.me.getLogger());
    }

    public void reload(FileConfiguration config, Logger logger) {
        actionBarDamageDealtEnabled = config.getBoolean("actionbar.damageDealt.enabled", true);
        actionBarDamageReceivedEnabled = config.getBoolean("actionbar.damageReceived.enabled", true);
        actionBarDamageDealtFormat = config.getString("actionbar.damageDealt.format", actionBarDamageDealtFormat);
        actionBarDamageReceivedFormat = config.getString("actionbar.damageReceived.format", actionBarDamageReceivedFormat);

        displayTextEnabled = config.getBoolean("displayText.enabled", true);
        lingerTime = config.getInt("displayText.lingertime", 10);
        nearbyRange = config.getDouble("displayText.nearbyRange", 16);
        onlyFromPlayers = config.getBoolean("displayText.onlyFromPlayers", false);
        maxTotal = Math.max(0, config.getInt("displayText.maxTotal", 64));
        maxPerChunk = Math.max(0, config.getInt("displayText.maxPerChunk", 8));
        viewerScoped = config.getBoolean("displayText.viewerScoped", true);
        displayFormat = config.getString("displayText.format", displayFormat);
        critFormat = config.getString("displayText.critFormat", critFormat);

        billboard = parseBillboard(config.getString("displayText.billboard", "VERTICAL"), logger);
        shadow = config.getBoolean("displayText.shadow", true);
        seeThrough = config.getBoolean("displayText.seeThrough", false);
        background = config.getBoolean("displayText.background", true);
        textOpacity = (byte) Math.max(0, Math.min(255, config.getInt("displayText.textOpacity", 255)));
        scale = (float) Math.max(0.01, config.getDouble("displayText.scale", 1.0));

        motionEnabled = config.getBoolean("displayText.motion.enabled", true);
        riseBlocks = Math.max(0.0, config.getDouble("displayText.motion.riseBlocks", 0.6));

        healsEnabled = config.getBoolean("heals.enabled", false);
        healFormat = config.getString("heals.format", healFormat);

        if (lingerTime <= 0) {
            if (logger != null) {
                logger.log(Level.WARNING,
                        "The configured linger time has been set to be equal or lower than 0, please add at least 1 tick to the linger time. If your intention was to disable the displayText please use displayText.enabled = false");
            }
            lingerTime = 1;
        }
    }

    private static Display.Billboard parseBillboard(String raw, Logger logger) {
        try {
            return Display.Billboard.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            if (logger != null) {
                logger.log(Level.WARNING, "Invalid displayText.billboard '" + raw + "', using VERTICAL");
            }
            return Display.Billboard.VERTICAL;
        }
    }

    public boolean isActionBarDamageDealtEnabled() {
        return actionBarDamageDealtEnabled;
    }

    public boolean isActionBarDamageReceivedEnabled() {
        return actionBarDamageReceivedEnabled;
    }

    public String getActionBarDamageDealtFormat() {
        return actionBarDamageDealtFormat;
    }

    public String getActionBarDamageReceivedFormat() {
        return actionBarDamageReceivedFormat;
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

    public String getDisplayFormat() {
        return displayFormat;
    }

    public String getCritFormat() {
        return critFormat;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public boolean isShadow() {
        return shadow;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public boolean isBackground() {
        return background;
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public float getScale() {
        return scale;
    }

    public boolean isMotionEnabled() {
        return motionEnabled;
    }

    public double getRiseBlocks() {
        return riseBlocks;
    }

    public boolean isHealsEnabled() {
        return healsEnabled;
    }

    public String getHealFormat() {
        return healFormat;
    }
}
