package net.myriantics.klaxon.mixin.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.jade.KlaxonJadePlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.addon.harvest.HarvestToolProvider;

import java.util.List;

@Mixin(HarvestToolProvider.class)
public abstract class HarvestToolProviderMixin {

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;",
                    ordinal = 1
            ),
            remap = false
    )
    private static List<Item> klaxon$appendCleaver(List<Item> original) {
        try {
            original = KlaxonJadePlugin.appendCleaver(original);
        } catch (Exception e) {
            KlaxonCommon.LOGGER.error(e.getMessage());
        }

        return original;
    }
}
