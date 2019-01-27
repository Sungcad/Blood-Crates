package me.sungcad.bloodcrates.listeners;

import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.sungcad.bloodcrates.BloodCratesPlugin;
import me.sungcad.bloodcrates.events.BloodCrateSpawnEvent;

public class KillListener implements Listener {
    public BloodCratesPlugin plugin;
    Random rand = new Random();

    public KillListener(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            if (e.getEntity().getKiller() instanceof Player) {
                Player killed = e.getEntity(), killer = killed.getKiller();
                if (plugin.getPlayerManager().canSpawn(killer, killed)) {
                    if (plugin.getCrateManager().canSpawn(killed.getLocation()) == 0) {
                        if (rand.nextInt(100) < CONFIG.getConfig().getInt("settings.spawnchance")) {
                            BloodCrateSpawnEvent event = new BloodCrateSpawnEvent(killer, killed);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                plugin.getCrateManager().spawnCrate(event.getLocation());
                                CONFIG.getConfig().getStringList("messages.killer").forEach(l -> killer.sendMessage(translateAlternateColorCodes('&', l)));
                                plugin.getPlayerManager().setCooldown(killed.getName());
                            }
                        }
                    }
                }
            }
        }
    }
}