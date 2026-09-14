package com.mao.barbequesdelight.content.recipe;

import com.mao.barbequesdelight.content.item.BBQSandwichItem;
import com.mao.barbequesdelight.init.data.BBQTagGen;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class CombineItemRecipe extends ShapelessRecipe {

	public static final MapCodec<CombineItemRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(CombineItemRecipe::result),
			Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(CombineItemRecipe::ingredients)
	).apply(inst, CombineItemRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CombineItemRecipe> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
			CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
			ItemStackTemplate.STREAM_CODEC, CombineItemRecipe::result,
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CombineItemRecipe::ingredients,
			CombineItemRecipe::new);

	private final ItemStackTemplate result;
	private final List<Ingredient> ingredients;

	public CombineItemRecipe(CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
		this(new Recipe.CommonInfo(true), bookInfo, result, ingredients);
	}

	public CombineItemRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo,
							 ItemStackTemplate result, List<Ingredient> ingredients) {
		super(commonInfo, bookInfo, result, ingredients);
		this.result = result;
		this.ingredients = ingredients;
	}

	public ItemStackTemplate result() {
		return result;
	}

	public List<Ingredient> ingredients() {
		return ingredients;
	}

	@Override
	public ItemStack assemble(CraftingInput cont) {
		ItemStack ans = super.assemble(cont);
		List<ItemStack> skewers = new ArrayList<>();
		for (int i = 0; i < cont.size(); i++) {
			ItemStack stack = cont.getItem(i);
			if (stack.is(BBQTagGen.GRILLED_SKEWERS)) {
				skewers.add(stack.copyWithCount(1));
			}
		}
		BBQSandwichItem.applyContents(ans, skewers);
		return ans;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RecipeSerializer<ShapelessRecipe> getSerializer() {
		// ShapelessRecipe fixes the serializer type parameter, the codec itself is the combine one
		return (RecipeSerializer<ShapelessRecipe>) (RecipeSerializer<?>) BBQDRecipes.COMBINE.get();
	}

}
