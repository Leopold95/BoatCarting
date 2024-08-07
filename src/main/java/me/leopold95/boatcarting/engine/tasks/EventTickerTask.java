package me.leopold95.boatcarting.engine.tasks;

import me.leopold95.boatcarting.abstrction.RepeatingTask;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.engine.Engine;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import org.bukkit.plugin.Plugin;

public class EventTickerTask extends RepeatingTask {
    private Arena arena;
    private Engine engine;

    private int playerWaitTime;
    private int maxGameTime;
    private int selfDestroyTime;

    private int secondsPassed;

    public EventTickerTask(Plugin plugin, Arena arena, Engine engine) {
        super(plugin, 0, 20);
        this.arena = arena;
        this.engine = engine;

        playerWaitTime = Config.getInt("times.player-wait");
        maxGameTime = Config.getInt("times.max-game-duration");

        selfDestroyTime = playerWaitTime + maxGameTime;
    }

    @Override
    public void run() {
        if(secondsPassed == selfDestroyTime){
            cancel();
            return;
        }

        if(secondsPassed == playerWaitTime && arena.getState() == ArenaState.PLAYERS_WAITING){
            engine.startGame(arena);
        }


        secondsPassed++;
    }
}
