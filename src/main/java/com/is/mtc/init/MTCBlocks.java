package com.is.mtc.init;

import com.is.mtc.MineTradingCards;
import com.is.mtc.displayer.DisplayerBlock;
import com.is.mtc.displayer_mono.MonoDisplayerBlock;
import com.is.mtc.util.Reference;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MTCBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
	public static final AbstractBlock.Properties DISPLAYER_PROPERTIES = AbstractBlock.Properties.of(new Material.Builder(MaterialColor.WOOD).build()).harvestTool(ToolType.AXE).strength(5.0F, 10.0F);

	public static RegistryObject<Block> displayerBlock = registerBlock("block_displayer", () -> new DisplayerBlock(DISPLAYER_PROPERTIES), MineTradingCards.ITEMGROUP_MTC);
	public static RegistryObject<Block> monoDisplayerBlock = registerBlock("block_monodisplayer", () -> new MonoDisplayerBlock(DISPLAYER_PROPERTIES), MineTradingCards.ITEMGROUP_MTC);


	private static RegistryObject<Block> registerBlock(String id, Supplier<Block> blockSupplier, ItemGroup itemGroup) {
		RegistryObject<Block> block = BLOCKS.register(id, blockSupplier);
		MTCItems.ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties().tab(itemGroup)));
		return block;
	}
}
