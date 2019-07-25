package com.is.mtc.binder;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import com.is.mtc.handler.GuiHandler;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Tools;

import javax.annotation.Nullable;

public class BinderItem extends Item {

	public static final int MODE_STD = 0; // Standard mode
	public static final int MODE_FIL = 1; // Fill mode. Starts from current page toward binder's slots limit
	//public static final int MODE_PLA = 2; // Place mode. Get card number and try to place that card somewhere
	//# Buggy when client and server doesn't use the same mods

	public static final String[] MODE_STR = {"Standard", "Fill", "Slot"};

	public BinderItem() {
		setUnlocalizedName("item_binder");
		setRegistryName("item_binder");
		//setTextureName(MineTradingCards.MODID + ":item_binder");
		setCreativeTab(MineTradingCards.MODTAB);
	}

	/*-*/

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		testNBT(player.getHeldItem(hand));
		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_BINDER, world, (int)player.posX, (int)player.posY, (int)player.posZ);

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		testNBT(stack);

		infos.add("");
		infos.add("Page: " + (stack.getTagCompound().getInteger("page") + 1) + "/" + BinderItemInventory.getTotalPages());
	}

	public static void testNBT(ItemStack binderStack) {
		if (binderStack.getTagCompound() == null) {// Create nbt if not already existing
			binderStack.setTagCompound(new NBTTagCompound());
			binderStack.getTagCompound().setInteger("page", 0);
			binderStack.getTagCompound().setInteger("mode_mtc", MODE_STD);
		}

	}

	public static int changePageBy(ItemStack binderStack, int count) {
		return setCurrentPage(binderStack, getCurrentPage(binderStack) + count);
	}

	public static int setCurrentPage(ItemStack binderStack, int page) {
		testNBT(binderStack);
		binderStack.getTagCompound().setInteger("page", (int)Tools.clamp(0, page, BinderItemInventory.getTotalPages() - 1));

		return getCurrentPage(binderStack);
	}

	public static int getCurrentPage(ItemStack binderStack) {
		testNBT(binderStack);

		return binderStack.getTagCompound().getInteger("page");
	}
}
