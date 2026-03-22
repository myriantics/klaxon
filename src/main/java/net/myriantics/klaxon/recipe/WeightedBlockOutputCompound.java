package net.myriantics.klaxon.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class WeightedBlockOutputCompound {
    private final Map<Block, Integer> blocksAndRawWeights;
    private final Map<Block, Double> blocksAndCompiledWeights;
    private ItemStack[] displayStacksCache = null;

    private WeightedBlockOutputCompound(Map<Block, Integer> blocksAndRawWeights) {
        this.blocksAndRawWeights = blocksAndRawWeights;

        Block[] sortedBlocks = blocksAndRawWeights.keySet().toArray(new Block[0]);
        Arrays.sort(sortedBlocks, Comparator.comparingInt(blocksAndRawWeights::get));

        if (blocksAndRawWeights.isEmpty()) {
            throw new IllegalStateException("Must have at least one block output defined");
        }

        // add up all the weights in the set
        int totalIntWeight = 0;
        for (int weight : blocksAndRawWeights.values()) {
            totalIntWeight += weight;
        }

        // if no weight was defined set output to nothing, guaranteed.
        if (totalIntWeight == 0) {
            this.blocksAndCompiledWeights = Map.of();
            return;
        }

        HashMap<Block, Double> blocksAndCompiledWeights = HashMap.newHashMap(blocksAndRawWeights.size());

        // get all the ratios and plonk them into the map
        for (Block block : sortedBlocks) {
            double dividedWeight = (double) blocksAndRawWeights.get(block) / totalIntWeight;

            blocksAndCompiledWeights.put(block, dividedWeight);
        }

        this.blocksAndCompiledWeights = blocksAndCompiledWeights;
    }

    public ItemStack[] getDisplayStacks() {
        if (this.displayStacksCache != null && this.displayStacksCache.length != 0) {
            return this.displayStacksCache;
        } else {
            int size = blocksAndCompiledWeights.size();
            ItemStack[] displayStacks = new ItemStack[size];
            Block[] blocks = blocksAndCompiledWeights.keySet().toArray(new Block[0]);


            for (int i = 0; i < size; i++) {
                Block block = blocks[i];
                double chance = blocksAndCompiledWeights.get(block);

                // prime the display stack and builder
                ItemStack displayStack;
                DataComponentMap.Builder changesBuilder = DataComponentMap.builder();

                // create items based off of blockitems if possible.
                // if a block doesn't have a blockitem, fall back to barrier with lore
                if (block.asItem().equals(Items.AIR)) {
                    displayStack = new ItemStack(Items.BARRIER);

                    // add component changes to builder
                    changesBuilder
                            .set(DataComponents.CUSTOM_NAME, block.getName().withStyle(ChatFormatting.RED))
                            .set(DataComponents.LORE, new ItemLore(
                                    List.of(Component.translatable("klaxon.text.tooltip.lore.missing_block_item")
                                            .withStyle(ChatFormatting.BOLD))
                            ));
                } else {
                    displayStack = new ItemStack(block.asItem());
                }

                // add chance lore and apply component changes
                displayStack.applyComponents(
                        changesBuilder
                                .set(KlaxonDataComponentTypes.RECIPE_OUTPUT_CHANCE_LORE.value(), chance)
                                .build()
                );

                // add newly made stack to
                displayStacks[i] = displayStack;
            }

            // cache the newly computed stacks
            this.displayStacksCache = displayStacks;

            return displayStacks;
        }
    }

    public @Nullable Block computeOutputBlock(RandomSource random) {
        double value = random.nextDouble();

        for (Block block : blocksAndCompiledWeights.keySet()) {
            if (value <= blocksAndRawWeights.get(block)) {
                return block;
            }
        }

        return null;
    }

    public static MapCodec<WeightedBlockOutputCompound> CODEC = Codec.simpleMap(
            BuiltInRegistries.BLOCK.holderByNameCodec().xmap(
                    Holder::value,
                    BuiltInRegistries.BLOCK::wrapAsHolder
            ).fieldOf("block").codec(),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("weight").codec(),
            BuiltInRegistries.BLOCK
    ).xmap(
            WeightedBlockOutputCompound::new,
            compound -> compound.blocksAndRawWeights
    );

    public static StreamCodec<RegistryFriendlyByteBuf, WeightedBlockOutputCompound> PACKET_CODEC = StreamCodec.of(
            WeightedBlockOutputCompound::write, WeightedBlockOutputCompound::read
    );

    private static void write(RegistryFriendlyByteBuf buf, WeightedBlockOutputCompound compound) {
        int size = compound.blocksAndRawWeights.size();

        ByteBufCodecs.VAR_INT.encode(buf, size);
        for (Block block : compound.blocksAndRawWeights.keySet()) {
            KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, block);
            ByteBufCodecs.VAR_INT.encode(buf, compound.blocksAndRawWeights.get(block));
        }
    }

    private static WeightedBlockOutputCompound read(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        HashMap<Block, Integer> entries = HashMap.newHashMap(size);

        for (int i = 0; i < size; i++) {
            entries.put(
                    KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf)
            );
        }

        return new WeightedBlockOutputCompound(entries);
    }

    public static class Builder {
        private final HashMap<Block, Integer> weightEntries;

        public Builder(int size) {
            weightEntries = HashMap.newHashMap(size);
        }

        public Builder add(Block block, int weight) {
            weightEntries.put(block, weight);
            return this;
        }

        public WeightedBlockOutputCompound build() {
            return new WeightedBlockOutputCompound(weightEntries);
        }
    }
}
