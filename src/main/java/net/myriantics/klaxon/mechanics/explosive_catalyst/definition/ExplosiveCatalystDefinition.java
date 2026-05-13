package net.myriantics.klaxon.mechanics.explosive_catalyst.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;

public record ExplosiveCatalystDefinition(Ingredient ingredient, ExplosiveCatalystData data) {


    public static final Codec<ExplosiveCatalystDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ExplosiveCatalystDefinition::ingredient),
            ExplosiveCatalystData.CODEC.fieldOf("data").forGetter(ExplosiveCatalystDefinition::data)
    ).apply(instance, ExplosiveCatalystDefinition::new));

    public ExplosiveCatalystDefinition(Holder<Item> holder, ExplosiveCatalystData data) {
        this(holder.value(), data);
    }

    public ExplosiveCatalystDefinition(ItemLike itemLike, ExplosiveCatalystData data) {
        this(itemLike.asItem(), data);
    }

    public ExplosiveCatalystDefinition(Item item, ExplosiveCatalystData data) {
        this(Ingredient.of(item), data);
    }

    public static @Nullable ExplosiveCatalystData find(ItemStack stack, HolderLookup.Provider provider) {
        Optional<HolderLookup.RegistryLookup<ExplosiveCatalystDefinition>> lookup = provider.lookup(KlaxonRegistries.EXPLOSIVE_CATALYST_DEFINITION);
        if (lookup.isPresent()) {
            Iterator<Holder.Reference<ExplosiveCatalystDefinition>> iterator = lookup.get().listElements().iterator();
            while (iterator.hasNext()) {
                ExplosiveCatalystDefinition selected = iterator.next().value();
                if (selected.ingredient.test(stack)) {
                    return selected.data();
                }
            }
        }
        return null;
    }
}
