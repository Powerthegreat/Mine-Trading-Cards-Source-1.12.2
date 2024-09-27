package com.is.mtc.handler;

import com.is.mtc.card.CardItem;
import com.is.mtc.init.MTCItems;
import com.is.mtc.pack.PackItemCustom;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.root.Rarity;
import com.is.mtc.util.Reference;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColourHandler {
	@SubscribeEvent
	public static void onHandleColors(ColorHandlerEvent.Item event) {
		// Card colors
		event.getItemColors().register(new CardItem.ColorableIcon(Rarity.COMMON), MTCItems.cardCommon.get());
		event.getItemColors().register(new CardItem.ColorableIcon(Rarity.UNCOMMON), MTCItems.cardUncommon.get());
		event.getItemColors().register(new CardItem.ColorableIcon(Rarity.RARE), MTCItems.cardRare.get());
		event.getItemColors().register(new CardItem.ColorableIcon(Rarity.ANCIENT), MTCItems.cardAncient.get());
		event.getItemColors().register(new CardItem.ColorableIcon(Rarity.LEGENDARY), MTCItems.cardLegendary.get());

		// Pack colors
		event.getItemColors().register(new PackItemRarity.ColorableIcon(Rarity.COMMON), MTCItems.packCommon.get());
		event.getItemColors().register(new PackItemRarity.ColorableIcon(Rarity.UNCOMMON), MTCItems.packUncommon.get());
		event.getItemColors().register(new PackItemRarity.ColorableIcon(Rarity.RARE), MTCItems.packRare.get());
		event.getItemColors().register(new PackItemRarity.ColorableIcon(Rarity.ANCIENT), MTCItems.packAncient.get());
		event.getItemColors().register(new PackItemRarity.ColorableIcon(Rarity.LEGENDARY), MTCItems.packLegendary.get());
		event.getItemColors().register(new PackItemStandard.ColorableIcon(), MTCItems.packStandard.get());
		event.getItemColors().register(new PackItemEdition.ColorableIcon(), MTCItems.packEdition.get());
		event.getItemColors().register(new PackItemCustom.ColorableIcon(), MTCItems.packCustom.get());
	}
}
