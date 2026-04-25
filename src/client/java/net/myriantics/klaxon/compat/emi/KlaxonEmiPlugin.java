package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiWorkstations;
import net.myriantics.klaxon.item.equipment.tools.LighterItem;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.compat.emi.recipes.*;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

// spectrum's emi plugin used as reference
public class KlaxonEmiPlugin implements EmiPlugin {

    public static final Random RANDOM = new Random();

    @Override
    public void register(EmiRegistry registry) {
        KlaxonEmiCategories.init(registry);
        KlaxonEmiWorkstations.init(registry);
        registerRecipes(registry);

        Level world = Minecraft.getInstance().level;
        if (world != null) {
            for (Holder<ToolUsageRecipeType> entry : world.registryAccess().registryOrThrow(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).asHolderIdMap()) {
                Optional<ResourceKey<ToolUsageRecipeType>> optionalKey = entry.unwrapKey();

                if (optionalKey.isPresent()) {
                    ToolUsageRecipeType type = entry.value();
                    EmiIngredient validTools = EmiIngredient.of(type.validTools());

                    // attempt to pull from the specified item, but if that fails, use the tag
                    EmiRenderable renderable;
                    if (type.display().isPresent() && BuiltInRegistries.ITEM.get(type.display().get()) instanceof Item item) {
                        renderable = EmiStack.of(item);
                    } else {
                        renderable = validTools;
                    }

                    EmiRecipeCategory category = KlaxonEmiCategories.of(
                            optionalKey.get().location(), renderable
                    );

                    registry.addCategory(category);
                    registry.addWorkstation(category, validTools);

                    for (RecipeHolder<ToolUsageRecipe> recipe : registry.getRecipeManager().getAllRecipesFor(KlaxonRecipeTypes.TOOL_USAGE)) {
                        if (recipe.value().getTypeKey().equals(optionalKey.get())) {
                            registry.addRecipe(new AbstractToolUsageEmiRecipe(recipe, validTools) {
                                @Override
                                public EmiRecipeCategory getCategory() {
                                    return category;
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void registerRecipes(EmiRegistry registry) {
        addAllExplosiveCatalystDefinition(registry, KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION, ExplosiveCatalystDefinitionEmiRecipe::new);
        addAll(registry, KlaxonRecipeTypes.BLAST_PROCESSING, (recipe) -> new BlastProcessingEmiRecipe(recipe, registry, recipe.id()));
        registerMiscRecipes(registry);
        addAllConditional(registry, KlaxonRecipeTypes.NETHER_REACTION, NetherReactionEmiRecipe::new, (recipeEntry -> !recipeEntry.id().getPath().contains("_wall_") && !KlaxonBlockItems.getBlockDisplayStack(recipeEntry.value().getOutputBlock()).getItem().equals(Items.BARRIER)));
        addAll(registry, KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, (entry) -> {
            WorldItemApplicationRecipe recipe = entry.value();
            return new EmiWorldInteractionRecipe(EmiWorldInteractionRecipe.builder()
                    .id(entry.id())
                    .leftInput(EmiIngredient.of(recipe.getValidBlockInputs()))
                    .rightInput(EmiIngredient.of(recipe.getInputIngredient()), false)
                    .output(EmiStack.of(recipe.getOutputBlock()))
            ) {
                @Override
                public EmiRecipeCategory getCategory() {
                    return KlaxonEmiCategories.WORLD_ITEM_APPLICATION;
                }
            };
        });
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        addSyntheticAnvilRecipe(registry, EmiStack.of(KlaxonItems.STEEL_LIGHTER.value()), EmiIngredient.of(LighterItem.LIGHTER_REPAIR_MATERIALS), "steel_lighter");
        addSyntheticAnvilRecipe(registry, EmiStack.of(KlaxonItems.STEEL_CABLE_SHEARS.value()), EmiIngredient.of(KlaxonItemTags.STEEL_PLATE_TOOL_MATERIAL_REPAIR_MATERIALS), "steel_cable_shears");
    }

    private void addSyntheticAnvilRecipe(EmiRegistry registry, EmiStack tool, EmiIngredient repairMaterial, String path) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(tool, repairMaterial, path));
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeHolder<C>, EmiRecipe> constructor) {
        for (RecipeHolder<C> recipeEntry : registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeHolder<C>, EmiRecipe> constructor, Predicate<RecipeHolder<C>> predicate) {
        for (RecipeHolder<C> recipeEntry : registry.getRecipeManager().getAllRecipesFor(type)) {
            if (predicate.test(recipeEntry)) {
                registry.addRecipe(constructor.apply(recipeEntry));
            }
        }
    }


    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAllExplosiveCatalystDefinition(EmiRegistry registry, RecipeType<C> type, Function<RecipeHolder<C>, EmiRecipe> constructor) {
        for (RecipeHolder<C> recipeEntry : registry.getRecipeManager().getAllRecipesFor(type)) {

            // dont show hidden recipes
            if (recipeEntry.value() instanceof ExplosiveCatalystDefinitionRecipe explosiveCatalystDefinitionRecipe) {
                if (!explosiveCatalystDefinitionRecipe.isHidden()) {
                    Holder<ExplosiveCatalystBehavior> behavior = ((ExplosiveCatalystDefinitionRecipe) recipeEntry.value()).getData().behavior();
                    String id = behavior.getRegisteredName();
                    registry.addRecipe(new ExplosiveCatalystDefinitionEmiRecipe(new RecipeHolder<>(recipeEntry.id(), explosiveCatalystDefinitionRecipe), descriptionFromBehaviorId(id)));
                }
            }
        }
    }

    private static Component maxFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(ResourceLocation.NAMESPACE_SEPARATOR);
        return Component.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".max");
    }

    private static Component minFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(ResourceLocation.NAMESPACE_SEPARATOR);
        return Component.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".min");
    }

    private static Component descriptionFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(ResourceLocation.NAMESPACE_SEPARATOR);
        return Component.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".description");
    }

}
