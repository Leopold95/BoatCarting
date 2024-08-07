package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class PlayerMoveListener implements Listener {
    private BoatCarting plugin;

    public PlayerMoveListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
//        if(!event.getPlayer().getPersistentDataContainer().has(plugin.getKeys().IS_PLAYER_CARTING))
//            return

        Player player = event.getPlayer();

        if(player.getPersistentDataContainer().has(plugin.getKeys().CANT_MOVE)) {
            event.setCancelled(true);
            return;
        }

        if(player.getVehicle() == null || !(player.getVehicle() instanceof Boat))
            return;

        Block underPlayerBlock = player.getLocation().subtract(0, 0.2, 0).getBlock();

        Boat boat = (Boat) player.getVehicle();

        Vector boatDirection = boat.getLocation().getDirection();
        Block perBoatBlock = boat.getLocation().add(boatDirection.multiply(1.1)).getBlock();

        plugin.getEngine().checkJump(boat, perBoatBlock.getType(), underPlayerBlock.getType());
    }
}
