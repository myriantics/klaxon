package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record RepairIngredientOverrideComponent(Ingredient repairMaterial) {
    public static final Codec<RepairIngredientOverrideComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("repair_ingredient").forGetter(RepairIngredientOverrideComponent::repairMaterial)
        ).apply(instance, RepairIngredientOverrideComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, RepairIngredientOverrideComponent> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, RepairIngredientOverrideComponent::repairMaterial,
            RepairIngredientOverrideComponent::new
    );

    public static @Nullable RepairIngredientOverrideComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.REPAIR_INGREDIENT_OVERRIDE, this).build());
    }
}
