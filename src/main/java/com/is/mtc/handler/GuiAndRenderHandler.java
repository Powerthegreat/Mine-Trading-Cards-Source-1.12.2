package com.is.mtc.handler;

import com.is.mtc.binder.BinderItemGuiContainer;
import com.is.mtc.displayer.DisplayerBlockGuiContainer;
import com.is.mtc.displayer.DisplayerBlockRenderer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockGuiContainer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockRenderer;
import com.is.mtc.init.MTCContainers;
import com.is.mtc.init.MTCTileEntities;
import com.is.mtc.util.Reference;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GuiAndRenderHandler {
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.register(MTCContainers.displayerBlockContainer.get(), DisplayerBlockGuiContainer::new);
		ScreenManager.register(MTCContainers.monoDisplayerBlockContainer.get(), MonoDisplayerBlockGuiContainer::new);
		ScreenManager.register(MTCContainers.binderContainer.get(), BinderItemGuiContainer::new);

		ClientRegistry.bindTileEntityRenderer(MTCTileEntities.displayerBlockTileEntity.get(), DisplayerBlockRenderer::new);
		ClientRegistry.bindTileEntityRenderer(MTCTileEntities.monoDisplayerBlockTileEntity.get(), MonoDisplayerBlockRenderer::new);
	}
}

