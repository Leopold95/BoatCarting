package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class PlayerJoinListener implements Listener {
    private BoatCarting plugin;

    public PlayerJoinListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoined(PlayerJoinEvent event){
        Player p = event.getPlayer();
        PersistentDataContainer pdc = p.getPersistentDataContainer();

        if(pdc.has(plugin.getKeys().CANT_MOVE))
            pdc.remove(plugin.getKeys().CANT_MOVE);
    }
}
