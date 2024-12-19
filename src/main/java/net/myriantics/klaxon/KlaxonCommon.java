package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.compat.KlaxonCompat;
import net.myriantics.klaxon.entity.KlaxonEntities;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.networking.KlaxonPackets;
import net.myriantics.klaxon.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import net.myriantics.klaxon.util.KlaxonScreenHandlers;
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
		KlaxonBlocks.registerModBlocks();
		KlaxonBlockEntities.registerBlockEntities();
		KlaxonItems.registerModItems();

		// if it's datagen, run my hacky hack of hacks
		if (isDatagenEnabled()) {
			KlaxonCompat.registerPhantomItemsForDatagen();
		}

		KlaxonEntities.registerModEntities();
		KlaxonRecipeTypes.registerSerializer();
		KlaxonDamageTypes.registerModDamageTypes();
		KlaxonPackets.registerModPackets();
		KlaxonPackets.registerC2SPacketRecievers();
		KlaxonScreenHandlers.registerModScreenHandlers();
		BlastProcessorBehavior.registerBlastProcessorBehaviors();

		LOGGER.info("KLAXON has loaded!");
	}

	public boolean isDatagenEnabled() {
		return FabricDataGenHelper.ENABLED;
	}
}