package me.leopold95.boatcarting;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import lombok.Getter;
import me.leopold95.boatcarting.commands.BoatCartingCommand;
import me.leopold95.boatcarting.core.ButtonFormats;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.core.ConfigArenas;
import me.leopold95.boatcarting.core.Keys;
import me.leopold95.boatcarting.engine.Engine;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoatCarting extends JavaPlugin {
    public static BoatCarting plugin;

    @Getter
    private Keys keys;

    @Getter
    private Engine engine;

    @Getter
    private RegionContainer regionContainer;

    @Getter
    private ButtonFormats buttonFormats;

    @Override
    public void onEnable() {
        plugin = this;

        Config.register(this);
        ConfigArenas.register(this, "arenas.yml");

        keys = new Keys(this);
        buttonFormats = new ButtonFormats(this);
        engine = new Engine(this);
        regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new VehicleLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getCommand(Commands.BOAT_CARTING).setExecutor(new BoatCartingCommand(this));
        getCommand(Commands.BOAT_CARTING).setTabCompleter(new BoatCartingCommand(this));
    }
}
