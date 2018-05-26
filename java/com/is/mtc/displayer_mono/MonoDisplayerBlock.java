package com.is.mtc.displayer_mono;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.is.mtc.handler.GuiHandler;
import com.is.mtc.root.MineTradingCards;
import com.is.mtc.root.Tools;

public class MonoDisplayerBlock extends BlockContainer {

	private IIcon iFace, iSides, iDisplaySide;

	public MonoDisplayerBlock() {
		super(Material.iron);

		setLightLevel(0.9375F);

		setBlockName("block_monodisplayer");
		setBlockTextureName(MineTradingCards.MODID + ":block_monodisplayer");
		setCreativeTab(MineTradingCards.MODTAB);

		setHardness(5.0F);
		setResistance(10.0F);

		isBlockContainer = true;
	}

	@Override
	public boolean onBlockActivated(World w, int px, int py, int pz, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		TileEntity tileEntity = w.getTileEntity(px, py, pz);

		if (tileEntity == null || !(tileEntity instanceof MonoDisplayerBlockTileEntity))
			return false;

		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_MONODISPLAYER, w, px, py, pz);
		return true;
	}

	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase player, ItemStack p_149689_6_) {
		int l = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;

		switch (l)
		{
		case 0:
			w.setBlockMetadataWithNotify(x, y, z, 2, 1 | 2);
			break;

		case 1:
			w.setBlockMetadataWithNotify(x, y, z, 5, 1 | 2);
			break;

		case 2:
			w.setBlockMetadataWithNotify(x, y, z, 3, 1 | 2);
			break;

		case 3:
			w.setBlockMetadataWithNotify(x, y, z, 4, 1 | 2);
			break;
		}
	}

	private void emptyMonoDisplayerBlockTileEntity(MonoDisplayerBlockTileEntity dte, World w, int x, int y, int z) {
		ItemStack[] content;

		if (dte == null)
			return;
		content = dte.getContent();

		for (int i = 0; i < 1; ++i) {
			ItemStack stack = content[i];

			if (stack != null) {
				EntityItem entity = new EntityItem(w, x, y, z, stack);

				w.spawnEntityInWorld(entity);
			}
		}
	}

	@Override
	public void onBlockPreDestroy(World w, int x, int y, int z, int oldMeta) {
		if (w.isRemote)
			return;

		emptyMonoDisplayerBlockTileEntity((MonoDisplayerBlockTileEntity)w.getTileEntity(x, y, z), w, x, y, z);
		w.removeTileEntity(x, y, z);
	}

	@Override
	public void registerBlockIcons(IIconRegister ireg) {
		iFace = ireg.registerIcon(MineTradingCards.MODID + ":block_displayer");
		iSides = ireg.registerIcon(MineTradingCards.MODID + ":block_raw"); // Faces w/o the illustration
		iDisplaySide = ireg.registerIcon(MineTradingCards.MODID + ":block_displayer_side");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (side == Tools.SIDE_TOP || side == Tools.SIDE_BOTTOM) ? iFace : side == meta ? iDisplaySide : iSides;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new MonoDisplayerBlockTileEntity();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}
