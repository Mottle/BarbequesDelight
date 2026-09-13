package com.mao.barbequesdelight.init.registrate;

import com.mao.barbequesdelight.content.block.GrillBlockItem;
import com.mao.barbequesdelight.content.item.BBQSandwichItem;
import com.mao.barbequesdelight.content.item.DCListStack;
import com.mao.barbequesdelight.content.item.FoodItem;
import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.food.BBQSeasoning;
import com.mao.barbequesdelight.init.food.BBQSkewers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class BBQDItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BarbequesDelight.MODID);
	public static final DeferredRegister.DataComponents DATA_COMPONENTS =
			DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BarbequesDelight.MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BarbequesDelight.MODID);

	public static final DeferredItem<FoodItem> BURNT_FOOD = ITEMS.registerItem("burnt_skewer", FoodItem::new,
			p -> p.craftRemainder(Items.STICK).food(
					new FoodProperties.Builder().nutrition(4).saturationModifier(.2f).alwaysEdible().build(),
					Consumables.defaultFood()
							.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON, 200, 1), 1.0f))
							.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 1), 1.0f))
							.build()));

	public static final DeferredItem<BBQSandwichItem> KEBAB_WRAP = ITEMS.registerItem("kebab_wrap", BBQSandwichItem::new,
			p -> p.food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.7f).build(),
					Consumables.defaultFood()
							.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.NOURISHMENT, 1200, 0), 0.5f))
							.build()));

	public static final DeferredItem<BBQSandwichItem> KEBAB_SANDWICH = ITEMS.registerItem("kebab_sandwich", BBQSandwichItem::new,
			p -> p.food(new FoodProperties.Builder().nutrition(14).saturationModifier(0.7f).build()));

	public static final DeferredItem<BBQSandwichItem> BIBIMBAP = ITEMS.registerItem("bibimbap", BBQSandwichItem::new,
			p -> p.food(new FoodProperties.Builder().nutrition(16).saturationModifier(0.7f).build(),
					Consumables.defaultFood()
							.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.NOURISHMENT, 2400), 1))
							.build()));

	public static final DeferredItem<GrillBlockItem> GRILL = ITEMS.registerItem("grill",
			p -> new GrillBlockItem(BBQDBlocks.GRILL.get(), p.useBlockDescriptionPrefix()));
	public static final DeferredItem<BlockItem> BASIN = ITEMS.registerSimpleBlockItem("basin", BBQDBlocks.BASIN);
	public static final DeferredItem<BlockItem> TRAY = ITEMS.registerSimpleBlockItem("tray", BBQDBlocks.TRAY);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<BBQSeasoning>> SEASONING =
			DATA_COMPONENTS.registerComponentType("seasoning", builder -> builder
					.persistent(BBQSeasoning.CODEC)
					.networkSynchronized(BBQSeasoning.STREAM_CODEC));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DCListStack>> CONTENTS =
			DATA_COMPONENTS.registerComponentType("content", builder -> builder
					.persistent(DCListStack.CODEC)
					.networkSynchronized(DCListStack.STREAM_CODEC)
					.cacheEncoding());

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("main",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.barbequesdelight.main"))
					.icon(() -> GRILL.toStack())
					.displayItems((params, output) -> {
						output.accept(GRILL);
						output.accept(BASIN);
						output.accept(TRAY);
						for (BBQSkewers skewer : BBQSkewers.values()) {
							output.accept(skewer.item.get());
							output.accept(skewer.skewer.get());
						}
						for (BBQSeasoning seasoning : BBQSeasoning.values()) {
							output.accept(seasoning.item.get());
						}
						output.accept(BURNT_FOOD.get());
						output.accept(KEBAB_WRAP.get());
						output.accept(KEBAB_SANDWICH.get());
						output.accept(BIBIMBAP.get());
					})
					.build());

	static {
		// the enums register their own items, so they have to be loaded through this class
		BBQSeasoning.register();
		BBQSkewers.register();
	}

}
