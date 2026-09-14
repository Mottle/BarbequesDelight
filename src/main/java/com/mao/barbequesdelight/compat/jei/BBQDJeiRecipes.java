package com.mao.barbequesdelight.compat.jei;

import com.mao.barbequesdelight.content.recipe.SimpleGrillingRecipe;
import com.mao.barbequesdelight.content.recipe.SimpleSkeweringRecipe;
import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

@EventBusSubscriber(modid = BarbequesDelight.MODID, value = Dist.CLIENT)
public final class BBQDJeiRecipes {

	private static RecipeMap recipes = RecipeMap.EMPTY;

	private BBQDJeiRecipes() {
	}

	@SubscribeEvent
	public static void onRecipesReceived(RecipesReceivedEvent event) {
		recipes = event.getRecipeMap();
	}

	public static List<RecipeHolder<SimpleGrillingRecipe>> grilling() {
		return recipes.byType(BBQDRecipes.RT_BBQ.get()).stream().toList();
	}

	public static List<RecipeHolder<SimpleSkeweringRecipe>> skewering() {
		return recipes.byType(BBQDRecipes.RT_SKR.get()).stream().toList();
	}

}
