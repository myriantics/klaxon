package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
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

	static {
		}

	@Override
	public void onInitialize() {
		KlaxonBlocks.registerModBlocks();
		KlaxonBlockEntities.registerBlockEntities();
		BlastProcessorBehavior.registerBlastProcessorBehaviors();
		KlaxonItems.registerModItems();
		KlaxonEntities.registerModEntities();
		KlaxonRecipeTypes.registerSerializer();
		KlaxonDamageTypes.registerModDamageTypes();
		KlaxonPackets.registerModPackets();
		KlaxonScreenHandlers.registerModScreenHandlers();

		LOGGER.info("KLAXON has loaded!");
	}
}