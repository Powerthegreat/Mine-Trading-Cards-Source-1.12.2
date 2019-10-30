package com.is.mtc.card;

import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CardItem extends Item {

	private static final String prefix = "item_card_";
	private static final int MAX_DESC_LENGTH = 42;

	private int rarity;

	public CardItem(int r) {
		setUnlocalizedName(prefix + Rarity.toString(r).toLowerCase());
		setRegistryName(prefix + Rarity.toString(r).toLowerCase());
		//setTextureName(MineTradingCards.MODID + ":" + prefix + Rarity.toString(r).toLowerCase());
		setCreativeTab(MineTradingCards.MODTAB);

		rarity = r;
	}

	public int getCardRarity() {
		return rarity;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String cdwd = Tools.hasCDWD(stack) ? stack.getTagCompound().getString("cdwd") : null;
		CardStructure cStruct = cdwd != null ? Databank.getCardByCDWD(cdwd) : null;

		if (cdwd != null) {
			if (cStruct == null) // Card not registered ? Display cdwd
				return cdwd;
			else
				return cStruct.getName();
		} else
			return super.getItemStackDisplayName(stack);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		//System.out.println(player.getHeldItem(hand).getTagCompound());
		//if (world.isRemote) {
		if (Tools.hasCDWD(player.getHeldItem(hand))) {
			//GuiHandler.currentMainItem = player.getHeldItem(hand);
			//System.out.println(player.getHeldItem(hand).getTagCompound());
			GuiHandler.hand = hand;
			player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_CARD, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		//return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		//}

		if (!player.getHeldItem(hand).hasTagCompound())
			player.getHeldItem(hand).setTagCompound(new NBTTagCompound());

		if (!Tools.hasCDWD(player.getHeldItem(hand))) {
			CardStructure cStruct = Databank.generateACard(rarity);

			if (cStruct != null) {
				NBTTagCompound nbtTag = player.getHeldItem(hand).getTagCompound();
				nbtTag.setString("cdwd", cStruct.getCDWD());
				nbtTag.setInteger("assetnumber", Tools.randInt(0, cStruct.getAssetPath().size()));
				//player.getHeldItem(hand).setTagCompound(nbtTag);
				//player.getHeldItem(hand).getTagCompound().setString("cdwd", cStruct.getCDWD());
			} else
				Logs.errLog("Unable to generate a card of this rarity: " + Rarity.toString(rarity));
		}

		NBTTagCompound nbtTag = player.getHeldItem(hand).getTagCompound();
		if (!nbtTag.hasKey("assetnumber")) {
			CardStructure cStruct = Databank.getCardByCDWD(nbtTag.getString("cdwd"));
			if (cStruct != null)
				nbtTag.setInteger("assetnumber", Tools.randInt(0, cStruct.getAssetPath().size()));
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		CardStructure cStruct;
		NBTTagCompound nbt;

		if (!stack.hasTagCompound() || !Tools.hasCDWD(stack))
			return;

		nbt = stack.getTagCompound();
		cStruct = Databank.getCardByCDWD(nbt.getString("cdwd"));

		if (cStruct == null) {
			infos.add(ChatFormatting.RED + "/!\\ Missing client-side data");
			infos.add(ChatFormatting.GRAY + nbt.getString("cdwd"));
			return;
		}

		infos.add("");
		infos.add("Edition: " + Rarity.toColor(rarity) + Databank.getEditionWithId(cStruct.getEdition()).getName());

		if (!cStruct.getCategory().isEmpty())
			infos.add("Category: " + ChatFormatting.WHITE + cStruct.getCategory());

		if (!cStruct.getDescription().isEmpty()) {
			String[] words = cStruct.getDescription().split(" ");
			String line = "";

			infos.add("");
			for (String word : words) {
				line = line + " " + word;
				line = Tools.clean(line);
				if (line.length() >= MAX_DESC_LENGTH) {
					infos.add(ChatFormatting.ITALIC + line);
					line = "";
				}
			}
			if (!line.isEmpty())
				infos.add(line);
		}

		infos.add("");
		infos.add(cStruct.numeral + "/" + Databank.getEditionWithId(cStruct.getEdition()).cCount);
	}
}
