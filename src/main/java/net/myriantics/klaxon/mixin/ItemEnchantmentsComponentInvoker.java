package net.myriantics.klaxon.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemEnchantmentsComponent.class)
public interface ItemEnchantmentsComponentInvoker {

    @Invoker(value = "getTooltipOrderList")
    public static <T> RegistryEntryList<T> klaxon$getTooltipOrderList(@Nullable RegistryWrapper.WrapperLookup registryLookup, RegistryKey<Registry<T>> registryRef, TagKey<T> tooltipOrderTag) {
        throw new AssertionError();
    }
}
