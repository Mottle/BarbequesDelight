package com.mao.barbequesdelight.content.block;

import com.mao.barbequesdelight.init.data.BBQLangData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.gui.GuiLayer;

public class BBQOverlay implements GuiLayer {

	public void render(GuiGraphicsExtractor g, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) return;
		HitResult ray = mc.hitResult;
		if (ray instanceof BlockHitResult bray) {
			BlockPos pos = bray.getBlockPos();
			BlockEntity entity = player.level().getBlockEntity(pos);
			if (entity instanceof GrillBlockEntity grill) {
				int id = grill.getSlotForHitting(bray, player.level());
				if (grill.canFlip(id)) {
					Component text = BBQLangData.INFO_FLIP.get();
					g.centeredText(mc.font, text, g.guiWidth() / 2, g.guiHeight() / 2 + 16, -1);
				}
			}
		}
	}

}
