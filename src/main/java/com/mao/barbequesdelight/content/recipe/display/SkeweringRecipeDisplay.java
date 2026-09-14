package com.mao.barbequesdelight.content.recipe.display;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record SkeweringRecipeDisplay(SlotDisplay tool, SlotDisplay ingredient, int ingredientCount,
									 Optional<SlotDisplay> side, int sideCount, SlotDisplay result,
									 SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<SkeweringRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SlotDisplay.CODEC.fieldOf("tool").forGetter(SkeweringRecipeDisplay::tool),
		SlotDisplay.CODEC.fieldOf("ingredient").forGetter(SkeweringRecipeDisplay::ingredient),
		Codec.INT.fieldOf("ingredient_count").forGetter(SkeweringRecipeDisplay::ingredientCount),
		SlotDisplay.CODEC.optionalFieldOf("side").forGetter(SkeweringRecipeDisplay::side),
		Codec.INT.fieldOf("side_count").forGetter(SkeweringRecipeDisplay::sideCount),
		SlotDisplay.CODEC.fieldOf("result").forGetter(SkeweringRecipeDisplay::result),
		SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SkeweringRecipeDisplay::craftingStation)
	).apply(instance, SkeweringRecipeDisplay::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SkeweringRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
		SlotDisplay.STREAM_CODEC, SkeweringRecipeDisplay::tool,
		SlotDisplay.STREAM_CODEC, SkeweringRecipeDisplay::ingredient,
		ByteBufCodecs.INT, SkeweringRecipeDisplay::ingredientCount,
		ByteBufCodecs.optional(SlotDisplay.STREAM_CODEC), SkeweringRecipeDisplay::side,
		ByteBufCodecs.INT, SkeweringRecipeDisplay::sideCount,
		SlotDisplay.STREAM_CODEC, SkeweringRecipeDisplay::result,
		SlotDisplay.STREAM_CODEC, SkeweringRecipeDisplay::craftingStation,
		SkeweringRecipeDisplay::new
	);
	public static final Type<SkeweringRecipeDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public Type<SkeweringRecipeDisplay> type() {
		return TYPE;
	}

	@Override
	public boolean isEnabled(FeatureFlagSet enabledFeatures) {
		return this.tool.isEnabled(enabledFeatures) && this.ingredient.isEnabled(enabledFeatures)
			&& this.side.map(s -> s.isEnabled(enabledFeatures)).orElse(true)
			&& RecipeDisplay.super.isEnabled(enabledFeatures);
	}
}
