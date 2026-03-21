package net.myriantics.klaxon.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class HallnoxDripParticle extends TextureSheetParticle {
    protected HallnoxDripParticle(ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
        this.setColor(255f/255, 234f/255, 185f/255);
        this.gravity = 0.01f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    protected int getLightColor(float tint) {
        return 240;
    }

    @Override
    public void tick() {

        if (this.onGround || this.age++ > this.lifetime) {
            this.remove();
        }

        if (!this.removed) {
            this.yd = this.yd - this.gravity;

            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;

            this.move(this.xd, this.yd, this.zd);
        }
    }

    public static class HallnoxDripParticleFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public HallnoxDripParticleFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            HallnoxDripParticle particle = new HallnoxDripParticle(world, x, y, z, velocityX, velocityY, velocityZ);

            particle.pickSprite(spriteProvider);

            return particle;
        }
    }
}
