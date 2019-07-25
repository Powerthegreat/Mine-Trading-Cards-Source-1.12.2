package com.is.mtc.init;

import com.is.mtc.village.ProfessionCardMaster;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod.EventBusSubscriber
public class MineTradingCardVillagers {
	public static VillagerRegistry.VillagerProfession professionCardMaster = new ProfessionCardMaster(
			"is_mtc:card_master",
			"is_mtc:textures/skins/card_master",
			"minecraft:textures/entity/zombie_villager/zombie_villager.png");

	@SubscribeEvent
	public void registerVillagers(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
		event.getRegistry().registerAll();
	}
}
