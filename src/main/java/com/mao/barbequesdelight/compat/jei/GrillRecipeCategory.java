package com.mao.barbequesdelight.compat.jei;

import com.mao.barbequesdelight.content.recipe.SimpleGrillingRecipe;
import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class GrillRecipeCategory implements IRecipeCategory<RecipeHolder<SimpleGrillingRecipe>> {

	public static final IRecipeHolderType<SimpleGrillingRecipe> TYPE = IRecipeHolderType.create(BarbequesDelight.id("grilling"));

	protected static final Identifier BG = BarbequesDelight.id("textures/gui/grill.png");

	private final IDrawable background;
	private final IDrawable icon;

	public GrillRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(BG, 0, 0, 118, 58);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BBQDBlocks.GRILL.toStack());
	}

	@Override
	public IRecipeType<RecipeHolder<SimpleGrillingRecipe>> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return BBQLangData.JEI_BBQ.get();
	}

	@Override
	public int getWidth() {
		return 118;
	}

	@Override
	public int getHeight() {
		return 58;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SimpleGrillingRecipe> holder, IFocusGroup focuses) {
		SimpleGrillingRecipe recipe = holder.value();
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 22).add(recipe.ingredient);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 22).add(recipe.output)
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(BBQLangData.JEI_TIME.get("" + recipe.barbecuingTime / 20)));
	}

	@Override
	public void draw(RecipeHolder<SimpleGrillingRecipe> holder, IRecipeSlotsView slots, GuiGraphicsExtractor g, double mouseX, double mouseY) {
		this.background.draw(g, 0, 0);
	}

}
