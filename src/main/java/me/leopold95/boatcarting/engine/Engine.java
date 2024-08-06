package me.leopold95.boatcarting.engine;

import lombok.Getter;
import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.abstrction.RepeatingTask;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.engine.tasks.EventTickerTask;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import me.leopold95.boatcarting.models.Event;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Engine {
    private BoatCarting plugin;

    @Getter
    private HashMap<Material, Double> toTopMap;
    @Getter
    private HashMap<Material, Double> toForwardMap;
    @Getter
    private ArenaManager arenaManager;
    @Getter
    private List<Event> activeEvents;
    @Getter
    private Location afterGameSpawn;

    public Engine(BoatCarting plugin) {
        this.plugin = plugin;

        toTopMap = new HashMap<>();
        toForwardMap = new HashMap<>();
        activeEvents = new ArrayList<>();

        arenaManager = new ArenaManager(plugin);

        loadTopMap();
        loadForwardMap();
        loadOther();
    }

    private void loadOther() {
        ConfigurationSection section = Config.getSection("after-game-spawn");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(Config.getMessage("loading.no-after-game-spawn-section"));
            return;
        }

        String spawnWorldName = Config.getString("after-game-spawn.world");
        World spawnWorld = Bukkit.getWorld(spawnWorldName);
        if(spawnWorld == null){
            plugin.getLogger().warning(Config.getMessage("loading.bad-after-game-spawn-world"));
            return;
        }

        double x = Config.getDouble("after-game-spawn.position.x");
        double y = Config.getDouble("after-game-spawn.position.y");
        double z = Config.getDouble("after-game-spawn.position.z");
        afterGameSpawn = new Location(spawnWorld, x, y, z);
    }

    /**
     * Запуск аодбора игроков для начала игры
     */
    public void startPlayersSearching(Player caller, Arena arena){
        String command = Config.getMessage("command-template")
                .replace("{first}", Commands.BOAT_CARTING)
                .replace("{second}", Commands.JOIN_EVENT)
                .replace("{third}", String.valueOf(arena.getNumericId()));

        String message = Config.getMessage("game.started") + command;

        TextComponent text = Component.text(message).clickEvent(ClickEvent.runCommand(command));

        for (Player player: Bukkit.getOnlinePlayers()){
            player.sendMessage(text);
        }

        arena.setState(ArenaState.PLAYERS_WAITING);
        new EventTickerTask(plugin, arena, this);
        arena.getPlayers().add(caller);
        caller.sendMessage(Config.getMessage("game.teleported-to-arena"));
        caller.teleport(arena.getLobbySpawn());
    }

    /**
     * Присоединится к игре
     * @param joiner игрок
     * @param arena арена
     */
    public void joinGame(Player joiner, Arena arena){
        joiner.teleport(arena.getLobbySpawn());
        joiner.sendMessage(Config.getMessage("game.join.ok"));
    }

    /**
     * НАчать игру
     * @param arena арена
     */
    public void startGame(Arena arena){
        arena.setState(ArenaState.ACTIVE_GAME);

        for(Player player: arena.getPlayers()){
            player.sendMessage(Config.getMessage("game.starting-5"));
        }

        arena.blockPlayerMovement(plugin.getKeys().CANT_MOVE);
        arena.teleportPlayersToPositions();

        new RepeatingTask(plugin,0, 20) {
            private int secondsPassed = 0;
            private int prepareTime = Config.getInt("times.game-prepare");

            @Override
            public void run() {
                if(secondsPassed == prepareTime){
                    for(Player player: arena.getPlayers()){
                        player.sendTitlePart(TitlePart.TITLE, Component.text(Config.getMessage("game.starting")));
                        arena.unlockPlayerMovement(plugin.getKeys().CANT_MOVE);
                    }

                    cancel();
                    return;
                }

                for(Player player: arena.getPlayers()){
                    player.sendTitlePart(TitlePart.TITLE, Component.text(Config.getMessage("game.starting-in")
                            .replace("{time}", String.valueOf(secondsPassed))));
                }

                secondsPassed++;
            }
        };
    }

    /**
     * Проверка для того чтобы подпрыгнуть
     * @param boat лодка
     * @param oppositeMaterial материал перед лодкой
     * @param underMaterial материал над лодкой
     */
    public void checkJump(Boat boat, Material oppositeMaterial, Material underMaterial){
        if(toTopMap.containsKey(oppositeMaterial)){
            addTopVelocity(boat, toTopMap.get(oppositeMaterial));
        }

        if(toForwardMap.containsKey(underMaterial)){
            addForwardVelocity(boat, toForwardMap.get(underMaterial));
        }
    }

    public void addTopVelocity(Boat boat, double mod){
        Vector v = boat.getVelocity();
        boat.setVelocity(new Vector(v.getX(), v.getY() + mod, v.getZ()));
    }

    public void addForwardVelocity(Boat boat, double mod){
        Vector v = boat.getLocation().getDirection();
        boat.setVelocity(v.multiply(mod));
    }
    
    private void loadTopMap(){
        ConfigurationSection section = Config.getSection("to-top-blocks");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(Config.getMessage("bad-to-top-section"));
            return;
        }

        for(String key: section.getKeys(true)){
            Material material = Material.getMaterial(key);
            if(material == null){
                plugin.getLogger().warning(Config.getMessage("baв-to-top-material").replace("{mat}", key));
                continue;
            }

            double modifier = section.getDouble(key);
            if(modifier == 0){
                plugin.getLogger().warning(Config.getMessage("bat-to-top-mod").replace("{mat}", key));
                continue;
            }

            toTopMap.put(material, modifier);
        }
    }

    private void loadForwardMap(){
        ConfigurationSection section = Config.getSection("to-forward-blocks");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(Config.getMessage("bad-to-forward-section"));
            return;
        }

        for(String key: section.getKeys(true)){
            Material material = Material.getMaterial(key);
            if(material == null){
                plugin.getLogger().warning(Config.getMessage("bad-to-forward-material").replace("{mat}", key));
                continue;
            }

            double modifier = section.getDouble(key);
            if(modifier == 0){
                plugin.getLogger().warning(Config.getMessage("bat-to-forward-mod").replace("{mat}", key));
                continue;
            }

            toForwardMap.put(material, modifier);
        }
    }
}
