package me.leopold95.boatcarting.engine;

import lombok.Getter;
import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemManager {
    private BoatCarting plugin;

    @Getter
    private HashMap<UUID, LocalTime> cooldowns;

    public ItemManager(BoatCarting plugin) {
        this.plugin = plugin;
        cooldowns = new HashMap<>();
    }

    public ItemStack getJumpItem(){
        Material mat = Material.valueOf(Config.getString("jump-item.material"));
        ItemStack itemStack = new ItemStack(mat);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Config.getString("jump-item.name"));

        List<TextComponent> newLove =  Config.getDesignConfig().getStringList("jump-item.lore")
            .stream()
            .map(Component::text)
            .toList();

        meta.lore(newLove);

        meta.getPersistentDataContainer().set(plugin.getKeys().JUMPING_ITEM, PersistentDataType.INTEGER, 1);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void tryUseJump(Player player){
        if(!(player.getVehicle() instanceof Boat)){
            player.sendMessage(Config.getMessage("jump-item.no-boat"));
            return;
        }

        if(cooldowns.containsKey(player.getUniqueId())){
            LocalTime lastUsage = cooldowns.get(player.getUniqueId());
            long duration = Duration.between(lastUsage, LocalTime.now()).getSeconds();

            long maxDuration = Config.getLong("jump-item.cooldown");

            if(duration < maxDuration){
                player.sendMessage(Config.getMessage("jump-item.bad-time").replace("{time}", String.valueOf(maxDuration - duration)));
                return;
            }
        }
        else {
            cooldowns.put(player.getUniqueId(), LocalTime.now());
        }

        double mod = Config.getDouble("jump-item.top");
        Boat boat = (Boat) player.getVehicle();
        plugin.getEngine().addTopVelocity(boat, mod);

        player.sendMessage(Config.getMessage("jump-item.ok"));
    }

    public void tryRemoveJumpingItem(Player player){
        for(ItemStack item: player.getInventory().getContents()){
            if(item == null || item.getItemMeta() == null)
                continue;

            if(item.getItemMeta().getPersistentDataContainer().has(plugin.getKeys().JUMPING_ITEM)){
                player.getInventory().remove(item);
            }
        }
    }
}
