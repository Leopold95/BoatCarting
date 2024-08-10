package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleLeaveListener implements Listener {
    private BoatCarting plugin;

    public VehicleLeaveListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onBoatLeave(VehicleExitEvent event){
        if(!(event.getExited() instanceof Player player))
            return;

        if(!player.getPersistentDataContainer().has(plugin.getKeys().IS_PLAYER_CARTING))
            return;

        if (!(event.getVehicle() instanceof Boat))
            return;

        event.setCancelled(true);
    }
}
