package me.leopold95.boatcarting.engine.tasks;

import me.leopold95.boatcarting.abstrction.RepeatingTask;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.engine.Engine;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import org.bukkit.plugin.Plugin;

public class EventTickerTask extends RepeatingTask {
    private Plugin plugin;
    private Arena arena;
    private Engine engine;

    private int playerWaitTime;
    private int maxGameTime;
    private int selfDestroyTime;

    private int secondsPassed;
    private int requredSeconds;

    public EventTickerTask(Plugin plugin, Arena arena, Engine engine) {
        super(plugin, 0, 20);
        this.plugin = plugin;
        this.arena = arena;
        this.engine = engine;

        playerWaitTime = Config.getInt("times.player-wait");
        maxGameTime = Config.getInt("times.max-game-duration");

        selfDestroyTime = playerWaitTime + maxGameTime;

        requredSeconds = playerWaitTime;
    }

    @Override
    public void run() {
        if(secondsPassed == maxGameTime){
            engine.stopGame(arena, Config.getMessage("stop.end-by-timer"));
            cancel();
            return;
        }

        //начало игры по таймеру
        if(secondsPassed == playerWaitTime && arena.getState() == ArenaState.PLAYERS_WAITING){

            //звершение события, если игроков недостаточно
            if(arena.getPlayers().size() < arena.getSpawnPoints().size()){
                engine.stopGame(arena, Config.getMessage("stop.was-not-started"));
                cancel();
                return;
            }

            engine.startGame(arena);
        }

        //завершение события, если арена пуста
        if(arena.getPlayers().isEmpty()){
            engine.clearArena(arena);
            cancel();
            return;
        }

        //plugin.getLogger().warning(String.valueOf(secondsPassed));
        secondsPassed++;
    }
}
