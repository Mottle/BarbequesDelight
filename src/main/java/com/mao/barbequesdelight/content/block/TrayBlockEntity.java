package com.mao.barbequesdelight.content.block;

import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TrayBlockEntity extends StorageTileBlockEntity {

	public TrayBlockEntity(BlockPos pos, BlockState state) {
		super(BBQDBlocks.TE_TRAY.get(), pos, state);
	}

	@Override
	public int size() {
		return 3;
	}

	@Override
	public AABB getBox() {
		return TrayBlock.OUTER.bounds().move(getBlockPos()).deflate(0.01f);
	}

	@Override
	public boolean specialClick(Player player, int i, InteractionHand hand) {
		return false;
	}

}
