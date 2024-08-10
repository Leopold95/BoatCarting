package me.leopold95.boatcarting.core;

import me.leopold95.boatcarting.BoatCarting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ButtonFormats {
    private BoatCarting plugin;

    public ButtonFormats(BoatCarting plugin) {
        this.plugin = plugin;
    }

    public void format(Inventory inv){
        formatDesign(inv);

        for(var v: plugin.getEngine().getBoatTypes()){
            inv.setItem(v.getSlot(), v.getItemStack());
        }
    }

    private void formatDesign(Inventory inv){
        ConfigurationSection section = Config.getDesignConfig().getConfigurationSection("slots");

        if(section == null){
            plugin.getLogger().warning(Config.getMessage("gui.bad-design-section"));
            return;
        }

        for(String key: section.getKeys(false)){
            int slot = Integer.parseInt(key);

            plugin.getEngine().getBannedSlots().add(slot);

            String materialName = Config.getDesignConfig().getString("slots." + key);
            Material material;

            try {
                material = Material.valueOf(materialName);
                inv.setItem(slot, new ItemStack(material));
            }
            catch (Exception exception){
                String message = Config.getMessage("gui.bad-design-material")
                        .replace("%exp%", exception.getMessage());
                plugin.getLogger().warning(message);
            }
        }
    }
}
