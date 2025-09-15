package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.datagen.KlaxonDatagenCompatIds;
import net.myriantics.klaxon.registry.*;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementCriteria;
import net.myriantics.klaxon.registry.behavior.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.behavior.KlaxonDispenserBehaviors;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.block.KlaxonOxidationRegistry;
import net.myriantics.klaxon.registry.block.KlaxonStrippedBlocksRegistry;
import net.myriantics.klaxon.registry.entity.*;
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

    public static String locateAlt(String name) {
        return MOD_ID + "." + name;
    }

    @Override
	public void onInitialize() {
		KlaxonWorldgenFeatures.init();
		KlaxonSaplingGenerators.init();
		// KlaxonEntityAttributes.init(); // Not currently used
		KlaxonBlocks.init();
		KlaxonBlockItems.init();
		KlaxonBlockEntities.init();
		KlaxonArmorMaterials.init();
		KlaxonItems.init();
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
		KlaxonGameRules.init();
		KlaxonFuelRegistry.init();
		KlaxonOxidationRegistry.init();
		KlaxonEventListeners.init();
		KlaxonDispenserBehaviors.init();
		KlaxonStrippedBlocksRegistry.init();
		KlaxonCompostableRegistry.init();
		KlaxonParticleTypes.init();
		KlaxonEntityTypes.init();
        KlaxonEntityAttributes.init();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}