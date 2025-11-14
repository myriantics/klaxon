package net.myriantics.klaxon.mixin.minecraft.advanced_item_models;

import net.minecraft.client.render.model.json.ModelOverride;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ModelOverride.class)
public interface ModelOverrideAccessor {
    @Accessor(value = "conditions")
    List<ModelOverride.Condition> klaxon$getConditions();
}
