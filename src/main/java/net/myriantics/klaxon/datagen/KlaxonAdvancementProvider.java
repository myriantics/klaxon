package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;
import net.myriantics.klaxon.advancement.criterion.AnvilRepairCriterion;
import net.myriantics.klaxon.advancement.criterion.HammerUseCriterion;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
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
        AdvancementEntry preludeRoot = generatePreludeRoot(consumer);
        generatePreludeAdvancements(consumer, preludeRoot);
    }

    private AdvancementEntry generatePreludeRoot(Consumer<AdvancementEntry> consumer) {

        return Advancement.Builder.create()
                .display(
                        KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.asItem(),
                        Text.translatable("advancements.klaxon_prelude.root.title"),
                        Text.translatable("advancements.klaxon_prelude.root.description"),
                        KlaxonCommon.locate("textures/gui/advancements/backgrounds/klaxon_prelude.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("blast_processor_obtain", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS)))
                .build(consumer, getKlaxonPreludeIdString("root"));
    }

    private void generatePreludeAdvancements(Consumer<AdvancementEntry> consumer, AdvancementEntry preludeRoot) {
        AdvancementEntry watchBlastProcessorCraft = Advancement.Builder.create()
                .parent(preludeRoot)
                .display(
                        KlaxonItems.FRACTURED_RAW_IRON,
                        Text.translatable("advancements.klaxon_prelude.watch_blast_processor_craft.title"),
                        Text.translatable("advancements.klaxon_prelude.watch_blast_processor_craft.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("watch_blast_processor_craft", BlockActivationCriterion.Conditions.create(KlaxonBlockTags.BLAST_PROCESSORS))
                .build(consumer, getKlaxonPreludeIdString("watch_blast_processor_craft"));
        AdvancementEntry successfullyHammerDroppedItem = Advancement.Builder.create()
                .parent(watchBlastProcessorCraft)
                .display(
                        KlaxonItems.CRUDE_STEEL_PLATE,
                        Text.translatable("advancements.klaxon_prelude.use_hammer_on_dropped_item.title"),
                        Text.translatable("advancements.klaxon_prelude.use_hammer_on_dropped_item.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("use_hammer_on_dropped_item", HammerUseCriterion.Conditions.createRecipeSuccess())
                .build(consumer, getKlaxonPreludeIdString("use_hammer_on_dropped_item"));
        AdvancementEntry successfullyHammerWalljump = Advancement.Builder.create()
                .parent(watchBlastProcessorCraft)
                .display(
                        KlaxonItems.STEEL_HAMMER,
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump.title"),
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("successful_hammer_walljump", HammerUseCriterion.Conditions.createWalljump(true))
                .build(consumer, getKlaxonPreludeIdString("successful_hammer_walljump"));
        AdvancementEntry strengthHammerWalljump = Advancement.Builder.create()
                .parent(successfullyHammerWalljump)
                .display(
                        Items.BLAZE_POWDER,
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump_strength.title"),
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump_strength.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .criterion("successful_hammer_walljump", HammerUseCriterion.Conditions.createStrengthWalljump())
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(consumer, getKlaxonPreludeIdString("successful_strength_hammer_walljump"));
        AdvancementEntry steelArmorJuggernaut = Advancement.Builder.create()
                .parent(successfullyHammerDroppedItem)
                .display(
                        KlaxonItems.STEEL_CHESTPLATE,
                        Text.translatable("advancements.klaxon_prelude.wear_full_steel_armor.title"),
                        Text.translatable("advancements.klaxon_prelude.wear_full_steel_armor.description"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .criterion("steel_armor", InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_HELMET, KlaxonItems.STEEL_CHESTPLATE, KlaxonItems.STEEL_LEGGINGS, KlaxonItems.STEEL_BOOTS))
                .build(consumer, getKlaxonPreludeIdString("steel_armor"));
        AdvancementEntry makeshiftItemFullRepair = Advancement.Builder.create()
                .parent(successfullyHammerDroppedItem)
                .display(
                        Items.ANVIL,
                        Text.translatable("advancements.klaxon_prelude.makeshift_item_full_repair.title"),
                        Text.translatable("advancements.klaxon_prelude.makeshift_item_full_repair.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("makeshift_crafting_full_repair", AnvilRepairCriterion.Conditions.createFromTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT, 0.0))
                .build(consumer, getKlaxonPreludeIdString("makeshift_crafting_full_repair"));
    }

    private static String getKlaxonPreludeIdString(String path) {
        return "klaxon_prelude/" + path;
    }
}
