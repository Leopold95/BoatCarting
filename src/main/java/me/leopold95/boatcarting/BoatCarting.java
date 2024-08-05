package me.leopold95.boatcarting;

import lombok.Getter;
import me.leopold95.boatcarting.commands.BoatCartingCommand;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.core.Keys;
import me.leopold95.boatcarting.engine.Engine;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.listeners.PlayerMoveListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoatCarting extends JavaPlugin {
    @Getter
    private Keys keys;

    @Getter
    private Engine engine;

    @Override
    public void onEnable() {
        Config.register(this);

        keys = new Keys(this);
        engine = new Engine(this);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        getCommand(Commands.BOAT_CARTING).setExecutor(new BoatCartingCommand());
        getCommand(Commands.BOAT_CARTING).setTabCompleter(new BoatCartingCommand());
    }
}
