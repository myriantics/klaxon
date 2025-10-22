package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.Optional;

public abstract class ExplosiveCatalystDefinitionRecipeLogic {
    public static ExplosiveCatalystData computeExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemConvertible item) {
        return computeExplosiveCatalystData(world, pos, blastProcessor, new ItemStack(item));
    }

    public static ExplosiveCatalystData computeExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemStack stack) {
        return computeExplosiveCatalystData(world, pos, blastProcessor, new ExplosiveCatalystDefinitionRecipeInput(stack));
    }

    public static ExplosiveCatalystData computeExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput input) {
        if (input.catalystStack().get(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA_OVERRIDE_COMPONENT) instanceof ExplosiveCatalystData data) {
            return data.behavior().value().transformExplosiveCatalystData(world, pos, blastProcessor, data);
        } else {
            Optional<RecipeEntry<ExplosiveCatalystDefinitionRecipe>> match = world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION, input, world);
            if (match.isPresent()) {
                ExplosiveCatalystData data = match.get().value().getData();
                return data.behavior().value().transformExplosiveCatalystData(world, pos, blastProcessor, data);
            }
        }

        return ExplosiveCatalystData.ZERO;
    }
}
