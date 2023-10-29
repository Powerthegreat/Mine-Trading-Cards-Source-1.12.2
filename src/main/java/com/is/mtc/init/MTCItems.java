package com.is.mtc.init;

import com.is.mtc.binder.BinderItem;
import com.is.mtc.card.CardItem;
import com.is.mtc.displayer.DisplayerBlock;
import com.is.mtc.displayer_mono.MonoDisplayerBlock;
import com.is.mtc.pack.PackItemBase;
import com.is.mtc.pack.PackItemCustom;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.root.Rarity;

import com.is.mtc.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MTCItems {

	// - Informations
	/*public static final String ITEM_HEAD = "item.";
	public static final String BLOCK_HEAD = "tile.";*/
	public static CardItem cardCommon, cardUncommon, cardRare, cardAncient, cardLegendary;
	public static PackItemBase packCommon, packUncommon, packRare, packAncient, packLegendary, packStandard, packEdition, packCustom;
	private static BinderItem binder;
	public static Block displayerBlock;
	public static Block monoDisplayerBlock;
	private static Item displayerItemBlock;
	private static Item monoDisplayerItemBlock;

	/*// - Functions
	private static String getItemNormalName(Item i) {
		return i.getUnlocalizedName().substring(Injector.ITEM_HEAD.length());
	}

	private static String getBlockNormalName(Block b) {
		return b.getUnlocalizedName().substring(Injector.BLOCK_HEAD.length());
	}*/

	/*public static void registerItem(Item i) {
		GameRegistry.registerItem(i, getItemNormalName(i));

		Logs.devLog("Injector: Registered item: " + i.getUnlocalizedName());
	}

	public static void registerBlock(Block b) {
		GameRegistry.registerBlock(b, getBlockNormalName(b));

		Logs.devLog("Injector: Registered block: " + b.getUnlocalizedName());
	}*/

	public static void init() {
		cardCommon = new CardItem(Rarity.COMMON);
		cardUncommon = new CardItem(Rarity.UNCOMMON);
		cardRare = new CardItem(Rarity.RARE);
		cardAncient = new CardItem(Rarity.ANCIENT);
		cardLegendary = new CardItem(Rarity.LEGENDARY);
		packCommon = new PackItemRarity(Rarity.COMMON);
		packUncommon = new PackItemRarity(Rarity.UNCOMMON);
		packRare = new PackItemRarity(Rarity.RARE);
		packAncient = new PackItemRarity(Rarity.ANCIENT);
		packLegendary = new PackItemRarity(Rarity.LEGENDARY);
		packStandard = new PackItemStandard();
		packEdition = new PackItemEdition();
		packCustom = new PackItemCustom();
		binder = new BinderItem();
		displayerBlock = new DisplayerBlock();
		displayerItemBlock = new ItemBlock(displayerBlock).setRegistryName(Reference.MODID, "block_displayer");
		monoDisplayerBlock = new MonoDisplayerBlock();
		monoDisplayerItemBlock = new ItemBlock(monoDisplayerBlock).setRegistryName(Reference.MODID, "block_monodisplayer");
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(cardCommon, cardUncommon, cardRare, cardAncient, cardLegendary,
				packCommon, packUncommon, packRare, packAncient, packLegendary, packStandard,
				packEdition, packCustom, binder);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(displayerBlock, monoDisplayerBlock);
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(displayerItemBlock, monoDisplayerItemBlock);
	}

	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(cardCommon);
		registerRender(cardUncommon);
		registerRender(cardRare);
		registerRender(cardAncient);
		registerRender(cardLegendary);
		registerRender(packCommon);
		registerRender(packUncommon);
		registerRender(packRare);
		registerRender(packAncient);
		registerRender(packLegendary);
		registerRender(packStandard);
		registerRender(packEdition);
		registerRender(packCustom);
		registerRender(binder);
		registerRender(Item.getItemFromBlock(displayerBlock));
		registerRender(Item.getItemFromBlock(monoDisplayerBlock));
	}

	public static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}