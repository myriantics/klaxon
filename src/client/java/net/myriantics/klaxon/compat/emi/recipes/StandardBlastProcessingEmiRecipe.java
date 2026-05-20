package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.compat.emi.infra.GeneratedTextWidget;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.recipe.blast_processing.StandardBlastProcessingRecipe;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import net.myriantics.klaxon.util.KlaxonMathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StandardBlastProcessingEmiRecipe implements EmiRecipe {
    private static final ResourceLocation BACKGROUND_TEXTURE = KlaxonCommon.locate("textures/gui/sprites/emi/deepslate_blast_processor_emi.png");

    private static final Random RANDOM = new Random();

    private final int unique;
    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> outputStacks;

    private final ExplosiveCatalystDefinition[] definitions;

    private final double explosionPowerMin;
    private final double explosionPowerMax;

    public StandardBlastProcessingEmiRecipe(StandardBlastProcessingRecipe recipe, ResourceLocation id) {
        this.id = id;
        this.outputStacks = new ArrayList<>();
        for (ItemStack stack : recipe.getRecipeOutputCompound().getDisplayStacks()) {
            outputStacks.add(EmiStack.of(stack));
        }
        this.explosionPowerMin = KlaxonMathHelper.roundToDecimalPlace(recipe.getExplosionPowerMin(), 2);
        this.explosionPowerMax = KlaxonMathHelper.roundToDecimalPlace(recipe.getExplosionPowerMax(), 2);
        this.definitions = getValidCatalysts(Minecraft.getInstance().level.registryAccess());
        this.unique = RANDOM.nextInt();

        this.input = List.of(EmiIngredient.of(recipe.getIngredientItem()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.BLAST_PROCESSING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return 147;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND_TEXTURE, 0, 0, 147, 60, 0, 0);

        widgets.addSlot(input.get(0), 18, 3).drawBack(false);
        widgets.addGeneratedSlot(
                random -> {
                    @Nullable ExplosiveCatalystDefinition definition = this.selectDefinition(random);
                    if (definition == null) {
                        return EmiStack.EMPTY;
                    } else {
                        return EmiIngredient.of(definition.ingredient());
                    }
                },
                this.unique,
                18, 39
        );

        widgets.addText(Component.literal("" + explosionPowerMin), 48, 44, 16777215, false);
        widgets.addText(Component.literal("" + explosionPowerMax), 48, 8, 16777215, false);
        widgets.add(new GeneratedTextWidget(random -> {
            @Nullable ExplosiveCatalystDefinition definition = this.selectDefinition(random);
            if (definition == null) {
                return Component.literal("---").getVisualOrderText();
            } else {
                return Component.literal(String.valueOf(definition.data().explosionPower())).getVisualOrderText();
            }}, this.unique, 48, 26, 16777215, false)
        );

        // add the 3x3 grid of output slots
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int index = x + y * 3;

                widgets.addSlot(
                        index < outputStacks.size() ? outputStacks.get(index) : EmiStack.EMPTY,
                        90 + x * 18,
                        3 + y * 18
                ).recipeContext(this).drawBack(false);
            }
        }
    }

    private @Nullable ExplosiveCatalystDefinition selectDefinition(Random random) {
        return this.definitions.length == 0 ? null : this.definitions[random.nextInt(this.definitions.length)];
    }

    private ExplosiveCatalystDefinition[] getValidCatalysts(RegistryAccess registryAccess) {
        Optional<HolderLookup.RegistryLookup<ExplosiveCatalystDefinition>> lookup = registryAccess.lookup(KlaxonRegistries.EXPLOSIVE_CATALYST_DEFINITION);
        if (lookup.isPresent()) {
            return lookup.get().filterElements(definition -> this.isCatalystDefinitionValid(definition, registryAccess)).listElements().map(Holder::value).toArray(ExplosiveCatalystDefinition[]::new);
        } else {
            return new ExplosiveCatalystDefinition[0];
        }
    }

    private boolean isCatalystDefinitionValid(ExplosiveCatalystDefinition definition, RegistryAccess access) {
        return definition.data().matchesConditions(this.explosionPowerMin, this.explosionPowerMax) && !definition.data().behavior(access).is(KlaxonExplosiveCatalystBehaviorTags.UNUSABLE_FOR_BLAST_PROCESSING);
    }
}
