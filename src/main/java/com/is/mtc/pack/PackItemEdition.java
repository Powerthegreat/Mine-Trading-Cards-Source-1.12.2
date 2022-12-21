package com.is.mtc.pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.data_manager.EditionStructure;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
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

public class PackItemEdition extends PackItemBase {

	private static final int[] cCount = {7, 2, 1};
	private static final int[] rWeight = {25, 29, 30};
	private static final int rtWeight = rWeight[2];
	
	private static final String EDITION_ID_KEY = "edition_id";
	
	public PackItemEdition() {
		setUnlocalizedName("item_pack_edition");
		setRegistryName("item_pack_edition");
		//setTextureName(MineTradingCards.MODID + ":item_pack_edition");
	}

	@Override
	public void onUpdate(ItemStack stack, World w, Entity player, int par_4, boolean par_5) {
		Random r = w.rand;
		
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if (!stack.getTagCompound().hasKey(EDITION_ID_KEY) && Databank.getEditionsCount() > 0) {
			int i = r.nextInt(Databank.getEditionsCount());

			NBTTagCompound nbtTag = stack.getTagCompound();
			nbtTag.setString(EDITION_ID_KEY, Databank.getEditionWithNumeralId(i).getId());
			stack.setTagCompound(nbtTag);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String eid = stack.hasTagCompound() && stack.getTagCompound().hasKey(EDITION_ID_KEY) ? stack.getTagCompound().getString(EDITION_ID_KEY) : null;
		EditionStructure eStruct = eid != null ? Databank.getEditionWithId(eid) : null;

		if (eid != null) {
			if (eStruct == null) // Pack was created earlier, but edition was removed in the mean time
				return "edition_pack_" + eid;
			else
				return eStruct.getName() + " Pack";
		} else
			return super.getItemStackDisplayName(stack);
	}

	public void addInformation(ItemStack stack, @Nullable World world, List<String> infos, ITooltipFlag flag) {
		EditionStructure eStruct;
		NBTTagCompound nbt;

		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(EDITION_ID_KEY))
			return;

		nbt = stack.getTagCompound();
		eStruct = Databank.getEditionWithId(stack.getTagCompound().getString(EDITION_ID_KEY));

		if (eStruct == null) {
			infos.add(TextFormatting.RED + "/!\\ Missing client-side edition");
			infos.add(TextFormatting.GRAY + nbt.getString(EDITION_ID_KEY));
			return;
		}

		infos.add("Contains cards from the edition '" + eStruct.getName() + "'");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ArrayList<String> created;
		EditionStructure eStruct;
		NBTTagCompound nbt;
		Random random = world.rand;
		int i;

		if (world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

		if (!player.getHeldItem(hand).hasTagCompound() || !player.getHeldItem(hand).getTagCompound().hasKey(EDITION_ID_KEY)) {
			Logs.errLog("PackItemEdition: Missing NBT or NBTTag");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		nbt = player.getHeldItem(hand).getTagCompound();
		eStruct = Databank.getEditionWithId(player.getHeldItem(hand).getTagCompound().getString(EDITION_ID_KEY));

		if (eStruct == null) {
			Logs.chatMessage(player, "The edition this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemEdition: Edition is missing");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		created = new ArrayList<String>();
		createCards(eStruct.getId(), Rarity.COMMON, cCount[Rarity.COMMON], created, random);
		createCards(eStruct.getId(), Rarity.UNCOMMON, cCount[Rarity.UNCOMMON], created, random);

		i = random.nextInt(rtWeight);
		if (i < rWeight[0])
			createCards(eStruct.getId(), Rarity.RARE, cCount[Rarity.RARE], created, random);
		else if (i < rWeight[1])
			createCards(eStruct.getId(), Rarity.ANCIENT, cCount[Rarity.RARE], created, random);
		else if (i < rWeight[2])
			createCards(eStruct.getId(), Rarity.LEGENDARY, cCount[Rarity.RARE], created, random);

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

	private void createCards(String edition, int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generatedACardFromEdition(cardRarity, edition, random);

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
	public static class ColorableIcon implements IItemColor 
	{
		@Override
	    @SideOnly(Side.CLIENT)
		public int colorMultiplier(ItemStack stack, int layer) 
		{
			if (layer==0)
	    	{
	    		String eid = stack.hasTagCompound() && stack.getTagCompound().hasKey(EDITION_ID_KEY) ? stack.getTagCompound().getString(EDITION_ID_KEY) : null;
	    		return eid != null && Databank.getEditionWithId(eid) != null ? Databank.getEditionWithId(eid).getColor() : Reference.COLOR_GRAY;
	    	}
	    	
	        return -1;
		}
	}
}
