package com.is.mtc.binder;

import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BinderItem extends Item {
	public static final int MODE_STD = 0; // Standard mode
	public static final int MODE_FIL = 1; // Fill mode. Starts from current page toward binder's slots limit
	//public static final int MODE_PLA = 2; // Place mode. Get card number and try to place that card somewhere
	//# Buggy when client and server doesn't use the same mods

	public static final String[] MODE_STR = {"screen." + Reference.MODID + ".binder.mode_standard", "screen." + Reference.MODID + ".binder.mode_fill"/*, "Slot"*/};

	public BinderItem(Properties properties) {
		super(properties);
	}

	public static void testNBT(ItemStack binderStack) {
		if (binderStack.getTag() == null) {// Create nbt if not already existing
			CompoundNBT nbtTag = new CompoundNBT();
			nbtTag.putInt("page", 0);
			nbtTag.putInt("mode_mtc", MODE_STD);
			binderStack.setTag(nbtTag);
		}

	}

	public static int changePageBy(ItemStack binderStack, int count) {
		return setCurrentPage(binderStack, getCurrentPage(binderStack) + count);
	}

	public static int setCurrentPage(ItemStack binderStack, int page) {
		testNBT(binderStack);
		CompoundNBT nbtTag = binderStack.getTag();
		nbtTag.putInt("page", (int) Tools.clamp(0, page, BinderItemInventory.getTotalPages() - 1));

		return getCurrentPage(binderStack);
	}

	public static int getCurrentPage(ItemStack binderStack) {
		testNBT(binderStack);

		return binderStack.getTag().getInt("page");
	}

	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		testNBT(player.getItemInHand(hand));
		if (!world.isClientSide) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new BinderContainerProvider(), buf -> {
			});
		}

		return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
	}

	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> infos, ITooltipFlag flag) {
		testNBT(stack);

		infos.add(new StringTextComponent(""));
		infos.add(new StringTextComponent("Page: " + (stack.getTag().getInt("page") + 1) + "/" + BinderItemInventory.getTotalPages()));
	}

	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new BinderItemInventory();
	}

	private static class BinderContainerProvider implements INamedContainerProvider {
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
			return new BinderItemContainer(windowId, playerInventory, playerInventory.getSelected());
		}

		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("container." + Reference.MODID + ".item_binder");
		}
	}
}
