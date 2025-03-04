package net.myriantics.klaxon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.myriantics.klaxon.datagen.*;
import net.myriantics.klaxon.datagen.recipe.KlaxonRecipeProvider;

public class KlaxonDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		// make modded items show up for datagen purposes

		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(KlaxonModelProvider::new);
		pack.addProvider(KlaxonItemTagProvider::new);
		pack.addProvider(KlaxonBlockTagProvider::new);
		pack.addProvider(KlaxonDamageTypeTagProvider::new);
		pack.addProvider(KlaxonEntityTypeTagProvider::new);
		pack.addProvider(KlaxonBlockLootTableProvider::new);
		pack.addProvider(KlaxonRecipeProvider::new);
		pack.addProvider(KlaxonAdvancementProvider::new);
		pack.addProvider(KlaxonStatusEffectTagProvider::new);
	}
}
