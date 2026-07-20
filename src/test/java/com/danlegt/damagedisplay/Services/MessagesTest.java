package com.danlegt.damagedisplay.Services;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @Test
    void formatReplacesPlaceholders() {
        String result = Messages.format("&c{damage} &7| &7{cause}", Map.of(
                "damage", "3.5",
                "cause", "Fall"
        ));
        assertEquals("&c3.5 &7| &7Fall", result);
    }

    @Test
    void formatLegacyConvertsAmpersandCodes() {
        String legacy = Messages.formatLegacy("&cHello", Map.of());
        assertTrue(legacy.contains("Hello"));
        assertFalse(legacy.contains("&c"));
        assertTrue(legacy.indexOf('§') >= 0);
    }

    @Test
    void roundDamageUsesTwoDecimals() {
        assertEquals("3.14", Messages.roundDamage(3.14159));
        assertEquals("1.0", Messages.roundDamage(1.001));
    }
}
