package me.leopold95.boatcarting.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Set;
import java.util.stream.Collectors;

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

        if(pdc.has(plugin.getKeys().IS_PLAYER_CARTING))
            pdc.remove(plugin.getKeys().IS_PLAYER_CARTING);

        RegionManager regionManager = plugin.getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
        if (regionManager == null)
            return;

        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
        Set<String> fromNames = regions.getRegions().stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
        boolean onArena = plugin.getEngine().getArenaRegions().stream().anyMatch(fromNames::contains);

        if(onArena)
            p.teleport(plugin.getEngine().getAfterGameSpawn());
    }
}
