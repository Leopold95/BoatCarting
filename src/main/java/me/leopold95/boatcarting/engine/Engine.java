package me.leopold95.boatcarting.engine;

import lombok.Getter;
import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Engine {
    private BoatCarting plugin;

    @Getter
    private HashMap<Material, Double> toTopMap;
    @Getter
    private HashMap<Material, Double> toForwardMap;

    public Engine(BoatCarting plugin) {
        this.plugin = plugin;

        toTopMap = new HashMap<>();
        toForwardMap = new HashMap<>();

        loadTopMap();
        loadForwardMap();
    }

    /**
     * Запуск аодбора игроков для начала игры
     */
    public void startGame(Player caller){

    }

    public void checkJump(Boat boat, Material oppositeMaterial, Material underMaterial){
        if(toTopMap.containsKey(oppositeMaterial)){
            addTopVelocity(boat, toTopMap.get(oppositeMaterial));
        }

        if(toForwardMap.containsKey(underMaterial)){
            addForwardVelocity(boat, toForwardMap.get(underMaterial));
        }
    }

    public void addTopVelocity(Boat boat, double mod){
        Vector v = boat.getVelocity();
        boat.setVelocity(new Vector(v.getX(), v.getY() + mod, v.getZ()));
    }

    public void addForwardVelocity(Boat boat, double mod){
        Vector v = boat.getLocation().getDirection();
        boat.setVelocity(v.multiply(mod));
    }



    private void loadTopMap(){
        ConfigurationSection section = Config.getSection("to-top-blocks");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(Config.getMessage("bad-to-top-section"));
            return;
        }

        for(String key: section.getKeys(true)){
            Material material = Material.getMaterial(key);
            if(material == null){
                plugin.getLogger().warning(Config.getMessage("baв-to-top-material").replace("{mat}", key));
                continue;
            }

            double modifier = section.getDouble(key);
            if(modifier == 0){
                plugin.getLogger().warning(Config.getMessage("bat-to-top-mod").replace("{mat}", key));
                continue;
            }

            toTopMap.put(material, modifier);
        }
    }

    private void loadForwardMap(){
        ConfigurationSection section = Config.getSection("to-forward-blocks");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(Config.getMessage("bad-to-forward-section"));
            return;
        }

        for(String key: section.getKeys(true)){
            Material material = Material.getMaterial(key);
            if(material == null){
                plugin.getLogger().warning(Config.getMessage("bad-to-forward-material").replace("{mat}", key));
                continue;
            }

            double modifier = section.getDouble(key);
            if(modifier == 0){
                plugin.getLogger().warning(Config.getMessage("bat-to-forward-mod").replace("{mat}", key));
                continue;
            }

            toForwardMap.put(material, modifier);
        }
    }
}
