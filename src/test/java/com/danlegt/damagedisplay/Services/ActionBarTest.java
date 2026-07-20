package com.danlegt.damagedisplay.Services;

import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionBarTest {

    @Test
    void parseStringToPrettyFormatsUnderscoredEnumNames() {
        assertEquals("Entity Attack", ActionBar.parseStringToPretty("ENTITY_ATTACK"));
        assertEquals("Fall", ActionBar.parseStringToPretty("FALL"));
        assertEquals("Projectile", ActionBar.parseStringToPretty("PROJECTILE"));
    }

    @Test
    void parseDamageCauseUsesPrecomputedMap() {
        assertEquals("Entity Attack", ActionBar.parseStringToPretty(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
        assertEquals("Fall", ActionBar.parseStringToPretty(EntityDamageEvent.DamageCause.FALL));
        assertEquals("Fire Tick", ActionBar.parseStringToPretty(EntityDamageEvent.DamageCause.FIRE_TICK));
    }

    @Test
    void everyDamageCauseHasAPrettyLabel() {
        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            String pretty = ActionBar.parseStringToPretty(cause);
            assertFalse(pretty.isBlank(), "Expected pretty label for " + cause);
            assertTrue(Character.isUpperCase(pretty.charAt(0)), "Expected capitalized label for " + cause);
        }
    }
}
