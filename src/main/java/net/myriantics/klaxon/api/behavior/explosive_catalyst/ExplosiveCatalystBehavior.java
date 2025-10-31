package net.myriantics.klaxon.api.behavior.explosive_catalyst;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public interface ExplosiveCatalystBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean modifyWorld);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ExplosiveCatalystData powerData);

    ExplosiveCatalystData transformExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data);

    BlastProcessingRecipeData getBlastProcessingPreviewData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory);

    boolean isVariable();

    default boolean isIn(TagKey<ExplosiveCatalystBehavior> tagKey) {
        return getRegistryEntry().isIn(tagKey);
    }

    Codec<RegistryEntry<ExplosiveCatalystBehavior>> ENTRY_CODEC = KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.getEntryCodec();

    PacketCodec<RegistryByteBuf, RegistryEntry<ExplosiveCatalystBehavior>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(KlaxonRegistryKeys.EXPLOSIVE_CATALYST_BEHAVIOR);

    default RegistryEntry<ExplosiveCatalystBehavior> getRegistryEntry() {
        return KlaxonRegistries.EXPLOSIVE_CATALYST_BEHAVIORS.getEntry(this);
    }
}