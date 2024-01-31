package com.is.mtc.binder;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class BinderItem extends Item {
	public static final int MODE_STD = 0; // Standard mode
	public static final int MODE_FIL = 1; // Fill mode. Starts from current page toward binder's slots limit
	//public static final int MODE_PLA = 2; // Place mode. Get card number and try to place that card somewhere
	//# Buggy when client and server doesn't use the same mods

	public static final String[] MODE_STR = {"Standard", "Fill"/*, "Slot"*/};

	public BinderItem() {
		setUnlocalizedName("item_binder");
		setRegistryName(Reference.MODID, "item_binder");
		setCreativeTab(MineTradingCards.MODTAB);
	}

	public static void testNBT(ItemStack binderStack) {
		if (binderStack.getTagCompound() == null) {// Create nbt if not already existing
			NBTTagCompound nbtTag = new NBTTagCompound();
			nbtTag.setInteger("page", 0);
			nbtTag.setInteger("mode_mtc", MODE_STD);
			binderStack.setTagCompound(nbtTag);
		}

	}

	public static int changePageBy(ItemStack binderStack, int count) {
		return setCurrentPage(binderStack, getCurrentPage(binderStack) + count);
	}

	public static int setCurrentPage(ItemStack binderStack, int page) {
		testNBT(binderStack);
		NBTTagCompound nbtTag = binderStack.getTagCompound();
		nbtTag.setInteger("page", (int) Tools.clamp(0, page, BinderItemInventory.getTotalPages() - 1));
		//binderStack.setTagCompound(nbtTag);
		//binderStack.writeToNBT(nbtTag);

		return getCurrentPage(binderStack);
	}

	public static int getCurrentPage(ItemStack binderStack) {
		testNBT(binderStack);

		return binderStack.getTagCompound().getInteger("page");
	}

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		testNBT(player.getHeldItem(hand));
		GuiHandler.hand = hand;
		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_BINDER, world, (int) player.posX, (int) player.posY, (int) player.posZ);

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		testNBT(stack);

		infos.add("");
		infos.add("Page: " + (stack.getTagCompound().getInteger("page") + 1) + "/" + BinderItemInventory.getTotalPages());
	}

	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new BinderItemInventory();
	}
}
