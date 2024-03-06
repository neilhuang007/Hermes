package dev.hermes.manager;

import net.minecraft.item.ItemStack;

public class InventoryManager {
    public static int getItemDurability(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
