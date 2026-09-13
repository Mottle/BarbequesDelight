package com.mao.barbequesdelight.content.block;

import com.mao.barbequesdelight.content.recipe.SkeweringInput;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class BasinBlockEntity extends StorageTileBlockEntity {

	public BasinBlockEntity(BlockPos pos, BlockState state) {
		super(BBQDBlocks.TE_BASIN.get(), pos, state);
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public AABB getBox() {
		return BasinBlock.OUTER.bounds().move(getBlockPos()).deflate(0.01f);
	}

	@Override
	public boolean specialClick(Player user, int slot, InteractionHand hand) {
		if (level == null) return false;
		ItemStack stack = user.getMainHandItem();
		ItemStack basin = getStack(slot);
		ItemStack garnishes = user.getOffhandItem();
		var cont = new SkeweringInput(stack, basin, garnishes);
		var optional = findRecipe(BBQDRecipes.RT_SKR.get(), cont);
		if (optional.isEmpty()) return false;
		ItemStack ret = optional.get().value().assemble(cont);
		setStack(slot, basin);
		if (user.getItemInHand(hand).isEmpty()) {
			user.setItemInHand(hand, ret);
		} else user.getInventory().placeItemBackInInventory(ret);
		return true;
	}

	private <R extends Recipe<SkeweringInput>> Optional<RecipeHolder<R>> findRecipe(RecipeType<R> type, SkeweringInput input) {
		Level level = this.level;
		MinecraftServer server = level == null ? null : level.getServer();
		if (server == null) return Optional.empty();
		return server.getRecipeManager().getRecipeFor(type, input, level);
	}

}
