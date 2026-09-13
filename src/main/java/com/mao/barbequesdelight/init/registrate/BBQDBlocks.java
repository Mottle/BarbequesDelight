package com.mao.barbequesdelight.init.registrate;

import com.mao.barbequesdelight.content.block.BasinBlock;
import com.mao.barbequesdelight.content.block.BasinBlockEntity;
import com.mao.barbequesdelight.content.block.GrillBlock;
import com.mao.barbequesdelight.content.block.GrillBlockEntity;
import com.mao.barbequesdelight.content.block.TrayBlock;
import com.mao.barbequesdelight.content.block.TrayBlockEntity;
import com.mao.barbequesdelight.init.BarbequesDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BBQDBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BarbequesDelight.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BarbequesDelight.MODID);

	public static final DeferredBlock<GrillBlock> GRILL = BLOCKS.registerBlock("grill",
			GrillBlock::new,
			() -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
					.strength(0.5F, 6.0F).sound(SoundType.LANTERN).noOcclusion()
					.lightLevel(state -> state.getValue(GrillBlock.CAMPFIRE) ? 15 : 0));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrillBlockEntity>> TE_GRILL =
			BLOCK_ENTITY_TYPES.register("grill",
					() -> new BlockEntityType<>(GrillBlockEntity::new, GRILL.get()));

	public static final DeferredBlock<BasinBlock> BASIN = BLOCKS.registerBlock("basin",
			BasinBlock::new,
			() -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasinBlockEntity>> TE_BASIN =
			BLOCK_ENTITY_TYPES.register("basin",
					() -> new BlockEntityType<>(BasinBlockEntity::new, BASIN.get()));

	public static final DeferredBlock<TrayBlock> TRAY = BLOCKS.registerBlock("tray",
			TrayBlock::new,
			() -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrayBlockEntity>> TE_TRAY =
			BLOCK_ENTITY_TYPES.register("tray",
					() -> new BlockEntityType<>(TrayBlockEntity::new, TRAY.get()));

}
