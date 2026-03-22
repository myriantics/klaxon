package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record RepairIngredientOverrideComponent(Ingredient repairMaterial) {
    public static final Codec<RepairIngredientOverrideComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("repair_ingredient").forGetter(RepairIngredientOverrideComponent::repairMaterial)
        ).apply(instance, RepairIngredientOverrideComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, RepairIngredientOverrideComponent> PACKET_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, RepairIngredientOverrideComponent::repairMaterial,
            RepairIngredientOverrideComponent::new
    );

    public static @Nullable RepairIngredientOverrideComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE.value());
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE.value(), this).build());
    }
}
