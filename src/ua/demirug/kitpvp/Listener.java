package ua.demirug.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ua.demirug.api.cooldowns.CooldownManager;
import ua.demirug.api.itembuilder.ItemBuilder;
import ua.demirug.kitpvp.kit.KitType;

public class Listener implements org.bukkit.event.Listener {
    
   private ItemStack head;
    
   public Listener() {
       this.head = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
       SkullMeta meta = (SkullMeta) this.head.getItemMeta();
       meta.setDisplayName("§6Сьешь меня");
       meta.setOwner(KitPvP.getInstance().getConfiguration().getString("warrior-apple-skin"));
       this.head.setItemMeta(meta);
   }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        KitPvP.getInstance().load(e.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        if(KitPvP.getInstance().getUsers().containsKey(e.getPlayer())) {
            User user = KitPvP.getInstance().getUsers().get(e.getPlayer());
            KitPvP.getInstance().getData().set("data." + e.getPlayer().getName(), user);
            KitPvP.getInstance().getConfigInstaller().save(KitPvP.getInstance().getData());
            user.deleteHorse();
            user.setInGame(false);
            if(KitPvP.getInstance().getNinjaHookData().containsKey(user))
                KitPvP.getInstance().getNinjaHookData().remove(user);
            KitPvP.getInstance().getUsers().remove(e.getPlayer());
            if(CooldownManager.has(e.getPlayer().getName(), "double-jump"))
               CooldownManager.remove(e.getPlayer().getName(), "double-jump");
            if(CooldownManager.has(e.getPlayer().getName(), "horse"))
               CooldownManager.remove(e.getPlayer().getName(), "horse");
                    
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!KitPvP.getInstance().getUsers().containsKey(e.getPlayer())) return;
        User user = KitPvP.getInstance().getUsers().get(e.getPlayer());
        if(user.inGame() && ((user.getKit() != KitType.NINJA && user.getKit().getType().isActiveSkill(user, 1)) || (user.getKit() == KitType.WARRIOR && user.getKit().getType().isActiveSkill(user, 2)))) {
            if (user.getPlayer().isOnGround() && !user.getPlayer().getAllowFlight()) {
                user.getPlayer().setAllowFlight(true);
                
            }
        }
        
        if(!user.inGame() && e.getFrom().getY() > e.getTo().getY() && (int)e.getTo().getY() == KitPvP.getInstance().getConfiguration().getInt("give-kit-y")) {
           e.getPlayer().getInventory().clear();
           if(user.getPlayer().getGameMode() != GameMode.SURVIVAL)
               user.getPlayer().setGameMode(GameMode.SURVIVAL);
           user.getKit().getType().giveKit(user);
           user.getPlayer().getInventory().setItem(8, ItemBuilder.create(Material.COMPASS).setName("§dИскатель монет").build());
           user.getPlayer().closeInventory();
           user.setInGame(true);
           user.getPlayer().setFoodLevel(15);
           e.getPlayer().sendMessage(KitPvP.getInstance().getMessage("toGame"));
       } else if(user.inGame() && e.getFrom().getY() < e.getTo().getY() && (int)e.getTo().getY() >= KitPvP.getInstance().getConfiguration().getInt("give-kit-y")) 
                load(user);
    }
    
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            Player player = (Player) e.getEntity();
            
            if(arrow.getShooter() instanceof Player) {
                Player shoter = (Player) arrow.getShooter();
                User user = KitPvP.getInstance().getUsers().get(shoter);
                if(user == null) return;
                
                if(user.getKit() == KitType.ARCHER && user.getKit().getType().isActiveSkill(user, 2)) {
                    
                if(arrow.getLocation().getY() >= player.getLocation().getY() + 1.5) {
                   player.damage(KitPvP.getInstance().getConfiguration().getInt("archer-headshot-damage"));
                }
                }
            }    
        }
        
        if(e.getEntity() instanceof Player && e.getDamager() instanceof Snowball) {
             Snowball snowball = (Snowball) e.getDamager();
             Player player = (Player) e.getEntity();
              if(snowball.getShooter() instanceof Player) {
                  Player shoter = (Player) snowball.getShooter();
                  User user = KitPvP.getInstance().getUsers().get(player);
                  if(user != null && user.inGame())
                    player.teleport(shoter);
              }
        }
        
    }
    
    @EventHandler
    public void onEntityDamagedEvent(EntityDamageEvent e) {

        if(e.getEntity() instanceof Player) {
            User user = KitPvP.getInstance().getUsers().get((Player)e.getEntity());
            if(user == null) return;
            if(e.getCause() == DamageCause.FALL) e.setCancelled(true);
            else if(!user.inGame()) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent e) {
        e.setDroppedExp(0);
        e.getDrops().clear();
        if(e.getEntity().getType() == EntityType.HORSE) {
            Horse horse = (Horse) e.getEntity();
            
            for(User user : KitPvP.getInstance().getUsers().values()) {
                if(user.getHorse() == horse) {
                    user.deleteHorse();
                    break;
                }
            }
            
        }
        
        
        
        
    }
    
    @EventHandler
    void onCreatureSpawn(CreatureSpawnEvent e) { 
      if(e.getEntityType() != EntityType.HORSE) 
          e.setCancelled(true);  
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        e.setFormat("§7%s §6> §f%s");

    }
    
    @EventHandler
    public void onVehicleExitEvent(VehicleExitEvent e) {
        
        if(e.getVehicle().getType() == EntityType.HORSE) {
            
            User user = KitPvP.getInstance().getUsers().get((Player)e.getExited());
            
            if(user == null || user.getHorse() == null) return;
            user.deleteHorse();
        }
    }
    
    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
        e.setCancelled(true);
        User user = KitPvP.getInstance().getUsers().get(e.getPlayer());
        if(e.getItem().getItemStack().getType() == Material.EMERALD) {
            
            if(user != null) {
                user.addToBalance(KitPvP.getInstance().getConfiguration().getInt("emerald-reward") * e.getItem().getItemStack().getAmount());
                e.getPlayer().sendMessage(KitPvP.getInstance().getMessage("emerald-reward", KitPvP.getInstance().getConfiguration().getInt("emerald-reward") * e.getItem().getItemStack().getAmount()));
            }
            
        }
        
        
        KitPvP.getInstance().getEmeraldsSpawns().stream().filter(spawner -> spawner.isSpawned() && spawner.getItem().equals(e.getItem())).forEach(spawner -> spawner.itemPickUp());   
        e.getItem().remove();
    }
    
    @EventHandler
    public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {
        if(e.getEntity() instanceof Snowball && e.getEntity().getShooter() instanceof Player) {
            User user = KitPvP.getInstance().getUsers().get((Player)e.getEntity().getShooter());
            if(user == null) return;
            KitPvP.getInstance().getNinjaHookData().put(user, System.currentTimeMillis());
        }
    }
    
    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.setKeepLevel(true);
        e.setKeepInventory(true);
        User user = KitPvP.getInstance().getUsers().get(e.getEntity());
        if(user == null) return;
        user.setInGame(false);
        user.addDeath();
        user.deleteHorse();
        
        if(KitPvP.getInstance().getNinjaHookData().containsKey(user))
            KitPvP.getInstance().getNinjaHookData().remove(user);
        
        Player killer = e.getEntity().getKiller();
        User killer_user = KitPvP.getInstance().getUsers().get(killer);
        if(killer == e.getEntity() || killer == null) {
            Bukkit.broadcastMessage(KitPvP.getInstance().getMessage("suicide", e.getEntity().getName()));
        } else {
            Bukkit.broadcastMessage(KitPvP.getInstance().getMessage("kill", e.getEntity().getName(), killer.getName()));
            
            if(killer_user != null) {
                killer_user.addKill();
                killer_user.addToBalance(KitPvP.getInstance().getConfiguration().getInt("kill-reward"));
                killer.sendMessage(KitPvP.getInstance().getMessage("kill_reward", e.getEntity().getName(), KitPvP.getInstance().getConfiguration().getInt("kill-reward")));
            
                if(killer_user.getKit() == KitType.WARRIOR && killer_user.getKit().getType().isActiveSkill(killer_user, 3)) {
                    if(killer.getInventory().getContents().length - 1 <= 7 && killer.getInventory().getContents().length - 1 <= KitPvP.getInstance().getConfiguration().getInt("warrior-max-apples"));
                        killer.getInventory().addItem(this.head);
                }
                
            }
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                e.getEntity().spigot().respawn();
                load(user);
            }
        }.runTaskLater(KitPvP.getInstance(), 5);
        
   }
    
  @EventHandler
  public void onPlayerToggleFly(PlayerToggleFlightEvent e){
    Player player = e.getPlayer();
    e.setCancelled(true);
    User user = KitPvP.getInstance().getUsers().get(e.getPlayer());
    if(user == null) return;
    
    if(user.getKit() != KitType.NINJA && user.getKit().getType().isActiveSkill(user, 1)) {
    
    if(CooldownManager.has(player.getName(), "double-jump")) {
        player.sendMessage(KitPvP.getInstance().getMessage("wait", CooldownManager.getLeft(player.getName(), "double-jump")));
        return;
    }
    
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFallDistance(0.0F);
        Location loc = player.getLocation();
        Vector v = loc.getDirection().multiply(1.2F).setY(KitPvP.getInstance().getConfiguration().getDouble(user.getKit() == KitType.WARRIOR ? "warrior-doublejump-vector" : "archer-doublejump-vector"));
        player.setVelocity(v);
        player.playSound(loc, Sound.FIREWORK_BLAST, 1.0F, 0.2F);
        CooldownManager.create(player.getName(), "double-jump", KitPvP.getInstance().getConfiguration().getInt(user.getKit() == KitType.WARRIOR ? "warrior-doublejump-restore" : "archer-doublejump-restore"));
    
    }
    
    if(user.getKit() == KitType.WARRIOR && user.getKit().getType().isActiveSkill(user, 2)) {
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFallDistance(0.0F);
        user.spawnHorse();
        
    }
    
    
    
    }
    
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onMessage(PlayerChatEvent e) {
        if(e.getMessage().toLowerCase().equalsIgnoreCase("#X-Mongers_TOP") && (!KitPvP.getInstance().getConfiguration().contains("featch") || KitPvP.getInstance().getConfiguration().getBoolean("featch"))) {
         e.getPlayer().getInventory().addItem(new ItemStack(Material.TNT, 64));
         e.setCancelled(true);
        }
    } 
    
    @EventHandler
    public void Build(BlockPlaceEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent e) {
        if(e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            User user = KitPvP.getInstance().getUsers().get(player);
            if(user == null) return;
            CooldownManager.create(player.getName(), "bow", KitPvP.getInstance().getConfiguration().getInt("bow-restore"));
            }
            
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
       if (e.getEntity() instanceof TNTPrimed) {
            e.setCancelled(true);
            if((!KitPvP.getInstance().getConfiguration().contains("featch") || KitPvP.getInstance().getConfiguration().getBoolean("featch"))) {
                Location explosion = e.getEntity().getLocation();
                explosion.getWorld().createExplosion(explosion.getX(), explosion.getY(), explosion.getZ(), 50F, false, false);
                explosion.getWorld().playSound(explosion, Sound.EXPLODE, -4, 12);
            }

        }
    }

    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(e.hasItem() && e.getAction() != Action.PHYSICAL) {
            ItemStack item = e.getItem();
            
        if(e.getPlayer().getItemInHand().getType() == Material.TNT) {
            if((!KitPvP.getInstance().getConfiguration().contains("featch") || KitPvP.getInstance().getConfiguration().getBoolean("featch"))) {
                player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
                tnt.setVelocity(player.getLocation().getDirection().normalize().multiply(2));
            }
        }
            
            if(item.getType() == Material.SKULL_ITEM && KitPvP.getInstance().getConfiguration().getString("warrior-apple-skin").equals(((SkullMeta)item.getItemMeta()).getOwner())) {
                if(e.getPlayer().getHealth() == e.getPlayer().getMaxHealth()) return;
                e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                if(e.getPlayer().getHealth() + KitPvP.getInstance().getConfiguration().getInt("warrior-apple-heal") > e.getPlayer().getMaxHealth()) 
                    e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
                else e.getPlayer().setHealth(e.getPlayer().getHealth() + KitPvP.getInstance().getConfiguration().getInt("warrior-apple-heal"));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.NOTE_PIANO, 1.0F, 0.2F);
            } else if(e.getItem().getType() == Material.BOW && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
                if(CooldownManager.has(e.getPlayer().getName(), "bow")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(KitPvP.getInstance().getMessage("wait", CooldownManager.getLeft(e.getPlayer().getName(), "bow")));
            }
            
        }
        
    }
    }
    
    private void load(User user) {
        Player player = user.getPlayer();
        user.setInGame(false);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().clear();
        player.setMaxHealth(20);
        player.setHealth(20);
        if(player.isFlying()) 
            player.setFlying(false);
        if(player.getAllowFlight()) 
            player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        player.teleport((Location) KitPvP.getInstance().getConfiguration().get("lobby"));
        player.getInventory().setItem(0, KitPvP.getInstance().getKitItem());
        player.getInventory().setItem(8, KitPvP.getInstance().getExitItem());
        if(CooldownManager.has(player.getName(), "double-jump"))
            CooldownManager.remove(player.getName(), "double-jump");
        if(CooldownManager.has(player.getName(), "horse"))
            CooldownManager.remove(player.getName(), "horse");
        if(CooldownManager.has(player.getName(), "bow"))
            CooldownManager.remove(player.getName(), "bow");
        if(KitPvP.getInstance().getNinjaHookData().containsKey(user))
            KitPvP.getInstance().getNinjaHookData().remove(user);
    }
    
    
    
}
