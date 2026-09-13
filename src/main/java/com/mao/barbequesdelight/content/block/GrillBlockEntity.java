package com.mao.barbequesdelight.content.block;

import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import vectorwing.farmersdelight.common.block.entity.HeatableBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;

import java.util.Optional;

public class GrillBlockEntity extends SyncedBlockEntity implements HeatableBlockEntity, BlockSlot {

	public static class ItemEntry {

		public int time, duration;
		public boolean flipped, burnt;

		public ItemStack stack = ItemStack.EMPTY;

		public void tick(GrillBlockEntity be, boolean heated) {
			if (be.level == null) return;
			if (stack.isEmpty()) return;
			if (!heated) {
				if (time > 0) {
					time -= 2;
					if (time <= 0) time = 0;
				}
				return;
			}
			time++;
			if (time < duration) return;
			if (time >= duration * 2) {
				burnt = true;
				stack = BBQDItems.BURNT_FOOD.toStack();
				be.inventoryChanged();
			}
			if (!flipped) return;
			if (time == duration && !be.level.isClientSide()) {
				var cont = new SingleRecipeInput(stack);
				var opt = be.findRecipe(BBQDRecipes.RT_BBQ.get(), cont);
				if (opt.isPresent()) {
					var tag = stack.getComponentsPatch();
					stack = opt.get().value().assemble(cont);
					stack.applyComponents(tag);
					be.inventoryChanged();
				}
			}
		}

		public boolean canFlip() {
			return !stack.isEmpty() && !flipped && !burnt && time >= duration / 2;
		}

		public boolean smoking() {
			return burnt || canFlip() || flipped && time >= duration;
		}

		public boolean flip(GrillBlockEntity be) {
			if (be.level == null) return false;
			if (!canFlip()) return false;
			flipped = true;
			time = duration / 2;
			if (!be.level.isClientSide())
				be.inventoryChanged();
			be.level.playSound(null, be.getBlockPos(),
					ModSounds.BLOCK_SKILLET_ADD_FOOD.get(),
					SoundSource.BLOCKS, 0.8F, 1.0F);
			return true;
		}

		public boolean addItem(GrillBlockEntity be, ItemStack stack) {
			if (be.level == null) return false;
			var opt = be.findRecipe(BBQDRecipes.RT_BBQ.get(), new SingleRecipeInput(stack));
			if (opt.isEmpty()) return false;
			duration = opt.get().value().getBarbecuingTime();
			time = 0;
			flipped = burnt = false;
			this.stack = stack.copyWithCount(1);
			stack.shrink(1);
			be.inventoryChanged();
			be.level.playSound(null, be.getBlockPos(), SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
			return true;
		}

		public Component getTooltip() {
			if (burnt) {
				return BBQLangData.JADE_BURNT.get();
			}
			if (!flipped) {
				if (time < duration / 2) {
					return BBQLangData.JADE_COOK.get((duration / 2 - time + 19) / 20);
				} else {
					return BBQLangData.JADE_FLIP.get();
				}
			} else {
				if (time < duration) {
					return BBQLangData.JADE_COOK.get((duration - time + 19) / 20);
				} else {
					return BBQLangData.JADE_COOKED.get();
				}
			}
		}

		private void load(ValueInput input) {
			time = input.getIntOr("time", 0);
			duration = input.getIntOr("duration", 0);
			flipped = input.getBooleanOr("flipped", false);
			burnt = input.getBooleanOr("burnt", false);
			stack = input.read("stack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
		}

		private void save(ValueOutput output) {
			output.putInt("time", time);
			output.putInt("duration", duration);
			output.putBoolean("flipped", flipped);
			output.putBoolean("burnt", burnt);
			output.store("stack", ItemStack.OPTIONAL_CODEC, stack);
		}

	}

	public final ItemEntry[] entries = {new ItemEntry(), new ItemEntry()};

	public GrillBlockEntity(BlockPos pos, BlockState state) {
		super(BBQDBlocks.TE_GRILL.get(), pos, state);
	}

	public int size() {
		return entries.length;
	}

	public ItemStack getStack(int i) {
		if (i < 0 || i >= size()) return ItemStack.EMPTY;
		return entries[i].stack;
	}

	public boolean addItem(int i, ItemStack stack) {
		if (i < 0 || i >= size()) return false;
		return entries[i].addItem(this, stack);
	}

	@Override
	public AABB getBox() {
		return GrillBlock.OUTER.bounds().move(getBlockPos())
				.move(0, 1 / 16f, 0)
				.deflate(0.01f);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, GrillBlockEntity grill) {
		boolean heat = grill.isHeated();
		for (var e : grill.entries) {
			e.tick(grill, heat);
		}
		if (level.isClientSide() && heat) {
			grill.addParticles();
		}
	}

	public boolean canFlip(int i) {
		if (i < 0 || i >= size()) return false;
		return entries[i].canFlip();
	}

	public boolean flip(int i) {
		if (i < 0 || i >= size()) return false;
		return entries[i].flip(this);
	}

	public boolean isFlipped(int i) {
		if (i < 0 || i >= size()) return false;
		return entries[i].flipped;
	}

	@Override
	public void inventoryChanged() {
		super.inventoryChanged();
	}

	public boolean isHeated() {
		return getBlockState().getValue(GrillBlock.CAMPFIRE) ||
				level != null && this.isHeated(level, getBlockPos());
	}

	public boolean isBarbecuing() {
		if (level == null) return false;
		if (!isHeated()) return false;
		for (var e : entries) {
			if (!e.stack.isEmpty())
				return true;
		}
		return false;
	}

	public int getComparatorOutput() {
		int occupied = 0;
		for (var e : entries) {
			if (!e.stack.isEmpty()) occupied++;
		}
		return occupied == 0 ? 0 : Mth.floor((float) occupied / entries.length * 14.0F) + 1;
	}

	public <R extends Recipe<SingleRecipeInput>> Optional<RecipeHolder<R>> findRecipe(RecipeType<R> type, SingleRecipeInput input) {
		Level level = this.level;
		MinecraftServer server = level == null ? null : level.getServer();
		if (server == null) return Optional.empty();
		return server.getRecipeManager().getRecipeFor(type, input, level);
	}

	private void addParticles() {
		Level level = getLevel();
		if (level == null) return;
		BlockPos pos = getBlockPos();
		var r = level.getRandom();
		if (r.nextFloat() < 0.2F && isBarbecuing()) {
			double x = pos.getX() + 0.5D + (r.nextDouble() * 0.4D - 0.2D);
			double y = pos.getY() + 1.0D;
			double z = pos.getZ() + 0.5D + (r.nextDouble() * 0.4D - 0.2D);
			double motionY = r.nextBoolean() ? 0.015D : 0.005D;
			level.addParticle(ModParticleTypes.STEAM.get(), x, y, z, 0.0D, motionY, 0.0D);
		}
		for (int i = 0; i < entries.length; ++i) {
			if (!entries[i].stack.isEmpty()) {
				double x0 = pos.getX() + .5d;
				double y = pos.getY() + 1.d;
				double z0 = pos.getZ() + .5d;
				var v1 = getOffset(i);
				Direction dir = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
				int index = dir.get2DDataValue();
				Vec2 offset = index % 2 == 0 ? new Vec2(v1, 0) : new Vec2(0, v1);
				double x = x0 - (dir.getStepX() * offset.x) + (dir.getClockWise().getStepX() * offset.x);
				double z = z0 - (dir.getStepZ() * offset.y) + (dir.getClockWise().getStepZ() * offset.y);
				for (int j = 0; j < (entries[i].smoking() ? 8 : 1); ++j) {
					if (r.nextFloat() < 0.2f) {
						double dx0 = (r.nextDouble() + r.nextDouble() - 1) * 0.1f;
						double dz0 = (r.nextDouble() + r.nextDouble() - 1) * 0.1f;
						level.addParticle(ParticleTypes.SMOKE, x + dx0, y, z + dz0, .0d, 5.e-4d, .0d);
					}
				}
			}
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			for (var e : entries) {
				if (!e.stack.isEmpty()) {
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), e.stack);
					e.stack = ItemStack.EMPTY;
				}
			}
		}
		super.preRemoveSideEffects(pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		// 1.21 L2 serialization stored entries under auto-serial.entries; migrate
		// when the new top-level `entries` key is absent.
		var list = input.childrenList("entries");
		if (list.isEmpty()) {
			input.child("auto-serial").ifPresent(legacy -> {
				int i = 0;
				for (ValueInput child : legacy.childrenListOrEmpty("entries")) {
					if (i >= entries.length) break;
					entries[i++].load(child);
				}
				inventoryChanged();
			});
			return;
		}
		int i = 0;
		for (ValueInput child : list.get()) {
			if (i >= entries.length) break;
			entries[i++].load(child);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		var list = output.childrenList("entries");
		for (var e : entries) {
			e.save(list.addChild());
		}
	}

}
