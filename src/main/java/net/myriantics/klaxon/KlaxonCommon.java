package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.KlaxonDatagenPhantomItems;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.registry.behavior.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.behavior.KlaxonDispenserBehaviors;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.block.KlaxonOxidationRegistry;
import net.myriantics.klaxon.registry.block.KlaxonStrippedBlocksRegistry;
import net.myriantics.klaxon.registry.entity.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.entity.KlaxonStatusEffects;
import net.myriantics.klaxon.registry.item.*;
import net.myriantics.klaxon.registry.misc.*;
import net.myriantics.klaxon.registry.worldgen.KlaxonSaplingGenerators;
import net.myriantics.klaxon.registry.worldgen.KlaxonWorldgenFeatures;
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
		KlaxonBlockItems.init();
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
		KlaxonCompostableRegistry.init();
		KlaxonParticleTypes.init();
		KlaxonEntityTypes.init();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}