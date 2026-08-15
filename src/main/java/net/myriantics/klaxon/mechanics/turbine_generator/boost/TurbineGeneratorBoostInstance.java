package net.myriantics.klaxon.mechanics.turbine_generator.boost;

public abstract sealed class TurbineGeneratorBoostInstance permits TurbineGeneratorBoostInstance.AddToBase, TurbineGeneratorBoostInstance.MultiplyFinal {

    private int remainingDuration = 0;

    public TurbineGeneratorBoostInstance(int duration) {
        this.remainingDuration = duration;
    }

    public boolean survivesDecay() {
        return this.remainingDuration-- > 0;
    }

    public void refresh(int refreshedDuration) {
        this.remainingDuration = refreshedDuration;
    }

    public static final class AddToBase extends TurbineGeneratorBoostInstance {

        private final long addedValue;

        public AddToBase(int duration, long addedValue) {
            super(duration);
            this.addedValue = addedValue;
        }

        public long getAddedValue() {
            return this.addedValue;
        }
    }

    public static final class MultiplyFinal extends TurbineGeneratorBoostInstance{

        private final double multiplyValue;

        public MultiplyFinal(int duration, double multiplyValue) {
            super(duration);
            this.multiplyValue = multiplyValue;
        }

        public double getMultiplyValue() {
            return this.multiplyValue;
        }
    }
}
