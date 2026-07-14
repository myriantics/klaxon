package net.myriantics.klaxon.render.blockentityrenderers;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.myriantics.klaxon.block.machines.energy.contact_charger.BaseContactChargerBlockEntity;

public class CreativeContactChargerBlockEntityRenderer extends AbstractContactChargerBlockEntityRenderer<BaseContactChargerBlockEntity> {
    public CreativeContactChargerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}
