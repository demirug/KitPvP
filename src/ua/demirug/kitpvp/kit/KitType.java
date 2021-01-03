package ua.demirug.kitpvp.kit;

public enum KitType {
    
    WARRIOR(new WarriorKit()),
    ARCHER(new ArcherKit()),
    NINJA(new NinjaKit());
    
    private Kit type;
    
    private KitType(Kit type) {
    
        this.type = type;
        
    }
     
    public Kit getType() {
        return type;
    }
    
}
