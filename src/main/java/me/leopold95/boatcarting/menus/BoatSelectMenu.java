package me.leopold95.boatcarting.menus;

import me.leopold95.boatcarting.core.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class BoatSelectMenu implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 54, Component.text(Config.getDesignConfig().getString("menu-name")));
    }
}
