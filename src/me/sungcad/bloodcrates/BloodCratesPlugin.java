package me.sungcad.bloodcrates;

import static me.sungcad.bloodcrates.managers.Files.COMMANDS;
import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static me.sungcad.bloodcrates.managers.Files.ITEMS;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.sungcad.bloodcrates.commands.BloodCratesCommand;
import me.sungcad.bloodcrates.commands.CommandTabCompleter;
import me.sungcad.bloodcrates.commands.DebugCommand;
import me.sungcad.bloodcrates.crates.CrateManager;
import me.sungcad.bloodcrates.listeners.CloseListener;
import me.sungcad.bloodcrates.listeners.KillListener;
import me.sungcad.bloodcrates.listeners.OpenListener;
import me.sungcad.bloodcrates.managers.ItemManager;
import me.sungcad.bloodcrates.managers.PlayerManager;

public class BloodCratesPlugin extends JavaPlugin {

    CrateManager cmanager;
    PlayerManager pmanager;
    ItemManager imanager;

    public CrateManager getCrateManager() {
        return cmanager;
    }

    public PlayerManager getPlayerManager() {
        return pmanager;
    }

    public ItemManager getItemManager() {
        return imanager;
    }

    @Override
    public void onDisable() {
        ITEMS.save(this);
        cmanager.despawnAllCrates();
    }

    @Override
    public void onEnable() {
        CONFIG.load(this);
        ITEMS.load(this);
        COMMANDS.load(this);
        cmanager = new CrateManager(this);
        pmanager = new PlayerManager(this);
        imanager = new ItemManager(this);
        getCommand("bloodcrates").setExecutor(new BloodCratesCommand(this));
        getCommand("bloodcrates").setTabCompleter(new CommandTabCompleter());
        getCommand("bloodcratesdebug").setExecutor(new DebugCommand(this));
        Bukkit.getPluginManager().registerEvents(new CloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new OpenListener(this), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(this), this);
        Bukkit.getPluginManager().registerEvents(imanager, this);
    }
}