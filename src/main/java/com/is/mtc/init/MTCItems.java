package com.is.mtc.init;

import com.is.mtc.MineTradingCards;
import com.is.mtc.binder.BinderItem;
import com.is.mtc.card.CardItem;
import com.is.mtc.pack.PackItemCustom;
import com.is.mtc.pack.PackItemEdition;
import com.is.mtc.pack.PackItemRarity;
import com.is.mtc.pack.PackItemStandard;
import com.is.mtc.root.Rarity;
import com.is.mtc.util.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MTCItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);
	public static final RegistryObject<Item>
			cardCommon = ITEMS.register(CardItem.makeRegistryName(Rarity.COMMON), () -> new CardItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.COMMON)),
			cardUncommon = ITEMS.register(CardItem.makeRegistryName(Rarity.UNCOMMON), () -> new CardItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.UNCOMMON)),
			cardRare = ITEMS.register(CardItem.makeRegistryName(Rarity.RARE), () -> new CardItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.RARE)),
			cardAncient = ITEMS.register(CardItem.makeRegistryName(Rarity.ANCIENT), () -> new CardItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.ANCIENT)),
			cardLegendary = ITEMS.register(CardItem.makeRegistryName(Rarity.LEGENDARY), () -> new CardItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.LEGENDARY));
	public static final RegistryObject<Item>
			packCommon = ITEMS.register(PackItemRarity.makeRegistryName(Rarity.COMMON), () -> new PackItemRarity(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.COMMON)),
			packUncommon = ITEMS.register(PackItemRarity.makeRegistryName(Rarity.UNCOMMON), () -> new PackItemRarity(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.UNCOMMON)),
			packRare = ITEMS.register(PackItemRarity.makeRegistryName(Rarity.RARE), () -> new PackItemRarity(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.RARE)),
			packAncient = ITEMS.register(PackItemRarity.makeRegistryName(Rarity.ANCIENT), () -> new PackItemRarity(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.ANCIENT)),
			packLegendary = ITEMS.register(PackItemRarity.makeRegistryName(Rarity.LEGENDARY), () -> new PackItemRarity(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC), Rarity.LEGENDARY)),
			packStandard = ITEMS.register("item_pack_standard", () -> new PackItemStandard(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC))),
			packEdition = ITEMS.register("item_pack_edition", () -> new PackItemEdition(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC))),
			packCustom = ITEMS.register("item_pack_custom", () -> new PackItemCustom(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC)));
	private static final RegistryObject<Item> binder = ITEMS.register("item_binder", () -> new BinderItem(new Item.Properties().tab(MineTradingCards.ITEMGROUP_MTC)));
}