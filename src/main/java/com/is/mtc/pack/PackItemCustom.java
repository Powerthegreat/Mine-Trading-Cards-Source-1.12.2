package com.is.mtc.pack;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.CustomPackStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Reference;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackItemCustom extends PackItemBase {

	private static final String CUSTOM_PACK_ID_KEY = "custom_pack_id";

	public PackItemCustom() {
		setTranslationKey("item_pack_custom");
		setRegistryName(Reference.MODID, "item_pack_custom");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int itemSlot, boolean isSelected) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		if (!stack.getTagCompound().hasKey(CUSTOM_PACK_ID_KEY) && Databank.getCustomPacksCount() > 0) {
			int i = world.rand.nextInt(Databank.getCustomPacksCount());

			NBTTagCompound nbtTag = stack.getTagCompound();
			nbtTag.setString(CUSTOM_PACK_ID_KEY, Databank.getCustomPackWithNumeralId(i).getId());
			stack.setTagCompound(nbtTag);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String cpid = stack.hasTagCompound() && stack.getTagCompound().hasKey(CUSTOM_PACK_ID_KEY) ? stack.getTagCompound().getString(CUSTOM_PACK_ID_KEY) : null;
		CustomPackStructure packStructure = cpid != null ? Databank.getCustomPackWithId(cpid) : null;

		if (cpid != null) {
			if (packStructure == null) { // Pack was created earlier, but edition was removed in the mean time
				return "custom_pack_" + cpid;
			} else {
				return packStructure.getName();
			}
		} else {
			return super.getItemStackDisplayName(stack);
		}
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		CustomPackStructure packStructure;
		NBTTagCompound nbt;

		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(CUSTOM_PACK_ID_KEY)) {
			return;
		}

		nbt = stack.getTagCompound();
		packStructure = Databank.getCustomPackWithId(stack.getTagCompound().getString(CUSTOM_PACK_ID_KEY));

		if (packStructure == null) {
			infos.add(TextFormatting.RED + "/!\\ Missing client-side custom pack");
			infos.add(TextFormatting.GRAY + nbt.getString(CUSTOM_PACK_ID_KEY));
			return;
		}

		infos.add("Contains cards from the custom pack '" + packStructure.getName() + "'");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ArrayList<String> created;
		CustomPackStructure packStructure;
		NBTTagCompound nbt;

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		if (!player.getHeldItem(hand).hasTagCompound() || !player.getHeldItem(hand).getTagCompound().hasKey(CUSTOM_PACK_ID_KEY)) {
			Logs.errLog("PackItemCustom: Missing NBT or NBTTag");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		nbt = player.getHeldItem(hand).getTagCompound();
		packStructure = Databank.getCustomPackWithId(player.getHeldItem(hand).getTagCompound().getString(CUSTOM_PACK_ID_KEY));

		if (packStructure == null) {
			Logs.chatMessage(player, "The custom pack this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemCustom: Custom pack is missing");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		created = new ArrayList<String>();
		packStructure.categoryQuantities.forEach((category, categoryInfo) -> createCards(category, categoryInfo[1], categoryInfo[0], created, world.rand));

		if (created.size() > 0) {
			for (String cdwd : created) {
				spawnCard(player, world, cdwd);
			}
			player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() - 1);
		} else {
			Logs.chatMessage(player, "Zero cards were registered, thus zero cards were generated");
			Logs.errLog("Zero cards were registered, thus zero cards were generated");
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	private void createCards(String category, int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generatedACardFromCategory(cardRarity, category, random);

				if (cStruct != null) {
					if (!created.contains(cStruct.getCDWD())) { // ... cards was not already created. Duplicate prevention
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
	public static class ColorableIcon implements IItemColor {
		@Override
		@SideOnly(Side.CLIENT)
		public int colorMultiplier(ItemStack stack, int layer) {
			if (layer == 0) {
				String eid = stack.hasTagCompound() && stack.getTagCompound().hasKey(CUSTOM_PACK_ID_KEY) ? stack.getTagCompound().getString(CUSTOM_PACK_ID_KEY) : null;
				return eid != null && Databank.getCustomPackWithId(eid) != null ? Databank.getCustomPackWithId(eid).getColor() : Reference.COLOR_GRAY;
			}

			return -1;
		}
	}
}
