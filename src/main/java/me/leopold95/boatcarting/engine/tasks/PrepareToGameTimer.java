package me.leopold95.boatcarting.engine.tasks;

import me.leopold95.boatcarting.abstrction.RepeatingTask;
import org.bukkit.plugin.Plugin;

public class PrepareToGameTimer extends RepeatingTask {
    private int prepareTime;
    private int timePassed = 0;

    public PrepareToGameTimer(Plugin plugin, int prepareTime) {
        super(plugin, 0, 20);
        this.prepareTime = prepareTime;
    }

    @Override
    public void run() {

    }
}
