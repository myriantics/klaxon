package net.myriantics.klaxon.mechanics.explosive_catalyst;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public interface ExplosiveCatalystBehavior {

    void onExplosion(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean modifyWorld);

    void ejectItems(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ExplosiveCatalystData powerData);

    ExplosiveCatalystData transformExplosiveCatalystData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data);

    BlastProcessingRecipeData getBlastProcessingPreviewData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    boolean shouldRunDispenserEffects(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory);

    boolean isVariable();

    default boolean isIn(TagKey<ExplosiveCatalystBehavior> tagKey) {
        return getRegistryEntry().is(tagKey);
    }

    Codec<Holder<ExplosiveCatalystBehavior>> ENTRY_CODEC = KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.holderByNameCodec();

    StreamCodec<RegistryFriendlyByteBuf, Holder<ExplosiveCatalystBehavior>> ENTRY_PACKET_CODEC = ByteBufCodecs.holderRegistry(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR);

    default Holder<ExplosiveCatalystBehavior> getRegistryEntry() {
        return KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.wrapAsHolder(this);
    }
}