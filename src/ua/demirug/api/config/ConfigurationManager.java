package ua.demirug.api.config;

import org.bukkit.plugin.Plugin;

public class ConfigurationManager {

    public static ConfigurationInstaller register(Plugin plugin) {
        return new ConfigurationInstaller(plugin);
    }
}

