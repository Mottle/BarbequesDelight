package com.mao.barbequesdelight.content.item;

import com.mao.barbequesdelight.init.registrate.BBQDItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BBQSandwichItem extends FoodItem {

	public static final String KEY = "Skewers";

	public static DCListStack getSkewers(ItemStack stack) {
		return stack.getOrDefault(BBQDItems.CONTENTS.get(), DCListStack.EMPTY);
	}

	public BBQSandwichItem(Properties properties) {
		super(properties);
	}

	/**
	 * Stores the skewers used to craft this sandwich and folds their effects into the
	 * sandwich components. The nutrition stays the one of the plain sandwich; the skewers
	 * can only add effects, shorten the eating time and make the sandwich always edible.
	 * Effects are collected in the order the skewers are stored in and a duplicate effect
	 * is ignored, so the first skewer carrying it decides its duration and strength.
	 */
	public static void applyContents(ItemStack result, List<ItemStack> skewers) {
		result.set(BBQDItems.CONTENTS.get(), new DCListStack(skewers));
		FoodProperties food = result.get(DataComponents.FOOD);
		Consumable consumable = result.get(DataComponents.CONSUMABLE);
		if (food == null || consumable == null) return;
		boolean alwaysEdible = food.canAlwaysEat();
		float seconds = consumable.consumeSeconds();
		List<ConsumeEffect> effects = new ArrayList<>();
		Set<Holder<MobEffect>> seen = new HashSet<>();
		collect(effects, seen, consumable);
		for (ItemStack stack : skewers) {
			Consumable extra = BBQSkewerItem.effectiveConsumable(stack);
			if (extra == null) continue;
			collect(effects, seen, extra);
			seconds = Math.min(seconds, extra.consumeSeconds());
			FoodProperties skewerFood = BBQSkewerItem.effectiveFood(stack);
			if (skewerFood != null) alwaysEdible |= skewerFood.canAlwaysEat();
		}
		FoodProperties.Builder foodBuilder = new FoodProperties.Builder()
				.nutrition(food.nutrition())
				.saturationModifier(food.nutrition() == 0 ? 0 : food.saturation() / food.nutrition() / 2);
		if (alwaysEdible) foodBuilder.alwaysEdible();
		Consumable.Builder consumableBuilder = Consumable.builder()
				.consumeSeconds(seconds)
				.animation(consumable.animation())
				.sound(consumable.sound())
				.hasConsumeParticles(consumable.hasConsumeParticles());
		effects.forEach(consumableBuilder::onConsume);
		result.set(DataComponents.FOOD, foodBuilder.build());
		result.set(DataComponents.CONSUMABLE, consumableBuilder.build());
	}

	private static void collect(List<ConsumeEffect> effects, Set<Holder<MobEffect>> seen, Consumable consumable) {
		for (ConsumeEffect effect : consumable.onConsumeEffects()) {
			if (effect instanceof ApplyStatusEffectsConsumeEffect apply) {
				for (MobEffectInstance ins : apply.effects()) {
					if (seen.add(ins.getEffect())) {
						effects.add(new ApplyStatusEffectsConsumeEffect(ins, apply.probability()));
					}
				}
			} else if (!effects.contains(effect)) {
				effects.add(effect);
			}
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		List<ItemStack> skewers = getSkewers(stack).stack();
		if (!skewers.isEmpty()) applyContents(stack, skewers);
		return super.use(level, player, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		for (ItemStack e : getSkewers(stack).stack()) {
			if (e.getItem() instanceof BBQSkewerItem item) {
				item.applyEffects(e, entity);
			}
		}
		return super.finishUsingItem(stack, level, entity);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		for (ItemStack e : getSkewers(stack).stack()) {
			builder.accept(e.getHoverName());
		}
		super.appendHoverText(stack, context, display, builder, flag);
	}

}
