package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ToolUsageRecipeType(Ingredient validTools, Optional<ResourceKey<Item>> display) {
    public ToolUsageRecipeType(Ingredient validTools) {
        this(validTools, Optional.empty());
    }
    public ToolUsageRecipeType(Ingredient validTools, @Nullable Item display) {
        this(validTools, display == null ? Optional.empty() : BuiltInRegistries.ITEM.getResourceKey(display));
    }
    public ToolUsageRecipeType(Ingredient validTools, @Nullable ResourceKey<Item> display) {
        this(validTools, Optional.ofNullable(display));
    }

    public static final Codec<ResourceKey<ToolUsageRecipeType>> KEY_CODEC = ResourceKey.codec(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE);
    public static final StreamCodec<ByteBuf, ResourceKey<ToolUsageRecipeType>> KEY_PACKET_CODEC = ResourceKey.streamCodec(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE);

    public static final Codec<ToolUsageRecipeType> CODEC = RecordCodecBuilder.create((instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("valid_tools").forGetter(ToolUsageRecipeType::validTools),
            ResourceKey.codec(Registries.ITEM).optionalFieldOf("display_item").forGetter(ToolUsageRecipeType::display)
    ).apply(instance, ToolUsageRecipeType::new)));
}
