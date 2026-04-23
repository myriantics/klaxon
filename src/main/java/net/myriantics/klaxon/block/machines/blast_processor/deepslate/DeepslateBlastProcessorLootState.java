package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.minecraft.util.StringRepresentable;

public enum DeepslateBlastProcessorLootState implements StringRepresentable {
    UNLOOTED,
    FULL,
    INGREDIENT_ONLY,
    CATALYST_ONLY,
    EMPTY;

    public static DeepslateBlastProcessorLootState update(DeepslateBlastProcessorBlockEntity blastProcessor) {
        if (blastProcessor.isUnlooted()) {
            return UNLOOTED;
        } else {
            boolean catalyst = !blastProcessor.getCatalystStack().isEmpty();
            boolean ingredient = !blastProcessor.getIngredientStack().isEmpty();

            if (catalyst && ingredient) {
                return FULL;
            } else if (catalyst) {
                return CATALYST_ONLY;
            } else if (ingredient) {
                return INGREDIENT_ONLY;
            } else {
                return EMPTY;
            }
        }
    }

    public boolean isUnlooted() {
        return this.equals(UNLOOTED);
    }

    public boolean hasKnownIngredient() {
        return this.equals(FULL) || this.equals(INGREDIENT_ONLY);
    }

    public boolean hasKnownCatalyst() {
        return this.equals(FULL) || this.equals(CATALYST_ONLY);
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case UNLOOTED -> "unlooted";
            case FULL -> "full";
            case INGREDIENT_ONLY -> "ingredient";
            case CATALYST_ONLY -> "catalyst";
            case EMPTY -> "empty";
        };
    }
}
