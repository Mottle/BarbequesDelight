package com.mao.barbequesdelight.init;

import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BarbequesDelight.MODID)
public final class BarbequesDelight {

    public static final String MODID = "barbequesdelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(BarbequesDelight.class);

    public BarbequesDelight(IEventBus modEventBus) {
        BBQDBlocks.BLOCKS.register(modEventBus);
        BBQDBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        BBQDItems.ITEMS.register(modEventBus);
        BBQDItems.DATA_COMPONENTS.register(modEventBus);
        BBQDItems.CREATIVE_TABS.register(modEventBus);
        BBQDRecipes.RECIPE_TYPES.register(modEventBus);
        BBQDRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        BBQDRecipes.RECIPE_DISPLAYS.register(modEventBus);
        modEventBus.addListener(BarbequesDelight::registerCapabilities);
        modEventBus.addListener(BarbequesDelight::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional().configurationToClient(
                ClientMarkerPayload.TYPE, ClientMarkerPayload.STREAM_CODEC, (payload, context) -> { });
    }

    /**
     * Zero-data marker that advertises the {@code barbequesdelight} namespace to
     * the Horizon server during configuration, satisfying the extension's
     * {@code requiredClient} check. Optional, so ordinary NeoForge servers are
     * unaffected.
     */
    private record ClientMarkerPayload() implements CustomPacketPayload {
        private static final ClientMarkerPayload INSTANCE = new ClientMarkerPayload();
        private static final Type<ClientMarkerPayload> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath(MODID, "client_marker"));
        private static final StreamCodec<FriendlyByteBuf, ClientMarkerPayload> STREAM_CODEC =
                StreamCodec.unit(INSTANCE);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, BBQDBlocks.TE_BASIN.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
        event.registerBlockEntity(Capabilities.Item.BLOCK, BBQDBlocks.TE_TRAY.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
