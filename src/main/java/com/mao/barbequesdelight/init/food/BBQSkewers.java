package com.mao.barbequesdelight.init.food;

import com.mao.barbequesdelight.content.item.BBQSkewerItem;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.registries.DeferredItem;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Locale;

public enum BBQSkewers {
	COD(7, 1f),
	SALMON(7, 1f),
	CHICKEN(7, 0.7f),
	MUSHROOM(6, 0.4f),
	BEEF(8, 0.7f, new EffectEntry(MobEffects.STRENGTH, 1800, 0, 0.5f)),
	LAMB(12, 0.8f, new EffectEntry(MobEffects.REGENERATION, 1800, 0, 0.5f)),
	RABBIT(10, 0.8f, new EffectEntry(MobEffects.JUMP_BOOST, 1800, 0, 1)),
	PORK_SAUSAGE(8, 0.7f, new EffectEntry(MobEffects.RESISTANCE, 1800, 0, 0.5f)),
	POTATO(6, 0.6f, new EffectEntry(ModEffects.NOURISHMENT, 1800, 0, 0.5f)),
	VEGETABLE(5, 0.5f, new EffectEntry(MobEffects.REGENERATION, 1200, 0, 0.25f)),
	;

	public final DeferredItem<BBQSkewerItem> item;
	public final DeferredItem<BBQSkewerItem> skewer;

	BBQSkewers(int food, float sat, EffectEntry... entries) {
		String id = name().toLowerCase(Locale.ROOT);
		FoodProperties properties = food(food, sat);
		Consumable consumable = consumable(entries);
		item = BBQDItems.ITEMS.registerItem("raw_" + id + "_skewer",
				p -> new BBQSkewerItem(p.craftRemainder(Items.STICK)));
		skewer = BBQDItems.ITEMS.registerItem("grilled_" + id + "_skewer",
				p -> new BBQSkewerItem(p.craftRemainder(Items.STICK).food(properties, consumable), properties, consumable));
	}

	private static FoodProperties food(int food, float sat) {
		return new FoodProperties.Builder()
				.nutrition(food).saturationModifier(sat)
				.build();
	}

	private static Consumable consumable(EffectEntry... entries) {
		Consumable.Builder builder = Consumables.defaultFood();
		for (var e : entries) {
			builder.onConsume(new ApplyStatusEffectsConsumeEffect(e.getEffect(), e.chance()));
		}
		return builder.build();
	}

	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static void register() {
	}

}
