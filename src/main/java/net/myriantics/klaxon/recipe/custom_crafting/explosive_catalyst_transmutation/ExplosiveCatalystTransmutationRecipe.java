package net.myriantics.klaxon.recipe.custom_crafting.explosive_catalyst_transmutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExplosiveCatalystTransmutationRecipe extends CustomRecipe {

    public static final MapCodec<ExplosiveCatalystTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category),
            ShapedRecipePattern.MAP_CODEC.validate(ExplosiveCatalystTransmutationRecipe::validatePattern).fieldOf("pattern").forGetter(i -> i.pattern),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(i -> i.result),
            Codec.BOOL.lenientOptionalFieldOf("requires_catalyst", false).forGetter(i -> i.requiresCatalyst)
    ).apply(instance, ExplosiveCatalystTransmutationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystTransmutationRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
            ShapedRecipePattern.STREAM_CODEC, i -> i.pattern,
            ItemStack.STREAM_CODEC, i -> i.result,
            ByteBufCodecs.BOOL, i -> i.requiresCatalyst,
            ExplosiveCatalystTransmutationRecipe::new
    );

    public final ShapedRecipePattern pattern;
    public final ItemStack result;
    public final boolean requiresCatalyst;

    public ExplosiveCatalystTransmutationRecipe(CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean requiresCatalyst) {
        super(category);
        this.pattern = pattern;
        this.result = result;
        this.requiresCatalyst = requiresCatalyst;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (this.pattern.matches(this.withoutCenter(input))) {
            ItemStack center = input.getItem(4);
            @Nullable ExplosiveCatalystData data = this.getDataForStack(center, stack -> ExplosiveCatalystData.findRaw(level, center));
            return data != null;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = this.getResultItem(registries).copy();
        ItemStack center = input.getItem(4);
        @Nullable ExplosiveCatalystData data = this.getDataForStack(center, stack -> ExplosiveCatalystData.findRaw(registries, center));
        if (data == null) {
            return ItemStack.EMPTY;
        } else {
            // no infinite recursive funkiness
            if (result.getCount() > 1) {
                data = data.copyWithPower(data.explosionPower() / result.getCount());
            }
            ExplosiveCatalystBehavior behavior = data.behavior(registries).value();
            result.applyComponents(center.getComponentsPatch().forget(behavior::isComponentIrrelevant));
            result.set(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA.value(), data);
            return result;
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.EXPLOSIVE_CATALYST_TRANSMUTATION_RECIPE_SERIALIZER.value();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    private CraftingInput withoutCenter(CraftingInput original) {
        if (original.items().size() < this.pattern.ingredients().size()) {
            return original;
        }

        return CraftingInput.of(
                3,
                3,
                List.of(
                        original.getItem(0),
                        original.getItem(1),
                        original.getItem(2),
                        original.getItem(3),
                        ItemStack.EMPTY,
                        original.getItem(5),
                        original.getItem(6),
                        original.getItem(7),
                        original.getItem(8)
                )
        );
    }

    private static DataResult<ShapedRecipePattern> validatePattern(ShapedRecipePattern pattern) {
        if (pattern.height() != 3) {
            return DataResult.error(() -> "Height of Explosive Catalyst Transmutation Recipe Pattern must be 3, was " + pattern.height());
        } else if (pattern.width() != 3) {
            return DataResult.error(() -> "Width of Explosive Catalyst Transmutation Recipe Pattern must be 3, was " + pattern.width());
        } else if (!pattern.ingredients().get(4).isEmpty()) {
            return DataResult.error(() -> "Center ingredient (index 4) of Explosive Catalyst Transmutation Recipe Pattern must be empty, was " + pattern.ingredients().get(4));
        } else {
            return DataResult.success(pattern);
        }
    }

    public @Nullable ExplosiveCatalystData getDataForStack(ItemStack stack, FallbackCatalystDataCalculator calculator) {
        if (stack.isEmpty() || stack.is(KlaxonItemTags.UNUSABLE_FOR_EXPLOSIVE_CATALYST_TRANSMUTATION)) {
            return ExplosiveCatalystData.ZERO;
        } else {
            return calculator.calculate(stack);
        }
    }

    public interface FallbackCatalystDataCalculator {
        @Nullable ExplosiveCatalystData calculate(ItemStack stack);
    }

    public static class Serializer implements RecipeSerializer<ExplosiveCatalystTransmutationRecipe> {

        @Override
        public MapCodec<ExplosiveCatalystTransmutationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExplosiveCatalystTransmutationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
