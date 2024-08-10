package me.leopold95.boatcarting.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class BoatContainer {
    private Boat.Type type;
    private int slot;
    private ItemStack itemStack;
}
