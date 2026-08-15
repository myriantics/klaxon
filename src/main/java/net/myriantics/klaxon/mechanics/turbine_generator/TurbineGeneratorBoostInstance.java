package net.myriantics.klaxon.mechanics.turbine_generator;

public abstract class TurbineGeneratorBoostInstance {
    private int remainingDuration;

    protected abstract long applyBoost();

    protected abstract boolean blocksStaticPowerSourceChecking();

    public static final class Add {
        final long addition;

        public Add(long addition) {
            this.addition = addition;
        }
    }

    public static final class MultiplyTotal {
        final double multiplier;

        public MultiplyTotal(double multiplier) {
            this.multiplier = multiplier;
        }
    }
}
