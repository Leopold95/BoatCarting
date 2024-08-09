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
import org.bukkit.entity.Player;

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

    public Optional<Arena> getByPlayer(Player player){
        return arenas.stream().filter(a -> a.getPlayers().contains(player)).findFirst();
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
        return arenas.stream().filter(a -> a.getNumericId() == id).findFirst();
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

            //инит позиций спавнов
            ConfigurationSection spawnsSection = section.getConfigurationSection(key + ".spawn-points.poses");
            if(spawnsSection == null || spawnsSection.getKeys(false).isEmpty()){
                plugin.getLogger().warning(Config.getMessage("arena-loading.no-spawns-sections"));
                return;
            }

            List<Location> spawns = new ArrayList<>();
            double sY = section.getDouble(key + ".spawn-points.y");
            int yaw = section.getInt(key + ".spawn-points.yaw");

            for(String line: spawnsSection.getKeys(true)){
                try {
                    String[] positions = spawnsSection.getString(line).split(":");
                    double sX = Double.parseDouble(positions[0]);
                    double sZ = Double.parseDouble(positions[1]);
                    Location lcoation = new Location(arenasWorld, sX, sY, sZ);
                    lcoation.setYaw(yaw);
                    spawns.add(lcoation);

                }catch (Exception exp){
                    plugin.getLogger().warning(
                            Config.getMessage("arena-loading.error-parsing-spawn-points").replace("{exp}", exp.getMessage()));
                }
            }

            plugin.getLogger().warning(section.getString(key + ".region"));

            try {
                arenas.add(new Arena(
                    specialKey,
                    numericId,
                    section.getString(key + ".region"),
                    ArenaState.EMPTY,
                    lobbySpawn,
                    new ArrayList<>(),
                    spawns,
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
