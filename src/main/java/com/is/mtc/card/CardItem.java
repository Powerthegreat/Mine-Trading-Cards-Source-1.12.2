package com.is.mtc.card;

import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CardItem extends Item {

	public static final int[] CARD_RARITY_ARRAY = new int[]{Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.ANCIENT, Rarity.LEGENDARY};
	private static final String PREFIX = "item_card_";
	private static final int MAX_DESC_LENGTH = 42;
	private final int rarity;

	public CardItem(Properties properties, int rarity) {
		super(properties);

		this.rarity = rarity;
	}

	public static String makeRegistryName(int rarity) {
		return PREFIX + Rarity.toString(rarity).toLowerCase();
	}

	public static ItemStack applyCDWDtoStack(ItemStack stack, CardStructure cStruct, Random random) {
		CompoundNBT nbtTag = stack.getOrCreateTag();
		nbtTag.putString("cdwd", cStruct.getCDWD());
		if (cStruct.getResourceLocations() != null && cStruct.getResourceLocations().size() > 1) {
			nbtTag.putInt("assetnumber", Tools.randInt(0, cStruct.getResourceLocations().size(), random));
		}
		return stack;
	}

	public int getCardRarity() {
		return rarity;
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		String cdwd = Tools.hasCDWD(stack) ? stack.getTag().getString("cdwd") : null;
		CardStructure cStruct = cdwd != null ? Databank.getCardByCDWD(cdwd) : null;

		if (cdwd != null) {
			if (cStruct == null) { // Card not registered ? Display cdwd
				return new StringTextComponent(cdwd);
			} else if (cStruct.getName() != null) {
				return new StringTextComponent(cStruct.getName());
			}
		}
		return super.getName(stack);
	}

	@Override
	public ActionResultType useOn(ItemUseContext p_195939_1_) {
		return super.useOn(p_195939_1_);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (world.isClientSide && Tools.hasCDWD(stack)) {
			CardStructure cStruct = Databank.getCardByCDWD(stack.getTag() != null ? stack.getTag().getString("cdwd") : null);
			if (CardStructure.isValidCStructAsset(cStruct, stack)) {
				Minecraft.getInstance().setScreen(new CardItemInterface(stack));
			} else {
				Logs.chatMessage(player, "Unable to open card illustration: Missing client side illustration: " + stack.getTag().getString("cdwd") + " asset number " + stack.getTag().getInt("assetnumber"));
				Logs.errLog("Unable to open card illustration: Missing client side illustration: " + stack.getTag().getString("cdwd") + " asset number " + stack.getTag().getInt("assetnumber"));
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		stack.getOrCreateTag();

		if (!Tools.hasCDWD(stack)) {
			CardStructure cStruct = Databank.generateACard(rarity, new Random()); // Using new Random() because world random can cause issues generating cards

			if (cStruct != null) {
				if (stack.getCount() != 1) { // Generate a single card from the stack and drop it into inventory
					ItemStack popoffStack = stack.copy();
					popoffStack.getOrCreateTag();
					popoffStack.setCount(1);
					popoffStack = applyCDWDtoStack(popoffStack, cStruct, world.getRandom());

					ItemEntity dropped_card = player.drop(popoffStack, false);
					dropped_card.setNoPickUpDelay();

					if (!player.isCreative()) {
						stack.shrink(1);
					}
				} else { // Add data to the singleton "empty" card
					stack = applyCDWDtoStack(stack, cStruct, world.getRandom());
				}
			} else {
				Logs.errLog("Unable to generate a card of this rarity: " + Rarity.toString(rarity));
			}
		}

		CompoundNBT nbtTag = stack.getTag();
		if (nbtTag.get("assetnumber") == null) {
			CardStructure cStruct = Databank.getCardByCDWD(nbtTag.getString("cdwd"));
			if (cStruct != null) {
				if (cStruct.getResourceLocations() != null && cStruct.getResourceLocations().size() > 1) {
					nbtTag.putInt("assetnumber", Tools.randInt(0, cStruct.getResourceLocations().size(), world.getRandom()));
				}
			}
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> infos, ITooltipFlag flag) {
		CardStructure cStruct;
		CompoundNBT nbt = stack.getTag();

		if (nbt == null || !Tools.hasCDWD(stack)) {
			return;
		}

		cStruct = Databank.getCardByCDWD(nbt.getString("cdwd"));

		if (cStruct == null) {
			infos.add(new StringTextComponent(TextFormatting.RED + "/!\\ Missing client-side data"));
			infos.add(new StringTextComponent(TextFormatting.GRAY + nbt.getString("cdwd")));
			return;
		}

		infos.add(new StringTextComponent(""));
		infos.add(new StringTextComponent("Edition: " + Rarity.toColor(rarity) + Databank.getEditionWithId(cStruct.getEdition()).getName()));

		if (!cStruct.getCategory().isEmpty()) {
			infos.add(new StringTextComponent("Category: " + TextFormatting.WHITE + cStruct.getCategory()));
		}

		if (!cStruct.getDescription().isEmpty()) {
			String[] lines = cStruct.getDescription().split("\\\\n");

			for (String currentLine : lines) {
				while (currentLine.length() >= MAX_DESC_LENGTH) {
					infos.add(new StringTextComponent(TextFormatting.ITALIC + currentLine.substring(0, MAX_DESC_LENGTH)));
					currentLine = currentLine.substring(MAX_DESC_LENGTH);
				}
				infos.add(new StringTextComponent(TextFormatting.ITALIC + currentLine));
			}
		}

		infos.add(new StringTextComponent(""));
		infos.add(new StringTextComponent(cStruct.numeral + "/" + Databank.getEditionWithId(cStruct.getEdition()).cCount));
	}


	// === ICON LAYERING AND COLORIZATION === //

	/**
	 * From https://github.com/matshou/Generic-Mod
	 */
	@OnlyIn(Dist.CLIENT)
	public static class ColorableIcon implements IItemColor {
		private int rarity;

		public ColorableIcon(int r) {
			rarity = r;
		}

		public int getColor(ItemStack stack, int layer) {
			if (layer == 0) {
				switch (rarity) {
					case Rarity.COMMON:
						return MineTradingCards.CARD_COLOR_COMMON;
					case Rarity.UNCOMMON:
						return MineTradingCards.CARD_COLOR_UNCOMMON;
					case Rarity.RARE:
						return MineTradingCards.CARD_COLOR_RARE;
					case Rarity.ANCIENT:
						return MineTradingCards.CARD_COLOR_ANCIENT;
					case Rarity.LEGENDARY:
						return MineTradingCards.CARD_COLOR_LEGENDARY;
				}
				return Reference.COLOR_GRAY;
			}

			return -1;
		}
	}
}
