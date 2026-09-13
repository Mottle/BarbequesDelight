package com.mao.barbequesdelight.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import javax.annotation.Nullable;

public abstract class StorageTileBlockEntity extends SyncedBlockEntity implements BlockSlot {

	protected final ItemStacksResourceHandler items;

	public StorageTileBlockEntity(BlockEntityType<? extends StorageTileBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		items = new ItemStacksResourceHandler(size()) {
			@Override
			protected void onContentsChanged(int index, ItemStack previousContents) {
				inventoryChanged();
			}
		};
	}

	public ItemStack getStack(int i) {
		if (i < 0 || i >= size()) return ItemStack.EMPTY;
		return items.getResource(i).toStack(items.getAmountAsInt(i));
	}

	public void setStack(int i, ItemStack stack) {
		items.set(i, stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack), stack.getCount());
	}

	public boolean insert(Level level, int i, ItemStack handStack, boolean all) {
		if (handStack.isEmpty() || i < 0 || i >= size()) return false;
		ItemStack stored = getStack(i);
		if (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, handStack)) return false;
		int split = Math.min(handStack.getMaxStackSize() - stored.getCount(), all ? handStack.getCount() : 1);
		if (split <= 0) return false;
		if (!level.isClientSide()) {
			ItemStack copy = handStack.split(split);
			copy.grow(stored.getCount());
			setStack(i, copy);
		}
		return true;
	}

	public ItemStack extract(int i, int amount) {
		ItemStack stored = getStack(i);
		int split = Math.min(amount, stored.getCount());
		if (split <= 0) return ItemStack.EMPTY;
		ItemStack ret = stored.split(split);
		setStack(i, stored);
		return ret;
	}

	public int getComparatorOutput() {
		int occupied = 0;
		float fill = 0;
		for (int i = 0; i < items.size(); i++) {
			ItemResource resource = items.getResource(i);
			int amount = items.getAmountAsInt(i);
			if (!resource.isEmpty() && amount > 0) {
				fill += (float) amount / items.getCapacityAsInt(i, resource);
				occupied++;
			}
		}
		fill /= items.size();
		return Mth.floor(fill * 14.0F) + (occupied > 0 ? 1 : 0);
	}

	public abstract boolean specialClick(Player player, int i, InteractionHand hand);

	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		return items;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		// 1.21 L2 serialization stored the inventory under auto-serial.items as a
		// list of ItemStack compounds; migrate it when the new `stacks` key is absent.
		if (input.listOrEmpty("stacks", ItemStack.OPTIONAL_CODEC).isEmpty()) {
			input.child("auto-serial").ifPresent(legacy -> {
				var list = legacy.listOrEmpty("items", ItemStack.OPTIONAL_CODEC);
				int i = 0;
				for (ItemStack stack : list) {
					if (i >= items.size()) break;
					items.set(i++, stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack), stack.getCount());
				}
			});
		}
		items.deserialize(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		items.serialize(output);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemResource resource = items.getResource(i);
				int amount = items.getAmountAsInt(i);
				if (!resource.isEmpty() && amount > 0) {
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), resource.toStack(amount));
					items.set(i, ItemResource.EMPTY, 0);
				}
			}
		}
		super.preRemoveSideEffects(pos, state);
	}

}
