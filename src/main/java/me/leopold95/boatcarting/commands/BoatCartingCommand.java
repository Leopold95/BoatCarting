package me.leopold95.boatcarting.commands;

import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.enums.Permissions;
import me.leopold95.boatcarting.menus.BoatSelectMenu;
import me.leopold95.boatcarting.models.Arena;
import me.leopold95.boatcarting.models.ArenaState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BoatCartingCommand implements CommandExecutor, TabCompleter {
    private BoatCarting plugin;

    public BoatCartingCommand(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of(
                Commands.START_EVENT,
                Commands.JOIN_EVENT,
                Commands.STOP_EVENT,
                Commands.LEAVE_EVENT,
                Commands.PRE_START_EVENT,
                Commands.BOAT,
                Commands.AVAILABLE
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(Config.getMessage("only-for-players"));
            return true;
        }

        switch (args[0]){
            case Commands.START_EVENT -> {
                if(!player.hasPermission(Permissions.START)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 1){
                    String message = Config.getMessage("bad-command-args")
                                    .replace("{first}", Commands.BOAT_CARTING)
                                    .replace("{second}", Commands.START_EVENT)
                                    .replace("{third}", "");
                    sender.sendMessage(message);
                    return true;
                }

                Optional<Arena> optArena = plugin.getEngine().getArenaManager().getByPlayer(player);
                if(optArena.isPresent()){
                    player.sendMessage(Config.getMessage("game.already-in-game"));
                    return true;
                }

                Optional<Arena> arena = plugin.getEngine().getArenaManager().findEmpty();
                if(arena.isEmpty()){
                    player.sendMessage(Config.getMessage("game.no-available-arena"));
                    return true;
                }

                plugin.getEngine().startPlayersSearching(player, arena.get());
            }

            case Commands.PRE_START_EVENT -> {
                if(!player.hasPermission(Permissions.PRE_START)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 1){
                    String message = Config.getMessage("bad-command-args")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.PRE_START_EVENT)
                            .replace("{third}", Config.getMessage("placeholders.arena"));
                    sender.sendMessage(message);
                    return true;
                }

                Optional<Arena> playerArena = plugin.getEngine().getArenaManager().getByPlayer(player);

                if(playerArena.isEmpty()){
                    player.sendMessage(Config.getMessage("pre-start.no-member"));
                    return true;
                }

                plugin.getEngine().startGame(playerArena.get());
            }

            case Commands.JOIN_EVENT -> {
                if(!player.hasPermission(Permissions.JOIN)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 2){
                    String message = Config.getMessage("bad-command-args")
                        .replace("{first}", Commands.BOAT_CARTING)
                        .replace("{second}", Commands.JOIN_EVENT)
                        .replace("{third}", Config.getMessage("placeholders.arena"));

                    sender.sendMessage(message);
                    return true;
                }

                Optional<Arena> arenaOpt = plugin.getEngine().getArenaManager().getByPlayer(player);
                if(arenaOpt.isPresent()){
                    player.sendMessage(Config.getMessage("game.join.already-member"));
                    return true;
                }

                try {
                    int numericId = Integer.parseInt(args[1]);

                    Optional<Arena> arena = plugin.getEngine().getArenaManager().getEmptyByNumeric(numericId);
                    if(arena.isEmpty()){
                        player.sendMessage(Config.getMessage("game.join.no-arena"));
                        return true;
                    }

                    plugin.getEngine().joinGame(player, arena.get());
                }
                catch (Exception ext){
                    plugin.getLogger().warning(ext.getMessage());
                    String message = Config.getMessage("bad-command-args")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.JOIN_EVENT)
                            .replace("{third}", Config.getMessage("placeholders.arena"));
                    sender.sendMessage(message);
                }
            }

            case Commands.STOP_EVENT -> {
                if(!player.hasPermission(Permissions.STOP)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 1){
                    String message = Config.getMessage("bad-command-args")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.STOP_EVENT)
                            .replace("{third}", "");
                    sender.sendMessage(message);
                    return true;
                }

                Optional<Arena> arena = plugin.getEngine().getArenaManager().getByPlayer(player);
                if(arena.isEmpty()){
                    player.sendMessage(Config.getMessage("stop.no-member"));
                    return true;
                }

                String message = Config.getMessage("stop.ended-by").replace("{name}", player.getName());
                plugin.getEngine().stopGame(arena.get(), message);
            }

            case Commands.LEAVE_EVENT -> {
                if(!player.hasPermission(Permissions.LEAVE)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 1){
                    String message = Config.getMessage("bad-command-args")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.LEAVE_EVENT)
                            .replace("{third}", "");
                    sender.sendMessage(message);
                    return true;
                }

                Optional<Arena> arena = plugin.getEngine().getArenaManager().getByPlayer(player);
                if(arena.isEmpty()){
                    player.sendMessage(Config.getMessage("leave.no-member"));
                    return true;
                }

                plugin.getEngine().leaveGame(player, arena.get());
            }

            case Commands.BOAT -> {
                if(!player.hasPermission(Permissions.BOAT)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                Inventory inv = new BoatSelectMenu().getInventory();
                plugin.getButtonFormats().format(inv);

                player.openInventory(inv);
            }

            case Commands.AVAILABLE -> {
                if(!player.hasPermission(Permissions.AVAILABLE)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                var list = plugin.getEngine().getArenaManager().getArenas().stream().filter(a -> a.getState() == ArenaState.PLAYERS_WAITING).toList();

                if(list.isEmpty()){
                    player.sendMessage(Config.getMessage("available.none"));
                    return true;
                }

                for(Arena a : list){
                    String message = Config.getMessage("available.arena")
                                    .replace("{num}", String.valueOf(a.getNumericId()))
                                    .replace("{name}", a.getName());
                    player.sendMessage(message);
                }
            }
        }
        return true;
    }
}
