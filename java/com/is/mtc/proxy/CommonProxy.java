package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.init.MineTradingCardVillagers;
import com.is.mtc.root.Logs;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public static boolean IS_REMOTE = false;

	public void preInit(FMLPreInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(MineTradingCards.INSTANCE, new GuiHandler());
	}

	public void init(FMLInitializationEvent event) {
		MineTradingCardVillagers.registerCareers();
	}

	public void postInit(FMLPostInitializationEvent event) {
		Logs.devLog("Proxy: Is remote ?: " + IS_REMOTE);
	}
}
