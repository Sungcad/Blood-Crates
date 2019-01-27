package me.sungcad.bloodcrates.listeners;

import static me.sungcad.bloodcrates.managers.Files.COMMANDS;
import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static org.bukkit.Bukkit.dispatchCommand;
import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.Material.CHEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class OpenListener implements Listener {
    BloodCratesPlugin plugin;
    Random rand = new Random();

    public OpenListener(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrateOpen(PlayerInteractEvent e) {
        if (e.getAction() == RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType().equals(CHEST)) {
                Location loc = e.getClickedBlock().getLocation();
                if (plugin.getCrateManager().isCrate(loc)) {
                    e.setCancelled(true);
                    if (CONFIG.getConfig().getString("settings.reward.type").equalsIgnoreCase("command")) {
                        plugin.getCrateManager().despawnCrate(loc);
                        if (plugin.getConfig().getBoolean("settings.reward.multiple")) {
                            int min = plugin.getConfig().getInt("settings.reward.min"), max = plugin.getConfig().getInt("settings.reward.max"), count = 0;
                            if (max < min) {
                                int hold = max;
                                max = min;
                                min = hold;
                            }
                            do {
                                for (String section : COMMANDS.getConfig().getKeys(false)) {
                                    if (count < max && rand.nextInt(100) < COMMANDS.getConfig().getInt(section + ".chance")) {
                                        rewardPlayer(e.getPlayer(), COMMANDS.getConfig().getConfigurationSection(section));
                                        count++;
                                    }
                                }
                            } while (count < min);
                        } else {
                            int outof = 0, rewardnum = rand.nextInt(outof);
                            for (String section : COMMANDS.getConfig().getKeys(false))
                                outof += COMMANDS.getConfig().getInt(section + ".chance");
                            for (String section : COMMANDS.getConfig().getKeys(false)) {
                                if (rewardnum < COMMANDS.getConfig().getInt(section + ".chance")) {
                                    rewardPlayer(e.getPlayer(), COMMANDS.getConfig().getConfigurationSection(section));
                                    return;
                                }
                                rewardnum -= COMMANDS.getConfig().getInt(section + ".chance");
                            }
                        }
                    } else if (plugin.getConfig().getString("settings.reward.type").equalsIgnoreCase("item")) {
                        e.getPlayer().openInventory(plugin.getCrateManager().getCrate(loc).getInventory());
                    }
                }
            }
        }
    }

    void rewardPlayer(Player player, ConfigurationSection section) {
        for (String s : section.getStringList("message"))
            player.sendMessage(translateAlternateColorCodes('&', s));
        for (String s : section.getStringList("commands"))
            dispatchCommand(getConsoleSender(), s.replace("{player}", player.getName()));
    }
}
