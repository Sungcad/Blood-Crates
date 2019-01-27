package me.sungcad.bloodcrates.managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public enum Files {
    CONFIG("config.yml"), ITEMS("items.yml"), COMMANDS("commands.yml");

    String name;
    File file;
    YamlConfiguration config;

    Files(String name) {
        this.name = name;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public boolean load(BloodCratesPlugin plugin) {
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }
        config = new YamlConfiguration();
        try {
            config.load(file);
            plugin.getLogger().info("File " + name + " loaded");
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Error loading file " + name);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reload(BloodCratesPlugin plugin) {
        file = new File(plugin.getDataFolder(), name);
        config = new YamlConfiguration();
        try {
            config.load(file);
            plugin.getLogger().info("File " + name + " loaded");
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Error loading file " + name);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void save(BloodCratesPlugin plugin) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error file " + name + " could not be saved");
            e.printStackTrace();
        }
    }
}
