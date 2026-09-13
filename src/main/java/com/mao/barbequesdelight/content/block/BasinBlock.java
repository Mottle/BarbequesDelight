package com.mao.barbequesdelight.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BasinBlock extends StorageBlock {

	public static final MapCodec<BasinBlock> CODEC = simpleCodec(BasinBlock::new);

	public static final VoxelShape OUTER, SHAPE_X, SHAPE_Z;

	static {
		OUTER = Block.box(1, 0, 1, 15, 4, 15);
		SHAPE_Z = Shapes.join(OUTER,
				Shapes.or(Block.box(2, 1, 2, 7.5, 4, 14),
						Block.box(8.5, 1, 2, 14, 4, 14)),
				BooleanOp.ONLY_FIRST);

		SHAPE_X = Shapes.join(OUTER,
				Shapes.or(Block.box(2, 1, 2, 14, 4, 7.5),
						Block.box(2, 1, 8.5, 14, 4, 14)),
				BooleanOp.ONLY_FIRST);

	}

	public BasinBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BasinBlockEntity(pos, state);
	}

}
