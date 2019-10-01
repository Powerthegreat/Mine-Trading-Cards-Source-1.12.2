package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		MineTradingCards.PROXY_IS_REMOTE = false;
		Logs.devLog("Detected proxy: Server");

		super.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
