package com.mao.barbequesdelight.content.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class GrillBlockItem extends BlockItem {

	public GrillBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Nullable
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
		var pos = ctx.getHitResult().getBlockPos();
		var state = ctx.getLevel().getBlockState(pos);
		if (state.is(Blocks.CAMPFIRE) &&
				state.getValue(BlockStateProperties.LIT) &&
				!state.getValue(BlockStateProperties.WATERLOGGED)) {
			ctx.replaceClicked = true;
		}
		return ctx;
	}

}
