package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;

public class ConfigService {

    public static void SaveDefault() {
        DamageDisplay.me.saveDefaultConfig();
    }

    public void reloadPluginConfig() {
        DamageDisplay.me.reloadConfig();
    }

    public void savePluginConfig() {
        DamageDisplay.me.saveConfig();
    }

}
