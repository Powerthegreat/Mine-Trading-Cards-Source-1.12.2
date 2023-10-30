package com.is.mtc.proxy;

import com.is.mtc.MineTradingCards;
import com.is.mtc.card.CardItem;
import com.is.mtc.displayer.DisplayerBlockRenderer;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockRenderer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.init.MTCItems;
import com.is.mtc.pack.PackItemCustom;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import net.minecraft.client.Minecraft;
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
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new CardItem.ColorableIcon(Rarity.COMMON), MTCItems.cardCommon);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new CardItem.ColorableIcon(Rarity.UNCOMMON), MTCItems.cardUncommon);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new CardItem.ColorableIcon(Rarity.RARE), MTCItems.cardRare);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new CardItem.ColorableIcon(Rarity.ANCIENT), MTCItems.cardAncient);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new CardItem.ColorableIcon(Rarity.LEGENDARY), MTCItems.cardLegendary);

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemRarity.ColorableIcon(Rarity.COMMON), MTCItems.packCommon);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemRarity.ColorableIcon(Rarity.UNCOMMON), MTCItems.packUncommon);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemRarity.ColorableIcon(Rarity.RARE), MTCItems.packRare);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemRarity.ColorableIcon(Rarity.ANCIENT), MTCItems.packAncient);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemRarity.ColorableIcon(Rarity.LEGENDARY), MTCItems.packLegendary);

		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemStandard.ColorableIcon(), MTCItems.packStandard);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemEdition.ColorableIcon(), MTCItems.packEdition);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PackItemCustom.ColorableIcon(), MTCItems.packCustom);
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

		ClientRegistry.bindTileEntitySpecialRenderer(DisplayerBlockTileEntity.class, new DisplayerBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MonoDisplayerBlockTileEntity.class, new MonoDisplayerBlockRenderer());
		//VillagerRegistry.instance().registerVillagerSkin(VillageHandler.TRADER_ID, new ResourceLocation("is_mtc", "textures/skins/card_master.png"));
	}
}
