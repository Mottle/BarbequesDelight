package com.mao.barbequesdelight.init.data;

import com.mao.barbequesdelight.init.BarbequesDelight;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.Locale;

public enum BBQLangData {
	CHANCE_EFFECT("tooltip.chance", "%1$s with %2$s%% chance", 2, ChatFormatting.GRAY),

	INFO_FLIP("info.flip", "Sneak right click to flip skewer", 0, ChatFormatting.WHITE),

	CHILI("seasoning.chili", "Chili Flavored ", 0, ChatFormatting.YELLOW),
	CUMIN("seasoning.cumin", "Cumin Flavored ", 0, ChatFormatting.YELLOW),
	PEPPER("seasoning.pepper", "Pepper Flavored ", 0, ChatFormatting.YELLOW),
	HONEY("seasoning.honey_mustard", "Honey Mustard Flavored ", 0, ChatFormatting.YELLOW),
	BUFFALO("seasoning.buffalo", "Buffalo Flavored ", 0, ChatFormatting.YELLOW),
	BBQ("seasoning.barbeque", "Barbeque Flavored ", 0, ChatFormatting.YELLOW),
	CHILI_INFO("tooltip.chili", "It will make you always want to take one more skewer!", 0, ChatFormatting.GRAY),
	CUMIN_INFO("tooltip.cumin", "Skewers with it will make you feel better~", 0, ChatFormatting.GRAY),
	PEPPER_INFO("tooltip.pepper", "You can't wait to finish skewers with it!", 0, ChatFormatting.GRAY),
	HONEY_INFO("tooltip.honey_mustard", "It will make skewer's flavor more apparent", 0, ChatFormatting.GRAY),
	BUFFALO_INFO("tooltip.buffalo", "It will make skewer's flavor stronger", 0, ChatFormatting.GRAY),
	BBQ_INFO("tooltip.barbeque", "It will make skewer's flavor last longer", 0, ChatFormatting.GRAY),


	JEI_BBQ("jei.grilling", "Grilling", 0, null),
	JEI_SKR("jei.skewering", "Skewering", 0, null),
	JEI_TIME("jei.time", "Grilling time: %s seconds each side", 1, ChatFormatting.GRAY),
	JEI_MAINHAND("jei.mainhand", "Right click basin with this item in main hand", 0, ChatFormatting.GRAY),
	JEI_OFFHAND("jei.offhand", "Hold this item in off hand", 0, ChatFormatting.GRAY),
	JEI_EMPTY("jei.offhand_empty", "Hold nothing in off hand", 0, ChatFormatting.GRAY),
	JEI_BASIN("jei.item_basin", "Click this item in basin", 0, ChatFormatting.GRAY),

	JADE_COOK("jade.cook", "Cooking: %ss", 1, ChatFormatting.GRAY),
	JADE_FLIP("jade.flip", "Ready to Flip!", 0, ChatFormatting.YELLOW),
	JADE_COOKED("jade.cooked", "Cooked!", 0, ChatFormatting.YELLOW),
	JADE_BURNT("jade.burnt", "Burnt!!", 0, ChatFormatting.RED),
	;

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;


	BBQLangData(String key, String def, int arg, @Nullable ChatFormatting format) {
		this.key = BarbequesDelight.MODID + "." + key;
		this.def = def;
		this.arg = arg;
		this.format = format;
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		MutableComponent ans = Component.translatable(key, args);
		if (format != null) {
			return ans.withStyle(format);
		}
		return ans;
	}

}
