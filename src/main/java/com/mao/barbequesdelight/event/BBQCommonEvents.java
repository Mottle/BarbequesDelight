package com.mao.barbequesdelight.event;

import com.mao.barbequesdelight.init.BarbequesDelight;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = BarbequesDelight.MODID)
public final class BBQCommonEvents {

    private BBQCommonEvents() {
    }

    @SubscribeEvent
    public static void sendSyncedRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(BBQDRecipes.RT_BBQ.get(), BBQDRecipes.RT_SKR.get());
    }
}
