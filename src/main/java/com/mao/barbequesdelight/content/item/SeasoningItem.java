package com.mao.barbequesdelight.content.item;

import com.mao.barbequesdelight.init.food.BBQSeasoning;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class SeasoningItem extends Item {

	private final BBQSeasoning seasoning;

	public SeasoningItem(Properties prop, BBQSeasoning seasoning) {
		super(prop);
		this.seasoning = seasoning;
	}

	public BBQSeasoning getSeasoning() {
		return seasoning;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
		builder.accept(seasoning.getInfo().withStyle(ChatFormatting.YELLOW));
		super.appendHoverText(stack, context, display, builder, flag);
	}

	public void sprinkle(ItemStack self, Vec3 pos, ItemStack skewer, Player player, InteractionHand hand) {
		if (!(player.level() instanceof ServerLevel sl)) return;
		skewer.set(BBQDItems.SEASONING.get(), getSeasoning());
		player.playSound(SoundEvents.SAND_BREAK, 1.0f, 1.0f);
		Integer color = seasoning.color.getColor();
		int col = color == null ? 0 : color;
		sl.sendParticles(new DustParticleOptions(col, 1f),
				pos.x, pos.y, pos.z, 8, 0d, 0, 0d, 1d);
		self.hurtAndBreak(1, player, hand);
	}

	public boolean canSprinkle(ItemStack storedStack) {
		if (storedStack.isEmpty())
			return false;
		if (!(storedStack.getItem() instanceof BBQSkewerItem))
			return false;
		return storedStack.get(BBQDItems.SEASONING.get()) == null;
	}

}
