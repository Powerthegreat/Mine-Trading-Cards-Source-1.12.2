package com.is.mtc.binder;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.MineTradingCards;
import com.is.mtc.root.Tools;

public class BinderItem extends Item {

	public static final int MODE_STD = 0; // Standard mode
	public static final int MODE_FIL = 1; // Fill mode. Starts from current page toward binder's slots limit
	//public static final int MODE_PLA = 2; // Place mode. Get card number and try to place that card somewhere
	//# Buggy when client and server doesn't use the same mods

	public static final String[] MODE_STR = {"Standard", "Fill", "Slot"};

	public BinderItem() {
		setUnlocalizedName("item_binder");
		setTextureName(MineTradingCards.MODID + ":item_binder");
		setCreativeTab(MineTradingCards.MODTAB);
	}

	/*-*/

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player) {

		if (w.isRemote)
			return stack;

		testNBT(stack);
		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_BINDER, w, (int)player.posX, (int)player.posY, (int)player.posZ);

		return stack;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List infos, boolean par_4) {
		testNBT(stack);

		infos.add("");
		infos.add("Page: " + (stack.stackTagCompound.getInteger("page") + 1) + "/" + BinderItemInventory.getTotalPages());
	}

	public static void testNBT(ItemStack binderStack) {
		if (binderStack.stackTagCompound == null) {// Create nbt if not already existing
			binderStack.stackTagCompound = new NBTTagCompound();
			binderStack.stackTagCompound.setInteger("page", 0);
			binderStack.stackTagCompound.setInteger("mode_mtc", MODE_STD);
		}

	}

	public static int changePageBy(ItemStack binderStack, int count) {
		return setCurrentPage(binderStack, getCurrentPage(binderStack) + count);
	}

	public static int setCurrentPage(ItemStack binderStack, int page) {
		testNBT(binderStack);
		binderStack.stackTagCompound.setInteger("page", (int)Tools.clamp(0, page, BinderItemInventory.getTotalPages() - 1));

		return getCurrentPage(binderStack);
	}

	public static int getCurrentPage(ItemStack binderStack) {
		testNBT(binderStack);

		return binderStack.stackTagCompound.getInteger("page");
	}
}
