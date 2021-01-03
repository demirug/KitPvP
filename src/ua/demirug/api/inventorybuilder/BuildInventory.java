package ua.demirug.api.inventorybuilder;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ua.demirug.api.itembuilder.BuilderItem;

public class BuildInventory {
    private Inventory inventory;
    private HashMap<Integer, InventoryItem> items = new HashMap();
    private boolean isCanceledClick = false;

    public BuildInventory(int slots) {
        this.inventory = Bukkit.createInventory((org.bukkit.inventory.InventoryHolder)new InventoryHolder(this), (int)slots);
    }

    public BuildInventory(int slots, String title) {
        this.inventory = Bukkit.createInventory((org.bukkit.inventory.InventoryHolder)new InventoryHolder(this), (int)slots, (String)ChatColor.translateAlternateColorCodes((char)'&', (String)title));
    }

    public BuildInventory(int slots, InventoryHolder holder, String title) {
        this.inventory = Bukkit.createInventory((org.bukkit.inventory.InventoryHolder)holder, (int)slots, (String)ChatColor.translateAlternateColorCodes((char)'&', (String)title));
    }

    public InventoryItem addItem(int slot, Material material) {
        InventoryItem mitem = new InventoryItem(this, material);
        this.items.put(slot, mitem);
        this.inventory.setItem(slot, mitem.build());
        return mitem;
    }

    public InventoryItem addItem(int slot, BuilderItem item) {
        InventoryItem mitem = new InventoryItem(this, item);
        this.items.put(slot, mitem);
        this.inventory.setItem(slot, mitem.build());
        return mitem;
    }

    public InventoryItem addItem(int slot, ItemStack item) {
        InventoryItem mitem = new InventoryItem(this, item);
        this.items.put(slot, mitem);
        this.inventory.setItem(slot, mitem.build());
        return mitem;
    }
    
    public InventoryItem getItem(int slot) {
        return this.items.get(slot);
    }

    public void info() {
        System.out.println(this.inventory.getItem(15) == null);
    }
    
    public BuildInventory removeItem(int slot) {
        //this.inventory.remove(this.items.get(slot).getItem());
        this.inventory.remove(slot);
        this.items.remove(slot);
        return this;
    }

    public BuildInventory setCanceledClick(boolean bln) {
        this.isCanceledClick = bln;
        return this;
    }

    public boolean isCanceledClick() {
        return this.isCanceledClick;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

