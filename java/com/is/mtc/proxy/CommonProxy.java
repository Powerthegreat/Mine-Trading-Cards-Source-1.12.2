package com.is.mtc.proxy;

import com.is.mtc.root.Logs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public static boolean IS_REMOTE = false;

	public void preInit(FMLPreInitializationEvent e) {
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
		Logs.devLog("Proxy: Is remote ?: " + IS_REMOTE);
	}
}
