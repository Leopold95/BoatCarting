package me.leopold95.boatcarting.core;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigArenas {
    private static File file;
    private static FileConfiguration configuration;

    public static String getArenasWorldName() {
        String name = configuration.getString("arenas-world");
        if(name == null || name.isEmpty())
            return null;

        return name;
    }

    public static ConfigurationSection getArenasList() {
        ConfigurationSection sec = configuration.getConfigurationSection("arenas");
        if(sec == null || sec.getKeys(false).isEmpty()){
            return null;
        }

        return sec;
    }

    public static void register(Plugin plugin, String fineName){
        file = new File(plugin.getDataFolder(), fineName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fineName, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
