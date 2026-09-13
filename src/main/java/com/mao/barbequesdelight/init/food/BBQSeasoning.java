package com.mao.barbequesdelight.init.food;

import com.mao.barbequesdelight.content.item.SeasoningItem;
import com.mao.barbequesdelight.init.data.BBQLangData;
import com.mao.barbequesdelight.init.registrate.BBQDItems;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Locale;
import java.util.function.IntFunction;

public enum BBQSeasoning implements StringRepresentable {
	CUMIN(BBQLangData.CUMIN, BBQLangData.CUMIN_INFO, ChatFormatting.YELLOW, true),
	PEPPER(BBQLangData.PEPPER, BBQLangData.PEPPER_INFO, ChatFormatting.GRAY, true),
	CHILI(BBQLangData.CHILI, BBQLangData.CHILI_INFO, ChatFormatting.RED, true),
	HONEY_MUSTARD(BBQLangData.HONEY, BBQLangData.HONEY_INFO, ChatFormatting.YELLOW, false),
	BUFFALO(BBQLangData.BUFFALO, BBQLangData.BUFFALO_INFO, ChatFormatting.GOLD, false),
	BARBEQUE(BBQLangData.BBQ, BBQLangData.BBQ_INFO, ChatFormatting.GOLD, false),
	;

	public static final Codec<BBQSeasoning> CODEC = Codec.STRING.comapFlatMap(
			BBQSeasoning::byName, BBQSeasoning::getSerializedName);
	public static final IntFunction<BBQSeasoning> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, BBQSeasoning> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

	private final BBQLangData lang, info;
	public final ChatFormatting color;
	private final String name;
	public final DeferredItem<SeasoningItem> item;

	BBQSeasoning(BBQLangData lang, BBQLangData info, ChatFormatting color, boolean powder) {
		this.lang = lang;
		this.info = info;
		this.color = color;
		name = name().toLowerCase(Locale.ROOT);
		item = BBQDItems.ITEMS.registerItem(name + (powder ? "_powder" : "_sauce"),
				p -> new SeasoningItem(p.durability(powder ? 64 : 16), this));
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Accepts both the current lowercase serialized name and the legacy 1.21
	 * {@code Enum::name} form (e.g. {@code HONEY_MUSTARD}) so stacks saved by the
	 * old L2 serialization still decode.
	 */
	private static com.mojang.serialization.DataResult<BBQSeasoning> byName(String name) {
		for (BBQSeasoning s : values()) {
			if (s.name.equals(name) || s.name().equals(name)) {
				return com.mojang.serialization.DataResult.success(s);
			}
		}
		return com.mojang.serialization.DataResult.error(() -> "Unknown seasoning: " + name);
	}

	public MutableComponent getTitle() {
		return lang.get().withStyle(color);
	}

	public MutableComponent getInfo() {
		return info.get().withStyle(color);
	}

	public void onFinish(Player player) {
		if (!(player.level() instanceof ServerLevel level)) return;
		if (this == CHILI || this == BUFFALO)
			player.hurtServer(level, player.damageSources().inFire(), 2);
		if (this == CUMIN)
			player.heal(2);
	}

	/**
	 * The nutrition of a seasoned stack is the nutrition of the unseasoned one; only the
	 * always-edible flag can be added.
	 */
	public FoodProperties deriveFood(FoodProperties base) {
		FoodProperties.Builder builder = new FoodProperties.Builder()
				.nutrition(base.nutrition())
				.saturationModifier(base.nutrition() == 0 ? 0 : base.saturation() / base.nutrition() / 2);
		if (base.canAlwaysEat() || this == CHILI) builder.alwaysEdible();
		return builder.build();
	}

	/**
	 * Seasoning rewrites the base effects (some shorten or lengthen them, some strengthen
	 * them, honey mustard doubles the chance) and can make the stack eat faster.
	 */
	public Consumable deriveConsumable(Consumable base) {
		Consumable.Builder builder = Consumable.builder()
				.consumeSeconds(isFast(base) ? 0.8F : base.consumeSeconds())
				.animation(base.animation())
				.sound(base.sound())
				.hasConsumeParticles(base.hasConsumeParticles());
		for (ConsumeEffect effect : base.onConsumeEffects()) {
			if (effect instanceof ApplyStatusEffectsConsumeEffect apply) {
				float chance = modify(apply.probability());
				for (MobEffectInstance ins : apply.effects()) {
					builder.onConsume(new ApplyStatusEffectsConsumeEffect(modify(ins), chance));
				}
			} else {
				builder.onConsume(effect);
			}
		}
		return builder.build();
	}

	private boolean isFast(Consumable base) {
		return this == PEPPER || base.consumeSeconds() < 1.0F;
	}

	private MobEffectInstance modify(MobEffectInstance ins) {
		int duration = ins.getDuration();
		int amplifier = ins.getAmplifier();
		if (this == HONEY_MUSTARD) {
			duration /= 2;
		} else if (this == BUFFALO) {
			duration /= 2;
			amplifier++;
		} else if (this == BARBEQUE) {
			duration *= 2;
		}
		return new MobEffectInstance(ins.getEffect(), duration, amplifier,
				ins.isAmbient(), ins.isVisible(), ins.showIcon());
	}

	private float modify(float chance) {
		return this == HONEY_MUSTARD ? Math.min(1.0F, chance * 2.0F) : chance;
	}

	public static void register() {
	}

}
