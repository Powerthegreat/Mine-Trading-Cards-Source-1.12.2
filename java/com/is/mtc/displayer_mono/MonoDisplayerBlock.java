package com.is.mtc.displayer_mono;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import com.is.mtc.handler.GuiHandler;
import com.is.mtc.MineTradingCards;

public class MonoDisplayerBlock extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	//private IIcon iFace, iSides, iDisplaySide;

	public MonoDisplayerBlock() {
		super(Material.IRON);

		setLightLevel(0.9375F);

		setUnlocalizedName("block_monodisplayer");
		setRegistryName("block_monodisplayer");
		//setBlockName("block_monodisplayer");
		//setBlockTextureName(MineTradingCards.MODID + ":block_monodisplayer");
		setCreativeTab(MineTradingCards.MODTAB);

		setHardness(5.0F);
		setResistance(10.0F);

		setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
		//isBlockContainer = true;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
									EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity == null || !(tileEntity instanceof MonoDisplayerBlockTileEntity))
			return false;

		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_MONODISPLAYER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		world.setBlockState(pos, getDefaultState().withProperty(FACING, player.getHorizontalFacing().getOpposite()));
		/*int l = MathHelper.floor((player.rotationYaw * 4F) / 360F + 0.5D) & 3;

		switch (l) {
			case 0:
				world.setBlockState(pos, getStateFromMeta(2));
				//world.setBlockMetadataWithNotify(pos, 2, 1 | 2);
				break;

			case 1:
				world.setBlockState(pos, getStateFromMeta(5));
				//world.setBlockMetadataWithNotify(pos, 5, 1 | 2);
				break;

			case 2:
				world.setBlockState(pos, getStateFromMeta(3));
				//world.setBlockMetadataWithNotify(pos, 3, 1 | 2);
				break;

			case 3:
				world.setBlockState(pos, getStateFromMeta(4));
				//world.setBlockMetadataWithNotify(pos, 4, 1 | 2);
				break;
		}*/
	}

	private void emptyMonoDisplayerBlockTileEntity(MonoDisplayerBlockTileEntity dte, World world, int x, int y, int z) {
		ItemStack[] content;

		if (dte == null)
			return;
		content = dte.getContent();

		for (int i = 0; i < 1; ++i) {
			ItemStack stack = content[i];

			if (stack != null) {
				EntityItem entity = new EntityItem(world, x, y, z, stack);

				world.spawnEntity(entity);
			}
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote) {
			return;
		}

		emptyMonoDisplayerBlockTileEntity((MonoDisplayerBlockTileEntity)world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
		world.removeTileEntity(pos);
	}

	/*@Override
	public void registerBlockIcons(IIconRegister ireg) {
		iFace = ireg.registerIcon(MineTradingCards.MODID + ":block_displayer");
		iSides = ireg.registerIcon(MineTradingCards.MODID + ":block_raw"); // Faces w/o the illustration
		iDisplaySide = ireg.registerIcon(MineTradingCards.MODID + ":block_displayer_side");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (side == Tools.SIDE_TOP || side == Tools.SIDE_BOTTOM) ? iFace : side == meta ? iDisplaySide : iSides;
	}*/

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new MonoDisplayerBlockTileEntity();
	}

	/*@Override
	public boolean renderAsNormalBlock() {
		return false;
	}*/
}
