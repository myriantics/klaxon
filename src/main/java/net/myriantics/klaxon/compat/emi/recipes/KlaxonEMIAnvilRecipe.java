package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonClient;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.KlaxonEmiPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class KlaxonEMIAnvilRecipe implements EmiRecipe {

    private final EmiStack tool;
    private final EmiIngredient resource;
    private final Identifier id;
    private final int uniq = KlaxonEmiPlugin.RANDOM.nextInt();

    public KlaxonEMIAnvilRecipe(EmiStack tool, EmiIngredient resource, String path) {
        this.tool = tool;
        this.resource = resource;
        this.id = KlaxonCommon.locate("/anvil/" + path);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(tool, resource);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(tool);
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addGeneratedSlot(r -> getTool(r, false), uniq, 0, 0);
        widgets.addSlot(resource, 49, 0);
        widgets.addGeneratedSlot(r -> getTool(r, true), uniq, 107, 0).recipeContext(this);
    }

    private EmiStack getTool(Random r, boolean repaired) {
        ItemStack stack = tool.getItemStack().copy();
        if (stack.getMaxDamage() <= 0) {
            return tool;
        }
        int d = r.nextInt(stack.getMaxDamage());
        if (repaired) {
            d -= stack.getMaxDamage() / 4;
            if (d <= 0) {
                return tool;
            }
        }
        stack.setDamage(d);
        return EmiStack.of(stack);
    }
}
