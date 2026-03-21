package net.myriantics.klaxon.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BlockIngredient implements Predicate<BlockState> {

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> PACKET_CODEC = ByteBufCodecs.holderSet(Registries.BLOCK)
            .map(
                    list -> ofEntries(list.stream().map(BlockIngredient.BlockEntry::new)),
                    blockIngredient -> HolderSet.direct(Arrays.stream(blockIngredient.getMatchingBlocks()).map(BuiltInRegistries.BLOCK::wrapAsHolder).toList())
            );
    public static final BlockIngredient EMPTY = new BlockIngredient(new Entry[0]);
    private final BlockIngredient.Entry[] entries;
    @Nullable
    private Block[] matchingBlocks;
    @Nullable
    private ItemStack[] displayStacksCache = null;
    @Nullable
    private IntList ids;
    public static final Codec<BlockIngredient> ALLOW_EMPTY_CODEC = createCodec(true);
    public static final Codec<BlockIngredient> DISALLOW_EMPTY_CODEC = createCodec(false);

    private BlockIngredient(Stream<? extends Entry> entryStream) {
        this.entries = entryStream.toArray(Entry[]::new);
    }

    private BlockIngredient(BlockIngredient.Entry[] entries) {
        this.entries = entries;
    }

    private Block[] getMatchingBlocks() {
        if (this.matchingBlocks == null) {
            Block[] matchingBlocks = new Block[0];

            for (Entry entry : entries) {
                Block[] entryBlocks = entry.getBlocks();

                Block[] combinedBlocks = new Block[matchingBlocks.length + entryBlocks.length];
                System.arraycopy(matchingBlocks, 0, combinedBlocks, 0, matchingBlocks.length);
                System.arraycopy(entryBlocks, 0, combinedBlocks, matchingBlocks.length, entryBlocks.length);

                matchingBlocks = combinedBlocks;
            }

            this.matchingBlocks = matchingBlocks;
        }

        return matchingBlocks;
    }

    public ItemStack[] getDisplayStacks() {
        if (displayStacksCache == null) {
            int size = getMatchingBlocks().length;
            ItemStack[] displayStacks = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                Block block = getMatchingBlocks()[i];

                // add newly made stack to
                displayStacks[i] = KlaxonBlockItems.getBlockDisplayStack(block);
            }

            // cache the newly computed stacks
            this.displayStacksCache = displayStacks;

            return displayStacks;
        } else {
            return displayStacksCache;
        }
    }

    public boolean test(@Nullable BlockState state) {
        if (state == null) {
            return false;
        } else if (this.isEmpty()) {
            return true;
        } else {
            for (Block block : this.getMatchingBlocks()) {
                if (state.is(block)) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList getMatchingBlockIds() {
        if (this.ids == null) {
            Block[] blocks = this.getMatchingBlocks();
            this.ids = new IntArrayList(blocks.length);

            for (Block block : blocks) {
                this.ids.add(BuiltInRegistries.BLOCK.getId(block));
            }

            this.ids.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.ids;
    }

    public boolean isEmpty() {
        return this.entries.length == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockIngredient blockIngredient && Arrays.equals(this.entries, blockIngredient.entries);
    }

    private static BlockIngredient ofEntries(Stream<? extends Entry> entries) {
        BlockIngredient blockIngredient = new BlockIngredient(entries);
        return blockIngredient.isEmpty() ? EMPTY : blockIngredient;
    }

    public BlockIngredient empty() {
        return EMPTY;
    }

    public static BlockIngredient ofBlocks(Block... blocks) {
        return ofEntries(Arrays.stream(blocks).map((block -> new BlockEntry(BuiltInRegistries.BLOCK.wrapAsHolder(block)))));
    }

    public static BlockIngredient fromTag(TagKey<Block> tag) {
        return ofEntries(Stream.of(new TagEntry(tag)));
    }

    private static Codec<BlockIngredient> createCodec(boolean allowEmpty) {
        Codec<BlockIngredient.Entry[]> codec = Codec.list(Entry.CODEC)
                .comapFlatMap(
                        entries -> !allowEmpty && entries.isEmpty()
                                ? DataResult.error(() -> "Block array cannot be empty, at least one block must be defined")
                                : DataResult.success(entries.toArray(new Entry[0])),
                        List::of
                );
        return Codec.either(codec, Entry.CODEC)
                .flatComapMap(
                        either -> either.map(BlockIngredient::new, entry -> new BlockIngredient(new Entry[] {entry})),
                        blockIngredient -> {
                            if (blockIngredient.entries.length == 1) {
                                return DataResult.success(Either.right(blockIngredient.entries[0]));
                            } else {
                                return blockIngredient.entries.length == 0 && !allowEmpty
                                        ? DataResult.error(() -> "Block array cannot be empty, at least one block must be defined")
                                        : DataResult.success(Either.left(blockIngredient.entries));
                            }
                        }
                );
    }

    interface Entry {
        Codec<BlockIngredient.Entry> CODEC = Codec.xor(BlockEntry.CODEC, TagEntry.CODEC)
                .xmap(either -> either.map(stackEntry -> stackEntry, tagEntry -> tagEntry), entry -> {
                    if (entry instanceof BlockIngredient.TagEntry tagEntry) {
                        return Either.right(tagEntry);
                    } else if (entry instanceof BlockIngredient.BlockEntry blockEntry) {
                        return Either.left(blockEntry);
                    } else {
                        throw new UnsupportedOperationException("This is neither a block value nor a block tag value");
                    }
                });

        Block[] getBlocks();
    }

    record BlockEntry(Holder<Block> block) implements Entry {
        static final Codec<BlockIngredient.BlockEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(BlockEntry::block)
                ).apply(
                        instance, BlockEntry::new
                )
        );

        @Override
        public Block[] getBlocks() {
            return new Block[] {block.value()};
        }
    }

    record TagEntry(TagKey<Block> blockTagKey) implements Entry {
        static final Codec<BlockIngredient.TagEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(TagEntry::blockTagKey)
                ).apply(
                        instance, TagEntry::new
                )
        );

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TagEntry tagEntry && tagEntry.blockTagKey.location().equals(this.blockTagKey.location());
        }

        @Override
        public Block[] getBlocks() {
            Optional<HolderSet.Named<Block>> entries = BuiltInRegistries.BLOCK.getTag(blockTagKey);
            if (entries.isPresent()) {
                Block[] blocks = new Block[entries.get().size()];
                for (int i = 0; i < blocks.length; i++) {
                    blocks[i] = entries.get().get(i).value();
                }
                return blocks;
            } else {
                return new Block[0];
            }
        }
    }
}
