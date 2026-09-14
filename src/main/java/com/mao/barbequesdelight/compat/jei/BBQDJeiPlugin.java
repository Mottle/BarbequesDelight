package com.mao.barbequesdelight.compat.jei;

import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class BBQDJeiPlugin implements IModPlugin {

	public static final Identifier ID = BarbequesDelight.id("main");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				new GrillRecipeCategory(helper),
				new BasinRecipeCategory(helper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(GrillRecipeCategory.TYPE, BBQDJeiRecipes.grilling());
		registration.addRecipes(BasinRecipeCategory.TYPE, BBQDJeiRecipes.skewering());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(GrillRecipeCategory.TYPE, BBQDBlocks.GRILL.toStack());
		registration.addCraftingStation(BasinRecipeCategory.TYPE, BBQDBlocks.BASIN.toStack());
	}

}
