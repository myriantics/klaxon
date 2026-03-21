package net.myriantics.klaxon.mixin.minecraft.cable_shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TripWireBlock;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripWireBlock.class)
public abstract class TripwireBlockMixin {

    @ModifyExpressionValue(
            method = "playerWillDestroy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    public boolean klaxon$cableShearsOverride(boolean original, @Local(argsOnly = true) Player player) {
        ItemStack miningToolStack = player.getMainHandItem();

        return original || miningToolStack.is(KlaxonItemTags.CABLE_SHEARS);
    }
}
