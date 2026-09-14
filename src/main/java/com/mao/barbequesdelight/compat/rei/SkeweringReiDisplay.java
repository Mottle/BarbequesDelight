package com.mao.barbequesdelight.compat.rei;

import java.util.List;

import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
import com.mojang.serialization.MapCodec;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SkeweringReiDisplay extends BasicDisplay {

	public static final MapCodec<SkeweringReiDisplay> MAP_CODEC =
			SkeweringRecipeDisplay.MAP_CODEC.xmap(SkeweringReiDisplay::new, SkeweringReiDisplay::recipe);
	public static final StreamCodec<RegistryFriendlyByteBuf, SkeweringReiDisplay> STREAM_CODEC =
			SkeweringRecipeDisplay.STREAM_CODEC.map(SkeweringReiDisplay::new, SkeweringReiDisplay::recipe);
	public static final DisplaySerializer<SkeweringReiDisplay> SERIALIZER = DisplaySerializer.of(MAP_CODEC, STREAM_CODEC);

	private final SkeweringRecipeDisplay recipe;

	public SkeweringReiDisplay(SkeweringRecipeDisplay recipe) {
		super(List.of(
				EntryIngredients.ofSlotDisplay(recipe.tool()),
				EntryIngredients.ofSlotDisplay(recipe.ingredient()),
				recipe.side().map(EntryIngredients::ofSlotDisplay).orElse(me.shedaniel.rei.api.common.entry.EntryIngredient.empty())),
				List.of(EntryIngredients.ofSlotDisplay(recipe.result())));
		this.recipe = recipe;
	}

	public SkeweringRecipeDisplay recipe() {
		return recipe;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return BBQDReiPlugin.SKEWERING;
	}

	@Override
	public DisplaySerializer<? extends SkeweringReiDisplay> getSerializer() {
		return SERIALIZER;
	}
}
