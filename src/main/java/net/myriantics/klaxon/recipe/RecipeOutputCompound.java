package net.myriantics.klaxon.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RecipeOutputCompound {
    private final List<Pair<ItemStack, Double>> dropsAndChances;

    // Display stacks to be rendered in EMI and other GUIs. Only computed upon request.
    private ItemStack[] displayStacksCache = null;

    private RecipeOutputCompound(List<Pair<ItemStack, Double>> chanceDrops) {
        this.dropsAndChances = chanceDrops;
    }

    public static RecipeOutputCompound of(ItemStack... guaranteedStacks) {
        return of(List.of(guaranteedStacks));
    }

    public static RecipeOutputCompound of(List<ItemStack> guaranteedStacks) {
        ArrayList<Pair<ItemStack, Double>> dropsAndChances = new ArrayList<>();

        // compute chance drops
        for (ItemStack stack : guaranteedStacks) {
            dropsAndChances.add(new Pair<>(stack, 1.0));
        }

        return new RecipeOutputCompound(List.copyOf(dropsAndChances));
    }

    public static RecipeOutputCompound of(Holder<Item> itemHolder, double d) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(itemHolder), d)));
    }

    public static RecipeOutputCompound of(ItemLike item, double d) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(item), d)));
    }

    public static RecipeOutputCompound of(Holder<Item> item, double d, Holder<Item> item1, double d1) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(item), d), new Pair<>(new ItemStack(item1), d1)));
    }

    public static RecipeOutputCompound of(ItemLike item, double d, ItemLike item1, double d1) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(item), d), new Pair<>(new ItemStack(item1), d1)));
    }

    public int size() {
        return dropsAndChances.size();
    }

    public ItemStack[] computeDrops(RandomSource random) {
        ItemStack[] outputList = new ItemStack[dropsAndChances.size()];

        // Roll the random drops and add copies to the list
        for (int i = 0; i < dropsAndChances.size(); i++) {
            Pair<ItemStack, Double> pair = dropsAndChances.get(i);
            ItemStack stack = pair.getFirst();
            double chance = pair.getSecond();

            // If a drop is guaranteed, just add it no questions asked
            if (chance >= 1) {
                outputList[i] = stack.copy();
                continue;
            }

            /*
            double max = 31 - Integer.numberOfLeadingZeros(stack.getCount() - 1);
            int count = (int) (stack.getCount() * chance + Math.sqrt(max * (1 - chance)) * random.nextGaussian() + 0.5);
            if (count > 0) {
                outputList.add(stack.copyWithCount(count));
            }*/

            int count = (int) (0.5 + random.triangle(
                    stack.getCount() * chance,
                    stack.getCount() / 2f
            ));
            if (count > 0) {
                outputList[i] = stack.copyWithCount(count);
            } else {
                outputList[i] = ItemStack.EMPTY;
            }
        }

        return outputList;
    }

    public ItemStack[] getDisplayStacks() {
        if (this.displayStacksCache != null && this.displayStacksCache.length > 0) {
            return this.displayStacksCache;
        } else {
            ItemStack[] displayStacks = new ItemStack[dropsAndChances.size()];

            for (int i = 0; i < dropsAndChances.size(); i++) {
                Pair<ItemStack, Double> pair = dropsAndChances.get(i);
                ItemStack stack = pair.getFirst();
                double chance = pair.getSecond();

                if (chance > 0) {
                    ItemStack display = stack.copy();
                    display.set(KlaxonDataComponentTypes.RECIPE_OUTPUT_CHANCE_LORE.value(), chance);
                    displayStacks[i] = display;
                }
            }

            this.displayStacksCache = displayStacks;
            return displayStacks;
        }
    }

    public List<Pair<ItemStack, Double>> getRawDropsAndChances() {
        return dropsAndChances;
    }

    public static Codec<RecipeOutputCompound> createCodec(int size) {
        return Codec.list(
                Codec.mapPair(
                        ItemStack.CODEC.fieldOf("stack"),
                        Codec.optionalField("chance", Codec.doubleRange(0.0, 1.0), true).xmap(
                                (optional) -> optional.orElse(1.0),
                                Optional::of
                        )
                ).codec(),
                0,
                size
        ).xmap(
                RecipeOutputCompound::new,
                RecipeOutputCompound::getRawDropsAndChances
        );
    }

    public static StreamCodec<RegistryFriendlyByteBuf, RecipeOutputCompound> STREAM_CODEC = StreamCodec.of(
            RecipeOutputCompound::write, RecipeOutputCompound::read
    );

    private static RecipeOutputCompound read(RegistryFriendlyByteBuf buf) {
        int size = ByteBufCodecs.VAR_INT.decode(buf);

        ArrayList<Pair<ItemStack, Double>> entries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            entries.add(new Pair<>(
                    ItemStack.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.DOUBLE.decode(buf)
            ));
        }

        return new RecipeOutputCompound(entries);
    }

    private static void write(RegistryFriendlyByteBuf buf, RecipeOutputCompound recipeOutputCompound) {
        List<Pair<ItemStack, Double>> rawDropsAndChances = recipeOutputCompound.getRawDropsAndChances();

        int size = rawDropsAndChances.size();
        ByteBufCodecs.VAR_INT.encode(buf, size);

        for (Pair<ItemStack, Double> pair : rawDropsAndChances) {
            ItemStack.STREAM_CODEC.encode(buf, pair.getFirst());
            ByteBufCodecs.DOUBLE.encode(buf, pair.getSecond());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ArrayList<Pair<ItemStack, Double>> pairs = new ArrayList<>();

        private Builder() {}

        public Builder guaranteed(ItemStack... stacks) {
            for (ItemStack stack : stacks) {
                pairs.add(new Pair<>(stack, 1.0));
            }

            return this;
        }

        public Builder chance(ItemStack stack, double chance) {
            if (chance > 1 || chance < 0) {
                throw new IllegalArgumentException();
            }

            pairs.add(new Pair<>(stack, chance));

            return this;
        }

        public Builder chance(Holder<Item> item, double chance) {
            return chance(new ItemStack(item), chance);
        }

        public Builder chance(ItemLike item, double chance) {
            return chance(new ItemStack(item), chance);
        }

        public Builder chance(Holder<Item> item, int count, double chance) {
            return chance(new ItemStack(item, count), chance);
        }

        public Builder chance(ItemLike item, int count, double chance) {
            return chance(new ItemStack(item, count), chance);
        }

        public RecipeOutputCompound build() {
            return new RecipeOutputCompound(pairs);
        }
    }
}
