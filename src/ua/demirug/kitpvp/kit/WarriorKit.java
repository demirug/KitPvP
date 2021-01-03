package ua.demirug.kitpvp.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.demirug.api.itembuilder.BuilderItem;
import ua.demirug.api.itembuilder.ItemBuilder;
import ua.demirug.kitpvp.KitPvP;
import ua.demirug.kitpvp.User;

public class WarriorKit implements Kit {


    @Override
    public void giveKit(User user) {
        Player player = user.getPlayer();
        player.setMaxHealth(40);
          player.setHealth(player.getMaxHealth());
          player.getInventory().setHelmet(ItemBuilder.create(Material.IRON_HELMET).setUnbreakable(true).build());
          player.getInventory().setChestplate(ItemBuilder.create(Material.IRON_CHESTPLATE).setUnbreakable(true).build());
          player.getInventory().setLeggings(ItemBuilder.create(Material.IRON_LEGGINGS).setUnbreakable(true).build());
          player.getInventory().setBoots(ItemBuilder.create(Material.IRON_BOOTS).setUnbreakable(true).build());
          BuilderItem sword = ItemBuilder.create(Material.IRON_SWORD).setName("&dЭскалибур").setUnbreakable(true);
          for(String str : KitPvP.getInstance().getConfiguration().getConfigurationSection("warrior-sword-enchants").getValues(false).keySet()) {
              try {
                  sword.addEnchant(Enchantment.getById(Integer.valueOf(str)), KitPvP.getInstance().getConfiguration().getInt("warrior-sword-enchants." + str));
              } catch(NumberFormatException e) {}
          }
          player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
          player.getInventory().addItem(sword.build());
    }

    @Override
    public boolean isActiveSkill(User user, int skillID) {
        if(hasSkill(user, skillID)) {
            return user.getActiveSkills()[0] == skillID;    
        } else return false;
    }
    
    @Override
    public boolean hasSkill(User user, int skillID) {
    
        switch(skillID) {
            case 1: return user.getData()[2];
            case 2: return user.getData()[3];
            case 3: return user.getData()[4];
            default: return false;
        }
    }

    @Override
    public String getName() {
        return "Воин";
    }
    
}
