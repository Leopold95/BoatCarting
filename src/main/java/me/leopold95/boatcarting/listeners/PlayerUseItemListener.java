package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerUseItemListener implements Listener {
    private BoatCarting plugin;

    public PlayerUseItemListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void playerUserItemRMB(PlayerInteractEvent event){

        //System.out.println(-1);
        if(!event.getAction().isRightClick())
            return;

        //System.out.println(0);

        if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
            return;

        //System.out.println(1);

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(!item.getItemMeta().getPersistentDataContainer().has(plugin.getKeys().JUMPING_ITEM))
            return;

        //System.out.println(2);

        event.setCancelled(true);
        plugin.getItemManager().tryUseJump(event.getPlayer());
    }
}
