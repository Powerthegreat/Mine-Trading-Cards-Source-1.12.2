package com.is.mtc.handler;

import com.is.mtc.root.CC_CreateCard;
import com.is.mtc.root.CC_ForceCreateCard;
import com.is.mtc.util.Reference;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandHandler {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CC_CreateCard.register(event.getDispatcher());
		CC_ForceCreateCard.register(event.getDispatcher());
	}
}
