package com.mao.barbequesdelight.compat.jei;

import com.mao.barbequesdelight.content.recipe.SimpleSkeweringRecipe;
import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class BasinRecipeCategory implements IRecipeCategory<RecipeHolder<SimpleSkeweringRecipe>> {

	public static final IRecipeHolderType<SimpleSkeweringRecipe> TYPE = IRecipeHolderType.create(BarbequesDelight.id("skewering"));

	protected static final Identifier BG = BarbequesDelight.id("textures/gui/skewering.png");

	private final IDrawable background;
	private final IDrawable icon;

	public BasinRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(BG, 0, 0, 118, 58);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BBQDBlocks.BASIN.toStack());
	}

	@Override
	public IRecipeType<RecipeHolder<SimpleSkeweringRecipe>> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return BBQLangData.JEI_SKR.get();
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
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SimpleSkeweringRecipe> holder, IFocusGroup focuses) {
		SimpleSkeweringRecipe recipe = holder.value();
		builder.addSlot(RecipeIngredientRole.INPUT, 13, 13).add(recipe.tool)
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(BBQLangData.JEI_MAINHAND.get()));
		IRecipeSlotBuilder side = builder.addSlot(RecipeIngredientRole.INPUT, 13, 32)
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(recipe.sideCount == 0 ?
						BBQLangData.JEI_EMPTY.get() : BBQLangData.JEI_OFFHAND.get()));
		if (recipe.side != null) {
			side.addItemStacks(countedStacks(recipe.side, recipe.sideCount));
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 33, 22).addItemStacks(countedStacks(recipe.ingredient, recipe.ingredientCount))
				.addRichTooltipCallback((view, tooltip) -> tooltip.add(BBQLangData.JEI_BASIN.get()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 22).add(recipe.output);
	}

	@Override
	public void draw(RecipeHolder<SimpleSkeweringRecipe> holder, IRecipeSlotsView slots, GuiGraphicsExtractor g, double mouseX, double mouseY) {
		this.background.draw(g, 0, 0);
	}

	private static List<ItemStack> countedStacks(Ingredient ingredient, int count) {
		return ingredient.getValues().stream().map(item -> new ItemStack(item, count)).toList();
	}

}
