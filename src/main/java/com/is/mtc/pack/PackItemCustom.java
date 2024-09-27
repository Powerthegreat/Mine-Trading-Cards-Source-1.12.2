package com.is.mtc.pack;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.CustomPackStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Reference;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackItemCustom extends PackItemBase {

	private static final String CUSTOM_PACK_ID_KEY = "custom_pack_id";

	public PackItemCustom(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity player, int itemSlot, boolean isSelected) {

		stack.getOrCreateTag();
		if (!stack.getTag().contains(CUSTOM_PACK_ID_KEY) && Databank.getCustomPacksCount() > 0) {
			int i = world.getRandom().nextInt(Databank.getCustomPacksCount());

			CompoundNBT nbtTag = stack.getTag();
			nbtTag.putString(CUSTOM_PACK_ID_KEY, Databank.getCustomPackWithNumeralId(i).getId());
			stack.setTag(nbtTag);
		}
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		String cpid = (stack.getTag() != null && stack.getTag().contains(CUSTOM_PACK_ID_KEY)) ? stack.getTag().getString(CUSTOM_PACK_ID_KEY) : null;
		CustomPackStructure packStructure = cpid != null ? Databank.getCustomPackWithId(cpid) : null;

		if (cpid != null) {
			if (packStructure == null) { // Pack was created earlier, but edition was removed in the mean time
				return new StringTextComponent("custom_pack_" + cpid);
			} else {
				return new StringTextComponent(packStructure.getName());
			}
		} else {
			return super.getName(stack);
		}
	}

	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> infos, ITooltipFlag flag) {
		CustomPackStructure packStructure;
		CompoundNBT nbt;

		if (stack.getTag() == null || !stack.getTag().contains(CUSTOM_PACK_ID_KEY)) {
			return;
		}

		nbt = stack.getTag();
		packStructure = Databank.getCustomPackWithId(stack.getTag().getString(CUSTOM_PACK_ID_KEY));

		if (packStructure == null) {
			infos.add(new StringTextComponent(TextFormatting.RED + "/!\\ Missing client-side custom pack"));
			infos.add(new StringTextComponent(TextFormatting.GRAY + nbt.getString(CUSTOM_PACK_ID_KEY)));
			return;
		}

		infos.add(new StringTextComponent("Contains cards from the custom pack '" + packStructure.getName() + "'"));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ArrayList<String> created;
		CustomPackStructure packStructure;
		CompoundNBT nbt;

		if (world.isClientSide) {
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}
		if (player.getItemInHand(hand).getTag() == null || !player.getItemInHand(hand).getTag().contains(CUSTOM_PACK_ID_KEY)) {
			Logs.errLog("PackItemCustom: Missing NBT or NBTTag");
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}

		nbt = player.getItemInHand(hand).getTag();
		packStructure = Databank.getCustomPackWithId(nbt.getString(CUSTOM_PACK_ID_KEY));

		if (packStructure == null) {
			Logs.chatMessage(player, "The custom pack this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemCustom: Custom pack is missing");
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}

		created = new ArrayList<String>();
		packStructure.categoryQuantities.forEach((category, categoryInfo) -> createCards(category, categoryInfo[1], categoryInfo[0], created, world.getRandom()));

		if (!created.isEmpty()) {
			for (String cdwd : created) {
				spawnCard(player, world, cdwd);
			}
			player.getItemInHand(hand).setCount(player.getItemInHand(hand).getCount() - 1);
		} else {
			Logs.chatMessage(player, "Zero cards were registered, thus zero cards were generated");
			Logs.errLog("Zero cards were registered, thus zero cards were generated");
		}

		return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
	}

	private void createCards(String category, int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generateACardFromCategory(cardRarity, category, new Random()); // Using new Random() because world random can cause issues generating cards

				if (cStruct != null) {
					if (!created.contains(cStruct.getCDWD())) { // ... card was not already created. Duplicate prevention
						created.add(cStruct.getCDWD());
						break;
					}
				}
			}
		}
	}


	// === ICON LAYERING AND COLORIZATION === //

	/**
	 * From https://github.com/matshou/Generic-Mod
	 */
	@OnlyIn(Dist.CLIENT)
	public static class ColorableIcon implements IItemColor {
		@Override
		public int getColor(ItemStack stack, int layer) {
			if (layer == 0) {
				String eid = stack.getTag() != null && stack.getTag().contains(CUSTOM_PACK_ID_KEY) ? stack.getTag().getString(CUSTOM_PACK_ID_KEY) : null;
				return eid != null && Databank.getCustomPackWithId(eid) != null ? Databank.getCustomPackWithId(eid).getColor() : Reference.COLOR_GRAY;
			}

			return -1;
		}
	}
}
