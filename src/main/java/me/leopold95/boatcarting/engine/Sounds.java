package me.leopold95.boatcarting.engine;

import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {
    private BoatCarting plugin;

    public Sounds(BoatCarting plugin) {
        this.plugin = plugin;
    }


    public void playTo(Player player, String config){
        try {
            Sound sound = Sound.valueOf(Config.getString(config));
            int volume = Config.getInt("sounds.volume");
            player.playSound(player, sound, volume, 1);
        }
        catch (Exception exp){
            String message = Config.getMessage("error-while-sound")
                    .replace("{cfg}", config)
                    .replace("{exp}", exp.getMessage());
            plugin.getLogger().warning(message);
        }
    }
}
