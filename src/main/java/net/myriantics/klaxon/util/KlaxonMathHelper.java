package net.myriantics.klaxon.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KlaxonMathHelper {
    public static double roundToTenth(double roundee) {
        return roundToDecimalPlace(roundee, 1);
    }

    public static double roundToDecimalPlace(double roundee, int decimals) {
        return Math.round(roundee * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static float yawBetween(Vec3d origin, Vec3d end) {
        double xDiff = origin.x - end.x;
        double zDiff = origin.z - end.z;
        return MathHelper.wrapDegrees((float)(MathHelper.atan2(zDiff, xDiff) * 180.0F / Math.PI) - 90.0F);
    }

    public static float pitchBetween(Vec3d origin, Vec3d end) {
        double xDiff = origin.x - end.x;
        double yDiff = origin.y - end.y;
        double zDiff = origin.z - end.z;
        // a^2 + b^2 = c^2
        double hyp = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        return MathHelper.wrapDegrees((float)(-(MathHelper.atan2(yDiff, hyp) * 180.0F / Math.PI)));
    }
}
