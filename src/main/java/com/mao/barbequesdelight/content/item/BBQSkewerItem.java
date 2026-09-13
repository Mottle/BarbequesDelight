package com.mao.barbequesdelight.content.item;

import com.mao.barbequesdelight.init.food.BBQSeasoning;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BBQSkewerItem extends FoodItem {

	private final @Nullable FoodProperties baseFood;
	private final @Nullable Consumable baseConsumable;

	@Nullable
	public static BBQSeasoning getSeasoning(ItemStack stack) {
		return stack.get(BBQDItems.SEASONING.get());
	}

	/**
	 * The food of this stack, with the seasoning applied. Raw skewers have none.
	 */
	@Nullable
	public static FoodProperties effectiveFood(ItemStack stack) {
		if (!(stack.getItem() instanceof BBQSkewerItem item) || item.baseFood == null) return null;
		BBQSeasoning seasoning = getSeasoning(stack);
		return seasoning == null ? item.baseFood : seasoning.deriveFood(item.baseFood);
	}

	/**
	 * The consume effects of this stack, with the seasoning applied. Raw skewers have none.
	 */
	@Nullable
	public static Consumable effectiveConsumable(ItemStack stack) {
		if (!(stack.getItem() instanceof BBQSkewerItem item) || item.baseConsumable == null) return null;
		BBQSeasoning seasoning = getSeasoning(stack);
		return seasoning == null ? item.baseConsumable : seasoning.deriveConsumable(item.baseConsumable);
	}

	public BBQSkewerItem(Properties prop) {
		this(prop, null, null);
	}

	public BBQSkewerItem(Properties prop, @Nullable FoodProperties baseFood, @Nullable Consumable baseConsumable) {
		super(prop);
		this.baseFood = baseFood;
		this.baseConsumable = baseConsumable;
	}

	/**
	 * Food is stored as components, so a seasoned skewer has to be rewritten before it is
	 * eaten for the seasoning to shorten the eating time and to allow eating when full.
	 */
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getSeasoning(stack) != null) {
			FoodProperties food = effectiveFood(stack);
			Consumable consumable = effectiveConsumable(stack);
			if (food != null) stack.set(DataComponents.FOOD, food);
			if (consumable != null) stack.set(DataComponents.CONSUMABLE, consumable);
		}
		return super.use(level, player, hand);
	}

	@Override
	public Component getName(ItemStack stack) {
		BBQSeasoning seasoning = getSeasoning(stack);
		if (seasoning != null)
			return seasoning.getTitle().append(super.getName(stack).copy().withStyle(seasoning.color));
		return super.getName(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
		applyEffects(stack, consumer);
		ItemStackTemplate remain = stack.getCraftingRemainder();
		ItemStack ans = super.finishUsingItem(stack, level, consumer);
		if (remain == null) {
			return ans;
		}
		if (ans.isEmpty()) {
			return remain.create();
		}
		if (consumer instanceof Player player && !player.isCreative()) {
			player.getInventory().placeItemBackInInventory(remain.create());
		}
		return ans;
	}

	protected void applyEffects(ItemStack stack, LivingEntity consumer) {
		BBQSeasoning seasoning = getSeasoning(stack);
		if (seasoning != null && consumer instanceof Player player) {
			seasoning.onFinish(player);
		}
	}

	@Override
	protected @Nullable Consumable getTooltipConsumable(ItemStack stack) {
		Consumable consumable = effectiveConsumable(stack);
		return consumable != null ? consumable : super.getTooltipConsumable(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		BBQSeasoning seasoning = getSeasoning(stack);
		if (seasoning != null)
			builder.accept(seasoning.getTitle());
		super.appendHoverText(stack, context, display, builder, flag);
	}

}
