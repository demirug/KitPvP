package ua.demirug.api.inventorybuilder;

import org.bukkit.inventory.Inventory;
import ua.demirug.api.inventorybuilder.BuildInventory;

public class InventoryHolder
implements org.bukkit.inventory.InventoryHolder {
    private BuildInventory minventory;

    public InventoryHolder(BuildInventory aThis) {
        this.minventory = aThis;
    }

    public BuildInventory getBuildInventory() {
        return this.minventory;
    }

    public Inventory getInventory() {
        return this.minventory.getInventory();
    }
}

