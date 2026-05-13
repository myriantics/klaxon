package net.myriantics.klaxon.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.compat.emi.registry.KlaxonEmiCategories;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformer;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystTransformerType;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.mechanics.explosive_catalyst.definition.ExplosiveCatalystDefinition;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.misc.KlaxonColors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveCatalystDefinitionEmiRecipe implements EmiRecipe {

    private static final Style ORANGE = Style.EMPTY.withColor(KlaxonColors.ORANGE.getRGB());
    private static final Style GREY = Style.EMPTY.withColor(CommonColors.GRAY);

    private static final int SPACING = 2;
    private static final int SLOT_SIDE_LENGTH = 18;
    private static final int CATALYST_DATA_ELEMENT_WIDTH = (SLOT_SIDE_LENGTH * 2) + (SPACING * 2);

    private final ResourceLocation id;
    private final List<EmiIngredient> ingredient;
    private final ExplosiveCatalystData baseCatalystData;

    private final List<Component> simpleExplosiveCatalystBehaviorLines;

    public ExplosiveCatalystDefinitionEmiRecipe(Holder<ExplosiveCatalystDefinition> definition) {
        this.id = definition.unwrapKey().get().location().withPrefix("/");
        this.ingredient = List.of(EmiIngredient.of(definition.value().ingredient()));
        this.baseCatalystData = definition.value().data();
        this.simpleExplosiveCatalystBehaviorLines = this.getSimpleExplosiveCatalystBehaviorTooltipText();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return KlaxonEmiCategories.EXPLOSIVE_CATALYST_DEFINITION;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.ingredient;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.ingredient.getFirst().getEmiStacks();
    }

    @Override
    public int getDisplayWidth() {
        return SPACING + SLOT_SIDE_LENGTH + (SPACING * 2) + SLOT_SIDE_LENGTH + (SPACING * 2) + CATALYST_DATA_ELEMENT_WIDTH + SPACING;
    }

    @Override
    public int getDisplayHeight() {
        return SPACING + SLOT_SIDE_LENGTH + SPACING;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        final int ingredientSlotX = SPACING;
        final int ingredientSlotY = SPACING;

        final int behaviorIconX = ingredientSlotX + SLOT_SIDE_LENGTH + (SPACING * 2);
        final int behaviorIconY = SPACING;

        final int catalystDataX = behaviorIconX + SLOT_SIDE_LENGTH + (SPACING * 2);
        final int catalystDataY = SPACING;
        final int catalystDataWidth = CATALYST_DATA_ELEMENT_WIDTH;
        final int catalystDataHeight = SLOT_SIDE_LENGTH;

        // ingredient slot
        widgetHolder.addSlot(this.ingredient.getFirst(), ingredientSlotX, SPACING).recipeContext(this);

        // catalyst behavior box (24, 2) -> (42, 20)
        // widgetHolder.addTooltipText();
        // should have a swirling texture with the catalyst behavior's color around it unless hovered
        widgetHolder.addTooltipText(
                this.simpleExplosiveCatalystBehaviorLines,
                SPACING + SLOT_SIDE_LENGTH + (SPACING * 2),
                SPACING,
                SLOT_SIDE_LENGTH,
                SLOT_SIDE_LENGTH
        );

        // catalyst data box
        widgetHolder.addTooltipText(
                this.getExplosiveCatalystTooltipComponents(),
                catalystDataX, catalystDataY,
                catalystDataWidth, catalystDataHeight
        );
    }

    private List<Component> getExplosiveCatalystTooltipComponents() {
        if (this.baseCatalystData.producesFire()) {
            return List.of(
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_explosion_power", Component.literal(String.valueOf(this.baseCatalystData.explosionPower())).setStyle(ORANGE)),
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_fiery").setStyle(ORANGE)
            );
        } else {
            return List.of(
                    Component.translatable("klaxon.emi.text.explosive_catalyst_definition.tooltip.data.base_explosion_power", this.baseCatalystData.explosionPower())
            );
        }
    }

    private List<Component> getSimpleExplosiveCatalystBehaviorTooltipText() {
        @Nullable Level level = Minecraft.getInstance().level;
        if (level != null) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable(this.baseCatalystData.behavior().location().toLanguageKey()));
            ExplosiveCatalystBehavior behavior = this.baseCatalystData.behavior(level).value();
            lines.add(behavior.handlerHolder.unwrapKey().map(key -> Component.translatable(key.location().toLanguageKey())).orElse(Component.literal("Invalid Handler!")));
            Component dash = Component.literal("- ").setStyle(GREY);
            for (ExplosiveCatalystTransformer transformer : behavior.transformers) {
                Holder<ExplosiveCatalystTransformerType<?>> typeHolder = KlaxonBuiltInRegistries.EXPLOSIVE_CATALYST_TRANSFORMER_TYPES.wrapAsHolder(transformer.getType());
                lines.add(dash.copy().append(typeHolder.unwrapKey().map(key -> Component.translatable(key.location().toLanguageKey())).orElse(Component.literal("Invalid Transformer!"))));
            }
            return lines;
        } else {
            return List.of(Component.literal("Missing Level!"));
        }
    }

}
