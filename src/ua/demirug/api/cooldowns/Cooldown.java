package ua.demirug.api.cooldowns;

public class Cooldown {
    private double time;
    private long starttime;

    public Cooldown(double time) {
        this.time = time;
        this.starttime = System.currentTimeMillis();
    }

    public boolean isLeft() {
        return this.getLeft() <= 0;
    }

    public int getLeft() {
        return (int)((this.time) - (System.currentTimeMillis() - this.starttime) / 1000L);
    }
}

