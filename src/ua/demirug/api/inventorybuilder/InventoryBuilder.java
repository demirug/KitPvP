package ua.demirug.api.inventorybuilder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryBuilder
implements Listener {
    public static BuildInventory create(int slots) {
        return new BuildInventory(slots);
    }

    public static BuildInventory create(int slots, String name) {
        return new BuildInventory(slots, name);
    }

    @EventHandler
    public void Click(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder)e.getInventory().getHolder();
            if (holder.getBuildInventory().isCanceledClick()) {
                e.setCancelled(true);
            }
           
            if (holder.getBuildInventory().getItem(e.getSlot()) != null && holder.getBuildInventory().getItem(e.getSlot()).hasClick()) {
                holder.getBuildInventory().getItem(e.getSlot()).getClick().onClick(e);
            }
        }
    }
}

