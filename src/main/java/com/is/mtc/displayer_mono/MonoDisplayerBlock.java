package com.is.mtc.displayer_mono;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import com.is.mtc.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class MonoDisplayerBlock extends Block {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);// BlockHorizontal.FACING;

	//private IIcon iFace, iSides, iDisplaySide;

	public MonoDisplayerBlock() {
		super(new Material(MapColor.WOOD));

		setLightLevel(0.9375F);

		setTranslationKey("block_monodisplayer");
		setRegistryName(Reference.MODID, "block_monodisplayer");
		setCreativeTab(MineTradingCards.MODTAB);

		setHardness(5.0F);
		setResistance(10.0F);
		setHarvestLevel("axe", 0);

		setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return getDefaultState().withProperty(FACING, enumfacing);
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof MonoDisplayerBlockTileEntity))
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
		IItemHandler content;

		if (dte == null)
			return;
		content = dte.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);//.getContent();

		for (int i = 0; i < 1; ++i) {
			ItemStack stack = content.getStackInSlot(i);//content[i];

			if (!stack.isEmpty()) {
				EntityItem entity = new EntityItem(world, x, y, z, stack);

				world.spawnEntity(entity);
			}
		}
	}

	/*@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;

		emptyMonoDisplayerBlockTileEntity((MonoDisplayerBlockTileEntity) world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
		world.removeTileEntity(pos);
	}*/

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		emptyMonoDisplayerBlockTileEntity((MonoDisplayerBlockTileEntity) world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
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

	@Nullable
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new MonoDisplayerBlockTileEntity();
	}

	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public Class<MonoDisplayerBlockTileEntity> getTileEntityClass() {
		return MonoDisplayerBlockTileEntity.class;
	}

	public MonoDisplayerBlockTileEntity getTileEntity(IBlockAccess world, BlockPos pos) {
		return (MonoDisplayerBlockTileEntity) world.getTileEntity(pos);
	}

	/*@Override
	public boolean renderAsNormalBlock() {
		return false;
	}*/
}
