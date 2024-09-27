package com.is.mtc.displayer;

import com.is.mtc.init.MTCBlocks;
import com.is.mtc.init.MTCTileEntities;
import com.is.mtc.util.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DisplayerBlockTileEntity extends LockableLootTileEntity {
	public static final int INVENTORY_SIZE = 4;
	protected NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

	public DisplayerBlockTileEntity() {
		super(MTCTileEntities.displayerBlockTileEntity.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		if (!this.trySaveLootTable(nbt)) {
			ItemStackHelper.saveAllItems(nbt, items);
		}
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
		if (!this.tryLoadLootTable(nbt)) {
			ItemStackHelper.loadAllItems(nbt, items);
		}
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Reference.MODID + ".block_displayer");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new DisplayerBlockContainer(id, player, this);
	}

	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		save(nbt);

		return new SUpdateTileEntityPacket(this.worldPosition, -1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		CompoundNBT nbt = pkt.getTag();
		load(MTCBlocks.displayerBlock.get().defaultBlockState(), nbt);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return INVENTORY_SIZE;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		save(nbt);

		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		load(state, tag);
	}

	public void spinCards() {
		ItemStack tempStack = items.get(0).copy();
		items.set(0, items.get(3).copy());
		items.set(3, items.get(1).copy());
		items.set(1, items.get(2).copy());
		items.set(2, tempStack);

		setChanged();

		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
	}
}
