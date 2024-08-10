package me.leopold95.boatcarting.core;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.NamespacedKey;

public class Keys {
    private BoatCarting plugin;

    public NamespacedKey IS_PLAYER_CARTING;
    public NamespacedKey CANT_MOVE;

    public NamespacedKey PLAYER_BOAT;
    public NamespacedKey CLICKABLE_BOAT;

    public Keys(BoatCarting plugin){
        this.plugin = plugin;

        IS_PLAYER_CARTING = new NamespacedKey(plugin, "IS_PLAYER_CARTING");
        CANT_MOVE = new NamespacedKey(plugin, "CANT_MOVE");

        PLAYER_BOAT = new NamespacedKey(plugin, "PLAYER_BOAT");
        CLICKABLE_BOAT = new NamespacedKey(plugin, "CLICKABLE_BOAT");
    }
}
