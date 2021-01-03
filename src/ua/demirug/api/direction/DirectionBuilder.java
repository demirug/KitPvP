package ua.demirug.api.direction;

import org.bukkit.Location;

public class DirectionBuilder {

    private Location location;
    
    public DirectionBuilder(Location location) {
        this.location = location;
    }
    
    public Location getAtFront(int front) {
        return build(new int[][]{{-front, 0, 0},{0, 0, -front},{front, 0, 0}, {0, 0, front}});
    }
    
    public Location getAtBehind(int behind) {
        return build(new int[][]{{behind, 0, 0},{0, 0, behind},{-behind, 0, 0}, {0, 0, -behind}});
    }
    
    public Location getAtLeft(int left) {
        return build(new int[][]{{0, 0, left},{-left, 0, 0},{0, 0, -left}, {left, 0, 0}});
    }    
    
    public Location getAtRight(int right) {
        return  build(new int[][]{{0, 0, -right},{right, 0, 0},{0, 0, right}, {-right, 0, 0}});
    } 
    
    private Location build(int[][] elements) {
        
        Location loc = this.location.clone();
        int direction = (int)this.location.getYaw();
        
        if(direction < 0) direction += 360;
        direction = (direction + 45) / 90;
        
        
        switch(direction) {
            
            case 1:
                loc.add(elements[0][0], elements[0][1], elements[0][2]);
                break;
                
            case 2:
                loc.add(elements[1][0], elements[1][1], elements[1][2]);
                break;
                
            case 3:
                loc.add(elements[2][0], elements[2][1], elements[2][2]);
                break;
                
            case 4:
            case 0:
                loc.add(elements[3][0], elements[3][1], elements[3][2]);
                break;
        }
        
        return loc;
        
    }
    
}
