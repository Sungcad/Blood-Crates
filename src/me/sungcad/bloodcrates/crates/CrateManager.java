package me.sungcad.bloodcrates.crates;

import static me.sungcad.bloodcrates.managers.Files.CONFIG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class CrateManager {
    BloodCratesPlugin plugin;
    Random rand = new Random();
    List<Crate> crates = new ArrayList<>();

    public CrateManager(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for (Crate c : crates) {
            locations.add(c.getLocation());
        }
        return locations;
    }

    public void despawnAllCrates() {
        while (crates.size() > 0) {
            crates.get(0).remove();
            crates.remove(0);
        }
    }

    public void despawnCrate(Location loc) {
        Crate crate = null;
        for (Crate c : crates) {
            if (c.equals(loc)) {
                crate = c;
            }
        }
        if (crate != null) {
            crates.remove(crate);
            crate.remove();
        }
    }

    public boolean isCrate(Location location) {
        for (Crate c : crates) {
            if (c.equals(location)) {
                return true;
            }
        }
        return false;
    }

    public Crate getCrate(Location loc) {
        for (Crate c : crates) {
            if (c.equals(loc)) {
                return c;
            }
        }
        return null;
    }

    public int canSpawn(Location location) {
        if (CONFIG.getConfig().getStringList("blacklist").contains(location.getWorld().getName()))
            return 2;
        if (stringContains(location.getBlock().getType().name(), "DOOR", "BED", "BANNER", "BREWING", "SIGN", "CHEST"))
            return 1;
        return 0;

    }

    public boolean spawnCrate(Location location) {
        if (canSpawn(location) == 0) {
            Crate crate = new Crate(plugin, location);
            crates.add(crate);
            long removetime = plugin.getConfig().getLong("settings.autoremovetime");
            if (removetime > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        crate.remove();
                        crates.remove(crate);
                    }
                }.runTaskLater(plugin, 20 * removetime);
            }
            return true;
        }
        return false;
    }

    private boolean stringContains(String input, String... strings) {
        input = input.toLowerCase();
        for (String s : strings) {
            if (input.contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}