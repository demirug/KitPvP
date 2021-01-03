package ua.demirug.kitpvp;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] args) {
        if(cs.hasPermission("kitpvp.setup") && cs instanceof Player) {
            Player player = (Player) cs;
            if(args.length == 0) {
                player.sendMessage("/setup setminpos - установка мин позиции");
                player.sendMessage("/setup setlobby - установка лобби");
                player.sendMessage("/setup addemerald - Добавить точку спавна монеток");
                return false;
            }
            
            switch(args[0].toLowerCase()) {
                case "addemerald":
                    
                    List<Location> emeralds = (List<Location>) KitPvP.getInstance().getConfiguration().getList("emeralds");
                    emeralds.add(player.getLocation());
                    KitPvP.getInstance().getConfiguration().set("emeralds", emeralds);
                    KitPvP.getInstance().getConfigInstaller().save(KitPvP.getInstance().getConfiguration());
                    player.sendMessage("Точка появления монеток #" + emeralds.size() + " успешно создана");
                    
                    break;
                case "setminpos":
                    KitPvP.getInstance().getConfiguration().set("give-kit-y", player.getLocation().getY());
                    KitPvP.getInstance().getConfigInstaller().save(KitPvP.getInstance().getConfiguration());
                    player.sendMessage("Точка минимальной позиции установлена");
                    break;
                    
                case "setlobby":
                    KitPvP.getInstance().getConfiguration().set("lobby", player.getLocation());
                    KitPvP.getInstance().getConfigInstaller().save(KitPvP.getInstance().getConfiguration());
                    player.sendMessage("Точка лобби установлена");
                    break;
                default:
                    player.sendMessage("/setup setminpos - установка мин позиции");
                    player.sendMessage("/setup setlobby - установка лобби");
                    player.sendMessage("/setup addemerald - Добавить точку спавна монеток");
                    break;
            }
            
        } else return false;
        return false;
    }

}
