package net.myriantics.klaxon.mixin.minecraft.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootContextTypes.class)
public interface LootContextTypesAccessor {
    @Accessor(value = "MAP")
    static BiMap<Identifier, LootContextType> klaxon$getLootContextTypeMap() {
        throw new AssertionError();
    };
}
