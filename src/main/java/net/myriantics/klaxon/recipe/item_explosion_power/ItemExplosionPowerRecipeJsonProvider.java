package net.myriantics.klaxon.recipe.item_explosion_power;

import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.myriantics.klaxon.registry.KlaxonRecipeTypes;
import org.jetbrains.annotations.Nullable;

public class ItemExplosionPowerRecipeJsonProvider implements RecipeJsonProvider {
    private ItemExplosionPowerRecipe itemExplosionPowerRecipe;

    public ItemExplosionPowerRecipeJsonProvider(ItemExplosionPowerRecipe itemExplosionPowerRecipe) {
        this.itemExplosionPowerRecipe = itemExplosionPowerRecipe;
    }

    @Override
    public void serialize(JsonObject json) {
        json.add("catalyst_ingredient", itemExplosionPowerRecipe.getIngredient().toJson());
        json.addProperty("explosion_power", itemExplosionPowerRecipe.getExplosionPower());
        json.addProperty("producesFire", itemExplosionPowerRecipe.producesFire());
        json.addProperty("isHidden", itemExplosionPowerRecipe.isHidden());
    }

    @Override
    public Identifier getRecipeId() {
        return itemExplosionPowerRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.ITEM_EXPLOSION_POWER_RECIPE_SERIALIZER;
    }

    @Nullable
    @Override
    public JsonObject toAdvancementJson() {
        return Advancement.Builder.create().toJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
        return getRecipeId().withPath("advancement/" + getRecipeId().getPath());
    }
}
