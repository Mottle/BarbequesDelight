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

import java.util.Random;

public class StorageTileRenderer implements BlockEntityRenderer<StorageTileBlockEntity, StorageTileRenderer.StorageRenderState> {

	private static final int[] COUNT = {0, 1, 2, 4, 6, 8, 12, 16, 24, 32, 40, 56, 64};
	private static final int[] REV = new int[64];

	static {
		int k = 0;
		for (int i = 0; i < 64; i++) {
			if (i > COUNT[k]) k++;
			REV[i] = k;
		}
	}

	private final Random random = new Random(42);
	private final ItemModelResolver itemModelResolver;

	public StorageTileRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public StorageRenderState createRenderState() {
		return new StorageRenderState();
	}

	@Override
	public void extractRenderState(StorageTileBlockEntity entity, StorageRenderState state, float pTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(entity, state, pTick, cameraPosition, breakProgress);
		Level level = entity.getLevel();
		if (level == null) return;
		state.direction = entity.getBlockState().getValue(StorageBlock.FACING).getOpposite();
		state.lightCoords = LevelRenderer.getLightCoords(level, entity.getBlockPos().above());
		state.slotCount = entity.size();
		state.items = new ItemStackRenderState[state.slotCount];
		state.offsets = new float[state.slotCount];
		state.seeds = new int[state.slotCount];
		state.modelCounts = new int[state.slotCount];
		for (int i = 0; i < state.slotCount; ++i) {
			ItemStack stack = entity.getStack(i);
			state.seeds[i] = stack.isEmpty() ? 187 : stack.getItem().hashCode() + stack.getCount() + i * 64;
			state.modelCounts[i] = getModelCount(stack);
			if (stack.isEmpty()) continue;
			ItemStackRenderState item = new ItemStackRenderState();
			this.itemModelResolver.updateForTopItem(item, stack, ItemDisplayContext.FIXED, level, null, i);
			state.items[i] = item;
			state.offsets[i] = entity.getOffset(i);
		}
	}

	@Override
	public void submit(StorageRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		for (int i = 0; i < state.slotCount; ++i) {
			ItemStackRenderState item = state.items[i];
			if (item == null) continue;
			this.random.setSeed(state.seeds[i]);
			for (int j = 0; j < state.modelCounts[i]; ++j) {
				float r = (this.random.nextFloat() * 2.0F - 1.0F) * 0.03F;
				pose.pushPose();

				pose.translate(0.5, 0.2, 0.5);
				pose.mulPose(Axis.YP.rotationDegrees(-state.direction.toYRot()));
				pose.translate(state.offsets[i] + r, 0, 0.35 - j * 0.05);
				pose.mulPose(Axis.XP.rotationDegrees(20 + j * 4));
				pose.scale(0.375f, 0.375f, 0.375f);

				item.submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

				pose.popPose();
			}
		}
	}

	protected int getModelCount(ItemStack stack) {
		int count = stack.getCount();
		return count < REV.length ? REV[count] : 12;
	}

	public static class StorageRenderState extends BlockEntityRenderState {

		public Direction direction = Direction.NORTH;
		public int slotCount;
		public ItemStackRenderState[] items = new ItemStackRenderState[0];
		public float[] offsets = new float[0];
		public int[] seeds = new int[0];
		public int[] modelCounts = new int[0];

	}

}
