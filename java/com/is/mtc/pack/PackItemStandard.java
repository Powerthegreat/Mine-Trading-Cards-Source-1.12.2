package com.is.mtc.pack;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;

public class PackItemStandard extends PackItemBase{

	private static final int cCount[] = { 7, 2, 1 };
	private static final int rWeight[] = { 25, 29, 30 };
	private static final int rtWeight = rWeight[2];

	public PackItemStandard() {
		setUnlocalizedName("item_pack_standard");
		setRegistryName("item_pack_standard");
		//setTextureName(MineTradingCards.MODID + ":item_pack_standard");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ArrayList<String> created;
		Random r;
		int i;

		if (world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

		created = new ArrayList<String>();
		createCards(Rarity.COMMON, cCount[Rarity.COMMON], created);
		createCards(Rarity.UNCOMMON, cCount[Rarity.UNCOMMON], created);

		r = new Random();
		i = r.nextInt(rtWeight);
		if (i < rWeight[0])
			createCards(Rarity.RARE, cCount[Rarity.RARE], created);
		else if (i < rWeight[1])
			createCards(Rarity.ANCIENT, cCount[Rarity.RARE], created);
		else if (i < rWeight[2])
			createCards(Rarity.LEGENDARY, cCount[Rarity.RARE], created);

		if (created.size() > 0) {
			for (String cdwd : created) {
				spawnCard(player, world, cdwd);
			}
			player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() - 1);
		}
		else {
			Logs.chatMessage(player, "Zero cards were registered, thus zero cards were generated");
			Logs.errLog("Zero cards were registered, thus zero cards can be generated");
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
