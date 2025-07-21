package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.registry.minecraft.KlaxonArmorMaterials;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.custom.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.custom.KlaxonEventListeners;
import net.myriantics.klaxon.registry.minecraft.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonCommon implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier locate(String name) {
		return Identifier.of(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		KlaxonWorldgenFeatures.init();
		KlaxonSaplingGenerators.init();
		KlaxonEntityAttributes.init();
		KlaxonBlocks.init();
		KlaxonBlockEntities.init();
		KlaxonArmorMaterials.init();
		KlaxonItems.init();

		// if it's datagen, run my hacky hack of hacks
		if (isDatagenEnabled()) {
			KlaxonDatagenPhantomItems.registerPhantomItemsForDatagen();
		}

		// KlaxonEntities.registerModEntities();
		KlaxonRegistryKeys.init();
		KlaxonRegistries.init();
		KlaxonRecipeTypes.init();
		KlaxonDamageTypes.init();
		KlaxonPackets.init();
		KlaxonPackets.initC2SRecievers();
		KlaxonScreenHandlers.init();
		KlaxonItemGroups.init();
		KlaxonBlastProcessorCatalystBehaviors.init();
		KlaxonDataComponentTypes.init();
		KlaxonAdvancementCriteria.init();
		KlaxonStatusEffects.init();
		KlaxonToolMaterials.init();
		KlaxonGamerules.init();
		KlaxonFuelRegistry.init();
		KlaxonOxidationRegistry.init();
		KlaxonEventListeners.init();
		KlaxonDispenserBehaviors.init();
		KlaxonStrippedBlocksRegistry.init();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}