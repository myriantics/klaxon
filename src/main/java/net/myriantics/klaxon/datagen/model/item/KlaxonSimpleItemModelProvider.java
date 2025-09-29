package net.myriantics.klaxon.datagen.model.item;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.Item;
import net.myriantics.klaxon.datagen.model.KlaxonItemModelSubProvider;
import net.myriantics.klaxon.datagen.model.KlaxonModelProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public class KlaxonSimpleItemModelProvider extends KlaxonItemModelSubProvider {
    public KlaxonSimpleItemModelProvider(KlaxonModelProvider provider, ItemModelGenerator generator) {
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
