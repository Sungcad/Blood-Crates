package me.sungcad.bloodcrates.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.sungcad.bloodcrates.BloodCratesPlugin;
import me.sungcad.bloodcrates.crates.Crate;

public class CloseListener implements Listener {
	BloodCratesPlugin plugin;

	public CloseListener(BloodCratesPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof Crate) {
			if (isEmpty(e.getInventory())) {
				Crate crate = (Crate) e.getInventory().getHolder();
				plugin.getCrateManager().despawnCrate(crate.getLocation());
			}
		}
	}

	boolean isEmpty(Inventory inv) {
		for (ItemStack i : inv.getContents()) {
			if (i != null) {
				return false;
			}
		}
		return true;
	}
}