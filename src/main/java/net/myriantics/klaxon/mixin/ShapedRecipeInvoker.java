package net.myriantics.klaxon.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeInvoker {

    @Invoker("removePadding")
    public static String[] klaxon$invokeRemovePadding(String... pattern) {
        throw new AssertionError();
    };

    @Invoker("createPatternMatrix")
    public static DefaultedList<Ingredient> klaxon$createPatternMatrix(String[] pattern, Map<String, Ingredient> symbols, int width, int height) {
        throw new AssertionError();
    };
}
