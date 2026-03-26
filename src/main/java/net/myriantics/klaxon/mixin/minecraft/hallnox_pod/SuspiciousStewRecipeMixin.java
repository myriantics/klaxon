package net.myriantics.klaxon.mixin.minecraft.hallnox_pod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SuspiciousStewRecipe;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(SuspiciousStewRecipe.class)
public abstract class SuspiciousStewRecipeMixin {
    @WrapOperation(
            method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;RED_MUSHROOM:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;BOWL:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean klaxon$checkForHallnoxPod(ItemStack instance, TagKey<Item> tag, Operation<Boolean> original) {
        return original.call(instance, tag) || instance.is(KlaxonItems.HALLNOX_POD);
    }
}
