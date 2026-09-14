package com.mao.barbequesdelight.compat.rei;

import java.util.ArrayList;
import java.util.List;

import com.mao.barbequesdelight.content.recipe.display.SkeweringRecipeDisplay;
import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SkeweringCategory implements DisplayCategory<SkeweringReiDisplay> {

	private static final int WIDTH = 118;
	private static final int HEIGHT = 58;
	private static final Identifier BG = BarbequesDelight.id("textures/gui/skewering.png");

	@Override
	public CategoryIdentifier<SkeweringReiDisplay> getCategoryIdentifier() {
		return BBQDReiPlugin.SKEWERING;
	}

	@Override
	public Component getTitle() {
		return BBQLangData.JEI_SKR.get();
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(BBQDBlocks.BASIN.get());
	}

	@Override
	public List<Widget> setupDisplay(SkeweringReiDisplay display, Rectangle bounds) {
		SkeweringRecipeDisplay recipe = display.recipe();
		Point start = new Point(bounds.getCenterX() - WIDTH / 2, bounds.getCenterY() - HEIGHT / 2);
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createTexturedWidget(BG, start.x, start.y, 0, 0, WIDTH, HEIGHT, 256, 256));
		widgets.add(Widgets.withTooltip(Widgets.createSlot(new Point(start.x + 13, start.y + 13))
						.entries(display.getInputEntries().get(0)).markInput(),
				BBQLangData.JEI_MAINHAND.get()));
		widgets.add(Widgets.withTooltip(Widgets.createSlot(new Point(start.x + 13, start.y + 32))
						.entries(withCount(display.getInputEntries().get(2), recipe.sideCount())).markInput(),
				recipe.sideCount() == 0 ? BBQLangData.JEI_EMPTY.get() : BBQLangData.JEI_OFFHAND.get()));
		widgets.add(Widgets.withTooltip(Widgets.createSlot(new Point(start.x + 33, start.y + 22))
						.entries(withCount(display.getInputEntries().get(1), recipe.ingredientCount())).markInput(),
				BBQLangData.JEI_BASIN.get()));
		widgets.add(Widgets.createSlot(new Point(start.x + 83, start.y + 22))
				.entries(display.getOutputEntries().get(0)).markOutput());
		return widgets;
	}

	private static EntryIngredient withCount(EntryIngredient entries, int count) {
		if (count <= 1) {
			return entries;
		}
		return entries.map(stack -> {
			if (stack.getValue() instanceof ItemStack itemStack) {
				return EntryStacks.of(itemStack.copyWithCount(count));
			}
			return (EntryStack<?>) stack;
		});
	}

	@Override
	public int getDisplayWidth(SkeweringReiDisplay display) {
		return WIDTH;
	}

	@Override
	public int getDisplayHeight() {
		return HEIGHT;
	}
}
