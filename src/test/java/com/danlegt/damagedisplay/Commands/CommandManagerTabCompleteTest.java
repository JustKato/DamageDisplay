package com.danlegt.damagedisplay.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandManagerTabCompleteTest {

    @Mock
    private CommandSender sender;

    @Mock
    private Command command;

    private final CommandManager manager = new CommandManager();

    @Test
    void firstArgCompletesCommandsAllowedByPermission() {
        when(sender.hasPermission("damagedisplay.command.toggle")).thenReturn(true);
        when(sender.hasPermission("damagedisplay.command.help")).thenReturn(false);
        when(sender.hasPermission("damagedisplay.command.info")).thenReturn(false);
        when(sender.hasPermission("damagedisplay.command.reload")).thenReturn(false);

        List<String> completions = manager.onTabComplete(sender, command, "dd", new String[]{""});

        assertEquals(List.of("toggle"), completions);
    }

    @Test
    void firstArgFiltersByPrefix() {
        lenient().when(sender.hasPermission("damagedisplay.command.toggle")).thenReturn(true);
        lenient().when(sender.hasPermission("damagedisplay.command.help")).thenReturn(true);
        lenient().when(sender.hasPermission("damagedisplay.command.info")).thenReturn(true);
        lenient().when(sender.hasPermission("damagedisplay.command.reload")).thenReturn(true);

        List<String> completions = manager.onTabComplete(sender, command, "dd", new String[]{"re"});

        assertEquals(List.of("reload"), completions);
    }

    @Test
    void toggleSecondArgCompletesActionbarAndHeals() {
        when(sender.hasPermission("damagedisplay.command.toggle")).thenReturn(true);

        List<String> completions = manager.onTabComplete(sender, command, "dd", new String[]{"toggle", ""});

        assertTrue(completions.contains("actionbar"));
        assertTrue(completions.contains("heals"));
        assertEquals(2, completions.size());
    }

    @Test
    void toggleSecondArgFiltersByPrefix() {
        when(sender.hasPermission("damagedisplay.command.toggle")).thenReturn(true);

        List<String> completions = manager.onTabComplete(sender, command, "dd", new String[]{"toggle", "he"});

        assertEquals(List.of("heals"), completions);
    }
}
