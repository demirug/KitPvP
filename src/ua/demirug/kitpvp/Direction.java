package ua.demirug.kitpvp;

import org.bukkit.entity.Player;
import ua.demirug.api.direction.DirectionBuilder;

public enum Direction {
  FRONT {
    @Override
    public void setDirection(Player player) {
      player.setCompassTarget(new DirectionBuilder(player.getLocation()).getAtFront(50));
    }
  },
  
  BEHIND {
    @Override
    public void setDirection(Player player) {
      player.setCompassTarget(new DirectionBuilder(player.getLocation()).getAtBehind(50));
    }
  },
  
  LEFT {
      @Override
      public void setDirection(Player player) {
          player.setCompassTarget(new DirectionBuilder(player.getLocation()).getAtLeft(50));
      }
  },
  
  RIGHT {
      @Override
      public void setDirection(Player player) {
          player.setCompassTarget(new DirectionBuilder(player.getLocation()).getAtRight(50));
      }
  };

  public abstract void setDirection(Player player);
  
}
