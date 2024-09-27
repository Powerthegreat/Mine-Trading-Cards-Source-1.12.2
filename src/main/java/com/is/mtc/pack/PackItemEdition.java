package com.is.mtc.pack;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackItemEdition extends PackItemBase {

	private static final String EDITION_ID_KEY = "edition_id";
	public static List<String> EDITION_PACK_CONTENT = PackItemStandard.STANDARD_PACK_CONTENT_DEFAULT;

	public PackItemEdition(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity player, int par_4, boolean par_5) {
		Random r = world.getRandom();

		stack.getOrCreateTag();

		if (!stack.getTag().contains(EDITION_ID_KEY) && Databank.getEditionsCount() > 0) {
			int i = r.nextInt(Databank.getEditionsCount());

			CompoundNBT nbtTag = stack.getTag();
			nbtTag.putString(EDITION_ID_KEY, Databank.getEditionWithNumeralId(i).getId());
			stack.setTag(nbtTag);
		}
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		String eid = stack.getTag() != null && stack.getTag().contains(EDITION_ID_KEY) ? stack.getTag().getString(EDITION_ID_KEY) : null;
		EditionStructure eStruct = eid != null ? Databank.getEditionWithId(eid) : null;

		if (eid != null) {
			if (eStruct == null) // Pack was created earlier, but edition was removed in the mean time
				return new StringTextComponent("edition_pack_" + eid);
			else
				return new StringTextComponent(eStruct.getName() + " Pack");
		} else
			return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> infos, ITooltipFlag flag) {
		EditionStructure eStruct;
		CompoundNBT nbt;

		if (stack.getTag() == null || !stack.getTag().contains(EDITION_ID_KEY))
			return;

		nbt = stack.getTag();
		eStruct = Databank.getEditionWithId(stack.getTag().getString(EDITION_ID_KEY));

		if (eStruct == null) {
			infos.add(new StringTextComponent(TextFormatting.RED + "/!\\ Missing client-side edition"));
			infos.add(new StringTextComponent(TextFormatting.GRAY + nbt.getString(EDITION_ID_KEY)));
			return;
		}

		infos.add(new StringTextComponent("Contains cards from the edition '" + eStruct.getName() + "'"));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (world.isClientSide) {
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		} // Don't do this on the client side

		if (player.getItemInHand(hand).getTag() == null || !player.getItemInHand(hand).getTag().contains(EDITION_ID_KEY)) {
			Logs.errLog("PackItemEdition: Missing NBT or NBTTag");
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}

		ArrayList<String> created;
		Random random = world.getRandom();
		CompoundNBT nbt;
		nbt = player.getItemInHand(hand).getTag();
		EditionStructure eStruct;
		eStruct = Databank.getEditionWithId(player.getItemInHand(hand).getTag().getString(EDITION_ID_KEY));

		if (eStruct == null) {
			Logs.chatMessage(player, "The edition this pack is linked to does not exist, thus zero cards were generated");
			Logs.errLog("PackItemEdition: Edition is missing");
			return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));
		}

		// Figure out how many of each card rarity to create

		int[] card_set_to_create = new int[]{0, 0, 0, 0, 0}; // Set of cards that will come out of the pack

		for (String entry : EDITION_PACK_CONTENT) {
			try {
				double[] card_weighted_dist = new double[]{0, 0, 0, 0, 0}; // Distribution used when a card is randomized

				// Split entry
				String[] split_entry = entry.toLowerCase().trim().split("x");

				float count = MathHelper.clamp(Float.parseFloat(split_entry[0]), 0F, 64F);
				int drop_count_characteristic = (int) count;
				float drop_count_mantissa = count % 1;

				String[] distribution_split = split_entry[1].split(":");

				for (int i = 0; i < distribution_split.length; i++) {
					card_weighted_dist[i] = Integer.parseInt(distribution_split[i].trim());
				}

				// Repeat for the number of cards prescribed
				for (int i = 0; i < drop_count_characteristic + (random.nextFloat() < drop_count_mantissa ? 1 : 0); i++) {
					Object chosen_rarity = Functions.weightedRandom(CardItem.CARD_RARITY_ARRAY, card_weighted_dist, random);

					if (chosen_rarity != null) {
						card_set_to_create[(Integer) chosen_rarity]++;
					}
				}
			} catch (Exception e) {
				Logs.errLog("Something went wrong parsing edition_pack_contents line: " + entry);
			}
		}

		// Actually create the cards

		created = new ArrayList<String>();

		for (int rarity : CardItem.CARD_RARITY_ARRAY) {
			createCards(eStruct.getId(), rarity, card_set_to_create[rarity], created, world.getRandom());
		}

		if (created.size() > 0) {
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

	private void createCards(String edition, int cardRarity, int count, ArrayList<String> created, Random random) {

		for (int x = 0; x < count; ++x) { // Generate x cards
			CardStructure cStruct = null;

			for (int y = 0; y < RETRY; ++y) { // Retry x times until...
				cStruct = Databank.generateACardFromEdition(cardRarity, edition, new Random()); // Using new Random() because world random can cause issues generating cards

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
				String eid = stack.getTag() != null && stack.getTag().contains(EDITION_ID_KEY) ? stack.getTag().getString(EDITION_ID_KEY) : null;
				return eid != null && Databank.getEditionWithId(eid) != null ? Databank.getEditionWithId(eid).getColor() : Reference.COLOR_GRAY;
			}

			return -1;
		}
	}
}
