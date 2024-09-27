package com.is.mtc.init;

import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.displayer_mono.MonoDisplayerBlockTileEntity;
import com.is.mtc.util.Reference;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MTCTileEntities {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MODID);
	public static RegistryObject<TileEntityType<DisplayerBlockTileEntity>> displayerBlockTileEntity = TILE_ENTITY_TYPES
			.register("block_displayer",
					() -> TileEntityType.Builder.of(DisplayerBlockTileEntity::new, MTCBlocks.displayerBlock.get()).build(null));
	public static RegistryObject<TileEntityType<MonoDisplayerBlockTileEntity>> monoDisplayerBlockTileEntity = TILE_ENTITY_TYPES
			.register("block_monodisplayer",
					() -> TileEntityType.Builder.of(MonoDisplayerBlockTileEntity::new, MTCBlocks.monoDisplayerBlock.get()).build(null));
}
