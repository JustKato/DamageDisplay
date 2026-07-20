package com.danlegt.damagedisplay.Services;

import com.danlegt.damagedisplay.DamageDisplay;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DisplayTracker {

    private final Map<TextDisplay, ChunkKey> activeDisplays = new HashMap<>();
    private final Map<ChunkKey, Integer> perChunkCounts = new HashMap<>();

    public boolean canSpawn(Chunk chunk, PluginSettings settings) {
        if (settings.getMaxTotal() > 0 && activeDisplays.size() >= settings.getMaxTotal()) {
            return false;
        }
        if (settings.getMaxPerChunk() > 0) {
            int count = perChunkCounts.getOrDefault(ChunkKey.of(chunk), 0);
            if (count >= settings.getMaxPerChunk()) {
                return false;
            }
        }
        return true;
    }

    public int getActiveCount() {
        return activeDisplays.size();
    }

    public void track(TextDisplay display) {
        ChunkKey key = ChunkKey.of(display.getLocation().getChunk());
        activeDisplays.put(display, key);
        perChunkCounts.merge(key, 1, Integer::sum);
    }

    public void applyStyle(TextDisplay display, PluginSettings settings) {
        display.setBillboard(settings.getBillboard());
        display.setShadowed(settings.isShadow());
        display.setSeeThrough(settings.isSeeThrough());
        display.setDefaultBackground(settings.isBackground());
        display.setTextOpacity(settings.getTextOpacity());

        float scale = settings.getScale();
        display.setTransformation(new Transformation(
                new Vector3f(),
                new AxisAngle4f(),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f()
        ));
    }

    public void trackAndScheduleRemoval(TextDisplay display, PluginSettings settings) {
        track(display);
        int lingerTime = settings.getLingerTime();

        if (settings.isMotionEnabled() && settings.getRiseBlocks() > 0 && lingerTime > 1) {
            Location start = display.getLocation().clone();
            double rise = settings.getRiseBlocks();

            new BukkitRunnable() {
                private int tick = 0;

                @Override
                public void run() {
                    if (!display.isValid()) {
                        cancel();
                        remove(display);
                        return;
                    }

                    tick++;
                    if (tick >= lingerTime) {
                        cancel();
                        remove(display);
                        return;
                    }

                    double progress = (double) tick / lingerTime;
                    Location next = start.clone().add(0, rise * progress, 0);
                    display.teleport(next);
                }
            }.runTaskTimer(DamageDisplay.me, 1L, 1L);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                remove(display);
            }
        }.runTaskLater(DamageDisplay.me, lingerTime);
    }

    public void remove(TextDisplay display) {
        ChunkKey key = activeDisplays.remove(display);
        if (key == null) {
            return;
        }

        perChunkCounts.computeIfPresent(key, (k, count) -> count <= 1 ? null : count - 1);

        if (display.isValid()) {
            display.remove();
        }
    }

    public void removeAll() {
        Iterator<Map.Entry<TextDisplay, ChunkKey>> iterator = activeDisplays.entrySet().iterator();
        while (iterator.hasNext()) {
            TextDisplay display = iterator.next().getKey();
            iterator.remove();
            if (display.isValid()) {
                display.remove();
            }
        }
        perChunkCounts.clear();
    }

    private static final class ChunkKey {
        private final UUID worldId;
        private final int x;
        private final int z;

        private ChunkKey(UUID worldId, int x, int z) {
            this.worldId = worldId;
            this.x = x;
            this.z = z;
        }

        static ChunkKey of(Chunk chunk) {
            return new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkKey chunkKey)) return false;
            return x == chunkKey.x && z == chunkKey.z && Objects.equals(worldId, chunkKey.worldId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(worldId, x, z);
        }
    }
}
