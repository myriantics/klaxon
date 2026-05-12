package net.myriantics.klaxon.registry.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeSerializer;
import net.myriantics.klaxon.recipe.blast_processing.special.DecoratedPotCrackingBlastProcessingRecipe;
import net.myriantics.klaxon.recipe.custom_crafting.fuse_extension.FuseExtensionRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipe;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shaped.MakeshiftShapedCraftingRecipeSerializer;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipe;
import net.myriantics.klaxon.recipe.makeshift_crafting.shapeless.MakeshiftShapelessCraftingRecipeSerializer;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipe;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeSerializer;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipe;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeSerializer;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipe;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeSerializer;

public abstract class KlaxonRecipeSerializers {
    public static Holder<BlastProcessingRecipeSerializer<BlastProcessingRecipe>> BLAST_PROCESSING_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.BLAST_PROCESSING_RECIPE_ID, new BlastProcessingRecipeSerializer<>(BlastProcessingRecipe::new));
    public static Holder<BlastProcessingRecipeSerializer<DecoratedPotCrackingBlastProcessingRecipe>> DECORATED_POT_CRACKING_BLAST_PROCESSING_SERIALIZER =
            registerSerializer("decorated_pot_cracking_blast_processing_serializer", new BlastProcessingRecipeSerializer<>(DecoratedPotCrackingBlastProcessingRecipe::new));
    public static Holder<ToolUsageRecipeSerializer> TOOL_USAGE_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.TOOL_USAGE_RECIPE_ID, new ToolUsageRecipeSerializer());
    public static Holder<WorldItemApplicationRecipeSerializer> WORLD_ITEM_APPLICATION_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION_RECIPE_ID, new WorldItemApplicationRecipeSerializer());
    public static Holder<NetherReactionRecipeSerializer> NETHER_REACTION_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.NETHER_REACTION_RECIPE_ID, new NetherReactionRecipeSerializer());
    public static Holder<ExplosiveCatalystDefinitionRecipeSerializer> EXPLOSIVE_CATALYST_DEFINITION_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.EXPLOSIVE_CATALYST_DEFINITION_ID, new ExplosiveCatalystDefinitionRecipeSerializer());
    public static Holder<MakeshiftShapedCraftingRecipeSerializer> MAKESHIFT_SHAPED_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.MAKESHIFT_SHAPED_CRAFTING_ID, new MakeshiftShapedCraftingRecipeSerializer());
    public static Holder<MakeshiftShapelessCraftingRecipeSerializer> MAKESHIFT_SHAPELESS_CRAFTING_RECIPE_SERIALIZER =
            registerSerializer(KlaxonRecipeTypes.MAKESHIFT_SHAPELESS_CRAFTING_ID, new MakeshiftShapelessCraftingRecipeSerializer());
    public static Holder<FuseExtensionRecipe.Serializer> FUSE_EXTENSION_RECIPE_SERIALIZER = registerSerializer(
            "fuse_extension", new FuseExtensionRecipe.Serializer()
    );

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> Holder<S> registerSerializer(String id, S serializer) {
        return (Holder<S>) Registry.registerForHolder(BuiltInRegistries.RECIPE_SERIALIZER, KlaxonCommon.locate(id), serializer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Recipe Serializers!");
    }
}
