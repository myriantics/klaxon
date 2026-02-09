package net.myriantics.klaxon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.datagen.*;
import net.myriantics.klaxon.datagen.advancement.KlaxonAdvancementProvider;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistryProvider;
import net.myriantics.klaxon.datagen.custom.providers.KlaxonDamageTypeProvider;
import net.myriantics.klaxon.datagen.custom.providers.KlaxonToolUsageRecipeTypeProvider;
import net.myriantics.klaxon.datagen.custom.providers.KlaxonVeinmineGroupProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.loot_table.KlaxonBlockLootTableProvider;
import net.myriantics.klaxon.datagen.loot_table.KlaxonGameplayLootTableProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;
import net.myriantics.klaxon.datagen.tag.*;

public class KlaxonDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(KlaxonDynamicRegistryProvider::new);
		pack.addProvider(KlaxonModelProvider::new);

        // loot tables
		pack.addProvider(KlaxonBlockLootTableProvider::new);
        pack.addProvider(KlaxonGameplayLootTableProvider::new);

		pack.addProvider(KlaxonRecipeProvider::new);
		pack.addProvider(KlaxonAdvancementProvider::new);

        pack.addProvider(KlaxonEnchantmentProvider::new);
		pack.addProvider(KlaxonFeatureProvider::new);
        pack.addProvider(KlaxonArmorTrimMaterialProvider::new);
		pack.addProvider(KlaxonEnUsLanguageProvider::new);

        // tags
        pack.addProvider(KlaxonItemTagProvider::new);
        pack.addProvider(KlaxonBlockTagProvider::new);
        pack.addProvider(KlaxonDamageTypeTagProvider::new);
        pack.addProvider(KlaxonEntityTypeTagProvider::new);
        pack.addProvider(KlaxonFluidTagProvider::new);
        pack.addProvider(KlaxonStatusEffectTagProvider::new);
        pack.addProvider(KlaxonBlockEntityTypeTagProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, KlaxonFeatureProvider::generateConfiguredFeatures);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, KlaxonFeatureProvider::generatePlacedFeatures);
		registryBuilder.addRegistry(RegistryKeys.TRIM_MATERIAL, KlaxonArmorTrimMaterialProvider::generateArmorTrimMaterials);
        registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, KlaxonEnchantmentProvider::bootstrap);
	}
}
