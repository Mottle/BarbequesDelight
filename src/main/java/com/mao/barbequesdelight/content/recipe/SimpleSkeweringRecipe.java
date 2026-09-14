package com.mao.barbequesdelight.content.recipe;

import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
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
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SimpleSkeweringRecipe implements Recipe<SkeweringInput> {

	public static final MapCodec<SimpleSkeweringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Ingredient.CODEC.fieldOf("tool").forGetter(r -> r.tool),
			Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
			Codec.INT.fieldOf("ingredientCount").forGetter(r -> r.ingredientCount),
			Ingredient.CODEC.optionalFieldOf("side").forGetter(r -> Optional.ofNullable(r.side)),
			Codec.INT.optionalFieldOf("sideCount", 0).forGetter(r -> r.sideCount),
			ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output)
	).apply(inst, SimpleSkeweringRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SimpleSkeweringRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, r -> r.tool,
			Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
			ByteBufCodecs.VAR_INT, r -> r.ingredientCount,
			Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> Optional.ofNullable(r.side),
			ByteBufCodecs.VAR_INT, r -> r.sideCount,
			ItemStackTemplate.STREAM_CODEC, r -> r.output,
			SimpleSkeweringRecipe::new);

	public final Ingredient tool, ingredient;
	public final int ingredientCount, sideCount;
	public final @Nullable Ingredient side;
	public final ItemStackTemplate output;

	public SimpleSkeweringRecipe(Ingredient tool, Ingredient ingredient, int ingredientCount,
								 Optional<Ingredient> side, int sideCount, ItemStackTemplate output) {
		this.tool = tool;
		this.ingredient = ingredient;
		this.ingredientCount = ingredientCount;
		this.side = side.orElse(null);
		this.sideCount = sideCount;
		this.output = output;
	}

	@Override
	public boolean matches(SkeweringInput cont, Level level) {
		return tool.test(cont.stick()) &&
				ingredient.test(cont.ingredient()) && cont.ingredient().getCount() >= ingredientCount &&
				(sideCount == 0 || side != null && side.test(cont.side()) && cont.side().getCount() >= sideCount);
	}

	@Override
	public ItemStack assemble(SkeweringInput cont) {
		cont.stick().shrink(1);
		cont.ingredient().shrink(ingredientCount);
		if (sideCount > 0 && side != null) {
			cont.side().shrink(sideCount);
		}
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
	public RecipeSerializer<? extends Recipe<SkeweringInput>> getSerializer() {
		return BBQDRecipes.RS_SKR.get();
	}

	@Override
	public RecipeType<? extends Recipe<SkeweringInput>> getType() {
		return BBQDRecipes.RT_SKR.get();
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
		return List.of(new SkeweringRecipeDisplay(
				tool.display(), ingredient.display(), ingredientCount,
				Optional.ofNullable(side).map(Ingredient::display), sideCount,
				new SlotDisplay.ItemStackSlotDisplay(output),
				new SlotDisplay.ItemSlotDisplay(BBQDBlocks.BASIN.get().asItem())));
	}

}
