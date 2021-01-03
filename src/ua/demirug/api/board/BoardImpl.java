package ua.demirug.api.board;

import java.util.List;
import org.bukkit.entity.Player;

public interface BoardImpl {

    public void setTitle(String var1);

    public void setLine(int var1, String var2);

    public void setText(List<String> var1);

    public void clearBoard();

    public void updateBoard();

    public void sendBoard(Player var1);

    public void removeBoard(Player var1);
}

