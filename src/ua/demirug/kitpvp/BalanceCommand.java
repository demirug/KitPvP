package ua.demirug.kitpvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
    
        if(cs instanceof Player) {
            User user = KitPvP.getInstance().getUsers().get((Player)cs);
            if(user != null) {
                cs.sendMessage(KitPvP.getInstance().getMessage("balance", user.getBalance()));
            }
        }
        return false;
        
    }
    
}
