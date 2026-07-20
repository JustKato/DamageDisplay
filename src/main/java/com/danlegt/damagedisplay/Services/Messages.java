package com.danlegt.damagedisplay.Services;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;

public final class Messages {

    private static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer SECTION = LegacyComponentSerializer.legacySection();

    private Messages() {
    }

    public static Component parse(String ampersandText) {
        if (ampersandText == null || ampersandText.isEmpty()) {
            return Component.empty();
        }
        return AMPERSAND.deserialize(ampersandText);
    }

    public static String toLegacy(Component component) {
        return SECTION.serialize(component);
    }

    public static String toLegacy(String ampersandText) {
        return toLegacy(parse(ampersandText));
    }

    public static String format(String template, Map<String, String> placeholders) {
        String result = template == null ? "" : template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public static String formatLegacy(String template, Map<String, String> placeholders) {
        return toLegacy(format(template, placeholders));
    }

    public static String roundDamage(double value) {
        return Double.toString(Math.round(value * 100.0) / 100.0);
    }
}
