package com.is.mtc.card;

import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CardItem extends Item {//implements IItemColor {

	public static final int[] CARD_RARITY_ARRAY = new int[]{Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.ANCIENT, Rarity.LEGENDARY};
	private static final String PREFIX = "item_card_";
	private static final int MAX_DESC_LENGTH = 42;
	private int rarity;

	public CardItem(int r) {
		setTranslationKey(PREFIX + Rarity.toString(r).toLowerCase());
		setRegistryName(Reference.MODID, PREFIX + Rarity.toString(r).toLowerCase());
		setCreativeTab(MineTradingCards.MODTAB);

		rarity = r;
	}

	public static ItemStack applyCDWDtoStack(ItemStack stack, CardStructure cStruct, Random random) {
		NBTTagCompound nbtTag = stack.getTagCompound();
		nbtTag.setString("cdwd", cStruct.getCDWD());
		if (cStruct.getResourceLocations() != null && cStruct.getResourceLocations().size() > 1) {
			nbtTag.setInteger("assetnumber", Tools.randInt(0, cStruct.getResourceLocations().size(), random));
		}
		return stack;
	}

	public int getCardRarity() {
		return rarity;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String cdwd = Tools.hasCDWD(stack) ? stack.getTagCompound().getString("cdwd") : null;
		CardStructure cStruct = cdwd != null ? Databank.getCardByCDWD(cdwd) : null;

		if (cdwd != null) {
			if (cStruct == null) { // Card not registered ? Display cdwd
				return cdwd;
			} else {
				return cStruct.getName();
			}
		} else {
			return super.getItemStackDisplayName(stack);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			if (Tools.hasCDWD(stack)) {
				GuiHandler.hand = hand;
				player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_CARD, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}

		if (!Tools.hasCDWD(stack)) {
			CardStructure cStruct = Databank.generateACard(rarity, new Random()); // Using new Random() because world random can cause issues generating cards

			if (cStruct != null) {
				if (stack.getCount() != 1) { // Generate a single card from the stack and drop it into inventory
					ItemStack popoffStack = stack.copy();
					if (!popoffStack.hasTagCompound()) {
						popoffStack.setTagCompound(new NBTTagCompound());
					}
					popoffStack.setCount(1);
					popoffStack = applyCDWDtoStack(popoffStack, cStruct, world.rand);

					EntityItem dropped_card = player.entityDropItem(popoffStack, 1);
					dropped_card.setPickupDelay(0);

					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}
				} else { // Add data to the singleton "empty" card
					stack = applyCDWDtoStack(stack, cStruct, world.rand);
				}
			} else {
				Logs.errLog("Unable to generate a card of this rarity: " + Rarity.toString(rarity));
			}
		}

		NBTTagCompound nbtTag = stack.getTagCompound();
		if (!nbtTag.hasKey("assetnumber")) {
			CardStructure cStruct = Databank.getCardByCDWD(nbtTag.getString("cdwd"));
			if (cStruct != null) {
				if (cStruct.getResourceLocations() != null && cStruct.getResourceLocations().size() > 1) {
					nbtTag.setInteger("assetnumber", Tools.randInt(0, cStruct.getResourceLocations().size(), world.rand));
				}
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		CardStructure cStruct;
		NBTTagCompound nbt;

		if (!stack.hasTagCompound() || !Tools.hasCDWD(stack)) {
			return;
		}

		nbt = stack.getTagCompound();
		cStruct = Databank.getCardByCDWD(nbt.getString("cdwd"));

		if (cStruct == null) {
			infos.add(TextFormatting.RED + "/!\\ Missing client-side data");
			infos.add(TextFormatting.GRAY + nbt.getString("cdwd"));
			return;
		}

		infos.add("");
		infos.add("Edition: " + Rarity.toColor(rarity) + Databank.getEditionWithId(cStruct.getEdition()).getName());

		if (!cStruct.getCategory().isEmpty()) {
			infos.add("Category: " + TextFormatting.WHITE + cStruct.getCategory());
		}

		if (!cStruct.getDescription().isEmpty()) {
			String[] words = cStruct.getDescription().split(" ");
			String line = "";

			infos.add("");
			for (String word : words) {
				line = line + " " + word;
				line = Tools.clean(line);
				if (line.length() >= MAX_DESC_LENGTH) {
					infos.add(TextFormatting.ITALIC + line);
					line = "";
				}
			}
			if (!line.isEmpty()) {
				infos.add(line);
			}
		}

		infos.add("");
		infos.add(cStruct.numeral + "/" + Databank.getEditionWithId(cStruct.getEdition()).cCount);
	}


	// === ICON LAYERING AND COLORIZATION === //

	/**
	 * From https://github.com/matshou/Generic-Mod
	 */
	public static class ColorableIcon implements IItemColor {
		private int rarity;

		public ColorableIcon(int r) {
			rarity = r;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int colorMultiplier(ItemStack stack, int layer) {
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
