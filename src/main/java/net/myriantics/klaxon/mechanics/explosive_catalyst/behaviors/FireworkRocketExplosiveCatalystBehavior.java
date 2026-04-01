package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;

public class FireworkRocketExplosiveCatalystBehavior extends AbstractExplosiveCatalystBehavior {

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        if (context.components().get(DataComponents.FIREWORKS) instanceof Fireworks component) {
            boolean producesFire = original.producesFire();
            double explosionPower = original.explosionPower();

            // compute explosion power data from gunpowder
            ExplosiveCatalystData gunpowderData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, Items.GUNPOWDER);

            // add explosion power for the flight duration
            producesFire = producesFire || gunpowderData.producesFire();
            explosionPower += component.flightDuration() * gunpowderData.explosionPower();

            for (FireworkExplosion explosionComponent : component.explosions()) {
                // prepare firework star stack with selected component
                ItemStack starStack = new ItemStack(Items.FIREWORK_STAR);
                starStack.applyComponents(DataComponentMap.builder().set(DataComponents.FIREWORK_EXPLOSION, explosionComponent).build());

                // get explosion power data from star stack
                ExplosiveCatalystData fireworkStarData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, starStack);

                // append values to stats
                producesFire = producesFire || fireworkStarData.producesFire();
                explosionPower += fireworkStarData.explosionPower();
            }

            // each recipe produces 3 rockets, so our actual value is 1/3 of what was calculated - then round to the nearest tenth.
            explosionPower /= 3;

            return new ExplosiveCatalystData(this, explosionPower, producesFire);
        }

        return original;
    }

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        DataComponentMap components = context.components();
        if (components.get(DataComponents.FIREWORKS) instanceof Fireworks) {
            Level level = context.level();
            ItemStack rocketStack = new ItemStack(Items.FIREWORK_ROCKET);
            rocketStack.applyComponents(components);
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(level, detonationPosition.x(), detonationPosition.y(), detonationPosition.z(), rocketStack);

            // explode using firework rocket entity code - summons dummy firework and detonates it
            level.addFreshEntity(fireworkRocket);

            // TIL you can't have an invoker method be the same name as the original method. The more you know!
            ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
        }
    }
}
