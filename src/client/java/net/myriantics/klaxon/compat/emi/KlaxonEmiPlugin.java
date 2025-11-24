package net.myriantics.klaxon.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.compat.emi.recipes.*;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import net.myriantics.klaxon.registry.item.KlaxonBlockItems;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
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
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);

        World world = MinecraftClient.getInstance().world;
        if (world != null) {
            for (RegistryEntry<ToolUsageRecipeType> entry : world.getRegistryManager().get(KlaxonRegistryKeys.TOOL_USAGE_RECIPE_TYPE).getIndexedEntries()) {
                Optional<RegistryKey<ToolUsageRecipeType>> optionalKey = entry.getKey();

                if (optionalKey.isPresent()) {
                    ToolUsageRecipeType type = entry.value();
                    EmiIngredient validTools = EmiIngredient.of(type.validTools());

                    // attempt to pull from the specified item, but if that fails, use the tag
                    EmiRenderable renderable;
                    if (type.display().isPresent() && Registries.ITEM.get(type.display().get()) instanceof Item item) {
                        renderable = EmiStack.of(item);
                    } else {
                        renderable = validTools;
                    }

                    EmiRecipeCategory category = KlaxonEmiRecipeCategories.of(
                            optionalKey.get().getValue(), renderable
                    );

                    registry.addCategory(category);
                    registry.addWorkstation(category, validTools);

                    for (RecipeEntry<ToolUsageRecipe> recipe : registry.getRecipeManager().listAllOfType(KlaxonRecipeTypes.TOOL_USAGE)) {
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

    private void registerCategories(EmiRegistry registry) {
        for (EmiRecipeCategory category : KlaxonEmiRecipeCategories.CATEGORIES) {
            registry.addCategory(category);
        }
    }

    private void registerWorkstations(EmiRegistry registry) {
        registry.addWorkstation(KlaxonEmiRecipeCategories.BLAST_PROCESSING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.EXPLOSIVE_CATALYST_DEFINITION, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
        registry.addWorkstation(KlaxonEmiRecipeCategories.NETHER_REACTION, EmiIngredient.of(KlaxonBlockTags.NETHER_REACTOR_CORES));
        registry.addWorkstation(KlaxonEmiRecipeCategories.WORLD_ITEM_APPLICATION, EmiStack.of(Items.DISPENSER));

        // Steel Hammer can mimic AnvilScreenHandler functionality
        registry.addWorkstation(VanillaEmiRecipeCategories.ANVIL_REPAIRING, EmiStack.of(KlaxonItems.STEEL_HAMMER));
        // Blast Processors can mimic Blasting Smelting functionality when using a catalyst that produces Fire
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, EmiStack.of(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR));
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
                    return KlaxonEmiRecipeCategories.WORLD_ITEM_APPLICATION;
                }
            };
        });
    }

    private void registerMiscRecipes(EmiRegistry registry) {
        registry.addRecipe(new KlaxonEMIAnvilRecipe(EmiStack.of(Items.FLINT_AND_STEEL), EmiIngredient.of(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS), "flint_and_steel"));
    }

    public <C extends Recipe<V>, T extends RecipeEntry<C>, V extends RecipeInput> void addAll(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipeEntry));
        }
    }

    public <C extends Recipe<V>, T extends RecipeEntry<C>, V extends RecipeInput> void addAllConditional(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor, Predicate<RecipeEntry<C>> predicate) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {
            if (predicate.test(recipeEntry)) {
                registry.addRecipe(constructor.apply(recipeEntry));
            }
        }
    }


    public <C extends Recipe<V>, T extends RecipeEntry<C>, V extends RecipeInput> void addAllExplosiveCatalystDefinition(EmiRegistry registry, RecipeType<C> type, Function<RecipeEntry<C>, EmiRecipe> constructor) {
        for (RecipeEntry<C> recipeEntry : registry.getRecipeManager().listAllOfType(type)) {

            // dont show hidden recipes
            if (recipeEntry.value() instanceof ExplosiveCatalystDefinitionRecipe explosiveCatalystDefinitionRecipe) {
                if (!explosiveCatalystDefinitionRecipe.isHidden()) {
                    RegistryEntry<ExplosiveCatalystBehavior> behavior = ((ExplosiveCatalystDefinitionRecipe) recipeEntry.value()).getData().behavior();
                    String id = behavior.getIdAsString();
                    if (behavior.value().isVariable()) {
                        registry.addRecipe(new ExplosiveCatalystDefinitionEmiRecipe(new RecipeEntry<>(recipeEntry.id(), explosiveCatalystDefinitionRecipe), minFromBehaviorId(id), maxFromBehaviorId(id), descriptionFromBehaviorId(id)));
                    } else {
                        registry.addRecipe(new ExplosiveCatalystDefinitionEmiRecipe(new RecipeEntry<>(recipeEntry.id(), explosiveCatalystDefinitionRecipe), descriptionFromBehaviorId(id)));
                    }
                }
            }
        }
    }

    private static Text maxFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(Identifier.NAMESPACE_SEPARATOR);
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".max");
    }

    private static Text minFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(Identifier.NAMESPACE_SEPARATOR);
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".min");
    }

    private static Text descriptionFromBehaviorId(String behaviorId) {
        int dividerIndex = behaviorId.lastIndexOf(Identifier.NAMESPACE_SEPARATOR);
        return Text.translatable("klaxon.emi.text.explosion_power_info.behavior." + behaviorId.substring(0, dividerIndex) + "." + behaviorId.substring(dividerIndex + 1) + ".description");
    }

}
