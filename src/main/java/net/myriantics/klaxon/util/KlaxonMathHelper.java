package net.myriantics.klaxon.util;

public class KlaxonMathHelper {
    public static double roundToTenth(double roundee) {
        return roundToDecimalPlace(roundee, 1);
    }

    public static double roundToDecimalPlace(double roundee, int decimals) {
        return Math.round(roundee * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }
}
