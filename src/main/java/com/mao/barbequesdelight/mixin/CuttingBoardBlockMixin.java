package com.mao.barbequesdelight.mixin;

import com.mao.barbequesdelight.content.item.SeasoningItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlock.class)
public abstract class CuttingBoardBlockMixin {

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	public void barbequesdelight$use$seasoning(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity be) {
			ItemStack handStack = player.getItemInHand(hand);
			ItemStack storedStack = be.getStoredItem();
			if (handStack.getItem() instanceof SeasoningItem seasoningItem && seasoningItem.canSprinkle(storedStack)) {
				seasoningItem.sprinkle(handStack, hit.getLocation(), storedStack, player, hand);
				if (!level.isClientSide()) {
					be.getInventory().set(0, ItemResource.of(storedStack), storedStack.getCount());
				}
				cir.setReturnValue(InteractionResult.SUCCESS);
			}
		}
	}

}
