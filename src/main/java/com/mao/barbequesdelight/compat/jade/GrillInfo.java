package com.mao.barbequesdelight.compat.jade;

import com.mao.barbequesdelight.content.block.GrillBlockEntity;
import com.mao.barbequesdelight.init.BarbequesDelight;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

public class GrillInfo implements IBlockComponentProvider {

	public static final Identifier ID = BarbequesDelight.id("grill");

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor access, IPluginConfig config) {
		if (access.getBlockEntity() instanceof GrillBlockEntity be) {
			for (int i = 0; i < be.size(); i++) {
				var s = be.entries[i];
				if (s.stack.isEmpty()) continue;
				tooltip.add(JadeUI.smallItem(s.stack));
				tooltip.append(s.getTooltip());
			}
		}
	}

	@Override
	public Identifier getUid() {
		return ID;
	}

}
