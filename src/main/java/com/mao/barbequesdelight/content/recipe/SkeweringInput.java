package com.mao.barbequesdelight.content.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record SkeweringInput(ItemStack stick, ItemStack ingredient, ItemStack side) implements RecipeInput {

	@Override
	public ItemStack getItem(int i) {
		return i == 0 ? stick : i == 1 ? ingredient : i == 2 ? side : ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 3;
	}

}
