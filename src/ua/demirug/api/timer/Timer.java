package ua.demirug.api.timer;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ua.demirug.kitpvp.KitPvP;

public class Timer {

    private int time;
    private BukkitTask task;
    
    public Timer(int time) {
        this.time = time;
    }
    
    public void start(onTime inter) {
        
        task = new BukkitRunnable() {
            @Override
            public void run() {
                inter.onTime(time--);
                if(time == -1) stop();
            }
            
        }.runTaskTimer(KitPvP.getInstance(), 0, 20);
        
    }
    
    public void stop() {
        if(this.task != null) {
            if(this.task.isSync()) this.task.cancel();
            this.task = null;
        }
    }
    
    public boolean isRunning() {
        if(this.task != null && this.task.isSync()) return true;
        return false;
    }
    
}
