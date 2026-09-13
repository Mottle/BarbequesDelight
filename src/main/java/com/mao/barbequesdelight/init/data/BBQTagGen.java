package com.mao.barbequesdelight.init.data;

import com.mao.barbequesdelight.init.BarbequesDelight;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BBQTagGen {

	public static final TagKey<Item> GRILLED_SKEWERS = ItemTags.create(BarbequesDelight.id("grilled_skewers"));
	public static final TagKey<Item> RAW_SKEWERS = ItemTags.create(BarbequesDelight.id("raw_skewers"));

}
