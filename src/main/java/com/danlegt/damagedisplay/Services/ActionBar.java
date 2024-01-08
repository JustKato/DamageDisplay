package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ActionBar {

    public static void sendToPlayer(Player p, String msg) {
        // Get the server
        Server server = DamageDisplay.me.getServer();
        // Use the lower API to send the message
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }

    public static String parseStringToPretty( String input ) {
        StringBuilder output = new StringBuilder();
        String[] words = input.split("_");

        for ( int i = 0; i < words.length; i++ ) {
            words[i] = words[i].toLowerCase();
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);

            output.append(words[i]).append(" ");
        }

        return output.toString();
    }

}
