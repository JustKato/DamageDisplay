package com.danlegt.damagedisplay.Services;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.TextDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisplayTrackerTest {

    private static final UUID WORLD_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private World world;

    private PluginSettings settings;
    private DisplayTracker tracker;

    @BeforeEach
    void setUp() {
        when(world.getUID()).thenReturn(WORLD_ID);

        settings = new PluginSettings();
        YamlConfiguration config = new YamlConfiguration();
        config.set("displayText.maxTotal", 2);
        config.set("displayText.maxPerChunk", 1);
        settings.reload(config, null);

        tracker = new DisplayTracker();
    }

    @Test
    void canSpawnAllowsFirstDisplayInChunk() {
        Chunk chunk = mockChunk(0, 0);
        assertTrue(tracker.canSpawn(chunk, settings));
    }

    @Test
    void canSpawnBlocksWhenPerChunkCapReached() {
        Chunk chunk = mockChunk(0, 0);
        TextDisplay display = mockDisplay(chunk);

        tracker.track(display);

        assertFalse(tracker.canSpawn(chunk, settings));
        assertTrue(tracker.canSpawn(mockChunk(1, 0), settings));
    }

    @Test
    void canSpawnBlocksWhenTotalCapReached() {
        TextDisplay first = mockDisplay(mockChunk(0, 0));
        TextDisplay second = mockDisplay(mockChunk(1, 0));

        tracker.track(first);
        tracker.track(second);

        assertEquals(2, tracker.getActiveCount());
        assertFalse(tracker.canSpawn(mockChunk(2, 0), settings));
    }

    @Test
    void removeFreesCapSlotsAndRemovesValidEntity() {
        Chunk chunk = mockChunk(0, 0);
        TextDisplay display = mockDisplay(chunk);
        when(display.isValid()).thenReturn(true);

        tracker.track(display);
        assertFalse(tracker.canSpawn(chunk, settings));

        tracker.remove(display);

        assertEquals(0, tracker.getActiveCount());
        assertTrue(tracker.canSpawn(chunk, settings));
        verify(display).remove();
    }

    @Test
    void removeDoesNotCallRemoveOnInvalidEntity() {
        TextDisplay display = mockDisplay(mockChunk(0, 0));
        when(display.isValid()).thenReturn(false);

        tracker.track(display);
        tracker.remove(display);

        verify(display, never()).remove();
        assertEquals(0, tracker.getActiveCount());
    }

    @Test
    void removeAllClearsEveryTrackedDisplay() {
        TextDisplay first = mockDisplay(mockChunk(0, 0));
        TextDisplay second = mockDisplay(mockChunk(1, 0));
        when(first.isValid()).thenReturn(true);
        when(second.isValid()).thenReturn(true);

        tracker.track(first);
        tracker.track(second);

        tracker.removeAll();

        assertEquals(0, tracker.getActiveCount());
        verify(first).remove();
        verify(second).remove();
        assertTrue(tracker.canSpawn(mockChunk(0, 0), settings));
    }

    @Test
    void zeroCapsDisableLimiting() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("displayText.maxTotal", 0);
        config.set("displayText.maxPerChunk", 0);
        settings.reload(config, null);

        Chunk chunk = mockChunk(0, 0);
        tracker.track(mockDisplay(chunk));
        tracker.track(mockDisplay(chunk));
        tracker.track(mockDisplay(chunk));

        assertTrue(tracker.canSpawn(chunk, settings));
    }

    private Chunk mockChunk(int x, int z) {
        Chunk chunk = mock(Chunk.class);
        lenient().when(chunk.getWorld()).thenReturn(world);
        lenient().when(chunk.getX()).thenReturn(x);
        lenient().when(chunk.getZ()).thenReturn(z);
        return chunk;
    }

    private TextDisplay mockDisplay(Chunk chunk) {
        TextDisplay display = mock(TextDisplay.class);
        Location location = mock(Location.class);
        lenient().when(display.getLocation()).thenReturn(location);
        lenient().when(location.getChunk()).thenReturn(chunk);
        return display;
    }
}
