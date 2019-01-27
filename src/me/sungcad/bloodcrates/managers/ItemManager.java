package me.sungcad.bloodcrates.managers;

import static me.sungcad.bloodcrates.managers.Files.CONFIG;
import static me.sungcad.bloodcrates.managers.Files.ITEMS;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import me.sungcad.bloodcrates.BloodCratesPlugin;

public class ItemManager implements InventoryHolder, Listener {
    private BloodCratesPlugin plugin;
    private List<ItemStack> items;
    private ItemStack next, last, save, reload, cancel;
    private Random rand;
    private Map<Integer, Inventory> temp;

    public ItemManager(BloodCratesPlugin plugin) {
        this.plugin = plugin;
        rand = new Random();
        loadItems();
        setupIcons();
    }

    public void setupIcons() {
        next = new ItemStack(Material.ARROW);
        last = new ItemStack(Material.ARROW);
        save = new Wool(DyeColor.GREEN).toItemStack(1);
        reload = new Wool(DyeColor.YELLOW).toItemStack(1);
        cancel = new Wool(DyeColor.RED).toItemStack(1);

        ItemMeta nmeta = next.getItemMeta();
        nmeta.setDisplayName(GRAY + "Next page");
        next.setItemMeta(nmeta);

        ItemMeta lmeta = last.getItemMeta();
        lmeta.setDisplayName(GRAY + "Previous page");
        last.setItemMeta(lmeta);

        ItemMeta smeta = save.getItemMeta();
        smeta.setDisplayName(GREEN + "Save chages");
        save.setItemMeta(smeta);

        ItemMeta rmeta = reload.getItemMeta();
        rmeta.setDisplayName(YELLOW + "Reload from config");
        reload.setItemMeta(rmeta);

        ItemMeta cmeta = cancel.getItemMeta();
        cmeta.setDisplayName(RED + "Cancel and revert changes");
        reload.setItemMeta(cmeta);
    }

    public void loadItems() {
        items = new ArrayList<>();
        for (String key : ITEMS.getConfig().getKeys(false)) {
            items.add(ITEMS.getConfig().getItemStack(key));
        }
    }

    public void saveItems() {
        int keys = ITEMS.getConfig().getKeys(false).size();
        int i = 0;
        for (ItemStack item : items) {
            ITEMS.getConfig().set(Integer.toString(i++), item);
        }
        while (i < keys) {
            ITEMS.getConfig().set(Integer.toString(i++), null);
        }
    }

    public void reloadItems() {
        items.clear();
        ITEMS.reload(plugin);
        for (String key : ITEMS.getConfig().getKeys(false)) {
            items.add(ITEMS.getConfig().getItemStack(key));
        }
        makePages();
    }

    public ItemStack[] getItems(int number) {
        ItemStack[] out = new ItemStack[number];
        int i = 0;
        while (i < number) {
            out[i++] = items.get(rand.nextInt(items.size()));
        }
        return out;
    }

    public ItemStack[] getItems() {
        if (CONFIG.getConfig().getBoolean("settings.reward.multiple", true)) {
            int min, max, a, b;
            a = CONFIG.getConfig().getInt("settings.reward.max", 2);
            b = CONFIG.getConfig().getInt("settings.reward.min", 1);
            max = Math.max(a, b);
            min = Math.min(a, b);
            return getItems(min + rand.nextInt(max - min));
        }
        return getItems(1);
    }

    public ItemStack getItem() {
        return getItems(1)[0];
    }

    public void addItems(ItemStack... itemstacks) {
        items.addAll(Arrays.asList(itemstacks));
    }

    public void addItems(Collection<? extends ItemStack> itemstacks) {
        items.addAll(itemstacks);
    }

    public void addItem(ItemStack item) {
        items.add(item);
    }

    @Override
    public Inventory getInventory() {
        if (temp == null || temp.isEmpty()) {
            makePages();
        }
        return getPage(0);
    }

    public Inventory getPage(int page) {
        return temp.get(page);
    }

    public void makePages() {
        if (temp == null)
            temp = new HashMap<>();
        else
            temp.clear();
        for (int page = 0, n = 0; n <= items.size(); n += 45, page++) {
            Inventory inv = Bukkit.createInventory(this, 54, ChatColor.RED + "Blood Crates items page " + page);
            for (int slot = 0; slot < 45 && slot + n < items.size(); slot++) {
                inv.setItem(slot, items.get(slot + n));
            }
            if (page != 0)
                inv.setItem(45, last);
            if (page + n < items.size())
                inv.setItem(53, next);
            inv.setItem(47, cancel);
            inv.setItem(49, save);
            inv.setItem(51, reload);
            temp.put(page, inv);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof ItemManager)) {
            return;
        }
        storePage(e.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof ItemManager))
            return;
        if (e.getSlot() < 45)
            return;
        e.setCancelled(true);
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        int page = Integer.valueOf(inv.getName().substring(inv.getName().length() - 1));
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
            return;
        if (e.getSlot() == 45) {
            storePage(inv);
            player.openInventory(getPage(page - 1));
            return;
        }
        if (e.getSlot() == 53) {
            storePage(inv);
            player.openInventory(getPage(page + 1));
            return;
        }
        if (e.getSlot() == 47) {
            player.closeInventory();
            return;
        }
        if (e.getSlot() == 49) {
            player.closeInventory();
            savePages();
            return;
        }
        if (e.getSlot() == 51) {
            reloadItems();
            player.openInventory(getInventory());
        }

    }

    private void savePages() {
        List<ItemStack> newitems = new ArrayList<>();
        for (Inventory inv : temp.values()) {
            for (int i = 0; i < 45; i++) {
                newitems.add(inv.getItem(i));
            }
        }
        items = newitems;
        for (String s : ITEMS.getConfig().getKeys(false))
            ITEMS.getConfig().set(s, null);
        int i = 0;
        for (ItemStack item : items)
            ITEMS.getConfig().set(Integer.toString(i++), item);
        ITEMS.save(plugin);
        makePages();
    }

    private void storePage(Inventory inv) {
        if (!(inv instanceof ItemManager))
            return;
        int page = Integer.valueOf(inv.getName().substring(inv.getName().length() - 1));
        temp.put(page, inv);
    }

}
