package net.myriantics.klaxon.recipes.blast_processing;

import com.google.gson.JsonObject;

public class BlastProcessorRecipeJsonFormat {
    JsonObject inputA;
    String outputItem;
    int outputAmount;
    double explosionPowerMin;
    double explosionPowerMax;
    boolean requiresFire;
}
