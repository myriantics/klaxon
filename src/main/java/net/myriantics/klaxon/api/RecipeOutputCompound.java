package net.myriantics.klaxon.api;

import com.mojang.serialization.Codec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

import java.util.*;

public final class RecipeOutputCompound {
    private final List<Pair<ItemStack, Double>> dropsAndChances;

    // Display stacks to be rendered in EMI and other GUIs. Only computed upon request.
    private List<ItemStack> displayStacksCache = List.of();

    public RecipeOutputCompound(List<Pair<ItemStack, Double>> chanceDrops) {
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

    public static RecipeOutputCompound of(ItemConvertible item, double d) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(item), d)));
    }

    public static RecipeOutputCompound of(ItemConvertible item, double d, ItemConvertible item1, double d1) {
        return new RecipeOutputCompound(List.of(new Pair<>(new ItemStack(item), d), new Pair<>(new ItemStack(item1), d1)));
    }

    public int size() {
        return dropsAndChances.size();
    }

    public List<ItemStack> computeDrops(Random random) {
        ArrayList<ItemStack> outputList = new ArrayList<>();

        // Roll the random drops and add copies to the list
        for (Pair<ItemStack, Double> pair : dropsAndChances) {
            ItemStack stack = pair.getFirst();
            double chance = pair.getSecond();

            // If a drop is guaranteed, just add it no questions asked
            if (chance >= 1) {
                outputList.add(stack.copy());
                continue;
            }

            /*
            double max = 31 - Integer.numberOfLeadingZeros(stack.getCount() - 1);
            int count = (int) (stack.getCount() * chance + Math.sqrt(max * (1 - chance)) * random.nextGaussian() + 0.5);
            if (count > 0) {
                outputList.add(stack.copyWithCount(count));
            }*/



            int count = (int) (0.5 + random.nextTriangular(
                    stack.getCount() * chance,
                    stack.getCount() / 2f
            ));
            if (count > 0) {
                outputList.add(stack.copyWithCount(count));
            }
        }

        return List.copyOf(outputList);
    }

    public List<ItemStack> getDisplayStacks() {
        if (!this.displayStacksCache.isEmpty()) {
            return this.displayStacksCache;
        } else {
            ArrayList<ItemStack> displayStacks = new ArrayList<>();

            for (Pair<ItemStack, Double> entry : dropsAndChances) {
                ItemStack stack = entry.getFirst();
                double chance = entry.getSecond();

                if (chance > 0) {
                    ItemStack display = stack.copy();
                    display.set(KlaxonDataComponentTypes.RECIPE_OUTPUT_LORE, chance);
                    displayStacks.add(display);
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

    public static PacketCodec<RegistryByteBuf, RecipeOutputCompound> PACKET_CODEC = PacketCodec.ofStatic(
            RecipeOutputCompound::write, RecipeOutputCompound::read
    );

    private static RecipeOutputCompound read(RegistryByteBuf buf) {
        int size = PacketCodecs.VAR_INT.decode(buf);

        ArrayList<Pair<ItemStack, Double>> entries = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            entries.add(new Pair<>(
                    ItemStack.PACKET_CODEC.decode(buf),
                    PacketCodecs.DOUBLE.decode(buf)
            ));
        }

        return new RecipeOutputCompound(entries);
    }

    private static void write(RegistryByteBuf buf, RecipeOutputCompound recipeOutputCompound) {
        List<Pair<ItemStack, Double>> rawDropsAndChances = recipeOutputCompound.getRawDropsAndChances();

        int size = rawDropsAndChances.size();
        PacketCodecs.VAR_INT.encode(buf, size);

        for (Pair<ItemStack, Double> pair : rawDropsAndChances) {
            ItemStack.PACKET_CODEC.encode(buf, pair.getFirst());
            PacketCodecs.DOUBLE.encode(buf, pair.getSecond());
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

        public Builder chance(Item item, double chance) {
            return chance(new ItemStack(item), chance);
        }

        public Builder chance(Item item, int count, double chance) {
            return chance(new ItemStack(item, count), chance);
        }

        public RecipeOutputCompound build() {
            return new RecipeOutputCompound(pairs);
        }
    }
}
