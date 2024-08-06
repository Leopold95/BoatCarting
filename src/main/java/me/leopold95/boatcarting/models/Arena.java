package me.leopold95.boatcarting.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.leopold95.boatcarting.core.Config;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Класс-модель арены
 */
@Getter
@AllArgsConstructor
public class Arena {
    private String specialId;
    private int numericId;
    private int maxPlayers;
    @Setter
    private ArenaState state;
    private Location lobbySpawn;
    private List<Player> players;

    public void teleportPlayersToPositions(){

    }

    public void blockPlayerMovement(NamespacedKey blockKey){
        for(Player player: players){
            if(player == null){
                continue;
            }
            player.getPersistentDataContainer().set(blockKey, PersistentDataType.INTEGER, 1);
        }
    }

    public void unlockPlayerMovement(NamespacedKey blockKey){
        for(Player player: players){
            if(player == null){
                continue;
            }

            player.getPersistentDataContainer().remove(blockKey);
        }
    }
}
