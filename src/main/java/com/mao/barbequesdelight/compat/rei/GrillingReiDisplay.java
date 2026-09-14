package com.mao.barbequesdelight.compat.rei;

import java.util.List;

import com.mao.barbequesdelight.content.recipe.display.GrillingRecipeDisplay;
import com.mojang.serialization.MapCodec;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class GrillingReiDisplay extends BasicDisplay {

	public static final MapCodec<GrillingReiDisplay> MAP_CODEC =
			GrillingRecipeDisplay.MAP_CODEC.xmap(GrillingReiDisplay::new, GrillingReiDisplay::recipe);
	public static final StreamCodec<RegistryFriendlyByteBuf, GrillingReiDisplay> STREAM_CODEC =
			GrillingRecipeDisplay.STREAM_CODEC.map(GrillingReiDisplay::new, GrillingReiDisplay::recipe);
	public static final DisplaySerializer<GrillingReiDisplay> SERIALIZER = DisplaySerializer.of(MAP_CODEC, STREAM_CODEC);

	private final GrillingRecipeDisplay recipe;

	public GrillingReiDisplay(GrillingRecipeDisplay recipe) {
		super(List.of(EntryIngredients.ofSlotDisplay(recipe.input())),
				List.of(EntryIngredients.ofSlotDisplay(recipe.result())));
		this.recipe = recipe;
	}

	public GrillingRecipeDisplay recipe() {
		return recipe;
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return BBQDReiPlugin.GRILLING;
	}

	@Override
	public DisplaySerializer<? extends GrillingReiDisplay> getSerializer() {
		return SERIALIZER;
	}
}
