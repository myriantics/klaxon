package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.client.renderer.block.model.ItemOverride;

@Mixin(ItemOverride.class)
public interface ItemOverrideAccessor {
    @Accessor(value = "predicates")
    List<ItemOverride.Predicate> klaxon$getConditions();
}
