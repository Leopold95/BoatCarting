package me.leopold95.boatcarting.core;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.NamespacedKey;

public class Keys {
    private BoatCarting plugin;

    public NamespacedKey IS_PLAYER_CARTING;

    public Keys(BoatCarting plugin){
        this.plugin = plugin;

        IS_PLAYER_CARTING = new NamespacedKey(plugin, "IS_PLAYER_CARTING");
    }


}
