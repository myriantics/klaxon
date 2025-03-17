package net.myriantics.klaxon.advancement.criterion;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.KlaxonCommon;

public class BlockActivationCriterion extends AbstractCriterion<BlockActivationCriterion.Conditions> {
    static final Identifier ID = KlaxonCommon.locate("block_activation");

    @Override
    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, BlockPos pos) {
        this.trigger(player, conditions -> conditions.matches(player.getServerWorld(), pos));
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        BlockPredicate acceptedBlocks = BlockPredicate.fromJson(jsonObject.get("accepted_blocks"));
        return Conditions.create(playerPredicate, acceptedBlocks);
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final BlockPredicate acceptedBlocks;

        public Conditions(LootContextPredicate player, BlockPredicate acceptedBlockStates) {
            super(BlockActivationCriterion.ID, player);
            this.acceptedBlocks = acceptedBlockStates;
        }

        public static BlockActivationCriterion.Conditions create(TagKey<Block> blockTag) {
            return create(LootContextPredicate.EMPTY, BlockPredicate.Builder.create().tag(blockTag).build());
        }

        public static BlockActivationCriterion.Conditions create(LootContextPredicate player, BlockPredicate acceptedBlocks) {
            return new BlockActivationCriterion.Conditions(player, acceptedBlocks);
        }

        boolean matches(ServerWorld serverWorld, BlockPos pos) {
            return acceptedBlocks.test(serverWorld, pos);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("accepted_blocks", this.acceptedBlocks.toJson());
            return jsonObject;
        }
    }
}
