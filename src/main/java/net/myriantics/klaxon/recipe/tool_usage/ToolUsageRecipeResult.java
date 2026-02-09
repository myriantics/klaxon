package net.myriantics.klaxon.recipe.tool_usage;

public enum ToolUsageRecipeResult {
    /**
     * Falls back to the existing interaction
     */
    FAIL,
    /**
     * Cancels the existing interaction. Overrides ItemActionResult to be ItemActionResult.SUCCESS
     */
    SUCCESS,
    /**
     * Doesn't cancel the existing interaction. Overrides ItemActionResult to be ItemActionResult.SUCCESS
     */
    COSMETIC_SUCCESS
}
