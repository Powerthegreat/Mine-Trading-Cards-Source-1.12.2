package com.is.mtc.pack;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.CustomPackStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Logs;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackItemCustom extends PackItemBase {

	public PackItemCustom() {
		setUnlocalizedName("item_pack_custom");
		setRegistryName("item_pack_custom");
	}

	@Override
	public void onUpdate(ItemStack stack, World w, Entity player, int par_4, boolean par_5) {

		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if (!stack.getTagCompound().hasKey("custom_pack_id") && Databank.getCustomPacksCount() > 0) {
			Random r = new Random();
			int i = r.nextInt(Databank.getCustomPacksCount());

			NBTTagCompound nbtTag = stack.getTagCompound();
			nbtTag.setString("custom_pack_id", Databank.getCustomPackWithNumeralId(i).getId());
			stack.setTagCompound(nbtTag);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String cpid = stack.hasTagCompound() && stack.getTagCompound().hasKey("custom_pack_id") ? stack.getTagCompound().getString("custom_pack_id") : null;
		CustomPackStructure packStructure = cpid != null ? Databank.getCustomPackWithId(cpid) : null;

		if (cpid != null) {
			if (packStructure == null) // Pack was created earlier, but edition was removed in the mean time
				return "custom_pack_" + cpid;
			else
				return packStructure.getName();
		} else
			return super.getItemStackDisplayName(stack);
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		CustomPackStructure packStructure;
		NBTTagCompound nbt;

		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("custom_pack_id"))
			return;

		nbt = stack.getTagCompound();
		packStructure = Databank.getCustomPackWithId(stack.getTagCompound().getString("custom_pack_id"));

		if (packStructure == null) {
			infos.add(ChatFormatting.RED + "/!\\ Missing client-side custom pack");
			infos.add(ChatFormatting.GRAY + nbt.getString("custom_pack_id"));
			return;
		}

		infos.add("Contains cards from the custom pack '" + packStructure.getName() + "'");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ArrayList<String> created;
		CustomPackStructure packStructure;
		NBTTagCompound nbt;

		if (world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

		if (!player.getHeldItem(hand).hasTagCompound() || !player.getHeldItem(hand).getTagCompound().hasKey("custom_pack_id")) {
			Logs.errLog("PackItemCustom: Missing NBT or NBTTag");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		nbt = player.getHeldItem(hand).getTagCompound();
		packStructure = Databank.getCustomPackWithId(player.getHeldItem(hand).getTagCompound().getString("custom_pack_id"));

		if (packStructure == null) {
			Logs.chatMessage(player, "The custom pack this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemCustom: Custom pack is missing");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		created = new ArrayList<String>();
		packStructure.categoryQuantities.forEach((category, categoryInfo) -> createCards(category, categoryInfo[1], categoryInfo[0], created));

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

	private void createCards(String category, int cardRarity, int count, ArrayList<String> created) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generatedACardFromCategory(cardRarity, category);

				if (cStruct != null) {
					if (!created.contains(cStruct.getCDWD())) { // ... cards was not already created. Duplicate prevention
						created.add(cStruct.getCDWD());
						break;
					}
				}
			}
		}
	}
}
