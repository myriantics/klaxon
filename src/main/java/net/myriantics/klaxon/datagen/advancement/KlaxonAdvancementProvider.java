package net.myriantics.klaxon.datagen.advancement;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.advancement.criterion.BlockActivationCriterion;
import net.myriantics.klaxon.advancement.criterion.AnvilRepairCriterion;
import net.myriantics.klaxon.advancement.criterion.WalljumpAbilityCriterion;
import net.myriantics.klaxon.advancement.criterion.ToolUsageRecipeCraftCriterion;
import net.myriantics.klaxon.datagen.advancement.providers.KlaxonPreludeAdvancementProvider;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.registry.minecraft.KlaxonItems;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
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
        AdvancementEntry preludeRoot = new KlaxonPreludeAdvancementProvider(consumer).generateAdvancements();
    }
}
