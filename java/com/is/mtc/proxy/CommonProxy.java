package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.Logs;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public static boolean IS_REMOTE = false;

	public void preInit(FMLPreInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(MineTradingCards.INSTANCE, new GuiHandler());
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
		Logs.devLog("Proxy: Is remote ?: " + IS_REMOTE);
	}
}
