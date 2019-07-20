package com.is.mtc.proxy;

import com.is.mtc.root.Logs;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
