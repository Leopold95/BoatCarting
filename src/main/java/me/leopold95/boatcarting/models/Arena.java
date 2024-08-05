package me.leopold95.boatcarting.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

/**
 * Класс-модель арены
 */
@Getter
@AllArgsConstructor
public class Arena {
    private String specialId;
    private int numericId;
    private ArenaState state;
    private Location lobbySpawn;

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
