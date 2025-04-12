package net.myriantics.klaxon.registry.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.api.behavior.ItemBlastProcessorBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.mixin.WindChargeEntityInvoker;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.List;

public class KlaxonBlastProcessorBehaviors {
    public static final Identifier FIREWORK_ROCKET_ID = KlaxonCommon.locate("firework_rocket_behavior");
    public static final Identifier FIREWORK_STAR_ID = KlaxonCommon.locate("firework_star_behavior");
    public static final Identifier BEDLIKE_EXPLODABLE_ID = KlaxonCommon.locate("bedlike_explodable_behavior");
    public static final Identifier WIND_CHARGE_ID = KlaxonCommon.locate("wind_charge_behavior");
    public static final Identifier DRAGONS_BREATH_ID = KlaxonCommon.locate("dragons_breath_behavior");

    public static final BlastProcessorBehavior FIREWORK_ROCKET = registerBehavior(FIREWORK_ROCKET_ID, new ItemBlastProcessorBehavior() {
        @Override
        public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput craftingInventory) {
            ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

            FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
            List<FireworkExplosionComponent> list =  fireworksComponent != null ? fireworksComponent.explosions() : List.of();

            double explosionPower = 0.3;
            if (!list.isEmpty()) {
                explosionPower += (list.size() * 0.5);
            }

            explosionPower = Math.min(explosionPower, 10.0);


            return new ItemExplosionPowerData(explosionPower, false);
        }

        @Override
        public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
            Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).copy());

            // explode using firework rocket entity code - summons dummy firework and detonates it
            world.spawnEntity(fireworkRocket);

            // clear the stack from inventory
            blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

            // TIL you can't have an invoker method be the same name as the original method. The more you know!
            ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
        }

        @Override
        public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
            return false;
        }

        @Override
        public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
            return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                    0.3,
                    10.0,
                    Text.translatable("klaxon.emi.text.explosion_power_info.firework_behavior_info"),
                    FIREWORK_ROCKET_ID.getPath()
            );
        }
    });

    public static final BlastProcessorBehavior FIREWORK_STAR = registerBehavior(FIREWORK_STAR_ID, new ItemBlastProcessorBehavior() {
        @Override
        public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput craftingInventory) {
            ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

            FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
            List<FireworkExplosionComponent> list =  fireworksComponent != null ? fireworksComponent.explosions() : List.of();

            double explosionPower = 0.3;
            if (!list.isEmpty()) {
                explosionPower += 0.5;
            }

            return new ItemExplosionPowerData(explosionPower, false);
        }

        @Override
        public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
            return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                    0.3,
                    0.8,
                    Text.empty(),
                    FIREWORK_STAR_ID.getPath()
            );
        }
    });

    public static final BlastProcessorBehavior BEDLIKE_EXPLODABLE = registerBehavior(BEDLIKE_EXPLODABLE_ID, new ItemBlastProcessorBehavior() {
        @Override
        public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {

            // if the bed doesnt work in dimension, explode
            if (!world.getDimension().bedWorks()) {
                return new ItemExplosionPowerData(5, true);
            }

            return super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
        }

        @Override
        public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
            return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                    0.0,
                    5.0,
                    Text.translatable("klaxon.emi.text.explosion_power_info.bed_behavior_info"),
                    BEDLIKE_EXPLODABLE_ID.getPath()
            );
        }
    });

    public static final BlastProcessorBehavior WIND_CHARGE = registerBehavior(WIND_CHARGE_ID, new ItemBlastProcessorBehavior() {
        @Override
        public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {
            // wind charges don't do any damage
            return new ItemExplosionPowerData(0, false);
        }

        @Override
        public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
            Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
            WindChargeEntity windCharge = new WindChargeEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), Vec3d.ZERO);
            WindChargeEntityInvoker windChargeInvoker = ((WindChargeEntityInvoker) windCharge);

            world.spawnEntity(windCharge);

            // explode
            windChargeInvoker.invokeCreateExplosion(new Vec3d(outputPos.getX(), outputPos.getY(), outputPos.getZ()));

            // remove stack and discard
            blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
            windCharge.discard();
        }

        @Override
        public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
            return false;
        }

        @Override
        public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
            return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                    0.0,
                    0.0,
                    Text.translatable("klaxon.emi.text.explosion_power_info.wind_charge_behavior_info"),
                    WIND_CHARGE_ID.getPath()
            );
        }
    });
    public static final BlastProcessorBehavior DRAGONS_BREATH = registerBehavior(DRAGONS_BREATH_ID, new ItemBlastProcessorBehavior() {
        @Override
        public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
            Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));

            if (!world.isClient()) {

                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ());

                areaEffectCloudEntity.setParticleType(ParticleTypes.DRAGON_BREATH);
                areaEffectCloudEntity.setRadius(1.0F);
                areaEffectCloudEntity.setDuration(80);
                areaEffectCloudEntity.setRadiusGrowth((0.6F - areaEffectCloudEntity.getRadius()) / areaEffectCloudEntity.getDuration());
                areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));

                world.syncWorldEvent(WorldEvents.DRAGON_BREATH_CLOUD_SPAWNS, pos, 1);
                world.spawnEntity(areaEffectCloudEntity);

                blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
            }
        }

        @Override
        public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {
            return new ItemExplosionPowerData(1.5, false);
        }

        @Override
        public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
            return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                    1.5,
                    1.5,
                    Text.translatable("klaxon.emi.text.explosion_power_info.dragons_breath_behavior_info"),
                    DRAGONS_BREATH_ID.getPath()
            );
        }

        @Override
        public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
            return false;
        }
    });

    private static BlastProcessorBehavior registerBehavior(Identifier id, BlastProcessorBehavior behavior) {
        return Registry.register(KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS, id, behavior);
    }

    public static void registerBlastProcessorBehaviors() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");
    }
}
