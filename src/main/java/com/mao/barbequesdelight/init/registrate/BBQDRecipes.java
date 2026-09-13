package com.mao.barbequesdelight.init.registrate;

import com.mao.barbequesdelight.content.recipe.CombineItemRecipe;
import com.mao.barbequesdelight.content.recipe.SimpleGrillingRecipe;
import com.mao.barbequesdelight.content.recipe.SimpleSkeweringRecipe;
import com.mao.barbequesdelight.content.recipe.display.GrillingRecipeDisplay;
import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
import com.mao.barbequesdelight.init.BarbequesDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BBQDRecipes {

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
			DeferredRegister.create(Registries.RECIPE_TYPE, BarbequesDelight.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
			DeferredRegister.create(Registries.RECIPE_SERIALIZER, BarbequesDelight.MODID);
	public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS =
			DeferredRegister.create(Registries.RECIPE_DISPLAY, BarbequesDelight.MODID);

	public static final DeferredHolder<RecipeType<?>, RecipeType<SimpleGrillingRecipe>> RT_BBQ =
			RECIPE_TYPES.register("grilling", () -> RecipeType.simple(BarbequesDelight.id("grilling")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<SimpleSkeweringRecipe>> RT_SKR =
			RECIPE_TYPES.register("skewering", () -> RecipeType.simple(BarbequesDelight.id("skewering")));
	// Matches the Horizon extension's combine recipe type; the recipe itself
	// still reports the vanilla crafting_shapeless type.
	public static final DeferredHolder<RecipeType<?>, RecipeType<CombineItemRecipe>> RT_COMBINE =
			RECIPE_TYPES.register("combine", () -> RecipeType.simple(BarbequesDelight.id("combine")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleGrillingRecipe>> RS_BBQ =
			RECIPE_SERIALIZERS.register("grilling", () -> new RecipeSerializer<>(SimpleGrillingRecipe.CODEC, SimpleGrillingRecipe.STREAM_CODEC));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SimpleSkeweringRecipe>> RS_SKR =
			RECIPE_SERIALIZERS.register("skewering", () -> new RecipeSerializer<>(SimpleSkeweringRecipe.CODEC, SimpleSkeweringRecipe.STREAM_CODEC));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CombineItemRecipe>> COMBINE =
			RECIPE_SERIALIZERS.register("combine", () -> new RecipeSerializer<>(CombineItemRecipe.MAP_CODEC, CombineItemRecipe.STREAM_CODEC));

	public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<GrillingRecipeDisplay>> RD_BBQ =
			RECIPE_DISPLAYS.register("grilling", () -> GrillingRecipeDisplay.TYPE);
	public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<SkeweringRecipeDisplay>> RD_SKR =
			RECIPE_DISPLAYS.register("skewering", () -> SkeweringRecipeDisplay.TYPE);

}
