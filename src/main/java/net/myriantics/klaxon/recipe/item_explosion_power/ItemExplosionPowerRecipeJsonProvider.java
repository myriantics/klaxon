package net.myriantics.klaxon.recipe.item_explosion_power;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
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
        json.addProperty("produces_fire", itemExplosionPowerRecipe.producesFire());
        json.addProperty("is_hidden", itemExplosionPowerRecipe.isHidden());
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
        Item item = itemExplosionPowerRecipe.getIngredient().getMatchingStacks()[0].getItem();

        return Advancement.Builder.create().criterion(Registries.ITEM.getId(item).getPath(), FabricRecipeProvider.conditionsFromItem(item)).build(getAdvancementId()).createTask().toJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
        return getRecipeId().withPath("advancement/" + getRecipeId().getPath());
    }
}
