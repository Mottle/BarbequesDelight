package com.mao.barbequesdelight.content.item;

import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.util.FDConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FoodItem extends Item {

	private static Component getTooltip(MobEffectInstance eff) {
		MutableComponent ans = Component.translatable(eff.getDescriptionId());
		MobEffect mobeffect = eff.getEffect().value();
		if (eff.getAmplifier() > 0) {
			ans = Component.translatable("potion.withAmplifier", ans,
					Component.translatable("potion.potency." + eff.getAmplifier()));
		}

		if (eff.getDuration() > 20) {
			ans = Component.translatable("potion.withDuration", ans,
					MobEffectUtil.formatDuration(eff, 1, 20));
		}

		return ans.withStyle(mobeffect.getCategory().getTooltipFormatting());
	}

	public static void getFoodEffects(ItemStack stack, Consumer<Component> list) {
		getFoodEffects(stack.get(DataComponents.CONSUMABLE), list);
	}

	public static void getFoodEffects(@Nullable Consumable consumable, Consumer<Component> list) {
		if (consumable == null) return;
		for (ConsumeEffect effect : consumable.onConsumeEffects()) {
			if (!(effect instanceof ApplyStatusEffectsConsumeEffect apply)) continue;
			int chance = Math.round(apply.probability() * 100);
			for (MobEffectInstance ins : apply.effects()) {
				Component ans = getTooltip(ins);
				list.accept(chance == 100 ? ans : BBQLangData.CHANCE_EFFECT.get(ans, chance));
			}
		}
	}

	public FoodItem(Properties properties) {
		super(properties);
	}

	/**
	 * The consume effects to show in the tooltip. Overridden by items whose effects depend
	 * on the components of the stack rather than on the item defaults.
	 */
	protected @Nullable Consumable getTooltipConsumable(ItemStack stack) {
		return stack.get(DataComponents.CONSUMABLE);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		if (FDConfig.addTooltip())
			getFoodEffects(getTooltipConsumable(stack), builder);
	}

}
