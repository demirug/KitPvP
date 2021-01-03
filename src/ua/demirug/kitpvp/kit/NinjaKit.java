package ua.demirug.kitpvp.kit;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.demirug.api.itembuilder.BuilderItem;
import ua.demirug.api.itembuilder.ItemBuilder;
import ua.demirug.kitpvp.KitPvP;
import ua.demirug.kitpvp.User;

public class NinjaKit implements Kit {

    @Override
    public void giveKit(User user) {
        Player player = user.getPlayer();
        player.setMaxHealth(40);
        player.setHealth(player.getMaxHealth());
          
          BuilderItem chestplate = ItemBuilder.create(Material.LEATHER_CHESTPLATE).setUnbreakable(true).setLeatherColor(Color.WHITE);
          
          if(user.getNinjaArmorLevel() == 1) {
              chestplate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
          }
          
         BuilderItem leggins = ItemBuilder.create(Material.LEATHER_LEGGINGS).setUnbreakable(true).setLeatherColor(Color.BLACK);
         BuilderItem boots = ItemBuilder.create(Material.LEATHER_BOOTS).setUnbreakable(true).setLeatherColor(Color.BLACK);

          if(user.getNinjaArmorLevel() == 2 || user.getNinjaArmorLevel() == 3) {
              leggins.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
              boots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
          }
         
          if(user.getNinjaArmorLevel() == 3) {
              player.getInventory().setHelmet(ItemBuilder.create(Material.LEATHER_HELMET).setUnbreakable(true).setLeatherColor(Color.WHITE).build());
          }
          
          player.getInventory().setChestplate(chestplate.build());
          player.getInventory().setLeggings(leggins.build());
          player.getInventory().setBoots(boots.build());
          
          BuilderItem sword = ItemBuilder.create(Material.IRON_SWORD).setUnbreakable(true);
          for(String str : KitPvP.getInstance().getConfiguration().getConfigurationSection("ninja-sword-enchants").getValues(false).keySet()) {
              try {
                  sword.addEnchant(Enchantment.getById(Integer.valueOf(str)), KitPvP.getInstance().getConfiguration().getInt("ninja-sword-enchants." + str));
              } catch(NumberFormatException e) {}
          }
          player.getInventory().addItem(sword.build());
          player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
          if(isActiveSkill(user, 1)) player.getInventory().addItem(ItemBuilder.create(Material.SNOW_BALL).setName("&bКрюк").build());
    }

    @Override
    public boolean isActiveSkill(User user, int skillID) {
        if(hasSkill(user, skillID)) {
            return user.getActiveSkills()[2] == skillID;    
        } else return false;
    }
    
    @Override
    public boolean hasSkill(User user, int skillID) {
    
        switch(skillID) {
            case 1: return user.getData()[7];
            default: return false;
        }
    }

    @Override
    public String getName() {
        return "Ниндзя";
    }
    
}
