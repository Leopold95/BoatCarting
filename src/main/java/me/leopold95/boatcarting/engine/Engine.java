package me.leopold95.boatcarting.engine;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.abstrction.RepeatingTask;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.core.ConfigArenas;
import me.leopold95.boatcarting.engine.tasks.EventTickerTask;
import me.leopold95.boatcarting.enums.BoatContainer;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import me.leopold95.boatcarting.models.Event;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;

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
    @Getter
    private List<String> finishes;
    @Getter
    private int maxWinners;
    @Getter
    private  List<String> arenaRegions;

    @Getter
    private List<Integer> bannedSlots;

    @Getter
    private List<BoatContainer> boatTypes;

    public Engine(BoatCarting plugin) {
        this.plugin = plugin;

        boatTypes = new ArrayList<>();
        bannedSlots = new ArrayList<>();

        toTopMap = new HashMap<>();
        toForwardMap = new HashMap<>();
        activeEvents = new ArrayList<>();

        arenaManager = new ArenaManager(plugin);

        loadTopMap();
        loadForwardMap();
        loadOther();
    }

    /**
     * Запуск аодбора игроков для начала игры
     */
    public void startPlayersSearching(Player caller, Arena arena){
        String command =  "/" + Config.getMessage("command-template")
                .replace("{first}", Commands.BOAT_CARTING)
                .replace("{second}", Commands.JOIN_EVENT)
                .replace("{third}", String.valueOf(arena.getNumericId()));

        String message = Config.getMessage("game.started") + command;

        TextComponent text = Component.text(message)
                .clickEvent(ClickEvent.runCommand(command))
                .hoverEvent(HoverEvent.showText(Component.text(Config.getMessage("clickable"))));

        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(text));

        arena.setState(ArenaState.PLAYERS_WAITING);
        new EventTickerTask(plugin, arena, this);
        arena.getPlayers().add(caller);
        caller.sendMessage(Config.getMessage("game.teleported-to-arena").replace("{number}", String.valueOf(arena.getNumericId())));
        caller.teleport(arena.getLobbySpawn());
        caller.getPersistentDataContainer().set(plugin.getKeys().IS_PLAYER_CARTING, PersistentDataType.INTEGER, 1);
        plugin.getSounds().playTo(caller, "sounds.joined");
    }

    /**
     * НАчать игру
     * @param arena арена
     */
    public void startGame(Arena arena){
        arena.setState(ArenaState.ACTIVE_GAME);

        arena.blockPlayerMovement(plugin.getKeys().CANT_MOVE);
        arena.teleportPlayersToPositions();

        new RepeatingTask(plugin,0, 20) {
            private int secondsPassed = 0;
            private int prepareTime = Config.getInt("times.game-prepare");

            @Override
            public void run() {
                if(secondsPassed == prepareTime){
                    for(Player player: arena.getPlayers()){
                        if(player == null)
                            continue;

                        player.sendTitlePart(TitlePart.TITLE, Component.text(Config.getMessage("game.starting")));
                    }

                    arena.unlockPlayerMovement(plugin.getKeys().CANT_MOVE);

                    cancel();
                    return;
                }

                for(Player player: arena.getPlayers()){
                    if(player == null)
                        continue;

                    player.sendTitlePart(TitlePart.TITLE, Component.text(Config.getMessage("game.starting-in")
                            .replace("{time}", String.valueOf(secondsPassed))));
                    plugin.getSounds().playTo(player, "sounds.timer-to-game-tick");
                }

                secondsPassed++;
            }
        };
    }

    /**
     * Присоединится к игре
     * @param joiner игрок
     * @param arena арена
     */
    public void joinGame(Player joiner, Arena arena){
        if(arena.getState() != ArenaState.PLAYERS_WAITING){
            joiner.sendMessage(Config.getMessage("game.join.cant-join"));
            return;
        }

        if(arena.getPlayers().size() >= arena.getSpawnPoints().size()){
            joiner.sendMessage(Config.getMessage("game.join.not-enough-space"));
            return;
        }

        arena.getPlayers().add(joiner);
        joiner.teleport(arena.getLobbySpawn());
        joiner.sendMessage(Config.getMessage("game.join.ok"));
        joiner.getPersistentDataContainer().set(plugin.getKeys().IS_PLAYER_CARTING, PersistentDataType.INTEGER, 1);
        plugin.getSounds().playTo(joiner, "sounds.joined");

        if(arena.getPlayers().size() == arena.getSpawnPoints().size()){
            arena.setState(ArenaState.ACTIVE_GAME);
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Config.getMessage("game.players-are-full")));
        }
    }

    /**
     * Зевершение игры без начала
     * @param arena игрена
     */
    public void stopGame(Arena arena, String message){
        for(Player p: arena.getPlayers()){
            if (p == null) continue;

            p.sendMessage(message);
            p.teleport(afterGameSpawn);
            p.getPersistentDataContainer().remove(plugin.getKeys().IS_PLAYER_CARTING);
            plugin.getSounds().playTo(p, "sounds.stopped");
            Bukkit.getScheduler().runTask(plugin, () -> {plugin.getItemManager().tryRemoveJumpingItem(p);});
        }

        clearArena(arena);
    }

    /**
     * Игрок покинул арену во время заезда не командой
     * @param player игрок
     */
    public void unexpectedArenaLeaving(Player player){
        Optional<Arena> playerArena = getArenaManager().getByPlayer(player);

        if(playerArena.isPresent()){
            Arena arena = playerArena.get();

            arena.getPlayers().remove(player);
        }

        player.teleport(afterGameSpawn);
        player.getPersistentDataContainer().remove(plugin.getKeys().IS_PLAYER_CARTING);
        player.sendMessage(Config.getMessage("leave.unexpected"));
        Bukkit.getScheduler().runTask(plugin, () -> {plugin.getItemManager().tryRemoveJumpingItem(player);});
    }

    /**
     * Завершение игры полсе победы
     * @param arena арена
     */
    public void endGameAfterWinners(Arena arena){
    }

    /**
     * Сообщить игру о его победе
     * @param winner игрок, победитель
     */
    public void informWinner(Player winner){
        Optional<Arena> optArena = getArenaManager().getByPlayer(winner);
        optArena.ifPresent(arena -> {
            winner.getPersistentDataContainer().remove(plugin.getKeys().IS_PLAYER_CARTING);
            arena.getWinners().add(winner);

            if(arena.getWinners().size() == maxWinners){
                winner.sendMessage(Config.getMessage("game.win-no-place"));
            }
            else {
                String message = Config.getMessage("game.win")
                        .replace("{place}", String.valueOf(arena.getWinners().size()));
                winner.sendMessage(message);
            }

            winner.teleport(afterGameSpawn);
            arena.getPlayers().remove(winner);
            Bukkit.getScheduler().runTask(plugin, () -> {plugin.getItemManager().tryRemoveJumpingItem(winner);});
        });
    }

    /**
     * Досрочно покинуть арену
     * @param player игрок
     * @param arena арена
     */
    public void leaveGame(Player player, Arena arena){
        player.teleport(afterGameSpawn);

        arena.getPlayers().remove(player);
        player.sendMessage(Config.getMessage("leave.ok"));
        player.getPersistentDataContainer().remove(plugin.getKeys().IS_PLAYER_CARTING);

        if(player.getPersistentDataContainer().has(plugin.getKeys().CANT_MOVE))
            player.getPersistentDataContainer().remove(plugin.getKeys().CANT_MOVE);

        Bukkit.getScheduler().runTask(plugin, () -> {plugin.getItemManager().tryRemoveJumpingItem(player);});
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
        // Create a vector for forward and upward movement
        Vector direction = boat.getLocation().getDirection();
        Vector upward = new Vector(0, mod, 0);
        Vector velocity = direction.add(upward);

        // Normalize and scale the vector for a balanced movement
        velocity.normalize().multiply(Config.getDouble("if-top-forward-velocity"));
        boat.setVelocity(direction.add(velocity));
        //addForwardVelocity(boat, Config.getDouble("if-top-forward-velocity"));
    }

    public void addForwardVelocity(Boat boat, double mod){
        Vector v = boat.getLocation().getDirection();
        boat.setVelocity(v.multiply(mod));
    }

    /**
     * Очистка арены
     * @param arena арена
     */
    public void clearArena(Arena arena){
        arena.getPlayers().clear();
        arena.getWinners().clear();
        arena.setState(ArenaState.EMPTY);

        World world = Bukkit.getWorld(ConfigArenas.getArenasWorldName());
        Bukkit.getScheduler().runTask(plugin, () -> removeEntitiesInRegion(world, arena.getRegion()));
    }

    public void removeEntitiesInRegion(World world, String regionName) {
        // Get the RegionManager for the world
        RegionManager regionManager = plugin.getRegionContainer().get(BukkitAdapter.adapt(world));
        if (regionManager == null)
            return;

        // Get the region by name
        ProtectedRegion region = regionManager.getRegion(regionName);

        if (region != null) {
            // Loop through all entities in the world
            for (Entity entity : world.getEntitiesByClasses(Boat.class)) {
                Location loc = entity.getLocation();
                BlockVector3 blockVector = BukkitAdapter.asBlockVector(loc);

                // Check if the entity is within the region
                if (region.contains(blockVector)) {
                    // Remove the entity
                    entity.remove();
                }
            }
        } else {
            Bukkit.getLogger().info("Region " + regionName + " not found in world " + world.getName());
        }
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

    private void loadOther() {
        ConfigurationSection section = Config.getSection("after-game-spawn");
        if(section == null || section.getKeys(false).isEmpty()){
            plugin.getLogger().warning(
                    Config.getMessage("loading.no-after-game-spawn-section"));
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

        //set max winners count
        maxWinners = Config.getInt("max-winners");

        //load arenas finishes
        finishes = Config.getStringList("finishes");

        arenaRegions = Config.getStringList("arenas-regions");

        loadButtons();
    }

    private void loadButtons(){
        ConfigurationSection section = Config.getDesignConfig().getConfigurationSection("boats");

        if(section == null){
            plugin.getLogger().warning(Config.getMessage("gui.bad-boats-section"));
            return;
        }

        for(String key: section.getKeys(true)){
            try {
                int slot = section.getInt(key + ".slot");
                String mat = section.getString(key + ".material");
                String name = section.getString(key + ".name");

                ItemStack item = new ItemStack(Material.valueOf(mat));
                ItemMeta meta = item.getItemMeta();

                meta.setDisplayName(name);

                List<TextComponent> newLove =  Config.getDesignConfig().getStringList(key + ".lore")
                        .stream()
                        .map(Component::text)
                        .toList();

                meta.lore(newLove);
                item.setItemMeta(meta);

                var type = new BoatContainer(
                    Boat.Type.valueOf(section.getString(key + ".type")),
                    slot,
                    item
                );

                bannedSlots.add(slot);
                boatTypes.add(type);
            }
            catch (Exception exception){
                String message = Config.getMessage("gui.bad-boats-material")
                        .replace("%exp%", exception.getMessage())
                        .replace("{sec}", key);
                plugin.getLogger().warning(message);
            }
        }

    }
}
