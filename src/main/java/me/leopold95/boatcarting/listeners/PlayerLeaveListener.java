package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.models.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerLeaveListener implements Listener {
    private BoatCarting plugin;

    public PlayerLeaveListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if (player.getPersistentDataContainer().has(plugin.getKeys().IS_PLAYER_CARTING)){
            Optional<Arena> optArena = plugin.getEngine().getArenaManager().getByPlayer(player);
            optArena.ifPresent(arena -> {
                arena.getPlayers().remove(player);
            });
        }
    }
}
