package com.mao.barbequesdelight.compat.jade;

import com.mao.barbequesdelight.content.block.GrillBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(new GrillInfo(), GrillBlock.class);
	}

}
