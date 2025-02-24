package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class KlaxonAdvancementProvider extends FabricAdvancementProvider {
    public KlaxonAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry root = generateRoot(consumer);
        generateAdvancements(consumer, root);
    }

    private AdvancementEntry generateRoot(Consumer<AdvancementEntry> consumer) {

        return Advancement.Builder.create()
                .display(
                        KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem(),
                        Text.translatable("advancements.klaxon.root.title"),
                        Text.translatable("advancements.klaxon.root.description"),
                        KlaxonCommon.locate("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("blast_processor_obtain", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)))
                .build(consumer, "klaxon/root");
    }

    private void generateAdvancements(Consumer<AdvancementEntry> consumer, AdvancementEntry root) {
        AdvancementEntry watchBlastProcessorCraft = Advancement.Builder.create()
                .parent(root)
                .display(
                        KlaxonItems.FRACTURED_RAW_IRON,
                        Text.translatable("advancements.klaxon.blast_processor_craft.title"),
                        Text.translatable("advancements.klaxon.blast_processor_craft.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("watch_blast_processor_craft", BlockActivationCriterion.Conditions.create(KlaxonBlockTags.BLAST_PROCESSORS))
                .build(consumer, "klaxon/watch_blast_processor_craft");
    }
}
