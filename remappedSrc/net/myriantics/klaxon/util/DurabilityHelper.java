package net.myriantics.klaxon.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.apache.commons.lang3.mutable.MutableFloat;

public abstract class DurabilityHelper {

    public static MutableFloat applyInnateUnbreaking(ItemStack input, ServerWorld serverWorld, MutableFloat originalDamage) {
        if (input.isIn(KlaxonItemTags.INNATE_UNBREAKING_EQUIPMENT)) {
            int innateUnbreakingLevel = 4;

            // yoink unbreaking and then apply it because yeah
            serverWorld.getServer().getRegistryManager().get(RegistryKeys.ENCHANTMENT).get(Enchantments.UNBREAKING).modifyItemDamage(serverWorld, innateUnbreakingLevel, input, originalDamage);
        }

        return originalDamage;
    }
}
