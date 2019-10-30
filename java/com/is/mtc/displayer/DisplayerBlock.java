package com.is.mtc.displayer;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.GuiHandler;
import net.minecraft.block.Block;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class DisplayerBlock extends Block {
	public DisplayerBlock() {
		super(Material.IRON);

		setLightLevel(0.9375F);

		setUnlocalizedName("block_displayer");
		setRegistryName("block_displayer");
		setCreativeTab(MineTradingCards.MODTAB);

		setHardness(5.0F);
		setResistance(10.0F);
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if (!(tileEntity instanceof DisplayerBlockTileEntity))
			return false;

		player.openGui(MineTradingCards.INSTANCE, GuiHandler.GUI_DISPLAYER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	private void emptyDisplayerBlockTileEntity(DisplayerBlockTileEntity dte, World world, int x, int y, int z) {
		IItemHandler content;

		if (dte == null)
			return;
		content = dte.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);//.getContent();

		for (int i = 0; i < 4; ++i) {
			ItemStack stack = content.getStackInSlot(i);//content[i];

			if (!stack.isEmpty()) {
				EntityItem entity = new EntityItem(world, x, y, z, stack);

				world.spawnEntity(entity);
			}
		}
	}

	/*public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;


	}*/

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		emptyDisplayerBlockTileEntity((DisplayerBlockTileEntity) world.getTileEntity(pos), world, pos.getX(), pos.getY(), pos.getZ());
		world.removeTileEntity(pos);
	}

	@Nullable
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new DisplayerBlockTileEntity();
	}

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
