package com.mao.barbequesdelight.compat.rei;

import java.util.List;

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
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class GrillingCategory implements DisplayCategory<GrillingReiDisplay> {

	private static final int WIDTH = 118;
	private static final int HEIGHT = 58;
	private static final Identifier BG = BarbequesDelight.id("textures/gui/grill.png");

	@Override
	public CategoryIdentifier<GrillingReiDisplay> getCategoryIdentifier() {
		return BBQDReiPlugin.GRILLING;
	}

	@Override
	public Component getTitle() {
		return BBQLangData.JEI_BBQ.get();
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(BBQDBlocks.GRILL.get());
	}

	@Override
	public List<Widget> setupDisplay(GrillingReiDisplay display, Rectangle bounds) {
		Point start = new Point(bounds.getCenterX() - WIDTH / 2, bounds.getCenterY() - HEIGHT / 2);
		return List.of(
				Widgets.createTexturedWidget(BG, start.x, start.y, 0, 0, WIDTH, HEIGHT, 256, 256),
				Widgets.createSlot(new Point(start.x + 16, start.y + 22))
						.entries(display.getInputEntries().get(0)).markInput(),
				Widgets.withTooltip(Widgets.createSlot(new Point(start.x + 85, start.y + 22))
								.entries(display.getOutputEntries().get(0)).markOutput(),
						BBQLangData.JEI_TIME.get(String.valueOf(display.recipe().barbecuingTime() / 20))));
	}

	@Override
	public int getDisplayWidth(GrillingReiDisplay display) {
		return WIDTH;
	}

	@Override
	public int getDisplayHeight() {
		return HEIGHT;
	}
}
