package ua.demirug.kitpvp;

import com.google.common.primitives.Booleans;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ua.demirug.api.board.NormalBoard;
import ua.demirug.api.cooldowns.CooldownManager;
import ua.demirug.api.inventorybuilder.BuildInventory;
import ua.demirug.api.inventorybuilder.Click;
import ua.demirug.api.inventorybuilder.InventoryBuilder;
import ua.demirug.api.itembuilder.BuilderItem;
import ua.demirug.api.timer.Timer;
import ua.demirug.api.timer.onTime;
import ua.demirug.kitpvp.kit.KitType;

public class User implements ConfigurationSerializable {

    private Player player;
    private String playerName;
    private KitType kit = KitType.WARRIOR;
    private int[] activeSkills = {0, 0, 0};
    private int ninjaArmorLevel = 0, kills = 0, deaths = 0;
    private boolean[] data = {false, false, false, false, false, false, false, false};
    private boolean topBoardActive = false;
    private long lastSkillUsage = 0;
    private Horse horse;
    private NormalBoard board = new NormalBoard(KitPvP.getInstance().getConfiguration().getString("scoreboard.title"));
    private Timer horseTimer;
    private List<Direction> directions = Arrays.asList(Direction.FRONT, Direction.RIGHT, Direction.BEHIND, Direction.LEFT);
    private Iterator<Direction> iterator = directions.iterator();
    
    // 0, 1 - киты (лучник и ниндзя)
    // 2 3 4 - мечник
    // 5 6 - лучник
    // 7 - ниндзя
    private long balance = 0;
    private boolean ingame = false;
    private BuildInventory selectKit, warriorUpgrade, archerUpgrade, ninjaUpgrade;
    
    public User(Map<String, Object> args) {
        this.kills = (int) args.get("kills");
        this.deaths = (int) args.get("deaths");
        this.data = Booleans.toArray((List<Boolean>)args.get("data"));
        this.balance = (int) args.get("balance");
        this.kit = KitType.valueOf((String)args.get("kit"));
        this.activeSkills = ((ArrayList<Integer>)args.get("activeSkills")).stream().mapToInt(Integer::intValue).toArray();
        this.ninjaArmorLevel = (int) args.get("ninjaArmorLevel");
        this.playerName = (String) args.get("name");
        this.initInventorys();
    }
    
    public User(Player player) {
        this.initInventorys();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        this.playerName = player.getName();
        this.updateBoard();
        this.board.sendBoard(this.player);
    }
    
    private void updateBoard() {
       List<String> body = KitPvP.getInstance().getConfiguration().getStringList("scoreboard.body");
       body.replaceAll(str -> String.format(str, this.kit.getType().getName(), this.kills, this.deaths, this.balance));
       this.board.setText(body);
        
    }
    
    private void initInventorys() {
        this.selectKit = InventoryBuilder.create(36, "Выберите кит");
        this.selectKit.setCanceledClick(true);
        
        this.warriorUpgrade = InventoryBuilder.create(27, "Улучшение Воина");
        this.warriorUpgrade.setCanceledClick(true);
        
        this.archerUpgrade = InventoryBuilder.create(27, "Улучшение Лучника");
        this.archerUpgrade.setCanceledClick(true);
        
        this.ninjaUpgrade = InventoryBuilder.create(27, "Улучшение Ниндзя");
        this.ninjaUpgrade.setCanceledClick(true);
        
        BuilderItem upgradeWarrior = new BuilderItem(Material.EMERALD).setName("&aУлучшение");
        upgradeWarrior.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.skills-open"), "Воин");
        this.selectKit.addItem(19, upgradeWarrior).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                
                e.getWhoClicked().openInventory(warriorUpgrade.getInventory());

            }
        });
        
        BuilderItem upgradeArcher = new BuilderItem(Material.EMERALD).setName("&aУлучшение");
        upgradeArcher.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.skills-open"), "Лучник");
        this.selectKit.addItem(22, upgradeArcher).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
               if(data[0] == true) e.getWhoClicked().openInventory(archerUpgrade.getInventory());
               else {
                   e.getWhoClicked().closeInventory();
                   e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-not-available"));
               }
            }
        });
        
        BuilderItem upgradeNinja = new BuilderItem(Material.EMERALD).setName("&aУлучшение");
        upgradeNinja.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.skills-open"), "Ниндзя");
        this.selectKit.addItem(25, upgradeNinja).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
               if(data[1] == true) e.getWhoClicked().openInventory(ninjaUpgrade.getInventory());
               else {
                   e.getWhoClicked().closeInventory();
                   e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-not-available"));
               }
            }
        });
        
        this.initMainItems();
        this.initUpgrade();
    }
    
    private void initUpgrade() {
        
        BuilderItem warriorJump = new BuilderItem(Material.SLIME_BLOCK).setName("&bДвойной прыжок");
        warriorJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.warrior-doublejump"));
        if(data[2] == false) warriorJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("warrior-doublejump-cost"));
        else if(activeSkills[0] == 1) warriorJump.addGlow();
        else warriorJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.warriorUpgrade.addItem(10, warriorJump).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
               
                if(data[2] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("warrior-doublejump-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("warrior-doublejump-cost"));
                        data[2] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Двойной прыжок"));
                    } 
                }
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Двойной прыжок"));
              activeSkills[0] = 1;
              initUpgrade();
              e.getWhoClicked().closeInventory();  
                
            }
        });
        
        BuilderItem warriorHorse = new BuilderItem(Material.GOLD_BARDING).setName("&bВызов лошади");
        warriorHorse.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.warrior-horse"));
        if(data[3] == false) warriorHorse.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("warrior-horse-cost"));
        else if(activeSkills[0] == 2) warriorHorse.addGlow();
        else warriorHorse.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.warriorUpgrade.addItem(13, warriorHorse).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(data[3] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("warrior-horse-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("warrior-horse-cost"));
                        data[3] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Вызов лошади"));
                    } 
                }
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Вызов лошади"));
              activeSkills[0] = 2;
              initUpgrade();
              e.getWhoClicked().closeInventory();  
            }
        });
        
        BuilderItem warriorApple = new BuilderItem(Material.GOLDEN_APPLE).setName("&bЗолотые яблоки");
        warriorApple.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.warrior-apples"));
        if(data[4] == false) warriorApple.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("warrior-apple-cost"));
        else if(activeSkills[0] == 3) warriorApple.setDurability((short)1);
        else warriorApple.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.warriorUpgrade.addItem(16, warriorApple).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(data[4] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("warrior-apple-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("warrior-apple-cost"));
                        data[4] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Золотые яблоки"));
                    } 
                }
                
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Золотые яблоки"));
              activeSkills[0] = 3;
              initUpgrade();
              e.getWhoClicked().closeInventory(); 
            }
        });
        
        //----------------------------------------//
        
        BuilderItem archerJump = new BuilderItem(Material.SLIME_BLOCK).setName("&bДвойной прыжок");
        archerJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.archer-doublejump"));
        if(data[5] == false) archerJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("archer-doublejump-cost"));
        else if(activeSkills[1] == 1) archerJump.addGlow();
        else archerJump.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.archerUpgrade.addItem(11, archerJump).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(data[5] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("archer-doublejump-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("archer-doublejump-cost"));
                        data[5] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Двойной прыжок"));
                    } 
                }
                
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Двойной прыжок"));
              activeSkills[1] = 1;
              initUpgrade();
              e.getWhoClicked().closeInventory(); 
            }
        });
        
        BuilderItem archerHeadShot = new BuilderItem(Material.REDSTONE).setName("&bУвеличение при хедшоте");
        archerHeadShot.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.archer-headshot"));
        if(data[6] == false) archerHeadShot.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("archer-headshot-cost"));
        else if(activeSkills[1] == 2) archerHeadShot.addGlow();
        else archerHeadShot.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.archerUpgrade.addItem(15, archerHeadShot).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(data[6] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("archer-headshot-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("archer-headshot-cost"));
                        data[6] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Увеличение урона при хедшоте"));
                    } 
                }
                
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Увеличение урона при хедшоте"));
              activeSkills[1] = 2;
              initUpgrade();
              e.getWhoClicked().closeInventory(); 
            }
        });
        
        //----------------------------------------//
        
        BuilderItem ninjaHock = new BuilderItem(Material.SNOW_BALL).setName("&bКрюк");
        ninjaHock.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.ninja-hook"));
        if(data[7] == false) ninjaHock.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("ninja-hook-cost"));
        else if(activeSkills[2] == 1) ninjaHock.addGlow();
        else ninjaHock.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.ninjaUpgrade.addItem(this.ninjaArmorLevel == 3 ? 13 : 11, ninjaHock).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(data[7] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("ninja-hook-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("ninja-hook-cost"));
                        data[7] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-buy", "Крюк"));
                    } 
                }
                
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("skill-selected", "Крюк"));
              activeSkills[2] = 1;
              initUpgrade();
              e.getWhoClicked().closeInventory(); 
            }
        });
        
        //----------------------------------------//
        if(this.ninjaArmorLevel < 3) {
        BuilderItem ninjaArmorUpgrade = new BuilderItem(Material.CHAINMAIL_CHESTPLATE).setName("&bУлудшить броню до " + (this.ninjaArmorLevel + 1));
        ninjaArmorUpgrade.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.ninja-armor-upgrade"));
        ninjaArmorUpgrade.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("ninja-armor-cost." + (this.ninjaArmorLevel + 1)));
    
        this.ninjaUpgrade.addItem(15, ninjaArmorUpgrade).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                
                if(balance < KitPvP.getInstance().getConfiguration().getInt("ninja-armor-cost." + (ninjaArmorLevel + 1))) {
                    e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                    e.getWhoClicked().closeInventory(); 
                }
                
                removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("ninja-armor-cost." + (ninjaArmorLevel + 1)));
                ninjaArmorLevel++;
                
                if(ninjaArmorLevel == 3) {
                    ninjaUpgrade.addItem(15, Material.AIR);
                    ninjaUpgrade.addItem(11, Material.AIR);
                }
                
                initUpgrade();
                e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("ninja-armor-upgraded", ninjaArmorLevel));
                e.getWhoClicked().closeInventory(); 
            }   
        });
        }    
    }
    
    public void nextCompassMode() {
        if (!this.iterator.hasNext())
            this.iterator = this.directions.iterator();
        
        this.iterator.next().setDirection(this.player);
    }
    
    private void initMainItems() {

        BuilderItem warrior = new BuilderItem(Material.IRON_CHESTPLATE).setName("&bВоин");
        warrior.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.warrior-kit"));
        if(this.kit == KitType.WARRIOR) warrior.addGlow();
        else warrior.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.selectKit.addItem(10, warrior).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
              if(kit == KitType.WARRIOR) return;
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-selected", "Воин"));
              setKit(KitType.WARRIOR);
              initMainItems();
              e.getWhoClicked().closeInventory();
            }
        });

        BuilderItem archer = new BuilderItem(Material.BOW).setName("&bЛучник");
        archer.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.archer-kit"));
        if(this.kit == KitType.ARCHER) archer.addGlow();
        else if(data[0] == false) archer.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("archer-kit-cost"));
        else archer.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.selectKit.addItem(13, archer).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(kit == KitType.ARCHER) return;
                if(data[0] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("archer-kit-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("archer-kit-cost"));
                        data[0] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-buy", "Лучник"));
                    } 
                }
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-selected", "Лучник"));
              setKit(KitType.ARCHER);
              initMainItems();
              e.getWhoClicked().closeInventory();
            }
        });
        

        BuilderItem ninja = new BuilderItem(Material.IRON_SWORD).setName("&bНиндзя");
        ninja.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.ninja-kit"));
        if(this.kit == KitType.NINJA) ninja.addGlow();
        else if(data[1] == false) ninja.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.cost"), KitPvP.getInstance().getConfiguration().getInt("ninja-kit-cost"));
        else ninja.addToLore(KitPvP.getInstance().getConfiguration().getStringList("lores.click-to-select"));
        this.selectKit.addItem(16, ninja).addClick(new Click() {
            @Override
            public void onClick(InventoryClickEvent e) {
                if(kit == KitType.NINJA) return;
                if(data[1] == false) {
                    if(balance < KitPvP.getInstance().getConfiguration().getInt("ninja-kit-cost")) {
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("not-enough-money"));
                        e.getWhoClicked().closeInventory();
                        return;
                    } else {
                        removeFromBalance(KitPvP.getInstance().getConfiguration().getInt("ninja-kit-cost"));
                        data[1] = true;
                        e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-buy", "Ниндзя"));
                    } 
                }
              e.getWhoClicked().sendMessage(KitPvP.getInstance().getMessage("kit-selected", "Ниндзя"));
              setKit(KitType.NINJA);
              initMainItems();
              e.getWhoClicked().closeInventory();
                
            }
        });
        
    }
    
    public Inventory getInventory() {
        return this.selectKit.getInventory();
    }
    
    public KitType getKit() {
        return this.kit;
    }
    
    public Timer getHorseTimer() {
        if(!this.horseTimer.isRunning()) this.horseTimer = null;
        return this.horseTimer;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void addKill() {
        this.kills++;
        this.updateBoard();
        KitPvP.getInstance().getData().set("data." + this.player.getName(), this);
    }

    public int getDeaths() {
        return this.deaths;
    }
    
    public void addDeath() {
        this.deaths++;
        this.updateBoard();
        KitPvP.getInstance().getData().set("data." + this.player.getName(), this);
    }
    
    public NormalBoard getBoard() {
        return this.board;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setKit(KitType kit) {
        this.kit = kit;
        KitPvP.getInstance().getData().set("data." + this.player.getName(), this);
        this.updateBoard();
    }
    
    public int[] getActiveSkills() {
        return this.activeSkills;
    }
    
    public Horse getHorse() {
        return this.horse;
    }
    
    public void spawnHorse() {
        
        if(this.horse != null) return;
        if(CooldownManager.has(this.player.getName(), "horse")) {
            this.player.sendMessage(KitPvP.getInstance().getMessage("wait", CooldownManager.getLeft(this.player.getName(), "horse")));
            return;
        }
        
        this.horse = (Horse) this.player.getWorld().spawnCreature(this.player.getLocation(), EntityType.HORSE);
        this.horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        this.horse.setColor(Horse.Color.BLACK);
        this.horse.setTamed(true);
        this.horse.setOwner(this.player);
        this.horse.setVariant(Horse.Variant.HORSE);
        this.horse.setPassenger(this.player);
        this.horseTimer = new Timer(KitPvP.getInstance().getConfiguration().getInt("warrior-horse-alive"));
        this.horseTimer.start(new onTime() {
            @Override
            public void onTime(int time) {
                if(horse == null || horse.isDead()) {
                    horseTimer.stop();
                    horseTimer = null;
                    return;
                }
                
                if(time == 0) deleteHorse();
                
            }
        });
        
        
    }
    
    public void deleteHorse() {
        if(this.horse != null) {
            if(!this.horse.isDead()) this.horse.remove();
            this.horse = null;
            if(this.horseTimer != null) {
                if(this.horseTimer.isRunning()) {
                    this.horseTimer.stop();
                }
                this.horseTimer = null;
            }
            CooldownManager.create(this.player.getName(), "horse", KitPvP.getInstance().getConfiguration().getInt("warrior-horse-restore"));
        }
    }
    
    public boolean inGame() {
        return this.ingame;
    }
    
    public void setInGame(boolean bln) {
        this.ingame = bln;
    }
    
    public boolean isTopBoardActive() {
        return this.topBoardActive;
    }
    
    public void setTopBoardStatus(boolean bln) {
        this.topBoardActive = bln;
    } 
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getNinjaArmorLevel() {
        return this.ninjaArmorLevel;
    }
    
    public boolean[] getData() {
        return this.data;
    }
    
    public void setData(boolean[] data) {
        this.data = data;
    }
    
    public long getBalance() {
        return this.balance;
    }
    
    public void addToBalance(long ammount) {
        this.balance += ammount;
        this.updateBoard();
        KitPvP.getInstance().getData().set("data." + this.player.getName(), this);
    }
    
    public void removeFromBalance(int ammount) {
        this.balance -= ammount;
        this.updateBoard();
        KitPvP.getInstance().getData().set("data." + this.player.getName(), this);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap();
        map.put("ninjaArmorLevel", this.ninjaArmorLevel);
        map.put("activeSkills", this.activeSkills);
        map.put("kit", this.kit.toString());
        map.put("balance", this.balance);
        map.put("data", this.data);
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        map.put("name", this.playerName);
        return map;
    }
    
    public static User deserialize(Map<String, Object> args) {
        return new User(args);
    }
    
}