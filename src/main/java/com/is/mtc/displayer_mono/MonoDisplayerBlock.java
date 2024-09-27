package com.is.mtc.displayer_mono;

import com.is.mtc.init.MTCTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class MonoDisplayerBlock extends Block {
	public static final Property<Direction> FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

	public MonoDisplayerBlock(Properties properties) {
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
		return MTCTileEntities.monoDisplayerBlockTileEntity.get().create();
	}

	@Override
	public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
		if (!world.isClientSide && hand == Hand.MAIN_HAND) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof MonoDisplayerBlockTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (MonoDisplayerBlockTileEntity) tileEntity, pos);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.is(newState.getBlock())) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof MonoDisplayerBlockTileEntity) {
				InventoryHelper.dropContents(world, pos, (IInventory) tileEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(oldState, world, pos, newState, isMoving);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(FACING);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
}
