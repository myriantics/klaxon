package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.registry.KlaxonDynamicRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ToolUsageRecipeType(Ingredient validTools, Optional<RegistryKey<Item>> display) {
    public ToolUsageRecipeType(Ingredient validTools) {
        this(validTools, Optional.empty());
    }
    public ToolUsageRecipeType(Ingredient validTools, @Nullable Item display) {
        this(validTools, display == null ? Optional.empty() : Registries.ITEM.getKey(display));
    }
    public ToolUsageRecipeType(Ingredient validTools, @Nullable RegistryKey<Item> display) {
        this(validTools, Optional.ofNullable(display));
    }

    public static final Codec<RegistryKey<ToolUsageRecipeType>> KEY_CODEC = RegistryKey.createCodec(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE);
    public static final PacketCodec<ByteBuf, RegistryKey<ToolUsageRecipeType>> KEY_PACKET_CODEC = RegistryKey.createPacketCodec(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE);

    public static final Codec<ToolUsageRecipeType> CODEC = RecordCodecBuilder.create((instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("valid_tools").forGetter(ToolUsageRecipeType::validTools),
            RegistryKey.createCodec(RegistryKeys.ITEM).optionalFieldOf("display_item").forGetter(ToolUsageRecipeType::display)
    ).apply(instance, ToolUsageRecipeType::new)));
}
