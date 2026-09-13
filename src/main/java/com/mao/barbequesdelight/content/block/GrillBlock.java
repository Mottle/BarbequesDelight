package com.mao.barbequesdelight.content.block;

import com.mao.barbequesdelight.content.item.SeasoningItem;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.registry.ModSounds;

import javax.annotation.Nullable;

public class GrillBlock extends BaseEntityBlock {

	public static final MapCodec<GrillBlock> CODEC = simpleCodec(GrillBlock::new);

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty CAMPFIRE = BooleanProperty.create("has_campfire");

	public static final VoxelShape OUTER = Block.box(1, 12, 1, 15, 15, 15);
	public static final VoxelShape SHAPE = Shapes.join(Shapes.block(),
			Block.box(1, 15, 1, 15, 16, 15), BooleanOp.ONLY_FIRST);

	public GrillBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(CAMPFIRE, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, CAMPFIRE);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof GrillBlockEntity grill) {
			if (grill.isBarbecuing()) {
				double x = (double) pos.getX() + 0.5;
				double y = (double) pos.getY();
				double z = (double) pos.getZ() + 0.5;
				if (random.nextInt(8) == 0) {
					level.playLocalSound(x, y, z, ModSounds.BLOCK_SKILLET_SIZZLE.get(), SoundSource.BLOCKS,
							0.4F, random.nextFloat() * 0.2F + 0.9F, false);
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState clicked = context.getLevel().getBlockState(context.getClickedPos());
		boolean campfire = clicked.is(Blocks.CAMPFIRE)
				&& clicked.getValue(BlockStateProperties.LIT)
				&& !clicked.getValue(BlockStateProperties.WATERLOGGED);
		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(CAMPFIRE, campfire);
	}

	@Override
	public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!state.getValue(CAMPFIRE) && stack.is(Items.CAMPFIRE) && hit.getDirection().getAxis() != Direction.Axis.Y) {
			if (!level.isClientSide()) {
				level.setBlockAndUpdate(pos, state.setValue(CAMPFIRE, true));
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
			}
			return InteractionResult.SUCCESS;
		}
		if (!(level.getBlockEntity(pos) instanceof GrillBlockEntity grill)) {
			return InteractionResult.PASS;
		}
		int i = grill.getSlotForHitting(hit, level);
		if (i < 0 || i >= grill.size()) return InteractionResult.PASS;
		ItemStack item = grill.getStack(i);
		if (stack.isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND;
		if (!item.isEmpty()) {
			if (stack.getItem() instanceof SeasoningItem seasoning && seasoning.canSprinkle(item)) {
				seasoning.sprinkle(stack, hit.getLocation(), item, player, hand);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (level.isClientSide()) {
			return InteractionResult.CONSUME;
		}
		return grill.addItem(i, stack) ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof GrillBlockEntity grill)) {
			return InteractionResult.PASS;
		}
		int i = grill.getSlotForHitting(hit, level);
		if (i < 0 || i >= grill.size()) return InteractionResult.PASS;
		ItemStack item = grill.getStack(i);
		if (player.isShiftKeyDown()) {
			return grill.flip(i) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
		if (!level.isClientSide()) {
			ItemStack ret = item.split(1);
			if (player.getMainHandItem().isEmpty()) {
				player.setItemInHand(InteractionHand.MAIN_HAND, ret);
			} else player.getInventory().placeItemBackInInventory(ret);
			grill.inventoryChanged();
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		return level.getBlockEntity(pos) instanceof GrillBlockEntity grill ? grill.getComparatorOutput() : 0;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GrillBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BBQDBlocks.TE_GRILL.get(), GrillBlockEntity::tick);
	}

}
