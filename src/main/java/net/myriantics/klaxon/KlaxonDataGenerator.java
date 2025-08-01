package net.myriantics.klaxon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.datagen.*;
import net.myriantics.klaxon.datagen.advancement.KlaxonAdvancementProvider;
import net.myriantics.klaxon.datagen.loot_table.KlaxonBlockLootTableProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.tag.*;

public class KlaxonDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		// make modded items show up for datagen purposes

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(KlaxonDamageTypeProviderImpl::new);
		pack.addProvider(KlaxonModelProvider::new);
		pack.addProvider(KlaxonItemTagProvider::new);
		pack.addProvider(KlaxonBlockTagProvider::new);
		pack.addProvider(KlaxonDamageTypeTagProvider::new);
		pack.addProvider(KlaxonEntityTypeTagProvider::new);
		pack.addProvider(KlaxonBlockLootTableProvider::new);
		pack.addProvider(KlaxonRecipeProvider::new);
		pack.addProvider(KlaxonAdvancementProvider::new);
		pack.addProvider(KlaxonStatusEffectTagProvider::new);
		pack.addProvider(KlaxonBlockEntityTypeTagProvider::new);
		pack.addProvider(KlaxonFluidTagProvider::new);
		pack.addProvider(KlaxonFeatureProvider::new);
		// pack.addProvider(KlaxonEnUsLanguageProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, KlaxonFeatureProvider::generateConfiguredFeatures);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, KlaxonFeatureProvider::generatePlacedFeatures);
	}
}
