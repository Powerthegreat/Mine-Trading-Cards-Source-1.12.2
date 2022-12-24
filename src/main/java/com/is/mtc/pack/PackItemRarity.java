package com.is.mtc.pack;

import java.util.ArrayList;
import java.util.Random;

import com.is.mtc.MineTradingCards;
import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.util.Functions;
import com.is.mtc.util.Reference;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PackItemRarity extends PackItemBase {
	
	public static final String[] COMMON_PACK_CONTENT_DEFAULT = new String[] {
			"7x",
			"2x0:1:0:0:0",
			"1x0:0:1:0:0",
	};
	public static final String[] UNCOMMON_PACK_CONTENT_DEFAULT = new String[] {
			"6x1:0:0:0:0",
			"3x",
			"1x0:0:1:0:0",
	};
	public static final String[] RARE_PACK_CONTENT_DEFAULT = new String[] {
			"5x1:0:0:0:0",
			"3x0:1:0:0:0",
			"2x",
	};
	public static final String[] ANCIENT_PACK_CONTENT_DEFAULT = new String[] {
			"3x1:0:0:0:0",
			"3x0:1:0:0:0",
			"3x0:0:1:0:0",
			"1x",
	};
	public static final String[] LEGENDARY_PACK_CONTENT_DEFAULT = new String[] {
			"1x",
	};
	public static String[] COMMON_PACK_CONTENT = COMMON_PACK_CONTENT_DEFAULT;
	public static String[] UNCOMMON_PACK_CONTENT = UNCOMMON_PACK_CONTENT_DEFAULT;
	public static String[] RARE_PACK_CONTENT = RARE_PACK_CONTENT_DEFAULT;
	public static String[] ANCIENT_PACK_CONTENT = ANCIENT_PACK_CONTENT_DEFAULT;
	public static String[] LEGENDARY_PACK_CONTENT = LEGENDARY_PACK_CONTENT_DEFAULT;
	
	private static final String ITEM_PACK_UNLOC_PREFIX = "item_pack_";

	private int rarity;

	public PackItemRarity(int r) {
		setUnlocalizedName(ITEM_PACK_UNLOC_PREFIX + Rarity.toString(r).toLowerCase());
		setRegistryName(ITEM_PACK_UNLOC_PREFIX + Rarity.toString(r).toLowerCase());

		rarity = r;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));} // Don't do this on the client side
		
		ArrayList<String> created;
		Random random = world.rand;
		
		// Figure out how many of each card rarity to create
		
		int[] card_set_to_create = new int[] {0,0,0,0,0}; // Set of cards that will come out of the pack
		String[][] set_distribution_array = new String[][] {COMMON_PACK_CONTENT, UNCOMMON_PACK_CONTENT, RARE_PACK_CONTENT, ANCIENT_PACK_CONTENT, LEGENDARY_PACK_CONTENT};
		
		for (String entry : set_distribution_array[rarity])
		{
			try {
				double[] card_weighted_dist = new double[] {0,0,0,0,0}; // Distribution used when a card is randomized
				
				// Split entry
				String[] split_entry = entry.toLowerCase().trim().split("x");
				
				float count = MathHelper.clamp(Float.parseFloat(split_entry[0]), 0F, 64F);
				int drop_count_characteristic = (int) count;
				float drop_count_mantissa = count % 1;
				
				if (split_entry.length>1) {
					String[] distribution_split = split_entry[1].split(":");
					
					for (int i=0; i<distribution_split.length; i++) {
						card_weighted_dist[i]=Integer.parseInt(distribution_split[i].trim());
					}
				}
				else {
					card_weighted_dist[rarity]=1;
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
				Logs.errLog("Something went wrong parsing pack contents line: " + entry);
			}
		}
		
		// Actually create the cards
		
		created = new ArrayList<String>();
		
		for (int rarity : CardItem.CARD_RARITY_ARRAY) {
			createCards(rarity, card_set_to_create[rarity], created, world.rand);
		}
		
		if (created.size() > 0) {
			for (String cdwd : created) {
				spawnCard(player, world, cdwd);
			}
			player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() - 1);
		} else {
			Logs.chatMessage(player, "Zero cards were registered, thus zero cards were generated");
			Logs.errLog("Zero cards were registered, thus zero cards can be generated");
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	protected void createCards(int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry y times until...
				cStruct = Databank.generateACard(cardRarity, random);

				if (cStruct != null && !created.contains(cStruct.getCDWD())) { // ... card was not already created. Duplicate prevention
					created.add(cStruct.getCDWD());
					break;
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
		private int rarity;
		
		public ColorableIcon(int r) {
			rarity = r;
		}
		
		@Override
	    @SideOnly(Side.CLIENT)
		public int colorMultiplier(ItemStack stack, int layer) 
		{
			if (layer==0)
	    	{
		    	switch (this.rarity)
		    	{
		    	case Rarity.COMMON:
		    		return MineTradingCards.PACK_COLOR_COMMON;
		    	case Rarity.UNCOMMON:
		    		return MineTradingCards.PACK_COLOR_UNCOMMON;
		    	case Rarity.RARE:
		    		return MineTradingCards.PACK_COLOR_RARE;
		    	case Rarity.ANCIENT:
		    		return MineTradingCards.PACK_COLOR_ANCIENT;
		    	case Rarity.LEGENDARY:
		    		return MineTradingCards.PACK_COLOR_LEGENDARY;
		    	}
		    	return Reference.COLOR_BLUE;
	    	}
	    	
	        return -1;
		}
	}

}
