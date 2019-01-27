package me.sungcad.bloodcrates.commands;

import static me.sungcad.bloodcrates.managers.Files.COMMANDS;
import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class BloodCratesCommand implements CommandExecutor {
    BloodCratesPlugin plugin;

    public BloodCratesCommand(BloodCratesPlugin plugin) {
        this.plugin = plugin;
    }

    String getConfigString(String path, int x, int y, int z) {
        return getConfigString(path, String.valueOf(x), String.valueOf(y), String.valueOf(z));
    }

    String getConfigString(String path, String... args) {
        String value = CONFIG.getConfig().getString(path);
        if (args.length == 1) {
            value = value.replace("{count}", args[0]);
        } else if (args.length == 2) {
            value = value.replace("{player}", args[0]).replace("{time}", args[1]);
        } else if (args.length == 3) {
            value = value.replace("{x}", args[0]).replace("{y}", args[1]).replace("{z}", args[2]);
        }
        return translateAlternateColorCodes('&', value);
    }

    boolean isSameString(String input, String... strings) {
        for (String s : strings) {
            if (input.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    String ms2s(long ms) {
        return (ms / 3600000 > 0 ? String.format("%dh ", (ms / 3600000)) : "") + ((ms / 60000) % 60 > 0 ? String.format("%02dm ", ((ms / 60000) % 60)) : "")
                + ((ms / 60000) % 60 > 0 ? String.format("%02ds", ((ms / 60000) % 60)) : (ms < 1000 ? "00s" : ""));
    }

    void noPermMessage(CommandSender sender) {
        sender.sendMessage(getConfigString("messages.error.nopermission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdname, String[] args) {
        if (args.length == 0) {
            runHelp(sender, args);
            return true;
        }
        switch (args[0].toLowerCase()) {
        case "cooldown":
        case "cooldowns":
            runCooldown(sender, args);
            break;
        case "locations":
        case "location":
        case "loc":
            runLocation(sender, args);
            break;
        case "check":
            runCheck(sender, args);
            break;
        case "reload":
            runReload(sender, args);
            break;
        case "config":
            runConfig(sender, args);
            break;
        case "version":
        case "ver":
            runVersion(sender, args);
            break;
        case "item":
        case "items":
            runItem(sender, args);
            break;
        default:
            runHelp(sender, args);
        }
        return true;
    }

    private void runCheck(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.check")) {
            Player player = null;
            if (args.length == 0) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage(getConfigString("messages.error.notaplayer"));
                }
            } else {
                player = Bukkit.getPlayer(args[0]);
            }
            if (player != null) {
                int i = plugin.getCrateManager().canSpawn(player.getLocation());
                if (i == 0) {
                    sender.sendMessage(getConfigString("messages.check.can"));
                } else if (i == 1) {
                    sender.sendMessage(getConfigString("messages.check.location"));
                } else {
                    sender.sendMessage(getConfigString("messages.check.world"));
                }
            } else {
                sender.sendMessage(getConfigString("messages.error.playernotfound"));
            }
        } else {
            noPermMessage(sender);
        }
    }

    private void runConfig(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.config")) {
            if (args.length == 2) {
                if (CONFIG.getConfig().isSet(args[1])) {
                    if (CONFIG.getConfig().isConfigurationSection(args[1])) {
                        ConfigurationSection section = CONFIG.getConfig().getConfigurationSection(args[1]);
                        for (String p : section.getKeys(true)) {
                            if (!section.isConfigurationSection(p)) {
                                if (section.isList(p)) {
                                    sender.sendMessage(args[1] + "." + p + ":");
                                    for (String s : section.getStringList(p))
                                        sender.sendMessage(s);
                                } else {
                                    sender.sendMessage(args[1] + "." + p + ": " + section.get(p));
                                }
                            }
                        }
                    } else if (CONFIG.getConfig().isList(args[1])) {
                        sender.sendMessage((String[]) CONFIG.getConfig().getStringList(args[1]).toArray());
                    } else {
                        sender.sendMessage(args[1] + ": " + CONFIG.getConfig().getString(args[1]));
                    }
                } else {
                    sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.error.pathnotfound").replace("{path}", args[0])));
                }
            } else if (args.length > 2) {
                StringBuilder arg = new StringBuilder();
                for (int i = 0; ++i < args.length; arg.append(args[i]).append('.'))
                    ;
                String[] newargs = { "config", arg.substring(0, arg.length() - 1) };
                runConfig(sender, newargs);
            } else {
                sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates config (path)&7 - Check a value from the config."));
            }
        } else {
            noPermMessage(sender);
        }
    }

    private void runCooldown(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.cooldowns")) {
            if (args.length == 1) {
                Map<String, Long> cooldowns = plugin.getPlayerManager().getCooldowns();
                sender.sendMessage(translateAlternateColorCodes('&', getConfigString("messages.cooldowns.list.top", String.valueOf(cooldowns.size()))));
                for (Entry<String, Long> e : cooldowns.entrySet()) {
                    sender.sendMessage(getConfigString("messages.cooldowns.list.player", e.getKey(), ms2s(e.getValue())));
                }
            } else {
                if (args[1].equalsIgnoreCase("set")) {
                    if (sender.hasPermission("bloodcrates.cooldowns.set")) {
                        if (args.length == 4) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player != null) {
                                long time;
                                try {
                                    time = Long.parseLong(args[3]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + " set (name) (time)&7 - Set a players cooldown."));
                                    return;
                                }
                                if (time > 0) {
                                    plugin.getPlayerManager().setCooldown(args[2], time);
                                    sender.sendMessage(getConfigString("messages.cooldowns.set", args[2], ms2s(plugin.getPlayerManager().getCooldown(args[2]))));
                                } else {
                                    plugin.getPlayerManager().removeCooldown(args[2]);
                                    sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.cooldowns.remove").replace("{player}", args[2])));
                                }
                            } else
                                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.error.playernotfound").replace("{player}", args[2])));
                        } else
                            sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + " set (name) (time)&7 - Set a players cooldown."));
                    } else
                        noPermMessage(sender);
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (sender.hasPermission("bloodcrates.cooldowns.remove")) {
                        if (args.length == 3) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player != null) {
                                plugin.getPlayerManager().removeCooldown(args[2]);
                                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.cooldowns.remove").replace("{player}", args[2])));
                            } else {
                                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.error.playernotfound").replace("{player}", args[2])));
                            }
                        } else {
                            sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + " remove (name)&7 - Remove a players cooldown."));
                        }
                    } else {
                        noPermMessage(sender);
                    }
                } else if (args[1].equalsIgnoreCase("check")) {
                    if (sender.hasPermission("bloodcrates.cooldowns.check")) {
                        if (args.length == 3) {
                            Player player = Bukkit.getPlayer(args[2]);
                            if (player != null) {
                                if (plugin.getPlayerManager().hasCooldown(args[2])) {
                                    sender.sendMessage(translateAlternateColorCodes('&', getConfigString("messages.cooldowns.check.yes", args[2], ms2s(plugin.getPlayerManager().getCooldown(args[2])))));
                                } else {
                                    sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.cooldowns.check.no").replace("{player}", args[2])));
                                }
                            } else {
                                sender.sendMessage(translateAlternateColorCodes('&', CONFIG.getConfig().getString("messages.error.playernotfound").replace("{player}", args[2])));
                            }
                        } else {
                            sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + " check (name)&7 - Check if a player has a cooldown."));
                        }
                    } else {
                        noPermMessage(sender);
                    }
                }
            }
        } else {
            noPermMessage(sender);
        }
    }

    private void runHelp(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.help")) {
            if (args.length >= 2) {
                // targeted help
                sender.sendMessage(translateAlternateColorCodes('&', "&6&lBlood Crates"));
                if (args[1].equalsIgnoreCase("reload") && sender.hasPermission("bloodcrates.reload")) {
                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates reload&7 - Reload the config"));
                } else if (isSameString(args[1], "cooldown", "cooldowns") && sender.hasPermission("bloodcrates.cooldowns")) {
                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + "&7 - Show all players with cooldowns"));
                    sendPermMessage(sender, "bloodcrates.cooldowns.check", "&6/bloodcrates " + args[1] + " check (name)&7 - Check a players cooldown.");
                    sendPermMessage(sender, "bloodcrates.cooldowns.remove", "&6/bloodcrates " + args[1] + " remove (name)&7 - Remove a players cooldown.");
                    sendPermMessage(sender, "bloodcrates.cooldowns.set", "&6/bloodcrates " + args[1] + " set (name) (time)&7 - Set a players cooldown.");
                } else if (isSameString(args[1], "location", "locations", "loc") && sender.hasPermission("bloodcrates.locations")) {
                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + "&7 - Show the location of all active crates"));
                } else if (args[1].equalsIgnoreCase("config") && sender.hasPermission("bloodcrates.config")) {
                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates " + args[1] + " (path)&7 - Check a value from the config."));
                } else if (args[1].equalsIgnoreCase("items") && sender.hasPermission("bloodcrates.items")) {
                    sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates items&7 - Open item rewards editor."));
                    sender.sendMessage(translateAlternateColorCodes('&', "&7Add items to the menu to have them as rewards in bloodcrates if the reward type is item"));
                } else {
                    sender.sendMessage(translateAlternateColorCodes('&', "&cHelp section not found"));
                }
            } else {
                // general help
                sender.sendMessage(translateAlternateColorCodes('&', "&6&lBlood Crates"));
                sender.sendMessage(translateAlternateColorCodes('&', "&6/bloodcrates help&7 - Show this list"));
                sendPermMessage(sender, "bloodcrates.reload", "&6/bloodcrates reload&7 - Reload the config");
                sendPermMessage(sender, "bloodcrates.cooldowns", "&6/bloodcrates cooldowns&7 - List all players with cooldowns");
                sendPermMessage(sender, "bloodcrates.locations", "&6/bloodcrates locations&7 - List the location of each active crate");
                sendPermMessage(sender, "bloodcrates.config", "&6/bloodcrates config (path)&7 - Check a value from the config.");
                sendPermMessage(sender, "bloodcrates.items", "&6/bloodcrates items&7 - Open item rewards editor.");
                sender.sendMessage(translateAlternateColorCodes('&', "&7For more information about a command use &6/bloodcrates help (command)"));
            }
        } else {
            noPermMessage(sender);
        }
    }

    private void runItem(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bloodcrates.items")) {
            noPermMessage(sender);
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigString("messages.error.notaplayer"));
            return;
        }
        ((Player) sender).openInventory(plugin.getItemManager().getInventory());
    }

    private void runLocation(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.locations")) {
            List<Location> locs = plugin.getCrateManager().getLocations();
            sender.sendMessage(getConfigString("messages.locations.top", String.valueOf(locs.size())));
            for (Location l : locs) {
                sender.sendMessage(getConfigString("messages.locations.list", l.getBlockX(), l.getBlockY(), l.getBlockZ()));
            }
        } else {
            noPermMessage(sender);
        }
    }

    private void runReload(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.reload")) {
            CONFIG.reload(plugin);
            COMMANDS.reload(plugin);
            sender.sendMessage(getConfigString("messages.reload"));
        } else {
            noPermMessage(sender);
        }
    }

    private void runVersion(CommandSender sender, String[] args) {
        if (sender.hasPermission("bloodcrates.version")) {
            PluginDescriptionFile des = plugin.getDescription();
            sender.sendMessage(des.getName() + " Version:" + des.getVersion());
            sender.sendMessage("by " + des.getAuthors().get(0));
            sender.sendMessage(des.getWebsite());
        } else {
            noPermMessage(sender);
        }
    }

    private void sendPermMessage(CommandSender sender, String permission, String message) {
        if (sender.hasPermission(permission)) {
            sender.sendMessage(translateAlternateColorCodes('&', message));
        }
    }
}