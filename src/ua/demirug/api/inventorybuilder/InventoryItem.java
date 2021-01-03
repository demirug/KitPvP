package ua.demirug.api.inventorybuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ua.demirug.api.inventorybuilder.BuildInventory;
import ua.demirug.api.inventorybuilder.Click;
import ua.demirug.api.itembuilder.BuilderItem;

public class InventoryItem
extends BuilderItem {
    private BuildInventory minventory;
    private Click click;

    public InventoryItem(BuildInventory minventory) {
        this.minventory = minventory;
    }

    public InventoryItem(BuildInventory minventory, Material item) {
        super(item);
        this.minventory = minventory;
    }

    public InventoryItem(BuildInventory minventory, ItemStack item) {
        super(item);
        this.minventory = minventory;
    }

    public InventoryItem(BuildInventory minventory, BuilderItem item) {
        super(item.build());
        this.minventory = minventory;
    }

    public InventoryItem addClick(Click click) {
        this.click = click;
        return this;
    }

    public Click getClick() {
        return this.click;
    }

    public boolean hasClick() {
        return this.click != null;
    }
}

