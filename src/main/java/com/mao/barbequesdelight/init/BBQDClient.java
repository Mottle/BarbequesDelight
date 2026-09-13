package com.mao.barbequesdelight.init;

import com.mao.barbequesdelight.content.block.BBQOverlay;
import com.mao.barbequesdelight.content.block.GrillBlockEntityRenderer;
import com.mao.barbequesdelight.content.block.StorageTileRenderer;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = BarbequesDelight.MODID)
public class BBQDClient {

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BBQDBlocks.TE_GRILL.get(), GrillBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BBQDBlocks.TE_BASIN.get(), StorageTileRenderer::new);
		event.registerBlockEntityRenderer(BBQDBlocks.TE_TRAY.get(), StorageTileRenderer::new);
	}

	@SubscribeEvent
	public static void onLayer(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, BarbequesDelight.id("block_info"), new BBQOverlay());
	}

}
