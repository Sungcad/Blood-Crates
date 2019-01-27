package me.sungcad.bloodcrates.commands;

import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class DebugCommand implements CommandExecutor {
    private BloodCratesPlugin plugin;
    private Random rand = new Random();

    public DebugCommand(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String cmdname, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.error.notaplayer")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("&6/" + cmdname + " ( spawn | force | chance )&7 - Spawn a BloodCrate.");
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("force")) {
            if (plugin.getCrateManager().spawnCrate(player.getLocation())) {
                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.debug.spawned")));
            } else {
                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.debug.fail")));
            }
        } else if (args[0].equalsIgnoreCase("chance") || args[0].equalsIgnoreCase("spawn")) {
            if (rand.nextInt(100) < CONFIG.getConfig().getInt("settings.spawnchance")) {
                if (plugin.getCrateManager().spawnCrate(player.getLocation())) {
                    sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.debug.spawned")));
                }
                else {
                    sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.debug.fail")));
                }
            }
            else {
                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.debug.didnt")));
            }
        }
        return true;
    }

}
