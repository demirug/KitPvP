package ua.demirug.api.itembuilder;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class BuilderItem {
    
    private ItemStack item;
    private ItemMeta meta;
    private List<String> lore = new ArrayList();
    
    public BuilderItem() {
        this.item = new ItemStack(Material.BARRIER);
        this.meta = this.item.getItemMeta();
    }

    public BuilderItem(Material item) {
        this.item = new ItemStack(item);
        this.meta = this.item.getItemMeta();
    }

    public BuilderItem(ItemStack item) {
        this.item = item;
        this.meta = this.item.getItemMeta();
    }

    public BuilderItem setName(String name) {
        this.meta.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        return this;
    }
    
    public BuilderItem addToLore(List<String> lore, Object... data) {
        lore.replaceAll(str -> String.format(ChatColor.translateAlternateColorCodes('&', str), data));
        this.lore.addAll(lore);
        this.setLore(this.lore);
        return this;
    }
    
    public BuilderItem setLore(List<String> lore) {
        List<String> lor = new ArrayList();
        for(String str : lore) {
            lor.add("§f" + ChatColor.translateAlternateColorCodes('&', str));
        }
        this.meta.setLore(lor);
        return this;
    }

    public BuilderItem setAmmount(int ammount) {
        this.item.setAmount(ammount);
        return this;
    }

    public BuilderItem setLeatherColor(Color color) {
        if(!(this.meta instanceof LeatherArmorMeta)) return this;
        LeatherArmorMeta Lmeta = (LeatherArmorMeta) this.meta;
        Lmeta.setColor(color);
        return this;
    }
    
    public BuilderItem setByte(byte byt) {
        this.item.getData().setData(byt);
        return this;
    }
    
    public BuilderItem addUnsaveEnchant(Enchantment ench, int level) {
        this.item.addUnsafeEnchantment(ench, level);
        return this;
    }

    public BuilderItem addEnchant(Enchantment ench, int level) {
        this.meta.addEnchant(ench, level, true);
        return this;
    }

    public BuilderItem setUnbreakable(boolean bln) {
        this.meta.spigot().setUnbreakable(bln);
        return this;
    }
    
    public BuilderItem removeEnchant(Enchantment ench) {
        this.item.removeEnchantment(ench);
        return this;
    }

    public BuilderItem addItemFlag(ItemFlag ... flag) {
        this.meta.addItemFlags(flag);
        return this;
    }

    public BuilderItem removeItemFlag(ItemFlag ... flag) {
        this.meta.removeItemFlags(flag);
        return this;
    }

    public BuilderItem setDurability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    public BuilderItem addGlow() {
        this.meta.addEnchant(Enchantment.LURE, 0, true);
        this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        return this;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ItemMeta getItemMeta() {
        return this.meta;
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}

