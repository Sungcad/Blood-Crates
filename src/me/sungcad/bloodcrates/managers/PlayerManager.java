package me.sungcad.bloodcrates.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.sungcad.bloodcrates.BloodCratesPlugin;
import static me.sungcad.bloodcrates.managers.Files.*;

public class PlayerManager {
    private Map<String, Long> cooldowns = new HashMap<>();
    private BloodCratesPlugin plugin;

    public PlayerManager(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean canSpawn(Player killer, Player killed) {
        if (!killed.equals(killer)) {
            if (!cooldowns.containsKey(killed.getName())) {
                if (CONFIG.getConfig().getBoolean("settings.ipdebug", false)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ip check debug info killer:");
                    sb.append(killer.getName());
                    sb.append(" killed:");
                    sb.append(killed.getName());
                    sb.append("\nkiller ip:");
                    sb.append(killer.getAddress().getAddress());
                    sb.append(" killed ip:");
                    sb.append(killed.getAddress().getAddress());
                    sb.append("\nkiller ip = killed ip:");
                    sb.append(killer.getAddress().getAddress().equals(killed.getAddress().getAddress()));
                    plugin.getLogger().info(sb.toString());
                }
                if (CONFIG.getConfig().getBoolean("settings.ipcheck", false)) {
                    if (killer.getAddress().getAddress().equals(killed.getAddress().getAddress())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean hasCooldown(String name) {
        return cooldowns.containsKey(name);
    }

    public void setCooldown(String name) {
        setCooldown(name, plugin.getConfig().getLong("settings.cooldown"));
    }

    public void setCooldown(String name, long time) {
        long systime = System.currentTimeMillis() + (time * 1000);
        cooldowns.put(name, systime);
        new BukkitRunnable() {
            @Override
            public void run() {
                removeCooldown(name, systime);
            }
        }.runTaskLater(plugin, time * 20);
    }

    public void removeCooldown(String name) {
        cooldowns.remove(name);
    }

    void removeCooldown(String name, long time) {
        cooldowns.remove(name, time);
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }

    public long getCooldown(String name) {
        return cooldowns.get(name);
    }
}