package com.mao.barbequesdelight.content.recipe;

import com.mao.barbequesdelight.content.recipe.display.GrillingRecipeDisplay;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

public class SimpleGrillingRecipe implements Recipe<SingleRecipeInput> {

	public static final MapCodec<SimpleGrillingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
			ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output),
			Codec.INT.fieldOf("barbecuingTime").forGetter(r -> r.barbecuingTime)
	).apply(inst, SimpleGrillingRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SimpleGrillingRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
			ItemStackTemplate.STREAM_CODEC, r -> r.output,
			ByteBufCodecs.VAR_INT, r -> r.barbecuingTime,
			SimpleGrillingRecipe::new);

	public final Ingredient ingredient;
	public final ItemStackTemplate output;
	public final int barbecuingTime;

	public SimpleGrillingRecipe(Ingredient ingredient, ItemStackTemplate output, int barbecuingTime) {
		this.ingredient = ingredient;
		this.output = output;
		this.barbecuingTime = barbecuingTime;
	}

	public int getBarbecuingTime() {
		return barbecuingTime;
	}

	@Override
	public boolean matches(SingleRecipeInput cont, Level level) {
		return ingredient.test(cont.getItem(0));
	}

	@Override
	public ItemStack assemble(SingleRecipeInput cont) {
		return output.create();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
		return BBQDRecipes.RS_BBQ.get();
	}

	@Override
	public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
		return BBQDRecipes.RT_BBQ.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new GrillingRecipeDisplay(
				ingredient.display(),
				new SlotDisplay.ItemStackSlotDisplay(output),
				new SlotDisplay.ItemSlotDisplay(BBQDBlocks.GRILL.get().asItem()),
				barbecuingTime));
	}

}
