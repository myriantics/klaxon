package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

import java.util.List;

public class FireworkStarBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public FireworkStarBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput craftingInventory) {
        ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponentTypes.FIREWORK_EXPLOSION) instanceof FireworkExplosionComponent component)  {
            // creeper-shaped fireworks boost explosion. will add different behaviors to KLAXON-specific firework shapes sometime in future.
            return new ItemExplosionPowerData(component.shape().equals(FireworkExplosionComponent.Type.CREEPER) ? 1.2 : 0.5, false);
        } else {
            return new ItemExplosionPowerData(0.3, false);
        }
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                0.3,
                1.2,
                Text.empty(),
                getId().getPath()
        );
    }
}
