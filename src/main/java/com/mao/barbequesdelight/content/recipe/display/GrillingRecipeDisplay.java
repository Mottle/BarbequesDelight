package com.mao.barbequesdelight.content.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record GrillingRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation,
									int barbecuingTime) implements RecipeDisplay {
	public static final MapCodec<GrillingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SlotDisplay.CODEC.fieldOf("input").forGetter(GrillingRecipeDisplay::input),
		SlotDisplay.CODEC.fieldOf("result").forGetter(GrillingRecipeDisplay::result),
		SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(GrillingRecipeDisplay::craftingStation),
		Codec.INT.fieldOf("barbecuing_time").forGetter(GrillingRecipeDisplay::barbecuingTime)
	).apply(instance, GrillingRecipeDisplay::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GrillingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
		SlotDisplay.STREAM_CODEC, GrillingRecipeDisplay::input,
		SlotDisplay.STREAM_CODEC, GrillingRecipeDisplay::result,
		SlotDisplay.STREAM_CODEC, GrillingRecipeDisplay::craftingStation,
		ByteBufCodecs.INT, GrillingRecipeDisplay::barbecuingTime,
		GrillingRecipeDisplay::new
	);
	public static final Type<GrillingRecipeDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public Type<GrillingRecipeDisplay> type() {
		return TYPE;
	}

	@Override
	public boolean isEnabled(FeatureFlagSet enabledFeatures) {
		return this.input.isEnabled(enabledFeatures) && RecipeDisplay.super.isEnabled(enabledFeatures);
	}
}
