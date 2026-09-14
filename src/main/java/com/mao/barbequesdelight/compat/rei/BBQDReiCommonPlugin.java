package com.mao.barbequesdelight.compat.rei;

import com.mao.barbequesdelight.content.recipe.SimpleGrillingRecipe;
import com.mao.barbequesdelight.content.recipe.SimpleSkeweringRecipe;
import com.mao.barbequesdelight.content.recipe.display.GrillingRecipeDisplay;
import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
import com.mao.barbequesdelight.init.BarbequesDelight;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;

@REIPluginCommon
public class BBQDReiCommonPlugin implements REICommonPlugin {

	@Override
	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
		registry.register(BarbequesDelight.id("grilling"), GrillingReiDisplay.SERIALIZER);
		registry.register(BarbequesDelight.id("skewering"), SkeweringReiDisplay.SERIALIZER);
	}

	@Override
	public void registerDisplays(ServerDisplayRegistry registry) {
		registry.beginRecipeFiller(SimpleGrillingRecipe.class).fillMultiple(holder -> holder.value().display().stream()
				.filter(GrillingRecipeDisplay.class::isInstance)
				.map(d -> new GrillingReiDisplay((GrillingRecipeDisplay) d))
				.toList());
		registry.beginRecipeFiller(SimpleSkeweringRecipe.class).fillMultiple(holder -> holder.value().display().stream()
				.filter(SkeweringRecipeDisplay.class::isInstance)
				.map(d -> new SkeweringReiDisplay((SkeweringRecipeDisplay) d))
				.toList());
	}
}
