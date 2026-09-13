package com.mao.barbequesdelight.content.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class DCListStack {

	public static final DCListStack EMPTY = new DCListStack(List.of());

	public static final Codec<DCListStack> CODEC = ItemStack.OPTIONAL_CODEC.listOf()
			.xmap(DCListStack::new, DCListStack::stack);

	public static final StreamCodec<RegistryFriendlyByteBuf, DCListStack> STREAM_CODEC =
			ItemStack.OPTIONAL_STREAM_CODEC.<List<ItemStack>>apply(ByteBufCodecs.list())
					.map(DCListStack::new, DCListStack::stack);

	private final List<ItemStack> stack;
	private final int hashCode;

	public DCListStack(List<ItemStack> stack) {
		this.stack = List.copyOf(stack);
		hashCode = hash(stack);
	}

	private static int hash(List<ItemStack> stack) {
		int result = 0;
		for (ItemStack s : stack) {
			result = result * 31 + ItemStack.hashItemAndComponents(s);
		}
		return result;
	}

	public List<ItemStack> stack() {
		return stack;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DCListStack s) || hashCode != s.hashCode || stack.size() != s.stack.size()) return false;
		for (int i = 0; i < stack.size(); i++) {
			if (!ItemStack.matches(stack.get(i), s.stack.get(i))) return false;
		}
		return true;
	}

}
