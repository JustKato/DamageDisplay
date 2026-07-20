package com.danlegt.damagedisplay.Services;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
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
        config.set("actionbar.damageDealt.format", "&eHit {damage}");
        config.set("displayText.enabled", false);
        config.set("displayText.lingertime", 25);
        config.set("displayText.nearbyRange", 32.5);
        config.set("displayText.onlyFromPlayers", true);
        config.set("displayText.maxTotal", 12);
        config.set("displayText.maxPerChunk", 3);
        config.set("displayText.viewerScoped", false);
        config.set("displayText.format", "&c{dmg}");
        config.set("displayText.critFormat", "&6CRIT {damage}");
        config.set("displayText.billboard", "CENTER");
        config.set("displayText.shadow", false);
        config.set("displayText.seeThrough", true);
        config.set("displayText.background", false);
        config.set("displayText.textOpacity", 200);
        config.set("displayText.scale", 1.5);
        config.set("displayText.motion.enabled", false);
        config.set("displayText.motion.riseBlocks", 1.2);
        config.set("heals.enabled", true);
        config.set("heals.format", "&aHeal {amount}");

        PluginSettings settings = new PluginSettings();
        settings.reload(config, null);

        assertFalse(settings.isActionBarDamageDealtEnabled());
        assertFalse(settings.isActionBarDamageReceivedEnabled());
        assertEquals("&eHit {damage}", settings.getActionBarDamageDealtFormat());
        assertFalse(settings.isDisplayTextEnabled());
        assertEquals(25, settings.getLingerTime());
        assertEquals(32.5, settings.getNearbyRange());
        assertTrue(settings.isOnlyFromPlayers());
        assertEquals(12, settings.getMaxTotal());
        assertEquals(3, settings.getMaxPerChunk());
        assertFalse(settings.isViewerScoped());
        assertEquals("&c{dmg}", settings.getDisplayFormat());
        assertEquals("&6CRIT {damage}", settings.getCritFormat());
        assertEquals(Display.Billboard.CENTER, settings.getBillboard());
        assertFalse(settings.isShadow());
        assertTrue(settings.isSeeThrough());
        assertFalse(settings.isBackground());
        assertEquals((byte) 200, settings.getTextOpacity());
        assertEquals(1.5f, settings.getScale());
        assertFalse(settings.isMotionEnabled());
        assertEquals(1.2, settings.getRiseBlocks());
        assertTrue(settings.isHealsEnabled());
        assertEquals("&aHeal {amount}", settings.getHealFormat());
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
        assertEquals(Display.Billboard.VERTICAL, settings.getBillboard());
        assertTrue(settings.isShadow());
        assertFalse(settings.isSeeThrough());
        assertTrue(settings.isBackground());
        assertEquals((byte) 255, settings.getTextOpacity());
        assertEquals(1.0f, settings.getScale());
        assertTrue(settings.isMotionEnabled());
        assertEquals(0.6, settings.getRiseBlocks());
        assertFalse(settings.isHealsEnabled());
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

    @Test
    void reloadFallsBackOnInvalidBillboard() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("displayText.billboard", "NOPE");

        CapturingLogger logger = new CapturingLogger();
        PluginSettings settings = new PluginSettings();
        settings.reload(config, logger);

        assertEquals(Display.Billboard.VERTICAL, settings.getBillboard());
        assertFalse(logger.warnings.isEmpty());
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
