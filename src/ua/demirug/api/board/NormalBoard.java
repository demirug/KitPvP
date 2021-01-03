package ua.demirug.api.board;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class NormalBoard implements BoardImpl {
    
    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective objective;
    private HashMap<Integer, String> lines = new HashMap();
    
    
    public NormalBoard() {
        this("MCBoard");
    }

    public NormalBoard(String title) {
        this.objective = this.scoreboard.registerNewObjective(title, "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.setTitle(title);
    }

    @Override
    public void setTitle(String title) {
        this.objective.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)title));
    }

    @Override
    public void clearBoard() {
        for (Integer line : this.lines.keySet()) {
            this.scoreboard.resetScores(this.lines.get(line));
        }
    }

    @Override
    public void setLine(int line, String text) {
        if (this.lines.containsKey(line)) {
            this.scoreboard.resetScores(this.lines.get(line));
        }
        this.objective.getScore(ChatColor.translateAlternateColorCodes('&', text)).setScore(line);
        this.lines.put(line, ChatColor.translateAlternateColorCodes('&', text));
    }

    @Override
    public void setText(List<String> text) {
        this.clearBoard();
        int i = text.size();
        for (String str : text) {
            this.setLine(i, str);
            --i;
        }
    }

    @Override
    public void sendBoard(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    @Override
    public void removeBoard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @Override
    public void updateBoard() {}
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
}

