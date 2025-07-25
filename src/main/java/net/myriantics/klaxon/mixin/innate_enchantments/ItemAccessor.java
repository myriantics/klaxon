package net.myriantics.klaxon.mixin.innate_enchantments;

import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {

    @Accessor("components")
    public void klaxon$setComponentMap(ComponentMap components);
}
