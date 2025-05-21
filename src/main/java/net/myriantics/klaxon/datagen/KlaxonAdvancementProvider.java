package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;
import net.myriantics.klaxon.advancement.criterion.AnvilRepairCriterion;
import net.myriantics.klaxon.advancement.criterion.WalljumpAbilityCriterion;
import net.myriantics.klaxon.advancement.criterion.ToolUsageRecipeCraftCriterion;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
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
                        Text.translatable("advancements.klaxon_prelude.use_hammer_to_make_plate.title"),
                        Text.translatable("advancements.klaxon_prelude.use_hammer_to_make_plate.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("use_hammer_to_make_plate", ToolUsageRecipeCraftCriterion.Conditions.createHammering(Ingredient.fromTag(KlaxonConventionalItemTags.PLATES)))
                .build(consumer, getKlaxonPreludeIdString("use_hammer_to_make_plate"));
        AdvancementEntry dealCleavingDamage = Advancement.Builder.create()
                .parent(successfullyHammerDroppedItem)
                .display(
                        KlaxonItems.STEEL_CLEAVER,
                        Text.translatable("advancements.klaxon_prelude.deal_cleaving_damage.title"),
                        Text.translatable("advancements.klaxon_prelude.deal_cleaving_damage.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("deal_cleaving_damage", InventoryChangedCriterion.Conditions.items(KlaxonItems.STEEL_CLEAVER))
                .build(consumer, getKlaxonPreludeIdString("deal_cleaving_damage"));
        AdvancementEntry obtainAnyRubberGlob = Advancement.Builder.create()
                .parent(watchBlastProcessorCraft)
                .display(
                        KlaxonItems.RUBBER_GLOB,
                        Text.translatable("advancements.klaxon_prelude.obtain_any_rubber_glob.title"),
                        Text.translatable("advancements.klaxon_prelude.obtain_any_rubber_glob.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("obtain_rubber_glob",  InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(KlaxonItemTags.MOLTEN_INCLUSIVE_RUBBER_GLOBS)))
                .build(consumer, getKlaxonPreludeIdString("obtain_rubber_glob"));
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
                .criterion("successful_hammer_walljump", WalljumpAbilityCriterion.Conditions.createWalljump(true))
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
                .criterion("successful_hammer_walljump", WalljumpAbilityCriterion.Conditions.createStrengthWalljump())
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(consumer, getKlaxonPreludeIdString("successful_strength_hammer_walljump"));
        AdvancementEntry minecartWalljump = Advancement.Builder.create()
                .parent(successfullyHammerWalljump)
                .display(
                        Items.CAULDRON,
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump_minecart.title"),
                        Text.translatable("advancements.klaxon_prelude.hammer_walljump_minecart.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true
                )
                .criterion("minecart_walljump", WalljumpAbilityCriterion.Conditions.createMinecartWalljump())
                .rewards(AdvancementRewards.Builder.experience(400))
                .build(consumer, getKlaxonPreludeIdString("minecart_walljump"));
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
