package com.mao.barbequesdelight.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class TrayBlock extends StorageBlock {

	public static final MapCodec<TrayBlock> CODEC = simpleCodec(TrayBlock::new);

	public static final VoxelShape OUTER = Block.box(0, 0, 0, 16, 3, 16);

	private static final VoxelShape SHAPE = Shapes.join(OUTER,
			Block.box(1, 1, 1, 15, 3, 15),
			BooleanOp.ONLY_FIRST);

	public static final BooleanProperty SUPPORT = BooleanProperty.create("support");

	public TrayBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SUPPORT, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SUPPORT);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(SUPPORT, !supported(context.getLevel(), context.getClickedPos()));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return supported(level, pos) || level.getBlockState(pos.below()).is(state.getBlock());
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		if (directionToNeighbour.getAxis().equals(Direction.Axis.Y)) {
			if (supported(level, pos)) {
				return state.setValue(SUPPORT, false);
			}
			if (level.getBlockState(pos.below()).is(this)) {
				return state.setValue(SUPPORT, true);
			}
			return Blocks.AIR.defaultBlockState();
		}
		return state;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrayBlockEntity(pos, state);
	}

	private static boolean supported(LevelReader level, BlockPos pos) {
		return Block.canSupportRigidBlock(level, pos.below());
	}

}
