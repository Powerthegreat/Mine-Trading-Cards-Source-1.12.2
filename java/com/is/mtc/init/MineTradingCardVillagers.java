package com.is.mtc.init;

import com.is.mtc.Reference;
import com.is.mtc.root.Logs;
import com.is.mtc.village.CardTrade;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod.EventBusSubscriber
public class MineTradingCardVillagers {
	public static VillagerProfession professionCardMaster = new VillagerProfession(
			Reference.MODID+":card_master",
			Reference.MODID+":textures/entities/card_master.png",
			"minecraft:textures/entity/zombie_villager/zombie_villager.png");
	public static VillagerCareer careerCardMaster;

	@SubscribeEvent
	public static void registerVillagers(RegistryEvent.Register<VillagerProfession> event) {
		event.getRegistry().registerAll(professionCardMaster);
	}

	public static void registerCareers() {
		Logs.stdLog("Registering Careers");
		careerCardMaster = (new VillagerCareer(professionCardMaster, "card_master"))
				// 1 emerald for 1-3 common packs
				.addTrade(1, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(1, 1),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packCommon, new EntityVillager.PriceInfo(1, 3)))
				// 1 emerald for 1-2 standard packs
				.addTrade(1, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(1, 1),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packStandard, new EntityVillager.PriceInfo(1, 2)))
				// 1 emerald for 1-2 edition packs
				.addTrade(1, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(1, 1),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packEdition, new EntityVillager.PriceInfo(1, 2)))
				// 1-2 emeralds for 1 uncommon pack
				.addTrade(2, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(1, 2),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packUncommon, new EntityVillager.PriceInfo(1, 1)))
				// 3-6 emeralds for 1 rare pack
				.addTrade(3, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(3, 6),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packRare, new EntityVillager.PriceInfo(1, 1)))
				// 5-10 emeralds for 1 ancient pack
				.addTrade(4, new CardTrade(
						Items.EMERALD, new EntityVillager.PriceInfo(5, 10),
						null, new EntityVillager.PriceInfo(0, 0),
						MTCItems.packAncient, new EntityVillager.PriceInfo(1, 1)));
	}
}
