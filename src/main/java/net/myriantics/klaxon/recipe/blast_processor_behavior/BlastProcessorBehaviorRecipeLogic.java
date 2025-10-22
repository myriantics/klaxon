package net.myriantics.klaxon.recipe.blast_processor_behavior;

import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.behavior.KlaxonBlastProcessorCatalystBehaviors;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.Optional;

public abstract class BlastProcessorBehaviorRecipeLogic {

    public static ExplosiveCatalystBehavior computeBehavior(World world, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        // get blast processor behavior from recipe
        Optional<RecipeEntry<BlastProcessorBehaviorRecipe>> behaviorRecipe = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.BLAST_PROCESSOR_BEHAVIOR, recipeInventory, world);

        // initialize as the default behavior
        ExplosiveCatalystBehavior blastProcessorBehavior = KlaxonBlastProcessorCatalystBehaviors.DEFAULT;

        // replace with new behavior if valid
        if (behaviorRecipe.isPresent()) {
            Identifier behaviorId = behaviorRecipe.get().value().getBehaviorId();

            ExplosiveCatalystBehavior interimBehavior = KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.get(behaviorId);

            if (interimBehavior != null) {
                blastProcessorBehavior = interimBehavior;
            }
        }

        return blastProcessorBehavior;
    }
}
