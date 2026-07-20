package com.danlegt.damagedisplay.Services;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.EnumMap;
import java.util.Map;

public class ActionBar {

    private static final Map<EntityDamageEvent.DamageCause, String> PRETTY_CAUSES = buildPrettyCauses();

    public static void sendToPlayer(Player player, String ampersandMessage) {
        String legacy = Messages.toLegacy(ampersandMessage);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(legacy));
    }

    public static String parseStringToPretty(EntityDamageEvent.DamageCause cause) {
        String pretty = PRETTY_CAUSES.get(cause);
        return pretty != null ? pretty : parseStringToPretty(cause.toString());
    }

    public static String parseStringToPretty(String input) {
        StringBuilder output = new StringBuilder();
        String[] words = input.split("_");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
            output.append(words[i]).append(" ");
        }

        return output.toString().trim();
    }

    private static Map<EntityDamageEvent.DamageCause, String> buildPrettyCauses() {
        Map<EntityDamageEvent.DamageCause, String> causes = new EnumMap<>(EntityDamageEvent.DamageCause.class);
        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            causes.put(cause, parseStringToPretty(cause.toString()));
        }
        return causes;
    }
}
