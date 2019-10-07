package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.displayer.DisplayerBlockRenderer;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockRenderer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.root.Logs;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		MineTradingCards.PROXY_IS_REMOTE = true;
		Logs.devLog("Dectected proxy: Client");

		super.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

		ClientRegistry.bindTileEntitySpecialRenderer(DisplayerBlockTileEntity.class, new DisplayerBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MonoDisplayerBlockTileEntity.class, new MonoDisplayerBlockRenderer());
		//VillagerRegistry.instance().registerVillagerSkin(VillageHandler.TRADER_ID, new ResourceLocation("is_mtc", "textures/skins/card_master.png"));
	}
}
