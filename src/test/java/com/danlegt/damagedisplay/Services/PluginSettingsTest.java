package com.danlegt.damagedisplay.Services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginSettingsTest {

    @Test
    void reloadAppliesConfiguredValues() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("actionbar.damageDealt.enabled", false);
        config.set("actionbar.damageReceived.enabled", false);
        config.set("displayText.enabled", false);
        config.set("displayText.lingertime", 25);
        config.set("displayText.nearbyRange", 32.5);
        config.set("displayText.onlyFromPlayers", true);
        config.set("displayText.maxTotal", 12);
        config.set("displayText.maxPerChunk", 3);
        config.set("displayText.viewerScoped", false);

        PluginSettings settings = new PluginSettings();
        settings.reload(config, null);

        assertFalse(settings.isActionBarDamageDealtEnabled());
        assertFalse(settings.isActionBarDamageReceivedEnabled());
        assertFalse(settings.isDisplayTextEnabled());
        assertEquals(25, settings.getLingerTime());
        assertEquals(32.5, settings.getNearbyRange());
        assertTrue(settings.isOnlyFromPlayers());
        assertEquals(12, settings.getMaxTotal());
        assertEquals(3, settings.getMaxPerChunk());
        assertFalse(settings.isViewerScoped());
    }

    @Test
    void reloadUsesDefaultsWhenKeysAreMissing() {
        PluginSettings settings = new PluginSettings();
        settings.reload(new YamlConfiguration(), null);

        assertTrue(settings.isActionBarDamageDealtEnabled());
        assertTrue(settings.isActionBarDamageReceivedEnabled());
        assertTrue(settings.isDisplayTextEnabled());
        assertEquals(10, settings.getLingerTime());
        assertEquals(16.0, settings.getNearbyRange());
        assertFalse(settings.isOnlyFromPlayers());
        assertEquals(64, settings.getMaxTotal());
        assertEquals(8, settings.getMaxPerChunk());
        assertTrue(settings.isViewerScoped());
    }

    @Test
    void reloadClampsInvalidLingerTimeAndWarnsOnce() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("displayText.lingertime", 0);

        CapturingLogger logger = new CapturingLogger();
        PluginSettings settings = new PluginSettings();
        settings.reload(config, logger);

        assertEquals(1, settings.getLingerTime());
        assertEquals(1, logger.warnings.size());
        assertTrue(logger.warnings.getFirst().contains("linger time"));
    }

    @Test
    void reloadClampsNegativeCapsToZero() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("displayText.maxTotal", -5);
        config.set("displayText.maxPerChunk", -2);

        PluginSettings settings = new PluginSettings();
        settings.reload(config, null);

        assertEquals(0, settings.getMaxTotal());
        assertEquals(0, settings.getMaxPerChunk());
    }

    private static final class CapturingLogger extends Logger {
        private final List<String> warnings = new ArrayList<>();

        private CapturingLogger() {
            super("plugin-settings-test", null);
            setUseParentHandlers(false);
            addHandler(new Handler() {
                @Override
                public void publish(LogRecord record) {
                    if (record.getLevel() == Level.WARNING) {
                        warnings.add(record.getMessage());
                    }
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() {
                }
            });
        }
    }
}
