package ua.demirug.api.interact;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ua.demirug.api.itembuilder.BuilderItem;
import ua.demirug.kitpvp.KitPvP;

public class InteractManager
implements Listener {
    private static HashMap<ItemStack, Click> items;

    public static void addItem(ItemStack item, Click click) {
        items.put(item, click);
    }

    public static void addItem(BuilderItem item, Click click) {
        items.put(item.build(), click);
    }

    public static void removeItem(ItemStack item) {
        if (items.containsKey(item)) {
            items.remove(item);
        }
    }


    public static HashMap<ItemStack, Click> getItems() {
        return items;
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        if (e.hasItem() && items.containsKey(e.getItem())) {
            items.get(e.getItem()).onClick(e);
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents((Listener)new InteractManager(), (Plugin)KitPvP.getInstance());
        items = new HashMap();
    }
}

