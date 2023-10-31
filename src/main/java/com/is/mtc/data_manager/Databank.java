package com.is.mtc.data_manager;

import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class Databank {

	private static Map<String, EditionStructure> editions_by_id;
	private static Map<String, EditionStructure> editions_by_name;
	private static Map<Integer, EditionStructure> editions_by_color;
	private static Map<Integer, EditionStructure> editions_by_numeral_id;

	private static Map<String, CardStructure> cards_by_cdwd;

	private static Map<Integer, Map<Integer, CardStructure>> cards_by_wrarity;
	private static Map<Integer, Integer> wrarity_tw;

	private static Map<String, Map<Integer, Map<Integer, CardStructure>>> cards_by_wraed;
	// String for editions, integer for rarity, integer for total weight, cs !
	private static Map<String, Map<Integer, Integer>> wraed;
	// String for edition, integer for rarity, integer for total weight of this pool

	private static Map<String, Map<Integer, Map<Integer, CardStructure>>> cards_by_wracat;
	// String for category, integer for rarity, integer for total weight, cs !
	private static Map<String, Map<Integer, Integer>> wracat;
	// String for edition, integer for rarity, integer for total weight of this pool

	private static int wedition_tw; // Weighted edition total weight

	private static Map<String, CustomPackStructure> custom_packs_by_id;
	private static Map<String, CustomPackStructure> custom_packs_by_name;
	private static Map<Integer, CustomPackStructure> custom_packs_by_color;
	private static Map<Integer, CustomPackStructure> custom_packs_by_numeral_id;

	/// NOTE LinkedHashMap to keep the precise order
	public static void setup() {
		editions_by_id = new LinkedHashMap<String, EditionStructure>();
		editions_by_name = new LinkedHashMap<String, EditionStructure>();
		editions_by_color = new LinkedHashMap<Integer, EditionStructure>();
		editions_by_numeral_id = new LinkedHashMap<Integer, EditionStructure>();

		custom_packs_by_id = new LinkedHashMap<String, CustomPackStructure>();
		custom_packs_by_name = new LinkedHashMap<String, CustomPackStructure>();
		custom_packs_by_color = new LinkedHashMap<Integer, CustomPackStructure>();
		custom_packs_by_numeral_id = new LinkedHashMap<Integer, CustomPackStructure>();

		cards_by_cdwd = new LinkedHashMap<String, CardStructure>();

		cards_by_wrarity = new LinkedHashMap<Integer, Map<Integer, CardStructure>>();
		cards_by_wrarity.put(Rarity.COMMON, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wrarity.put(Rarity.UNCOMMON, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wrarity.put(Rarity.RARE, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wrarity.put(Rarity.ANCIENT, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wrarity.put(Rarity.LEGENDARY, new LinkedHashMap<Integer, CardStructure>());

		wrarity_tw = new LinkedHashMap<Integer, Integer>();
		wrarity_tw.put(Rarity.COMMON, 0);
		wrarity_tw.put(Rarity.UNCOMMON, 0);
		wrarity_tw.put(Rarity.RARE, 0);
		wrarity_tw.put(Rarity.ANCIENT, 0);
		wrarity_tw.put(Rarity.LEGENDARY, 0);

		cards_by_wraed = new LinkedHashMap<String, Map<Integer, Map<Integer, CardStructure>>>();
		cards_by_wracat = new LinkedHashMap<String, Map<Integer, Map<Integer, CardStructure>>>();
		wraed = new LinkedHashMap<String, Map<Integer, Integer>>();
		wracat = new LinkedHashMap<String, Map<Integer, Integer>>();
	}

	public static boolean registerAnEdition(EditionStructure eStruct) {
		if (!eStruct.isValid()) {
			Logs.errLog("Edition is invalid (Invalid/missing ID or name)");
			return false;
		}

		if (editions_by_id.containsKey(eStruct.getId())) {
			Logs.errLog("Edition ID is already used");
			return false;
		}

		// Standard pools
		editions_by_id.put(eStruct.getId(), eStruct);
		editions_by_name.put(eStruct.getName(), eStruct);
		editions_by_color.put(eStruct.getColor(), eStruct);

		eStruct.eNI = editions_by_numeral_id.size();
		editions_by_numeral_id.put(eStruct.eNI, eStruct);

		// For wraed
		cards_by_wraed.put(eStruct.getId(), new LinkedHashMap<Integer, Map<Integer, CardStructure>>());
		cards_by_wraed.get(eStruct.getId()).put(Rarity.COMMON, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wraed.get(eStruct.getId()).put(Rarity.UNCOMMON, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wraed.get(eStruct.getId()).put(Rarity.RARE, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wraed.get(eStruct.getId()).put(Rarity.ANCIENT, new LinkedHashMap<Integer, CardStructure>());
		cards_by_wraed.get(eStruct.getId()).put(Rarity.LEGENDARY, new LinkedHashMap<Integer, CardStructure>());

		wraed.put(eStruct.getId(), new LinkedHashMap<Integer, Integer>());
		wraed.get(eStruct.getId()).put(Rarity.COMMON, 0);
		wraed.get(eStruct.getId()).put(Rarity.UNCOMMON, 0);
		wraed.get(eStruct.getId()).put(Rarity.RARE, 0);
		wraed.get(eStruct.getId()).put(Rarity.ANCIENT, 0);
		wraed.get(eStruct.getId()).put(Rarity.LEGENDARY, 0);

		Logs.devLog("Edition registered: " + eStruct.toString());

		return true;
	}

	public static int getEditionsCount() {
		return editions_by_id.size();
	}

	public static EditionStructure getEditionWithId(String id) {
		return editions_by_id.getOrDefault(id, null);
	}

	public static EditionStructure getEditionWithName(String name) {
		return editions_by_name.getOrDefault(name, null);
	}

	public static EditionStructure getEditionWithColor(int color) {
		return editions_by_color.getOrDefault(color, null);
	}

	public static EditionStructure getEditionWithNumeralId(int nid) {
		return editions_by_numeral_id.getOrDefault(nid, null);
	}

	public static boolean registerACustomPack(CustomPackStructure cpStruct) {
		if (!cpStruct.isValid()) {
			Logs.errLog("Custom pack is invalid (Invalid/missing ID or name)");
			return false;
		}

		if (custom_packs_by_id.containsKey(cpStruct.getId())) {
			Logs.errLog("Custom pack ID is already used");
			return false;
		}

		// Standard pools
		custom_packs_by_id.put(cpStruct.getId(), cpStruct);
		custom_packs_by_name.put(cpStruct.getName(), cpStruct);
		custom_packs_by_color.put(cpStruct.getColor(), cpStruct);

		cpStruct.customPackNumeralID = custom_packs_by_numeral_id.size();
		custom_packs_by_numeral_id.put(cpStruct.customPackNumeralID, cpStruct);

		Logs.devLog("Custom pack registered: " + cpStruct.toString());

		return true;
	}

	public static int getCustomPacksCount() {
		return custom_packs_by_id.size();
	}

	public static CustomPackStructure getCustomPackWithId(String id) {
		return custom_packs_by_id.getOrDefault(id, null);
	}

	public static CustomPackStructure getCustomPackWithName(String name) {
		return custom_packs_by_name.getOrDefault(name, null);
	}

	public static CustomPackStructure getCustomPackWithNumeralId(int nid) {
		return custom_packs_by_numeral_id.getOrDefault(nid, null);
	}

	public static CustomPackStructure getCustomPackWithColor(int nid) {
		return custom_packs_by_color.getOrDefault(nid, null);
	}

	public static boolean registerACard(CardStructure cStruct) {
		if (!cStruct.isValid()) {
			Logs.errLog("Card is invalid (Invalid/missing ID, name or rarity)");
			return false;
		}

		if (!editions_by_id.containsKey(cStruct.getEdition())) { // Is edition existing
			Logs.errLog("Specified edition for card is unknown");
			return false;
		}

		if (cards_by_cdwd.containsKey(cStruct.getCDWD())) { // Does this edition has a card with the same CDWD already registered
			Logs.errLog("Card ID is already used for the edition '" + cStruct.getEdition() + "'");
			return false;
		}

		cards_by_cdwd.put(cStruct.getCDWD(), cStruct);
		editions_by_id.get(cStruct.getEdition()).cCount += 1;
		cStruct.numeral = editions_by_id.get(cStruct.getEdition()).cCount;

		if (!(cStruct.getCategory().isEmpty() || cards_by_wracat.containsKey(cStruct.getCategory()))) {
			cards_by_wracat.put(cStruct.getCategory(), new LinkedHashMap<Integer, Map<Integer, CardStructure>>());
			cards_by_wracat.get(cStruct.getCategory()).put(Rarity.COMMON, new LinkedHashMap<Integer, CardStructure>());
			cards_by_wracat.get(cStruct.getCategory()).put(Rarity.UNCOMMON, new LinkedHashMap<Integer, CardStructure>());
			cards_by_wracat.get(cStruct.getCategory()).put(Rarity.RARE, new LinkedHashMap<Integer, CardStructure>());
			cards_by_wracat.get(cStruct.getCategory()).put(Rarity.ANCIENT, new LinkedHashMap<Integer, CardStructure>());
			cards_by_wracat.get(cStruct.getCategory()).put(Rarity.LEGENDARY, new LinkedHashMap<Integer, CardStructure>());

			wracat.put(cStruct.getCategory(), new LinkedHashMap<Integer, Integer>());
			wracat.get(cStruct.getCategory()).put(Rarity.COMMON, 0);
			wracat.get(cStruct.getCategory()).put(Rarity.UNCOMMON, 0);
			wracat.get(cStruct.getCategory()).put(Rarity.RARE, 0);
			wracat.get(cStruct.getCategory()).put(Rarity.ANCIENT, 0);
			wracat.get(cStruct.getCategory()).put(Rarity.LEGENDARY, 0);
		}

		if (cStruct.getWeight() > 0) { // Can be dropped ? Then add it to the drop pools

			cards_by_wrarity.get(cStruct.getRarity()).put(wrarity_tw.get(cStruct.getRarity()) + cStruct.getWeight(), cStruct);
			wrarity_tw.put(cStruct.getRarity(), wrarity_tw.get(cStruct.getRarity()) + cStruct.getWeight());

			cards_by_wraed.get(cStruct.getEdition()).get(cStruct.getRarity()).put(wraed.get(cStruct.getEdition()).get(cStruct.getRarity()) + cStruct.getWeight(), cStruct);
			wraed.get(cStruct.getEdition()).put(cStruct.getRarity(), wraed.get(cStruct.getEdition()).get(cStruct.getRarity()) + cStruct.getWeight());

			if (!(cStruct.getCategory().isEmpty())) {
				cards_by_wracat.get(cStruct.getCategory()).get(cStruct.getRarity()).put(wracat.get(cStruct.getCategory()).get(cStruct.getRarity()) + cStruct.getWeight(), cStruct);
				wracat.get(cStruct.getCategory()).put(cStruct.getRarity(), wracat.get(cStruct.getCategory()).get(cStruct.getRarity()) + cStruct.getWeight());
			}
		} else {
			Logs.errLog("Warning: Card does not have a strictly positive weight. Card will be usable but not droppable");
			Logs.devLog("Card registered: " + cStruct.toString());

			return false;
		}

		Logs.devLog("Card registered: " + cStruct.toString());

		return true;
	}

	public static CardStructure getCardByCDWD(String cdwd) {
		return cards_by_cdwd.getOrDefault(cdwd, null);
	}

	public static CardStructure generateACard(int rarity, Random random) {
		int i;

		if (rarity <= Rarity.UNSET || rarity >= Rarity.RCOUNT)
			return null;

		if (cards_by_wrarity.get(rarity).size() <= 0)
			return null;

		i = random.nextInt(wrarity_tw.get(rarity));

		for (Map.Entry<Integer, CardStructure> entry : cards_by_wrarity.get(rarity).entrySet()) {
			if (i < entry.getKey())
				return entry.getValue();
		}

		Logs.errLog("Error: In 'generateACard': {i:" + i + " wrarity_tw:" + wrarity_tw.get(rarity) + "}");
		return null;
	}

	public static CardStructure generatedACardFromEdition(int rarity, String edition_id, Random random) {
		int i;

		if (rarity <= Rarity.UNSET || rarity >= Rarity.RCOUNT)
			return null;

		if (!editions_by_id.containsKey(edition_id))
			return null;

		if (cards_by_wraed.get(edition_id).get(rarity).size() <= 0) // No cards from the specified rarity in this edition
			return null;

		i = random.nextInt(wraed.get(edition_id).get(rarity));

		for (Map.Entry<Integer, CardStructure> entry : cards_by_wraed.get(edition_id).get(rarity).entrySet()) {
			if (i < entry.getKey())
				return entry.getValue();
		}

		Logs.errLog("Error: In 'generatedACardFromEdition': {i:" + i + " wraed:" + wraed.get(edition_id).get(rarity) + "}");
		return null;
	}

	public static CardStructure generatedACardFromCategory(int rarity, String category, Random random) {
		int i;

		if (rarity <= Rarity.UNSET || rarity >= Rarity.RCOUNT)
			return null;

		if (!cards_by_wracat.containsKey(category))
			return null;

		if (cards_by_wracat.get(category).get(rarity).size() <= 0) // No cards from the specified rarity in this edition
			return null;

		i = random.nextInt(wracat.get(category).get(rarity));

		for (Map.Entry<Integer, CardStructure> entry : cards_by_wracat.get(category).get(rarity).entrySet()) {
			if (i < entry.getKey())
				return entry.getValue();
		}

		Logs.errLog("Error: In 'generatedACardFromCategory': {i:" + i + " wracat:" + wracat.get(category).get(rarity) + "}");
		return null;
	}
}
