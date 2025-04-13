package net.myriantics.klaxon.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;


// Used during datagen to improve recipe name readability when there's no output item to pull the name from
public final class NamedIngredient {
    private final Ingredient ingredient;
    private final String name;

    private NamedIngredient(Ingredient ingredient, String name) {
        this.name = name;
        this.ingredient = ingredient;
    }

    public String getName() {
        return this.name;
    }

    public Ingredient toIngredient() {
        return this.ingredient;
    }

    public static NamedIngredient fromTag(TagKey<Item> tag) {
        return new NamedIngredient(Ingredient.fromTag(tag), tag.id().getPath());
    }

    public static NamedIngredient ofStacks(ItemStack... stacks) {
        String name = "empty";
        if (stacks.length > 0) {
            name = Registries.ITEM.getId(stacks[0].getItem()).getPath();
        }
        return new NamedIngredient(Ingredient.ofStacks(stacks), name);
    }

    public static NamedIngredient ofItems(ItemConvertible... items) {
        String name = "empty";
        if (items.length > 0) {
            name = Registries.ITEM.getId(items[0].asItem()).getPath();
        }
        return new NamedIngredient(Ingredient.ofItems(items), name);
    }
}
