package com.is.mtc.displayer;

import com.is.mtc.init.MTCTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DisplayerBlock extends Block {
	private boolean wasPowered;

	public DisplayerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 14;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return MTCTileEntities.displayerBlockTileEntity.get().create();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
		if (!world.isClientSide && hand == Hand.MAIN_HAND) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof DisplayerBlockTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (DisplayerBlockTileEntity) tileEntity, pos);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.is(newState.getBlock())) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof DisplayerBlockTileEntity) {
				InventoryHelper.dropContents(world, pos, (IInventory) tileEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(oldState, world, pos, newState, isMoving);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
		if (!world.isClientSide) {
			boolean isPowered = world.hasNeighborSignal(pos);
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof DisplayerBlockTileEntity && isPowered && !wasPowered) {
				((DisplayerBlockTileEntity) tileEntity).spinCards();
			}

			wasPowered = isPowered;
		}
	}
}
