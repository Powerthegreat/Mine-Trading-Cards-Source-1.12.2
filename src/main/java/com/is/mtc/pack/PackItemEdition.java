package com.is.mtc.pack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.data_manager.EditionStructure;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Functions;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PackItemEdition extends PackItemBase {
	
	public static String[] EDITION_PACK_CONTENT = PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT;
	
	private static final String EDITION_ID_KEY = "edition_id";
	
	public PackItemEdition() {
		setUnlocalizedName("item_pack_edition");
		setRegistryName("item_pack_edition");
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity player, int par_4, boolean par_5) {
		Random r = world.rand;
		
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

	@Override
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
		if (world.isRemote) {return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));} // Don't do this on the client side
		
		if (!player.getHeldItem(hand).hasTagCompound() || !player.getHeldItem(hand).getTagCompound().hasKey(EDITION_ID_KEY)) {
			Logs.errLog("PackItemEdition: Missing NBT or NBTTag");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		
		ArrayList<String> created;
		Random random = world.rand;
		NBTTagCompound nbt;
		nbt = player.getHeldItem(hand).getTagCompound();
		EditionStructure eStruct;
		eStruct = Databank.getEditionWithId(player.getHeldItem(hand).getTagCompound().getString(EDITION_ID_KEY));
		
		if (eStruct == null) {
			Logs.chatMessage(player, "The edition this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemEdition: Edition is missing");
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		
		// Figure out how many of each card rarity to create
		
		int[] card_set_to_create = new int[] {0,0,0,0,0}; // Set of cards that will come out of the pack

		for (String entry : EDITION_PACK_CONTENT)
		{
			try {
				double[] card_weighted_dist = new double[] {0,0,0,0,0}; // Distribution used when a card is randomized
				
				// Split entry
				String[] split_entry = entry.toLowerCase().trim().split("x");
				
				float count = MathHelper.clamp(Float.parseFloat(split_entry[0]), 0F, 64F);
				int drop_count_characteristic = (int) count;
				float drop_count_mantissa = count % 1;
				
				String[] distribution_split = split_entry[1].split(":");
				
				for (int i=0; i<distribution_split.length; i++) {
					card_weighted_dist[i]=Integer.parseInt(distribution_split[i].trim());
				}
				
				// Repeat for the number of cards prescribed
				for (int i=0; i<drop_count_characteristic + (random.nextFloat()<drop_count_mantissa ? 1 : 0); i++)
				{
					Object chosen_rarity = Functions.weightedRandom(CardItem.CARD_RARITY_ARRAY, card_weighted_dist, random);
					
					if (chosen_rarity!=null) {
						card_set_to_create[(Integer)chosen_rarity]++;
					}
				}
			}
			catch (Exception e) {
				Logs.errLog("Something went wrong parsing edition_pack_contents line: " + entry);
			}
		}

		// Actually create the cards
		
		created = new ArrayList<String>();

		for (int rarity : CardItem.CARD_RARITY_ARRAY) {
			createCards(eStruct.getId(), rarity, card_set_to_create[rarity], created, world.rand);
		}
		
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
