package com.is.mtc.displayer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.is.mtc.handler.GuiHandler;
import com.is.mtc.MineTradingCards;

public class DisplayerBlock extends BlockContainer {

	//private IIcon iFace, iSides;

	public DisplayerBlock() {
		super(Material.IRON);

		setLightLevel(0.9375F);

		setUnlocalizedName("block_displayer");
		setRegistryName("block_displayer");
		//setBlockName("block_displayer");
		//setBlockTextureName(MineTradingCards.MODID + ":block_displayer");
		setCreativeTab(MineTradingCards.MODTAB);

		setHardness(5.0F);
		setResistance(10.0F);

		//isBlockContainer = true;
	}

	/*-*/

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
									EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null || !(tileEntity instanceof DisplayerBlockTileEntity))
			return false;

		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_DISPLAYER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	private void emptyDisplayerBlockTileEntity(DisplayerBlockTileEntity dte, World world, int x, int y, int z) {
		ItemStack[] content;

		if (dte == null)
			return;
		content = dte.getContent();

		for (int i = 0; i < 4; ++i) {
			ItemStack stack = content[i];

			if (stack != null) {
				EntityItem entity = new EntityItem(world, x, y, z, stack);

				world.spawnEntity(entity);
			}
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;

		emptyDisplayerBlockTileEntity((DisplayerBlockTileEntity)world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
		world.removeTileEntity(pos);
	}

	/*-*/

	/*@Override
	public void registerBlockIcons(IIconRegister ireg) {
		iFace = ireg.registerIcon(getTextureName());
		iSides = ireg.registerIcon(getTextureName() + "_side");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (side == Tools.SIDE_TOP || side == Tools.SIDE_BOTTOM) ? iFace : iSides;
	}*/

	/*-*/

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new DisplayerBlockTileEntity();
	}

	/*@Override
	public boolean renderAsNormalBlock() {
		return false;
	}*/
}
