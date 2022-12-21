package com.is.mtc.pack;

import java.util.ArrayList;

import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PackItemStandard extends PackItemBase {

	private static final int[] cCount = {7, 2, 1};
	private static final int[] rWeight = {25, 29, 30};
	private static final int rtWeight = rWeight[2];

	public PackItemStandard() {
		setUnlocalizedName("item_pack_standard");
		setRegistryName("item_pack_standard");
		//setTextureName(MineTradingCards.MODID + ":item_pack_standard");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ArrayList<String> created;
		int i;

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		created = new ArrayList<String>();
		createCards(Rarity.COMMON, cCount[Rarity.COMMON], created, world.rand);
		createCards(Rarity.UNCOMMON, cCount[Rarity.UNCOMMON], created, world.rand);

		i = world.rand.nextInt(rtWeight);
		if (i < rWeight[0]) {
			createCards(Rarity.RARE, cCount[Rarity.RARE], created, world.rand);
		}
		else if (i < rWeight[1]) {
			createCards(Rarity.ANCIENT, cCount[Rarity.RARE], created, world.rand);
		}
		else if (i < rWeight[2]) {
			createCards(Rarity.LEGENDARY, cCount[Rarity.RARE], created, world.rand);
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
	    		return MineTradingCards.PACK_COLOR_STANDARD;
	    	}
	    	
	        return -1;
		}
	}
}
