package me.leopold95.boatcarting.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerMoveListener implements Listener {
    private BoatCarting plugin;

    public PlayerMoveListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        //если игрок отмечен "заблокировано движение" - блокируем его движение
        if(player.getPersistentDataContainer().has(plugin.getKeys().CANT_MOVE)) {
            event.setCancelled(true);
            return;
        }

        //если игрок не участник гонки - скип
        if(!event.getPlayer().getPersistentDataContainer().has(plugin.getKeys().IS_PLAYER_CARTING))
            return;

        checkLeftEvent(player, event.getFrom(), event.getTo());

        tryBoatJump(player);
    }


    /**
     * ОБработка прыжков на лодке
     * @param player игрок
     */
    private void tryBoatJump(Player player){
        if(player.getVehicle() == null || !(player.getVehicle() instanceof Boat))
            return;

        Block underPlayerBlock = player.getLocation().subtract(0, 0.2, 0).getBlock();

        Boat boat = (Boat) player.getVehicle();

        Vector boatDirection = boat.getLocation().getDirection();
        Block perBoatBlock = boat.getLocation().add(boatDirection.multiply(1.1)).getBlock();

        plugin.getEngine().checkJump(boat, perBoatBlock.getType(), underPlayerBlock.getType());
    }

    /**
     * Проверка на то, что игрок покинул заезд не командой
     */
    private void checkLeftEvent(Player player, Location from, Location to){
        RegionManager regions = plugin.getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (regions == null) return;

        ApplicableRegionSet fromRegions = regions.getApplicableRegions(BukkitAdapter.asBlockVector(from));
        ApplicableRegionSet toRegions = regions.getApplicableRegions(BukkitAdapter.asBlockVector(to));

        Set<String> fromNames = fromRegions.getRegions().stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
        Set<String> toNames = toRegions.getRegions().stream().map(ProtectedRegion::getId).collect(Collectors.toSet());

        boolean wasOnArena = plugin.getEngine().getArenaRegions().stream().anyMatch(fromNames::contains);
        boolean nowOnArena = plugin.getEngine().getArenaRegions().stream().anyMatch(toNames::contains);

        if(wasOnArena && !nowOnArena){
            plugin.getEngine().unexpectedArenaLeaving(player);
            return;
        }

        boolean canWin = plugin.getEngine().getFinishes().stream().anyMatch(toNames::contains);
        if(canWin){
            plugin.getEngine().informWinner(player);
        }

    }

    /**
     * Проверка на то, что игрок победи
     * @param player игрок
     */
    private void checkWinnable(Player player){

    }
}
