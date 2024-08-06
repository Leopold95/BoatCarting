package me.leopold95.boatcarting.engine;

import lombok.Getter;
import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.core.ConfigArenas;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArenaManager {
    private BoatCarting plugin;

    @Getter
    private List<Arena> arenas;

    public ArenaManager(BoatCarting plugin) {
        this.plugin = plugin;
        arenas = new ArrayList<>();
        loadArenas();
    }

    /**
     * Найти первую пустую арену
     * @return опцанальная арена
     */
    public Optional<Arena> findEmpty(){
        return arenas.stream().filter(a -> a.getState() == ArenaState.EMPTY).findFirst();
    }

    /**
     * НАйти аренц по ид
     * @param id ид для поиска
     * @return опцианальная арена
     */
    public Optional<Arena> getEmptyByNumeric(int id){
        return arenas.stream().filter(a -> a.getNumericId() == id && a.getState() == ArenaState.EMPTY).findFirst();
    }

    /**
     * Предзагрузка списка арен
     */
    private void loadArenas(){
        ConfigurationSection section = ConfigArenas.getArenasList();
        if(section == null)
            return;

        String arenaWorldString = ConfigArenas.getArenasWorldName();
        if(arenaWorldString == null)
            return;

        //мир арен
        World arenasWorld = Bukkit.getWorld(arenaWorldString);
        if(arenasWorld == null){
            plugin.getLogger().warning(Config.getMessage("arena-loading.no-world"));
            return;
        }


        for(String key: section.getKeys(true)){
            //проверка уникального ключа
            String specialKey = section.getString(key);
            if(arenas.stream().anyMatch(a -> a.getSpecialId().equals(key))){
                plugin.getLogger().warning(Config.getMessage("arena-loading.same-special"));
                continue;
            }

            //проверка уникального цифрового ключа
            int numericId = section.getInt(key + ".numeric-id");
            if(arenas.stream().anyMatch(a -> a.getNumericId() == numericId)){
                plugin.getLogger().warning(Config.getMessage("arena-loading.same-numeric"));
                continue;
            }

            //инит спавна лобби
            double x = section.getDouble(key + ".lobbySpawn.x");
            double y = section.getDouble(key + ".lobbySpawn.y");
            double z = section.getDouble(key + ".lobbySpawn.z");
            Location lobbySpawn = new Location(arenasWorld, x, y, z);

            try {
                arenas.add(new Arena(
                    specialKey,
                    numericId,
                    ArenaState.EMPTY,
                    lobbySpawn,
                    new ArrayList<>()
                ));
            }
            catch (Exception exp){
                plugin.getLogger().warning(Config.getMessage("arena-loading.unexpected")
                        .replace("{exp}", exp.getMessage()));
            }
        }
    }
}
