package ua.demirug.kitpvp.kit;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.demirug.api.itembuilder.BuilderItem;
import ua.demirug.api.itembuilder.ItemBuilder;
import ua.demirug.kitpvp.KitPvP;
import ua.demirug.kitpvp.User;

public class ArcherKit implements Kit {


    @Override
    public void giveKit(User user) {
        Player player = user.getPlayer();
        player.setMaxHealth(40);
          player.setHealth(player.getMaxHealth());
          player.getInventory().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).setUnbreakable(true).setLeatherColor(Color.LIME).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + KitPvP.getInstance().getConfiguration().getInt("archer-armor-protect-level")).build());
          player.getInventory().setChestplate(ItemBuilder.create(Material.LEATHER_CHESTPLATE).setUnbreakable(true).setLeatherColor(Color.LIME).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + KitPvP.getInstance().getConfiguration().getInt("archer-armor-protect-level")).build());
          player.getInventory().setLeggings(ItemBuilder.create(Material.LEATHER_LEGGINGS).setUnbreakable(true).setLeatherColor(Color.LIME).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + KitPvP.getInstance().getConfiguration().getInt("archer-armor-protect-level")).build());
          player.getInventory().setBoots(ItemBuilder.create(Material.LEATHER_BOOTS).setUnbreakable(true).setLeatherColor(Color.LIME).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + KitPvP.getInstance().getConfiguration().getInt("archer-armor-protect-level")).build());
          player.getInventory().setItem(27, new ItemStack(Material.ARROW));
          BuilderItem bow = ItemBuilder.create(Material.BOW).setUnbreakable(true).addEnchant(Enchantment.ARROW_INFINITE, 1);
          BuilderItem sword = ItemBuilder.create(Material.STONE_SWORD).setUnbreakable(true);
          for(String str : KitPvP.getInstance().getConfiguration().getConfigurationSection("archer-bow-enchants").getValues(false).keySet()) {
              try {
                  bow.addEnchant(Enchantment.getById(Integer.valueOf(str)), KitPvP.getInstance().getConfiguration().getInt("archer-bow-enchants." + str));
              } catch(NumberFormatException e) {}
          }
          
          for(String str : KitPvP.getInstance().getConfiguration().getConfigurationSection("archer-sword-enchants").getValues(false).keySet()) {
              try {
                  sword.addEnchant(Enchantment.getById(Integer.valueOf(str)), KitPvP.getInstance().getConfiguration().getInt("archer-sword-enchants." + str));
              } catch(NumberFormatException e) {}
          }
          
          player.getInventory().addItem(sword.build());
          player.getInventory().addItem(bow.build());
      
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }


    @Override
    public boolean isActiveSkill(User user, int skillID) {
        if(hasSkill(user, skillID)) {
            return user.getActiveSkills()[1] == skillID;    
        } else return false;
    }
    
    @Override
    public boolean hasSkill(User user, int skillID) {
    
        switch(skillID) {
            case 1: return user.getData()[5];
            case 2: return user.getData()[6];
            default: return false;
        }
    }

    @Override
    public String getName() {
        return "Лучник";
    }
    
}
