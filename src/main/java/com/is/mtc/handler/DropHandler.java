package com.is.mtc.handler;

import com.is.mtc.init.MTCItems;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Functions;
import com.is.mtc.util.Reference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DropHandler {

	public static final List<String> ENDER_DRAGON_DROPS_DEFAULT = Arrays.asList(
			"common_pack:7",
			"uncommon_pack:5",
			"rare_pack:3",
			"ancient_pack:2",
			"legendary_pack:1"
	);
	public static final List<String> BOSS_DROPS_DEFAULT = Arrays.asList(
			"common_pack:3",
			"uncommon_pack:3",
			"rare_pack:2",
			"ancient_pack:1",
			"legendary_pack:0.25"
	);
	public static boolean CAN_DROP_CARDS_ANIMAL = false;
	public static boolean CAN_DROP_CARDS_PLAYER = false;
	public static boolean CAN_DROP_CARDS_MOB = true;
	public static boolean CAN_DROP_PACKS_ANIMAL = false;
	public static boolean CAN_DROP_PACKS_PLAYER = false;
	public static boolean CAN_DROP_PACKS_MOB = true;
	public static boolean ONLY_ONE_DROP = false;
	// 1 chance out of DROP_RATE_X (test order)
	public static double CARD_DROP_RATE_COM = 16F;
	public static double CARD_DROP_RATE_UNC = 32F;
	public static double CARD_DROP_RATE_RAR = 48F;
	public static double CARD_DROP_RATE_ANC = 64F;
	public static double CARD_DROP_RATE_LEG = 256F;
	public static double PACK_DROP_RATE_COM = 16F;
	public static double PACK_DROP_RATE_UNC = 32F;
	public static double PACK_DROP_RATE_RAR = 48F;
	public static double PACK_DROP_RATE_ANC = 64F;
	public static double PACK_DROP_RATE_LEG = 256F;
	public static double PACK_DROP_RATE_STD = 40F;
	public static double PACK_DROP_RATE_EDT = 40F;
	public static double PACK_DROP_RATE_CUS = 40F;
	public static List<String> ENDER_DRAGON_DROPS = ENDER_DRAGON_DROPS_DEFAULT;
	public static List<String> BOSS_DROPS = BOSS_DROPS_DEFAULT;

	private static void addDrop(Item drop, LivingDropsEvent event, int count) {
		if (count == 0) {
			return;
		}

		ItemStack dropStack = new ItemStack(drop, count);
		event.getDrops().add(new ItemEntity(event.getEntity().level, event.getEntity().position().x(), event.getEntity().position().y(), event.getEntity().position().z(), dropStack));
	}

	private static void addDrop(Item drop, LivingDropsEvent event) {
		addDrop(drop, event, 1);
	}

	private static boolean testWhetherDrop(double rate, Random random) {
		if (rate == 0) {
			return false;
		}
		return random.nextFloat() * rate < 1F;
	}

	private static Hashtable<String, Integer> addToDropDict(Hashtable<String, Integer> dict, String key, int valueToAdd) {
		dict.put(key, dict.get(key) + valueToAdd);
		return dict;
	}

	@SubscribeEvent()
	public static void onEvent(LivingDropsEvent event) {

		// === HANDLE PACK DROPS === //

		// Ignore if drops are not enabled
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		if (!CAN_DROP_CARDS_MOB && !CAN_DROP_PACKS_MOB && event.getEntity() instanceof MobEntity && !event.getEntity().canChangeDimensions()) {
			return;
		}
		if (!CAN_DROP_CARDS_ANIMAL && !CAN_DROP_PACKS_ANIMAL && event.getEntity() instanceof AnimalEntity && !event.getEntity().canChangeDimensions()) {
			return;
		}
		if (!CAN_DROP_CARDS_PLAYER && !CAN_DROP_PACKS_PLAYER && event.getEntity() instanceof PlayerEntity && !event.getEntity().canChangeDimensions()) {
			return;
		}

		// Set flags to determine what can drop
		boolean willDropCards = false;
		boolean willDropPacks = false;
		if (event.getEntityLiving() instanceof MobEntity) {
			willDropCards = CAN_DROP_CARDS_MOB;
			willDropPacks = CAN_DROP_PACKS_MOB;
		} else if (event.getEntityLiving() instanceof AnimalEntity) {
			willDropCards = CAN_DROP_CARDS_ANIMAL;
			willDropPacks = CAN_DROP_PACKS_ANIMAL;
		} else if (event.getEntityLiving() instanceof PlayerEntity) {
			willDropCards = CAN_DROP_CARDS_PLAYER;
			willDropPacks = CAN_DROP_PACKS_PLAYER;
		}

		Random random = event.getEntityLiving().level.getRandom();

		// Initialize empty dictionary
		Hashtable<String, Integer> dropCountDict = new Hashtable<String, Integer>();
		dropCountDict.put(Reference.KEY_CARD_COM, 0);
		dropCountDict.put(Reference.KEY_CARD_UNC, 0);
		dropCountDict.put(Reference.KEY_CARD_RAR, 0);
		dropCountDict.put(Reference.KEY_CARD_ANC, 0);
		dropCountDict.put(Reference.KEY_CARD_LEG, 0);

		dropCountDict.put(Reference.KEY_PACK_COM, 0);
		dropCountDict.put(Reference.KEY_PACK_UNC, 0);
		dropCountDict.put(Reference.KEY_PACK_RAR, 0);
		dropCountDict.put(Reference.KEY_PACK_ANC, 0);
		dropCountDict.put(Reference.KEY_PACK_LEG, 0);
		dropCountDict.put(Reference.KEY_PACK_STD, 0);
		dropCountDict.put(Reference.KEY_PACK_EDT, 0);
		dropCountDict.put(Reference.KEY_PACK_CUS, 0);

		// Increment drops based on successful triggers
		Hashtable<String, Integer> randomizedDrops = new Hashtable<String, Integer>();
		randomizedDrops.put(Reference.KEY_CARD_COM, 0);
		randomizedDrops.put(Reference.KEY_CARD_UNC, 0);
		randomizedDrops.put(Reference.KEY_CARD_RAR, 0);
		randomizedDrops.put(Reference.KEY_CARD_ANC, 0);
		randomizedDrops.put(Reference.KEY_CARD_LEG, 0);

		randomizedDrops.put(Reference.KEY_PACK_COM, 0);
		randomizedDrops.put(Reference.KEY_PACK_UNC, 0);
		randomizedDrops.put(Reference.KEY_PACK_RAR, 0);
		randomizedDrops.put(Reference.KEY_PACK_ANC, 0);
		randomizedDrops.put(Reference.KEY_PACK_LEG, 0);
		randomizedDrops.put(Reference.KEY_PACK_STD, 0);
		randomizedDrops.put(Reference.KEY_PACK_EDT, 0);
		randomizedDrops.put(Reference.KEY_PACK_CUS, 0);
		int dropsGenerated = 0;

		if (willDropCards && testWhetherDrop(CARD_DROP_RATE_LEG, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_CARD_LEG, 1);
			dropsGenerated++;
		}
		if (willDropCards && testWhetherDrop(CARD_DROP_RATE_ANC, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_CARD_ANC, 1);
			dropsGenerated++;
		}
		if (willDropCards && testWhetherDrop(CARD_DROP_RATE_RAR, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_CARD_RAR, 1);
			dropsGenerated++;
		}
		if (willDropCards && testWhetherDrop(CARD_DROP_RATE_UNC, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_CARD_UNC, 1);
			dropsGenerated++;
		}
		if (willDropCards && testWhetherDrop(CARD_DROP_RATE_COM, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_CARD_COM, 1);
			dropsGenerated++;
		}

		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_LEG, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_LEG, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_ANC, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_ANC, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_CUS, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_CUS, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_EDT, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_EDT, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_STD, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_STD, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_RAR, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_RAR, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_UNC, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_UNC, 1);
			dropsGenerated++;
		}
		if (willDropPacks && testWhetherDrop(PACK_DROP_RATE_COM, random)) {
			addToDropDict(randomizedDrops, Reference.KEY_PACK_COM, 1);
			dropsGenerated++;
		}

		if (dropsGenerated > 0) {
			if (ONLY_ONE_DROP) {
				// Select only one from the above set
				String dropSelected = (String) Functions.weightedRandom(
						new String[]{
								Reference.KEY_CARD_LEG,
								Reference.KEY_CARD_ANC,
								Reference.KEY_CARD_RAR,
								Reference.KEY_CARD_UNC,
								Reference.KEY_CARD_COM,

								Reference.KEY_PACK_LEG,
								Reference.KEY_PACK_ANC,
								Reference.KEY_PACK_CUS,
								Reference.KEY_PACK_EDT,
								Reference.KEY_PACK_STD,
								Reference.KEY_PACK_RAR,
								Reference.KEY_PACK_UNC,
								Reference.KEY_PACK_COM
						},
						new double[]{
								randomizedDrops.get(Reference.KEY_CARD_LEG) * (CARD_DROP_RATE_LEG > 0 ? 1D / CARD_DROP_RATE_LEG : 0),
								randomizedDrops.get(Reference.KEY_CARD_ANC) * (CARD_DROP_RATE_ANC > 0 ? 1D / CARD_DROP_RATE_ANC : 0),
								randomizedDrops.get(Reference.KEY_CARD_RAR) * (CARD_DROP_RATE_RAR > 0 ? 1D / CARD_DROP_RATE_RAR : 0),
								randomizedDrops.get(Reference.KEY_CARD_UNC) * (CARD_DROP_RATE_UNC > 0 ? 1D / CARD_DROP_RATE_UNC : 0),
								randomizedDrops.get(Reference.KEY_CARD_COM) * (CARD_DROP_RATE_COM > 0 ? 1D / CARD_DROP_RATE_COM : 0),

								randomizedDrops.get(Reference.KEY_PACK_LEG) * (PACK_DROP_RATE_LEG > 0 ? 1D / PACK_DROP_RATE_LEG : 0),
								randomizedDrops.get(Reference.KEY_PACK_ANC) * (PACK_DROP_RATE_ANC > 0 ? 1D / PACK_DROP_RATE_ANC : 0),
								randomizedDrops.get(Reference.KEY_PACK_CUS) * (PACK_DROP_RATE_CUS > 0 ? 1D / PACK_DROP_RATE_CUS : 0),
								randomizedDrops.get(Reference.KEY_PACK_EDT) * (PACK_DROP_RATE_EDT > 0 ? 1D / PACK_DROP_RATE_EDT : 0),
								randomizedDrops.get(Reference.KEY_PACK_STD) * (PACK_DROP_RATE_STD > 0 ? 1D / PACK_DROP_RATE_STD : 0),
								randomizedDrops.get(Reference.KEY_PACK_RAR) * (PACK_DROP_RATE_RAR > 0 ? 1D / PACK_DROP_RATE_RAR : 0),
								randomizedDrops.get(Reference.KEY_PACK_UNC) * (PACK_DROP_RATE_UNC > 0 ? 1D / PACK_DROP_RATE_UNC : 0),
								randomizedDrops.get(Reference.KEY_PACK_COM) * (PACK_DROP_RATE_COM > 0 ? 1D / PACK_DROP_RATE_COM : 0)
						},
						random
				);

				if (dropSelected != null) {
					addToDropDict(dropCountDict, dropSelected, 1);
				}
			} else {
				// Add every triggered drop
				addToDropDict(dropCountDict, Reference.KEY_CARD_LEG, randomizedDrops.get(Reference.KEY_CARD_LEG));
				addToDropDict(dropCountDict, Reference.KEY_CARD_ANC, randomizedDrops.get(Reference.KEY_CARD_ANC));
				addToDropDict(dropCountDict, Reference.KEY_CARD_RAR, randomizedDrops.get(Reference.KEY_CARD_RAR));
				addToDropDict(dropCountDict, Reference.KEY_CARD_UNC, randomizedDrops.get(Reference.KEY_CARD_UNC));
				addToDropDict(dropCountDict, Reference.KEY_CARD_COM, randomizedDrops.get(Reference.KEY_CARD_COM));

				addToDropDict(dropCountDict, Reference.KEY_PACK_LEG, randomizedDrops.get(Reference.KEY_PACK_LEG));
				addToDropDict(dropCountDict, Reference.KEY_PACK_ANC, randomizedDrops.get(Reference.KEY_PACK_ANC));
				addToDropDict(dropCountDict, Reference.KEY_PACK_CUS, randomizedDrops.get(Reference.KEY_PACK_CUS));
				addToDropDict(dropCountDict, Reference.KEY_PACK_EDT, randomizedDrops.get(Reference.KEY_PACK_EDT));
				addToDropDict(dropCountDict, Reference.KEY_PACK_STD, randomizedDrops.get(Reference.KEY_PACK_STD));
				addToDropDict(dropCountDict, Reference.KEY_PACK_RAR, randomizedDrops.get(Reference.KEY_PACK_RAR));
				addToDropDict(dropCountDict, Reference.KEY_PACK_UNC, randomizedDrops.get(Reference.KEY_PACK_UNC));
				addToDropDict(dropCountDict, Reference.KEY_PACK_COM, randomizedDrops.get(Reference.KEY_PACK_COM));
			}
		}

		// Add set drops to bosses
		if (event.getEntityLiving() instanceof EnderDragonEntity) { // 18 packs
			for (String line : ENDER_DRAGON_DROPS) {
				try {
					String[] split_config_entry = line.toLowerCase().trim().split(":");

					float drop_count = MathHelper.clamp(Float.parseFloat(split_config_entry[1].trim()), 0F, 64F);
					int drop_count_characteristic = (int) drop_count;
					float drop_count_mantissa = drop_count % 1;

					addToDropDict(dropCountDict, split_config_entry[0].trim(), drop_count_characteristic + (random.nextFloat() < drop_count_mantissa ? 1 : 0));
				} catch (Exception e) {
					Logs.errLog("Malformed config entry: " + line);
				}
			}
		} else if (!event.getEntityLiving().canChangeDimensions()) {
			for (String line : BOSS_DROPS) {
				try {
					String[] split_config_entry = line.toLowerCase().trim().split(":");

					float drop_count = MathHelper.clamp(Float.parseFloat(split_config_entry[1].trim()), 0F, 64F);
					int drop_count_characteristic = (int) drop_count;
					float drop_count_mantissa = drop_count % 1;

					addToDropDict(dropCountDict, split_config_entry[0].trim(), drop_count_characteristic + (random.nextFloat() < drop_count_mantissa ? 1 : 0));
				} catch (Exception e) {
					Logs.errLog("Malformed config entry: " + line);
				}
			}
		}

		// Add all the drops
		addDrop(MTCItems.cardLegendary.get(), event, dropCountDict.get(Reference.KEY_CARD_LEG));
		addDrop(MTCItems.cardAncient.get(), event, dropCountDict.get(Reference.KEY_CARD_ANC));
		addDrop(MTCItems.cardRare.get(), event, dropCountDict.get(Reference.KEY_CARD_RAR));
		addDrop(MTCItems.cardUncommon.get(), event, dropCountDict.get(Reference.KEY_CARD_UNC));
		addDrop(MTCItems.cardCommon.get(), event, dropCountDict.get(Reference.KEY_CARD_COM));

		addDrop(MTCItems.packLegendary.get(), event, dropCountDict.get(Reference.KEY_PACK_LEG));
		addDrop(MTCItems.packAncient.get(), event, dropCountDict.get(Reference.KEY_PACK_ANC));
		addDrop(MTCItems.packCustom.get(), event, dropCountDict.get(Reference.KEY_PACK_CUS));
		addDrop(MTCItems.packEdition.get(), event, dropCountDict.get(Reference.KEY_PACK_EDT));
		addDrop(MTCItems.packStandard.get(), event, dropCountDict.get(Reference.KEY_PACK_STD));
		addDrop(MTCItems.packRare.get(), event, dropCountDict.get(Reference.KEY_PACK_RAR));
		addDrop(MTCItems.packUncommon.get(), event, dropCountDict.get(Reference.KEY_PACK_UNC));
		addDrop(MTCItems.packCommon.get(), event, dropCountDict.get(Reference.KEY_PACK_COM));
	}
}
