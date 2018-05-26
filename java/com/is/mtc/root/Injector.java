package com.is.mtc.root;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class Injector {

	// - Informations
	public static final String ITEM_HEAD = "item.";
	public static final String BLOCK_HEAD = "tile.";

	// - Functions
	private static String getItemNormalName(Item i) {
		return i.getUnlocalizedName().substring(Injector.ITEM_HEAD.length());
	}

	private static String getBlockNormalName(Block b) {
		return b.getUnlocalizedName().substring(Injector.BLOCK_HEAD.length());
	}

	public static void registerItem(Item i) {
		GameRegistry.registerItem(i, getItemNormalName(i));

		Logs.devLog("Injector: Registered item: " + i.getUnlocalizedName());
	}

	public static void registerBlock(Block b) {
		GameRegistry.registerBlock(b, getBlockNormalName(b));

		Logs.devLog("Injector: Registered block: " + b.getUnlocalizedName());
	}
}