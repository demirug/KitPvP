package ua.demirug.api.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigurationInstaller {

    private Plugin plugin;
    private HashMap<FileConfiguration, String> files;

    public ConfigurationInstaller(Plugin plugin) {
        this.plugin = plugin;
        this.files = new HashMap();
    }

    public FileConfiguration get(String name) {
        YamlConfiguration fcg = null;
        File f = new File(this.plugin.getDataFolder(), name);
        if (this.plugin.getResource(name) == null) {
            try {
                fcg = YamlConfiguration.loadConfiguration((File)f);
                fcg.save(new File(this.plugin.getDataFolder(), name));
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            if (!f.exists()) {
                this.plugin.saveResource(name, false);
            }
            fcg = YamlConfiguration.loadConfiguration((File)f);
        }
        this.files.put(fcg, name);
        return fcg;
    }

    public FileConfiguration save(FileConfiguration config) {
        String name = this.files.get(config);
        if (name == null) {
            return null;
        }
        try {
            config.save(new File(this.plugin.getDataFolder(), name));
        }
        catch (IOException e) {
           e.printStackTrace();
        }
        return config;
    }
}

