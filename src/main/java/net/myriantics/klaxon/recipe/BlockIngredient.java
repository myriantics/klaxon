package net.myriantics.klaxon.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BlockIngredient implements Predicate<BlockState> {

    public static final PacketCodec<RegistryByteBuf, BlockIngredient> PACKET_CODEC = PacketCodecs.registryEntryList(RegistryKeys.BLOCK)
            .xmap(
                    list -> ofEntries(list.stream().map(BlockIngredient.BlockEntry::new)),
                    blockIngredient -> RegistryEntryList.of(Arrays.stream(blockIngredient.getMatchingBlocks()).map(Registries.BLOCK::getEntry).toList())
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
                if (state.isOf(block)) {
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
                this.ids.add(Registries.BLOCK.getRawId(block));
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
        return ofEntries(Arrays.stream(blocks).map((block -> new BlockEntry(Registries.BLOCK.getEntry(block)))));
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

    record BlockEntry(RegistryEntry<Block> block) implements Entry {
        static final Codec<BlockIngredient.BlockEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter(BlockEntry::block)
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
                        TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(TagEntry::blockTagKey)
                ).apply(
                        instance, TagEntry::new
                )
        );

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TagEntry tagEntry && tagEntry.blockTagKey.id().equals(this.blockTagKey.id());
        }

        @Override
        public Block[] getBlocks() {
            Optional<RegistryEntryList.Named<Block>> entries = Registries.BLOCK.getEntryList(blockTagKey);
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
