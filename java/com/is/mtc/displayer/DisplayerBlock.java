package com.is.mtc.displayer;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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

		if (!(tileEntity instanceof DisplayerBlockTileEntity))
			return false;

		if (/*!*/world.isRemote)
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

			if (stack != ItemStack.EMPTY) {
				EntityItem entity = new EntityItem(world, x, y, z, stack);

				world.spawnEntity(entity);
			}
		}
	}

	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;

		emptyDisplayerBlockTileEntity((DisplayerBlockTileEntity) world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
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

	public TileEntity createNewTileEntity(World world, int meta) {
		return new DisplayerBlockTileEntity();
	}

	/*@Override
	public boolean renderAsNormalBlock() {
		return false;
	}*/

	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public Class<DisplayerBlockTileEntity> getTileEntityClass() {
		return DisplayerBlockTileEntity.class;
	}

	public DisplayerBlockTileEntity getTileEntity(IBlockAccess world, BlockPos pos) {
		return (DisplayerBlockTileEntity) world.getTileEntity(pos);
	}
}
