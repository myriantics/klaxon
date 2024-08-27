package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.compat.emi.recipes.BlastProcessingEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.HammeringEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.ItemExplosionPowerEmiInfoRecipe;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.hammer.HammerRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipeSerializer;
import net.myriantics.klaxon.util.KlaxonTags;

import java.util.List;
import java.util.function.Function;

// spectrum's emi plugin used as reference
public class KlaxonEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        RecipeManager manager = registry.getRecipeManager();

        registerCategories(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registry.addCategory(KlaxonEmiRecipeCategories.HAMMERING);
        registry.addCategory(KlaxonEmiRecipeCategories.BLAST_PROCESSING);
        registry.addCategory(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER);

        registry.addWorkstation(KlaxonEmiRecipeCategories.HAMMERING, EmiIngredient.of(KlaxonTags.Blocks.HAMMER_INTERACTION_POINT));
        registry.addWorkstation(KlaxonEmiRecipeCategories.BLAST_PROCESSING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.ITEM_EXPLOSION_POWER, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
    }

    private void registerRecipes(EmiRegistry registry) {
        addAll(registry, HammerRecipe.Type.INSTANCE, HammeringEmiRecipe::new);
        addAll(registry, ItemExplosionPowerRecipe.Type.INSTANCE, ItemExplosionPowerEmiInfoRecipe::new);
        addAll(registry, BlastProcessorRecipe.Type.INSTANCE, (recipe -> new BlastProcessingEmiRecipe(recipe, registry)));
    }

    public <C extends Inventory, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<T, EmiRecipe> constructor) {
        for (T recipe : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }
}
