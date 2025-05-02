package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.cooling.ItemCoolingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private World world;

    @Inject(
            method = "extinguish",
            at = @At(value = "HEAD")
    )
    private void klaxon$processItemCoolingRecipe(CallbackInfo ci) {
        Entity self = (Entity)(Object) this;

        // we don't need to run cooling visual effects on the client - at least not right now
        if (!world.isClient() && self instanceof ItemEntity itemEntity && ItemCoolingHelper.test(world, itemEntity.getStack())) {

            Optional<ItemStack> potentialOutput = ItemCoolingHelper.getCooledStack(world, itemEntity.getStack());
            KlaxonCommon.LOGGER.info("Selected Output Stack: " + potentialOutput);
            potentialOutput.ifPresent(itemEntity::setStack);
        }
    }

    @ModifyReturnValue(
            method = "isOnFire",
            at = @At(value = "RETURN")
    )
    private boolean klaxon$coolableItemOnFireOverride(boolean original) {
        Entity self = (Entity)(Object) this;

        // report items that can be cooled as on fire so that extinguish() is called
        // dont do this on the client because otherwise the items will render as on fire.
        return original || (!world.isClient() && self instanceof ItemEntity itemEntity && ItemCoolingHelper.test(world, itemEntity.getStack()));
    }
}
