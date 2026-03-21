package net.myriantics.klaxon.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;


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

    public NamedIngredient withName(String name) {
        return new NamedIngredient(ingredient, name);
    }

    public static NamedIngredient fromTag(TagKey<Item> tag) {
        return new NamedIngredient(Ingredient.of(tag), tag.location().getPath());
    }

    public static NamedIngredient ofStacks(ItemStack... stacks) {
        String name = "empty";
        if (stacks.length > 0) {
            name = BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).getPath();
        }
        return new NamedIngredient(Ingredient.of(stacks), name);
    }

    public static NamedIngredient ofItems(ItemLike... items) {
        String name = "empty";
        if (items.length > 0) {
            name = BuiltInRegistries.ITEM.getKey(items[0].asItem()).getPath();
        }
        return new NamedIngredient(Ingredient.of(items), name);
    }
}
