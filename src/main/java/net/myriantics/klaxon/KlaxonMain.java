package net.myriantics.klaxon;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.blockentities.blast_chamber.BlastChamberScreenHandler;
import net.myriantics.klaxon.entity.KlaxonEntities;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.recipes.hammer.HammerRecipeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KlaxonMain implements ModInitializer {
	public static final String MOD_ID = "klaxon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ScreenHandlerType<BlastChamberScreenHandler> BLAST_CHAMBER_SCREEN_HANDLER;

	static {
		BLAST_CHAMBER_SCREEN_HANDLER = new ScreenHandlerType<>(BlastChamberScreenHandler::new, FeatureSet.empty());
	}

	@Override
	public void onInitialize() {

		KlaxonBlocks.registerModBlocks();
		KlaxonBlockEntities.registerBlockEntities();
		KlaxonItems.registerModItems();
		KlaxonEntities.registerModEntities();
		Registry.register(Registries.RECIPE_SERIALIZER, HammerRecipeSerializer.ID,
				HammerRecipeSerializer.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, new Identifier("hammer_recipe", HammerRecipe.Type.ID), HammerRecipe.Type.INSTANCE);

		LOGGER.info("Klaxon has loaded - i remembered to change this :)");
	}
}