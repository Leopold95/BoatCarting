package me.leopold95.boatcarting.listeners;

import me.leopold95.boatcarting.BoatCarting;
import me.leopold95.boatcarting.core.Config;
import me.leopold95.boatcarting.menus.BoatSelectMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class MenuClickListener implements Listener {
    private BoatCarting plugin;

    public MenuClickListener(BoatCarting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onSelectProfessionInvClicked(InventoryClickEvent event){
        if(!(event.getInventory().getHolder() instanceof BoatSelectMenu))
            return;

        if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
            event.setCancelled(true);

        if(event.getAction() == InventoryAction.PICKUP_ALL)
            event.setCancelled(true);

        if(event.getAction() == InventoryAction.PLACE_ALL)
            event.setCancelled(true);

        if(event.getAction() == InventoryAction.PICKUP_HALF)
            event.setCancelled(true);

        if(!(event.getWhoClicked() instanceof Player player))
            return;

        if(event.getCurrentItem() == null)
            return;

        //только клики в меню
        if(event.getSlot() != event.getRawSlot())
            return;

        //блокировка действий при нажатии на кнопки интерфнйса
        if(plugin.getEngine().getBannedSlots().contains(event.getSlot())){
            event.setCancelled(true);
        }

        ItemStack button = event.getCurrentItem();

        var opt = plugin.getEngine().getBoatTypes().stream().filter(bt -> bt.getItemStack().equals(button)).findFirst();

        if(opt.isEmpty())
            return;

        player.getPersistentDataContainer().set(plugin.getKeys().PLAYER_BOAT, PersistentDataType.STRING, opt.get().getType().toString());
        String message = Config.getMessage("gui.selected")
                        .replace("{name}", button.getItemMeta().getDisplayName())
                        .replace("{type}", opt.get().getType().toString());
        player.sendMessage(message);
        player.closeInventory();
    }
}
