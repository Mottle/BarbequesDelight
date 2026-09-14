package com.mao.barbequesdelight.compat.rei;

import com.mao.barbequesdelight.content.recipe.display.GrillingRecipeDisplay;
import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class BBQDReiPlugin implements REIClientPlugin {

	public static final CategoryIdentifier<GrillingReiDisplay> GRILLING =
			CategoryIdentifier.of("barbequesdelight:grilling");
	public static final CategoryIdentifier<SkeweringReiDisplay> SKEWERING =
			CategoryIdentifier.of("barbequesdelight:skewering");

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new GrillingCategory());
		registry.add(new SkeweringCategory());
		registry.addWorkstations(GRILLING, EntryStacks.of(BBQDBlocks.GRILL.get()));
		registry.addWorkstations(SKEWERING, EntryStacks.of(BBQDBlocks.BASIN.get()));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.beginRecipeFiller(GrillingRecipeDisplay.class).fill((display, id) -> new GrillingReiDisplay(display));
		registry.beginRecipeFiller(SkeweringRecipeDisplay.class).fill((display, id) -> new SkeweringReiDisplay(display));
	}
}
