package me.leopold95.boatcarting.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.leopold95.boatcarting.core.Config;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Класс-модель арены
 */
@Getter
@AllArgsConstructor
@ToString
public class Arena {
    private String specialId;
    private int numericId;
    @Setter
    private ArenaState state;
    private Location lobbySpawn;
    private List<Player> players;
    private List<Location> spawnPoints;
    private List<Player> winners;

    /**
     * Теоепортирует всех игроков по местом спавна
     */
    public void teleportPlayersToPositions(){
        for(int i = 0; i < players.size(); i++){
            Player player = players.get(i);
            player.teleport(spawnPoints.get(i).toCenterLocation());
            Boat boat = (Boat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);
            boat.addPassenger(player);
        }
    }

    /**
     * Блокирует передвидение всех игроков арены
     * @param blockKey ключ блока
     */
    public void blockPlayerMovement(NamespacedKey blockKey){
        for(Player player: players){
            if(player == null){
                continue;
            }
            player.getPersistentDataContainer().set(blockKey, PersistentDataType.INTEGER, 1);
        }
    }

    /**
     * Заблокирует передвижение всех игроков арены.
     * @param blockKey ключ блокировки
     */
    public void unlockPlayerMovement(NamespacedKey blockKey){
        for(Player player: players){
            if(player == null){
                continue;
            }

            player.getPersistentDataContainer().remove(blockKey);
        }
    }
}
