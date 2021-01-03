package ua.demirug.kitpvp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class Spawner {

    private Location location;
    private Item spawnedItem;
    
    public Spawner(Location loc) {
        this.location = loc;
    }
    
    public void spawn() {
        if(this.isSpawned()) return;
        this.spawnedItem = this.location.getWorld().dropItem(this.location, new ItemStack(Material.EMERALD));
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public Item getItem() {
        return this.spawnedItem;
    }
    
    public void itemPickUp() {
        this.spawnedItem = null;
    }
    
    public boolean isSpawned() {
        return this.spawnedItem != null && this.spawnedItem.isValid();
    }
    
}
