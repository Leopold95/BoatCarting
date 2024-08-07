package me.leopold95.boatcarting.engine.tasks;

import me.leopold95.boatcarting.BoatCarting;
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
            //plugin.getLogger().warning("game ended");
            engine.endGameWithNoWinners(arena);
            cancel();
            return;
        }

        if(secondsPassed == playerWaitTime && arena.getState() == ArenaState.PLAYERS_WAITING){
            if(arena.getPlayers().size() < arena.getSpawnPoints().size()){
                engine.endGameWithNoWinners(arena);
                cancel();
                return;
            }

            engine.startGame(arena);
        }

        if(arena.getPlayers().isEmpty()){
            cancel();
            return;
        }

        plugin.getLogger().warning(String.valueOf(secondsPassed));
        secondsPassed++;
    }
}
