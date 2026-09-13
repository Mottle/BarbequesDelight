package com.mao.barbequesdelight.content.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class GrillBlockEntityRenderer implements BlockEntityRenderer<GrillBlockEntity, GrillBlockEntityRenderer.GrillRenderState> {

	private final ItemModelResolver itemModelResolver;

	public GrillBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public GrillRenderState createRenderState() {
		return new GrillRenderState();
	}

	@Override
	public void extractRenderState(GrillBlockEntity entity, GrillRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);
		Level level = entity.getLevel();
		if (level == null) return;
		state.direction = entity.getBlockState().getValue(GrillBlock.FACING).getOpposite();
		state.lightCoords = LevelRenderer.getLightCoords(level, entity.getBlockPos().above());
		state.slotCount = entity.size();
		state.items = new ItemStackRenderState[state.slotCount];
		state.offsets = new float[state.slotCount];
		state.flipped = new boolean[state.slotCount];
		for (int i = 0; i < state.slotCount; ++i) {
			ItemStack stack = entity.getStack(i);
			if (stack.isEmpty()) continue;
			ItemStackRenderState item = new ItemStackRenderState();
			this.itemModelResolver.updateForTopItem(item, stack, ItemDisplayContext.FIXED, level, null, i);
			state.items[i] = item;
			state.offsets[i] = entity.getOffset(i);
			state.flipped[i] = entity.isFlipped(i);
		}
	}

	@Override
	public void submit(GrillRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		for (int i = 0; i < state.slotCount; ++i) {
			ItemStackRenderState item = state.items[i];
			if (item == null) continue;
			poseStack.pushPose();
			poseStack.translate(0.5, 0.96, 0.5);
			poseStack.mulPose(Axis.YP.rotationDegrees(-state.direction.toYRot()));
			poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			poseStack.translate(state.offsets[i], 0, 0.0);
			poseStack.scale(0.4f, 0.4f, 0.4f);
			poseStack.mulPose(Axis.YP.rotationDegrees(state.flipped[i] ? 180 : 0));
			item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
			poseStack.popPose();
		}
	}

	public static class GrillRenderState extends BlockEntityRenderState {

		public Direction direction = Direction.NORTH;
		public int slotCount;
		public ItemStackRenderState[] items = new ItemStackRenderState[0];
		public float[] offsets = new float[0];
		public boolean[] flipped = new boolean[0];

	}

}
