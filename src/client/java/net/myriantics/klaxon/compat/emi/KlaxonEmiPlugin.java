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
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.compat.emi.recipes.special.ExplosiveCatalystTransmutationEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.special.FuseExtensionEmiRecipe;
import net.myriantics.klaxon.compat.emi.recipes.special.KlaxonSuspiciousStewRecipe;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiWorkstations;
import net.myriantics.klaxon.item.equipment.tools.LighterItem;
import net.myriantics.klaxon.compat.emi.recipes.*;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.recipe.custom_crafting.explosive_catalyst_transmutation.ExplosiveCatalystTransmutationRecipe;
import net.myriantics.klaxon.recipe.custom_crafting.fuse_extension.FuseExtensionRecipe;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

import java.util.*;
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
        ExplosiveCatalystDefinition[] explosiveCatalystDefinitions = this.initExplosiveCatalystDefinition(registry);
        initCustomCraftingRecipes(registry, explosiveCatalystDefinitions);

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            for (Holder<ToolUsageRecipeType> typeHolder : level.registryAccess().registryOrThrow(KlaxonRegistries.TOOL_USAGE_RECIPE_TYPE).asHolderIdMap()) {
                Optional<ResourceKey<ToolUsageRecipeType>> optionalKey = typeHolder.unwrapKey();

                if (optionalKey.isPresent()) {
                    ToolUsageRecipeType type = typeHolder.value();
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

                    for (RecipeHolder<ToolUsageRecipe> recipe : registry.getRecipeManager().getAllRecipesFor(KlaxonRecipeTypes.TOOL_USAGE.value())) {
                        if (recipe.value().getTypeKey().equals(optionalKey.get())) {
                            registry.addRecipe(new AbstractToolUsageEmiRecipe(recipe, ToolUsageRecipeType.animationRlFromHolder(typeHolder), validTools) {
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

    private ExplosiveCatalystDefinition[] initExplosiveCatalystDefinition(EmiRegistry registry) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return new ExplosiveCatalystDefinition[0];
        }

        Optional<Registry<ExplosiveCatalystDefinition>> reg = level.registryAccess().registry(KlaxonRegistries.EXPLOSIVE_CATALYST_DEFINITION);
        if (reg.isPresent()) {
            List<Holder.Reference<ExplosiveCatalystDefinition>> holders = reg.get().holders().toList();
            ExplosiveCatalystDefinition[] array = new ExplosiveCatalystDefinition[holders.size()];
            for (int i = 0; i < holders.size(); i++) {
                Holder<ExplosiveCatalystDefinition> holder = holders.get(i);
                registry.addRecipe(new ExplosiveCatalystDefinitionEmiRecipe(holder));
                array[i] = holder.value();
            }

            return array;
        }

        return new ExplosiveCatalystDefinition[0];
    }

    private void registerRecipes(EmiRegistry registry) {
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

    private void initCustomCraftingRecipes(EmiRegistry registry, ExplosiveCatalystDefinition[] definitions) {
        for (RecipeHolder<CraftingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (holder.value() instanceof FuseExtensionRecipe fuseExtensionRecipe) {
                registry.addRecipe(new FuseExtensionEmiRecipe(fuseExtensionRecipe, holder.id()));
            } else if (holder.value() instanceof ExplosiveCatalystTransmutationRecipe recipe) {
                registry.addRecipe(new ExplosiveCatalystTransmutationEmiRecipe(recipe, holder.id(), definitions));
            }
        }
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        addSyntheticAnvilRecipe(registry, EmiStack.of(KlaxonItems.STEEL_LIGHTER.value()), EmiIngredient.of(LighterItem.LIGHTER_REPAIR_MATERIALS), "steel_lighter");
        addSyntheticAnvilRecipe(registry, EmiStack.of(KlaxonItems.STEEL_CABLE_SHEARS.value()), EmiIngredient.of(KlaxonItemTags.STEEL_PLATE_TOOL_MATERIAL_REPAIR_MATERIALS), "steel_cable_shears");

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Optional<HolderSet.Named<Item>> holders = level.registryAccess().registryOrThrow(Registries.ITEM).getTag(KlaxonItemTags.SUSPICIOUS_STEW_INGREDIENTS);
            holders.ifPresent(itemNamed -> registry.addRecipe(new KlaxonSuspiciousStewRecipe(itemNamed)));
        }
    }

    private void addSyntheticAnvilRecipe(EmiRegistry registry, EmiStack tool, EmiIngredient repairMaterial, String path) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(tool, repairMaterial, path));
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAll(EmiRegistry registry, Holder<RecipeType<C>> type, Function<RecipeHolder<C>, EmiRecipe> constructor) {
        this.addAll(registry, type.value(), constructor);
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeHolder<C>, EmiRecipe> constructor) {
        for (RecipeHolder<C> recipeEntry : registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAllConditional(EmiRegistry registry, Holder<RecipeType<C>> type, Function<RecipeHolder<C>, EmiRecipe> constructor, Predicate<RecipeHolder<C>> predicate) {
        this.addAllConditional(registry, type.value(), constructor, predicate);
    }

    public <C extends Recipe<V>, T extends RecipeHolder<C>, V extends RecipeInput> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeHolder<C>, EmiRecipe> constructor, Predicate<RecipeHolder<C>> predicate) {
        for (RecipeHolder<C> recipeEntry : registry.getRecipeManager().getAllRecipesFor(type)) {
            if (predicate.test(recipeEntry)) {
                registry.addRecipe(constructor.apply(recipeEntry));
            }
        }
    }
}
