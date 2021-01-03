package ua.demirug.api.itembuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder {
    public static BuilderItem create() {
        return new BuilderItem();
    }

    public static BuilderItem create(Material material) {
        return new BuilderItem(material);
    }

    public static BuilderItem load(ItemStack item) {
        return new BuilderItem(item);
    }
}

