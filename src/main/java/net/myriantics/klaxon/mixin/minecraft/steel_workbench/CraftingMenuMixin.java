package net.myriantics.klaxon.mixin.minecraft.steel_workbench;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    public CraftingMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @WrapOperation(
            method = "stillValid",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/CraftingMenu;stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z")
    )
    private boolean klaxon$checkForSteelWorkbench(ContainerLevelAccess containerLevelAccess, Player player, Block block, Operation<Boolean> original) {
        return original.call(containerLevelAccess, player, block) || original.call(containerLevelAccess, player, KlaxonBlocks.STEEL_WORKBENCH.value());
    }
}
