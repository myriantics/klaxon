package net.myriantics.klaxon.datagen.custom.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.NamedIngredient;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public class KlaxonExplosiveCatalystDefinitionProvider extends KlaxonDynamicRegistrySubProvider<ExplosiveCatalystDefinition> {
    public KlaxonExplosiveCatalystDefinitionProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {
        // normie recipes
        add(NamedIngredient.ofItems(Items.GUNPOWDER), new ExplosiveCatalystData(0.8, false));
        add(NamedIngredient.ofItems(Items.BLAZE_POWDER), new ExplosiveCatalystData(0.5, true));
        add(NamedIngredient.ofItems(Items.FIRE_CHARGE), new ExplosiveCatalystData(1.3, true));

        // these have custom behaviors
        add(NamedIngredient.ofItems(Items.TNT), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.TNT, 4.0, false));
        add(NamedIngredient.ofItems(Items.CREEPER_HEAD), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.CHARGED_CREEPER_MIMIC, 6.0, false));
        add(NamedIngredient.ofItems(Items.END_CRYSTAL), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.END_CRYSTAL, 6.0, false));
        add(NamedIngredient.ofItems(Items.TNT_MINECART), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.TNT_MINECART, 5.0, false));
        add(NamedIngredient.ofItems(Items.GLOWSTONE_DUST), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.RESPAWN_ANCHORLIKE, 1.3, true));
        add(NamedIngredient.ofItems(Items.GLOWSTONE), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.RESPAWN_ANCHORLIKE, 5.0, true));
        add(NamedIngredient.ofItems(Items.DRAGON_BREATH), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH, 2.5, false));
        add(NamedIngredient.ofItems(Items.WIND_CHARGE), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.WIND_BURST, 1.2, false));
        add(NamedIngredient.fromTag(KlaxonItemTags.BEDLIKE_EXPLODABLES), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.BEDLIKE, 5.0, true));
        add(NamedIngredient.ofItems(Items.FIREWORK_ROCKET), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.FIREWORK_ROCKET, 0.0, false));
        add(NamedIngredient.ofItems(Items.FIREWORK_STAR), new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.FIREWORK_STAR, 0.8, false));

        // meme recipes
        add(NamedIngredient.ofItems(Items.CREEPER_SPAWN_EGG), new ExplosiveCatalystData(3.0, false));
        add(NamedIngredient.ofItems(Items.GHAST_SPAWN_EGG), new ExplosiveCatalystData(3.5, true));
    }
    
    private ExplosiveCatalystDefinition add(Holder<Item> itemHolder, ExplosiveCatalystData data) {
        return this.add(NamedIngredient.ofItems(itemHolder.value()), data);
    }
    
    private ExplosiveCatalystDefinition add(ItemLike itemLike, ExplosiveCatalystData data) {
        return this.add(NamedIngredient.ofItems(itemLike), data);
    }
    
    private ExplosiveCatalystDefinition add(Item item, ExplosiveCatalystData data) {
        return this.add(NamedIngredient.ofItems(item), data);
    }

    private ExplosiveCatalystDefinition add(TagKey<Item> tagKey, ExplosiveCatalystData data) {
        return this.add(NamedIngredient.fromTag(tagKey), data);
    }
    
    private ExplosiveCatalystDefinition add(NamedIngredient ingredient, ExplosiveCatalystData data) {
        return this.add(ResourceKey.create(KlaxonRegistries.EXPLOSIVE_CATALYST_DEFINITION, KlaxonCommon.locate(ingredient.getName())), new ExplosiveCatalystDefinition(ingredient.toIngredient(), data));
    }
}
