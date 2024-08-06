package me.leopold95.boatcarting.commands;

import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.enums.Commands;
import me.leopold95.boatcarting.enums.Permissions;
import me.leopold95.boatcarting.models.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(Config.getMessage("only-for-players"));
            return true;
        }

//        if(args.length != 1){
//            String message = Config.getMessage("bad-command-args")
//                            .replace("{first}", Commands.BOAT_CARTING)
//                            .replace("{second}", Config.getMessage("placeholders.args"))
//                            .replace("{third}", "");
//
//            sender.sendMessage(message);
//            return true;
//        }

        switch (args[0]){
            case Commands.START_EVENT -> {
                if(!player.hasPermission(Permissions.START)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 2){
                    String message = Config.getMessage("command-template")
                                    .replace("{first}", Commands.BOAT_CARTING)
                                    .replace("{second}", Commands.START_EVENT)
                                    .replace("{third}", "");
                    sender.sendMessage("/"+message);
                    return true;
                }

                Optional<Arena> arena = plugin.getEngine().getArenaManager().findEmpty();
                if(arena.isEmpty()){
                    player.sendMessage(Config.getMessage("game.no-available-arena"));
                    return true;
                }

                plugin.getEngine().startPlayersSearching(player, arena.get());
            }

            case Commands.JOIN_EVENT -> {
                if(!player.hasPermission(Permissions.JOIN)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

                if(args.length != 3){
                    String message = Config.getMessage("command-template")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.JOIN_EVENT)
                            .replace("{third}", Config.getMessage("placeholders.arena"));
                    sender.sendMessage("/"+message);
                    return true;
                }

                try {
                    int numericId = Integer.parseInt(args[2]);

                    Optional<Arena> arena = plugin.getEngine().getArenaManager().getEmptyByNumeric(numericId);

                    if(arena.isEmpty()){
                        player.sendMessage(Config.getMessage("game.join.no-arena"));
                        return true;
                    }

                    plugin.getEngine().joinGame(player, arena.get());
                }
                catch (Exception ext){
                    String message = Config.getMessage("command-template")
                            .replace("{first}", Commands.BOAT_CARTING)
                            .replace("{second}", Commands.JOIN_EVENT)
                            .replace("{third}", Config.getMessage("placeholders.arena"));
                    sender.sendMessage("/"+message);
                }
            }

            case Commands.STOP_EVENT -> {
                if(!player.hasPermission(Permissions.STOP)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }

            }

            case Commands.LEAVE_EVENT -> {
                if(!player.hasPermission(Permissions.LEAVE)){
                    player.sendMessage(Config.getMessage("no-permission"));
                    return true;
                }
            }

        }



//        if(args[0].equals("top")){
//            Boat boat = (Boat) player.getVehicle();
//
//            Vector v = player.getVelocity();
//            boat.setVelocity(new Vector(v.getX(), v.getY() + 1, v.getZ()));
//        }
//
//        if(args[0].equals("forward")){
//            Boat boat = (Boat) player.getVehicle();
//            Vector v = player.getEyeLocation().getDirection();
//            boat.setVelocity(v.multiply(1.5));
//        }
//
//        if(args[0].equals("forwardv")){
//            Boat boat = (Boat) player.getVehicle();
//            Vector v = boat.getLocation().getDirection();
//            boat.setVelocity(v.multiply(1.5));
//        }

        return true;
    }
}
