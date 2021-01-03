package ua.demirug.kitpvp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ua.demirug.api.board.NormalBoard;
import ua.demirug.api.config.ConfigurationInstaller;
import ua.demirug.api.config.ConfigurationManager;
import ua.demirug.api.interact.Click;
import ua.demirug.api.interact.InteractManager;
import ua.demirug.api.inventorybuilder.InventoryBuilder;
import ua.demirug.api.itembuilder.ItemBuilder;

public class KitPvP extends JavaPlugin {

    private static KitPvP instance;
    private ConfigurationInstaller configInstaller;
    private HashMap<Player, User> users;
    private FileConfiguration config, data;
    private ItemStack kitItem, exitItem;
    private BukkitTask  task;
    private ConcurrentHashMap<User, Long> ninja_hook_data;
    private List<Spawner> emeralds_spawns;
    private Random random;
    private final Comparator<User> userComprator = new Comparator<User>() {
           @Override
           public int compare(User o1, User o2) {
           
               if(o1.getKills() > o2.getKills()) return 1;
               else if(o1.getKills() < o2.getKills()) return -1;
               else return 0;
               
           }
       };
    private boolean active = false;
    
    private NormalBoard board;
    
    @Override
    public void onEnable() {
        instance = this;
        this.random = new Random();
        this.users = new HashMap();
        this.ninja_hook_data = new ConcurrentHashMap();
        this.emeralds_spawns = new ArrayList();
        ConfigurationSerialization.registerClass(User.class, "User");
        this.configInstaller = ConfigurationManager.register(this);
        this.config = this.configInstaller.get("config.yml");
        this.data = this.configInstaller.get("data.yml");
        if(!this.getConfiguration().getBoolean("setup")) {
            this.getCommand("leave").setExecutor(new LeaveCommand());
            this.board = new NormalBoard(KitPvP.getInstance().getConfiguration().getString("scoreboard.top-title"));
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.initItems();
            for(Location loc : (List<Location>) this.config.getList("emeralds")) {
                this.emeralds_spawns.add(new Spawner(loc));
            }
            this.getCommand("balance").setExecutor(new BalanceCommand());
            Bukkit.getPluginManager().registerEvents(new InventoryBuilder(), this);
            Bukkit.getPluginManager().registerEvents(new Listener(), this);
            Bukkit.getOnlinePlayers().forEach(player -> load(player));
            
            this.task = new BukkitRunnable() {
                
                int emeraldSeconds = 0, regenSeconds = 0;
                
                @Override
                public void run() {
                    
                    for(World world : Bukkit.getWorlds()) {
                        if(world.isThundering() || world.hasStorm()) {
                            world.setStorm(false);
                            world.setThundering(false);
                        }
                    }
                    
                    long time = System.currentTimeMillis();
                    List<Spawner> avalibleSpawners = emeralds_spawns.stream().filter(spawner -> !spawner.isSpawned()).collect(Collectors.toList());
                    ninja_hook_data.forEach((user, stime) -> {
                    
                        if((time - stime) / 1000 >= config.getInt("ninja-hook-restore")) {
                            if(user.inGame() && !user.getPlayer().isDead()) user.getPlayer().getInventory().addItem(ItemBuilder.create(Material.SNOW_BALL).setName("&bКрюк").build());
                            ninja_hook_data.remove(user);
                        }
                    });
                    
                    if(emeraldSeconds >= config.getInt("emerald-spawn") * 4) {
                        if(!avalibleSpawners.isEmpty()) { 
                            Spawner spawner = avalibleSpawners.get(random.nextInt(avalibleSpawners.size()));
                            avalibleSpawners.remove(spawner);
                            spawner.spawn();
                        }
                        emeraldSeconds = 0;
                    } else emeraldSeconds++;
                   
                    users.values().forEach(user -> {
                    
                        if(user.inGame()) {
                            
                            
                            HashMap<Double, Item> map = new HashMap();
                            double lower_distance = Integer.MAX_VALUE;
                            for(Spawner spawner : emeralds_spawns.stream().filter(spawn -> !avalibleSpawners.contains(spawn)).collect(Collectors.toList())) {
                                map.put(spawner.getItem().getLocation().distance(user.getPlayer().getLocation()), spawner.getItem());
                            }
                            
                            for(double light : map.keySet()) {
                                if(light < lower_distance) lower_distance = light;
                            }
                            if(map.isEmpty())
                                user.nextCompassMode();
                            else 
                               user.getPlayer().setCompassTarget(map.get(lower_distance).getLocation());
                        }
                    
                    });
                   
                    if(regenSeconds >= config.getInt("1-heal-regen") * 4) {
                        
                        List<User> top = buildTOP();

                            if(!top.isEmpty()) {

                                board.clearBoard();

                                for(int i = 0; i<top.size(); i++) {
                                    board.setLine(i + 1, ChatColor.translateAlternateColorCodes('&', String.format(config.getString("scoreboard.top-format"), top.get(i).getPlayerName(), top.get(i).getKills())));
                                }

                            }
                        
                        users.values().stream().filter(user -> user.getPlayer() != null).forEach(user -> {
                        
                            if(user.isTopBoardActive()) {
                                user.getBoard().sendBoard(user.getPlayer());
                                user.setTopBoardStatus(false);
                            }
                            else {
                                
                                    if(!top.isEmpty()) {
                                
                                    board.sendBoard(user.getPlayer());
                                    user.setTopBoardStatus(true);
                                    }
                                }
                            
                        });
                        
                        users.values().stream().filter(user -> user.inGame()).forEach(user -> {
                        
                            Player player = user.getPlayer();
                            if(player.getHealth() + 1 < player.getMaxHealth())
                                player.setHealth(player.getHealth() + 1);
                            
                        });
                        regenSeconds = 0;
                    }
                    
                    regenSeconds++;
                    
                }
                
            }.runTaskTimer(this, 0, 5);
            
        } else this.getCommand("setup").setExecutor(new SetupCommand());
        System.out.println("==================================");
        System.out.println("          KitPvP v1.0");
        System.out.println("   Разработано командой X-Mongers");
        System.out.println("     https://vk.com/xmongers");
        System.out.println("==================================");
    }
    
    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
        
            if(KitPvP.getInstance().getUsers().containsKey(player)) {
               KitPvP.getInstance().getData().set("data." + player.getName(), KitPvP.getInstance().getUsers().get(player));
               KitPvP.getInstance().getConfigInstaller().save(KitPvP.getInstance().getData());
            }
        });
        this.emeralds_spawns.stream().filter(spawner -> spawner.isSpawned()).forEach(spawner -> spawner.getItem().remove());
        if(this.task != null && this.task.isSync())
            this.task.cancel();
    }
    
    public static KitPvP getInstance() {
        return instance;
    }
    
    public String getMessage(String path, Object... elements) {
        return String.format(ChatColor.translateAlternateColorCodes('&', this.config.getString("messages." + path)), elements);
    }
    
    public ConcurrentHashMap<User, Long> getNinjaHookData() {
        return this.ninja_hook_data;
    }
    
    public List<Spawner> getEmeraldsSpawns() {
        return this.emeralds_spawns;
    }
    
    public void initItems() {
        this.kitItem = ItemBuilder.create(Material.COMPASS).setName("&aВыбор кита").build();
        this.exitItem = ItemBuilder.create(Material.BED).setName("&cВыход").build();
        InteractManager.addItem(this.kitItem, new Click() {
            @Override
            public void onClick(PlayerInteractEvent e) {
                User user = users.get(e.getPlayer());
                if(user == null || user.inGame()) return;
                user.getPlayer().openInventory(user.getInventory());
            }
        });
        
        InteractManager.addItem(this.exitItem, new Click() {
            @Override
            public void onClick(PlayerInteractEvent e) {
                exit(e.getPlayer());
            }
        });
        
    }
    
    private List<User> buildTOP() {
        
       List<User> data = new ArrayList();
       if(this.data.contains("data"))
           for(String str : this.data.getConfigurationSection("data").getValues(false).keySet()) {
                data.add((User) this.data.get("data." + str));
           }
       
       
       Collections.sort(data, this.userComprator);
       
       return data.stream().limit(10).collect(Collectors.toList());
       
    }
    
    public void exit(Player player) {
        player.sendMessage(this.getMessage("exit"));
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
              out.writeUTF("Connect");
              out.writeUTF(this.config.getString("exit-server"));
         } catch (IOException ex) {}
         player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }
    
    public void load(Player player) {
        User user = data.contains("data." + player.getName()) ? (User)data.get("data." + player.getName()) : new User(player);
        user.setPlayer(player);
        KitPvP.getInstance().getUsers().put(player, user);
        player.teleport((Location) KitPvP.getInstance().getConfiguration().get("lobby"));
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setMaxHealth(20);
        player.setHealth(20);
        if(player.isFlying()) player.setFlying(false);
        if(player.getAllowFlight()) player.setAllowFlight(false);
        player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
        player.getInventory().setItem(0, KitPvP.getInstance().getKitItem());
        player.getInventory().setItem(8, KitPvP.getInstance().getExitItem());
    }
    
    public ItemStack getKitItem() {
        return this.kitItem;
    }
    
    public ItemStack getExitItem() {
        return this.exitItem;
    }
    
    public ConfigurationInstaller getConfigInstaller() {
        return this.configInstaller;
    }
    
    public FileConfiguration getConfiguration() {
        return this.config;
    }
    
    public HashMap<Player, User> getUsers() {
        return this.users;
    }
    
    public FileConfiguration getData() {
        return this.data;
    }
    
}
