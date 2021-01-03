package ua.demirug.api.cooldowns;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class CooldownManager {

    private static Table<String, String, Cooldown> cooldowns = HashBasedTable.create();

    public static void create(String key1, String key2, int time) {
        cooldowns.put(key1, key2, new Cooldown(time));
    }

    public static int getLeft(String key1, String key2) {
        if (!cooldowns.contains(key1, key2)) {
            return 0;
        }
        Cooldown cld = (Cooldown)cooldowns.get(key1, key2);
        if (cld.getLeft() < 0) {
            return 0;
        }
        return cld.getLeft();
    }

    public static boolean has(String key1, String key2) {
        if (!cooldowns.contains(key1, key2)) {
            return false;
        }
        Cooldown cld = (Cooldown)cooldowns.get(key1, key2);
        if (cld.isLeft()) {
            CooldownManager.remove(key1, key2);
            return false;
        }
        return true;
    }

    public static void remove(String key1, String key2) {
        cooldowns.remove(key1, key2);
    }

    public static Table<String, String, Cooldown> getCooldowns() {
        return cooldowns;
    }
}

