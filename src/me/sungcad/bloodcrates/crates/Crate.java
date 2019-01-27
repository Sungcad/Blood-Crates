package me.sungcad.bloodcrates.crates;

import static com.gmail.filoghost.holographicdisplays.api.HologramsAPI.createHologram;
import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class Crate implements InventoryHolder {
    private Block block;
    private Material material;
    private Location hololocation;
    private Hologram hologram;
    private Inventory inventory;

    Crate(BloodCratesPlugin plugin, Location location) {
        block = location.getBlock();
        material = block.getType();
        block.setType(Material.CHEST, false);
        hololocation = location.getBlock().getLocation().add(.5, CONFIG.getConfig().getDouble("hologram.height"), .5);
        hologram = createHologram(plugin, hololocation);
        for (String s : CONFIG.getConfig().getStringList("hologram.text")) {
            hologram.appendTextLine(translateAlternateColorCodes('&', s));
        }
        if (CONFIG.getConfig().getString("settings.reward.type").equals("item")) {
            inventory = Bukkit.createInventory(this, 27, translateAlternateColorCodes('&', CONFIG.getConfig().getString("settings.chest")));
            addItems(plugin.getItemManager().getItems());
        }
    }

    public Location getLocation() {
        return block.getLocation();
    }

    void remove() {
        block.setType(material, false);
        hologram.delete();
    }

    public boolean equals(Location location) {
        return location.getBlock().equals(block);
    }

    public void addItems(ItemStack... items) {
        inventory.addItem(items);
    }

    public boolean isEmpty() {
        for (ItemStack i : inventory.getContents()) {
            if (i != null && i.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}