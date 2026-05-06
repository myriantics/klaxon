package net.myriantics.klaxon.mechanics.explosive_catalyst.handler;

import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public class FireworkExplosiveCatalystHandler extends ExplosiveCatalystHandler {
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
