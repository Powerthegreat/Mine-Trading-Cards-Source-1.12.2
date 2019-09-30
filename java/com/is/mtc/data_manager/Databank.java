package com.is.mtc.data_manager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;

public class Databank {

	private static Map<String, EditionStructure> editions_by_id;
	private static Map<String, EditionStructure> editions_by_name;
	private static Map<Integer, EditionStructure> editions_by_numeral_id;

	//-

	/// NOTE LinkedHashMap to keep the precise order
	public static void setup() {
		editions_by_id = new LinkedHashMap<String, EditionStructure>();
		editions_by_name = new LinkedHashMap<String, EditionStructure>();
		editions_by_numeral_id = new LinkedHashMap<Integer, EditionStructure>();

		cards_by_cdwd = new LinkedHashMap<String, CardStructure>();

		cards_by_wrarity = new LinkedHashMap<Integer, Map<Integer,CardStructure>>();
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

		cards_by_wraed = new LinkedHashMap<String, Map<Integer,Map<Integer,CardStructure>>>();
		wraed = new LinkedHashMap<String, Map<Integer,Integer>>();
	}

	//-

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

	public static int getEditionsCount() { return editions_by_id.size(); }

	public static EditionStructure getEditionWithId(String id) {
		return editions_by_id.containsKey(id) ? editions_by_id.get(id) : null;
	}

	public static EditionStructure getEditionWithName(String name) {
		return editions_by_name.containsKey(name) ? editions_by_name.get(name) : null;
	}

	public static EditionStructure getEditionWithNumeralId(int nid) {
		return editions_by_numeral_id.containsKey(nid) ? editions_by_numeral_id.get(nid) : null;
	}

	//-

	private static Map<String, CardStructure> cards_by_cdwd;

	private static Map<Integer, Map<Integer, CardStructure>> cards_by_wrarity;
	private static Map<Integer, Integer> wrarity_tw;

	private static Map<String, Map<Integer, Map<Integer, CardStructure>>> cards_by_wraed;
	// String for editions, integer for rarity, integer for total weight, cs !
	private static Map<String, Map<Integer, Integer>> wraed;
	// String for edition, integer for rarity, integer for total weight of this pool

	private static int wedition_tw; // Weighted edition total weight

	public static boolean registerACard(CardStructure cStruct)
	{
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

		if (cStruct.getWeight() > 0) { // Can be dropped ? Then add it to the drop pools

			cards_by_wrarity.get(cStruct.getRarity()).put(wrarity_tw.get(cStruct.getRarity()) + cStruct.getWeight(), cStruct);
			wrarity_tw.put(cStruct.getRarity(), wrarity_tw.get(cStruct.getRarity()) + cStruct.getWeight());

			cards_by_wraed.get(cStruct.getEdition()).get(cStruct.getRarity()).put(wraed.get(cStruct.getEdition()).get(cStruct.getRarity()) + cStruct.getWeight(), cStruct);
			wraed.get(cStruct.getEdition()).put(cStruct.getRarity(), wraed.get(cStruct.getEdition()).get(cStruct.getRarity()) + cStruct.getWeight());
		} else {
			Logs.errLog("Warning: Card does not have a strictely positive weight. Card will be usable but not droppable");
			Logs.devLog("Card registered: " + cStruct.toString());

			return false;
		}

		Logs.devLog("Card registered: " + cStruct.toString());

		return true;
	}

	public static CardStructure getCardByCDWD(String cdwd) {
		return cards_by_cdwd.getOrDefault(cdwd, null);
	}

	public static CardStructure generateACard(int rarity) {
		Random r;
		int i;

		if (rarity <= Rarity.UNSET || rarity >= Rarity.RCOUNT)
			return null;

		if (cards_by_wrarity.get(rarity).size() <= 0)
			return null;

		r = new Random();
		i = r.nextInt(wrarity_tw.get(rarity));

		for (Map.Entry<Integer, CardStructure> entry : cards_by_wrarity.get(rarity).entrySet()) {
			if (i < entry.getKey())
				return entry.getValue();
		}

		Logs.errLog("Error: In 'generateACard': {i:" + i + " wrarity_tw:" + wrarity_tw.get(rarity) + "}");
		return null;
	}

	public static CardStructure generatedACardFromEdition(int rarity, String edition_id) {
		Random r;
		int i;

		if (rarity <= Rarity.UNSET || rarity >= Rarity.RCOUNT)
			return null;

		if (!editions_by_id.containsKey(edition_id))
			return null;

		if (cards_by_wraed.get(edition_id).get(rarity).size() <= 0) // No cards from the specified rarity in this edition
			return null;

		r = new Random();
		i = r.nextInt(wraed.get(edition_id).get(rarity));

		for (Map.Entry<Integer, CardStructure> entry : cards_by_wraed.get(edition_id).get(rarity).entrySet()) {
			if (i < entry.getKey())
				return entry.getValue();
		}

		Logs.errLog("Error: In 'generatedACardFromEdition': {i:" + i + " wraed:" + wraed.get(edition_id).get(rarity) + "}");
		return null;
	}
}
