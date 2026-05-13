package net.myriantics.klaxon.registry.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeSerializer;
import net.myriantics.klaxon.recipe.blast_processing.special.DecoratedPotCrackingBlastProcessingRecipe;
import net.myriantics.klaxon.recipe.custom_crafting.explosive_catalyst_transmutation.ExplosiveCatalystTransmutationRecipe;
import net.myriantics.klaxon.recipe.custom_crafting.fuse_extension.FuseExtensionRecipe;
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
    // blast processing
    public static final Holder<BlastProcessingRecipeSerializer<BlastProcessingRecipe>> BLAST_PROCESSING_RECIPE_SERIALIZER = registerSerializer(
            KlaxonRecipeTypes.BLAST_PROCESSING, new BlastProcessingRecipeSerializer<>(BlastProcessingRecipe::new)
    );
    public static final Holder<BlastProcessingRecipeSerializer<DecoratedPotCrackingBlastProcessingRecipe>> DECORATED_POT_CRACKING_BLAST_PROCESSING_SERIALIZER = registerSerializer(
            "decorated_pot_cracking_blast_processing_serializer", new BlastProcessingRecipeSerializer<>(DecoratedPotCrackingBlastProcessingRecipe::new)
    );
    // tool usage
    public static final Holder<ToolUsageRecipeSerializer> TOOL_USAGE_RECIPE_SERIALIZER = registerSerializer(
            KlaxonRecipeTypes.TOOL_USAGE, new ToolUsageRecipeSerializer()
    );
    // world item application
    public static final Holder<WorldItemApplicationRecipeSerializer> WORLD_ITEM_APPLICATION_RECIPE_SERIALIZER = registerSerializer(
            KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, new WorldItemApplicationRecipeSerializer()
    );
    // nether reaction
    public static final Holder<NetherReactionRecipeSerializer> NETHER_REACTION_RECIPE_SERIALIZER = registerSerializer(
            KlaxonRecipeTypes.NETHER_REACTION, new NetherReactionRecipeSerializer()
    );
    // crafting
    public static final Holder<MakeshiftShapedCraftingRecipeSerializer> MAKESHIFT_SHAPED_CRAFTING_RECIPE_SERIALIZER = registerSerializer(
            "makeshift_shaped", new MakeshiftShapedCraftingRecipeSerializer()
    );
    public static final Holder<MakeshiftShapelessCraftingRecipeSerializer> MAKESHIFT_SHAPELESS_CRAFTING_RECIPE_SERIALIZER = registerSerializer(
            "makeshift_shapeless", new MakeshiftShapelessCraftingRecipeSerializer()
    );
    public static final Holder<FuseExtensionRecipe.Serializer> FUSE_EXTENSION_RECIPE_SERIALIZER = registerSerializer(
            "fuse_extension", new FuseExtensionRecipe.Serializer()
    );
    public static final Holder<ExplosiveCatalystTransmutationRecipe.Serializer> EXPLOSIVE_CATALYST_TRANSMUTATION_RECIPE_SERIALIZER = registerSerializer(
            "explosive_catalyst_transmutation", new ExplosiveCatalystTransmutationRecipe.Serializer()
    );

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> Holder<S> registerSerializer(Holder<RecipeType<T>> type, S serializer) {
        return (Holder<S>) Registry.registerForHolder(BuiltInRegistries.RECIPE_SERIALIZER, KlaxonCommon.locate(type.unwrapKey().get().location().getPath()), serializer);
    }

    @SuppressWarnings("unchecked")
    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> Holder<S> registerSerializer(String id, S serializer) {
        return (Holder<S>) Registry.registerForHolder(BuiltInRegistries.RECIPE_SERIALIZER, KlaxonCommon.locate(id), serializer);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Recipe Serializers!");
    }
}
