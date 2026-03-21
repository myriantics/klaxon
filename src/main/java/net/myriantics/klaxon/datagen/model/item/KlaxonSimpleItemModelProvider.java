package net.myriantics.klaxon.datagen.model.item;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.datagen.model.KlaxonItemModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonSimpleItemModelProvider extends KlaxonItemModelSubProvider {
    public KlaxonSimpleItemModelProvider(KlaxonModelProvider provider, ItemModelGenerators generator) {
        super(provider, generator);
    }

    @Override
    public void generateModels() {
        registerSimpleItems();
    }

    private void registerSimpleItems()  {
        for (Item item : KlaxonItems.simpleItems) {
            registerSimpleItem(item);
        }
    }
}
