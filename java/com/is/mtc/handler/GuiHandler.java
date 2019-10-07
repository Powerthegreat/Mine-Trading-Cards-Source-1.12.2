package com.is.mtc.handler;

import com.is.mtc.binder.BinderItemContainer;
import com.is.mtc.binder.BinderItemGuiContainer;
import com.is.mtc.binder.BinderItemInventory;
import com.is.mtc.card.CardItemInterface;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlockContainer;
import com.is.mtc.displayer.DisplayerBlockGuiContainer;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockContainer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockGuiContainer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final int GUI_CARD = 0;
	public static final int GUI_DISPLAYER = 1;
	public static final int GUI_BINDER = 2;
	public static final int GUI_MONODISPLAYER = 3;
	//public static ItemStack currentMainItem;
	public static EnumHand hand = EnumHand.MAIN_HAND;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID) {
			case GUI_DISPLAYER:
				if (tileEntity != null) {
					return new DisplayerBlockContainer(player.inventory, (DisplayerBlockTileEntity) tileEntity);
				}
				break;

			case GUI_BINDER:
				return new BinderItemContainer(player.inventory, new BinderItemInventory(player.getHeldItem(hand)));

			case GUI_MONODISPLAYER:
				if (tileEntity != null) {
					return new MonoDisplayerBlockContainer(player.inventory, (MonoDisplayerBlockTileEntity) tileEntity);
				}
				break;
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

		switch (ID) {
			case GUI_CARD:
				//ItemStack stack = player.getActiveItemStack();
				ItemStack stack = player.getHeldItem(hand);
				/*if(currentMainItem != null) {
					stack = currentMainItem;
				}*/
				//ItemStack stack = player.getActiveItemStack();
				//System.out.println(stack.getTagCompound());
				if (Tools.hasCDWD(stack)) {
					CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

					if (cStruct != null && cStruct.getDynamicTexture() != null) { // Card registered and dynamic texture (illustration) exists
						return new CardItemInterface(stack);
					} else {
						Logs.chatMessage(player, "Unable to open card illustration: Missing client side illustration: " + stack.getTagCompound().getString("cdwd"));
						Logs.errLog("Unable to open card illustration: Missing client side illustration: " + stack.getTagCompound().getString("cdwd"));
					}
				}
				break;

			case GUI_DISPLAYER:
				if (tileEntity != null)
					return new DisplayerBlockGuiContainer(player.inventory, (DisplayerBlockContainer) getServerGuiElement(ID, player, world, x, y, z));
				break;

			case GUI_BINDER:
				return new BinderItemGuiContainer((BinderItemContainer) getServerGuiElement(ID, player, world, x, y, z));

			case GUI_MONODISPLAYER:
				if (tileEntity != null)
					return new MonoDisplayerBlockGuiContainer(player.inventory, (MonoDisplayerBlockContainer) getServerGuiElement(ID, player, world, x, y, z));
				break;
		}

		return null;
	}
}

