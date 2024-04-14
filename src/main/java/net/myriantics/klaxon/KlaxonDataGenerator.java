package net.myriantics.klaxon;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.myriantics.klaxon.datagen.KlaxonItemTagProvider;
import net.myriantics.klaxon.datagen.KlaxonModelProvider;

public class KlaxonDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(KlaxonModelProvider::new);
		pack.addProvider(KlaxonItemTagProvider::new);
	}
}
