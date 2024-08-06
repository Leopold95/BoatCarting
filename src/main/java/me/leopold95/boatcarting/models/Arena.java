package me.leopold95.boatcarting.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.leopold95.boatcarting.core.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Класс-модель арены
 */
@Getter
@AllArgsConstructor
public class Arena {
    private String specialId;
    private int numericId;
    @Setter
    private ArenaState state;
    private Location lobbySpawn;
    private List<Player> players;

    /**
     * Закрыть арену на ожидание игроков
     */
    public void setGameWaiting(){
        state = ArenaState.PLAYERS_WAITING;
    }

    /**
     * ЗАкрыть арену на игру
     */
    public void setGameActive(){
        state = ArenaState.ACTIVE_GAME;
    }

    /**
     * Открыть арену
     */
    public void open(){
        state = ArenaState.EMPTY;
    }
}
