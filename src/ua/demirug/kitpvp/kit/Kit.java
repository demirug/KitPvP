package ua.demirug.kitpvp.kit;

import ua.demirug.kitpvp.User;

public interface Kit {
    
    public void giveKit(User user);
    
    public boolean isActiveSkill(User user, int skillID);
    
    public boolean hasSkill(User user, int skillID);
    
    public String getName();
    
    
    
}
