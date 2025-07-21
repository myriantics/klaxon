package net.myriantics.klaxon.recipe.blast_processor_behavior;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.behavior.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.Optional;

public abstract class BlastProcessorBehaviorRecipeLogic {

    public static BlastProcessorCatalystBehavior computeBehavior(World world, ExplosiveCatalystRecipeInput recipeInventory) {
        // get blast processor behavior from recipe
        Optional<RecipeEntry<BlastProcessorBehaviorRecipe>> behaviorRecipe = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR, recipeInventory, world);

        // initialize as the default behavior
        BlastProcessorCatalystBehavior blastProcessorBehavior = KlaxonBlastProcessorCatalystBehaviors.DEFAULT;

        // replace with new behavior if valid
        if (behaviorRecipe.isPresent()) {
            Identifier behaviorId = behaviorRecipe.get().value().getBehaviorId();

            BlastProcessorCatalystBehavior interimBehavior = KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.get(behaviorId);

            if (interimBehavior != null) {
                blastProcessorBehavior = interimBehavior;
            }
        }

        return blastProcessorBehavior;
    }
}
