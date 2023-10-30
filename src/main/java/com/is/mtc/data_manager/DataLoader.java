package com.is.mtc.data_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;


public class DataLoader {

	private static FilenameFilter createFilenameFilter(final String extension) {
		FilenameFilter fnf = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		};

		return fnf;
	}

	public static void readAndLoad() {
		File resourcesFolder = new File(MineTradingCards.getDataDir());
		List<File> editions_folders = new ArrayList<>();
		List<File> cards_folders = new ArrayList<>();
		List<File> custom_packs_folders = new ArrayList<>();

		if (resourcesFolder.exists() && resourcesFolder.isDirectory()) {
			for (File resourcePack : resourcesFolder.listFiles()) { // Iterate through resource packs for valid MTC folders
				if (resourcePack.exists() && resourcePack.isDirectory()) {
					File editions_folder = new File(resourcePack.getPath() + "/assets/is_mtc/mtc/editions/");
					File cards_folder = new File(resourcePack.getPath() + "/assets/is_mtc/mtc/cards/");
					File custom_packs_folder = new File(resourcePack.getPath() + "/assets/is_mtc/mtc/packs/");

					if (editions_folder.exists() && editions_folder.isDirectory()) {
						editions_folders.add(editions_folder);
					}

					if (cards_folder.exists() && cards_folder.isDirectory()) {
						cards_folders.add(cards_folder);
					}

					if (custom_packs_folder.exists() && custom_packs_folder.isDirectory()) {
						custom_packs_folders.add(custom_packs_folder);
					}
				}
			}
		}

		Logs.stdLog("MTC is now reading and loading data");
		if (editions_folders.isEmpty()) {
			Logs.errLog("Editions folder not found. Cards and informations will be missing");
			Logs.errLog("Expected path: <Resource Pack>/assets/is_mtc/mtc/editions/");

			return;
		}

		if (cards_folders.isEmpty()) {
			Logs.errLog("Cards folder not found. Cards data and informations will be missing");
			Logs.errLog("Expected path: <Resource Pack>/assets/is_mtc/mtc/cards/");

			return;
		}

		Logs.stdLog("Loading editions");
		for (File editions_folder : editions_folders) {
			getEditions(editions_folder);
		}
		Logs.stdLog("Done loading editions");

		Logs.stdLog("Loading cards");
		for (File cards_folder : cards_folders) {
			getCards(cards_folder);
		}
		Logs.stdLog("Done loading cards");

		for (int i = 0; i < Databank.getEditionsCount(); ++i) {
			EditionStructure eStruct = Databank.getEditionWithNumeralId(i);

			Logs.stdLog(eStruct.toString());
		}

		if (custom_packs_folders.isEmpty()) {
			Logs.errLog("Custom packs folder not found. No custom packs will be loaded.");
			Logs.errLog("Expected path: <Resource Pack>/assets/is_mtc/mtc/packs/");
		} else {
			Logs.stdLog("Loading custom packs");
			for (File custom_packs_folder : custom_packs_folders) {
				getPacks(custom_packs_folder);
			}
			Logs.stdLog("Done loading custom packs");
		}

		Logs.stdLog("MTC data loading is done");
	}

	private static void getEditions(File folder) {
		File[] files = folder.listFiles(createFilenameFilter(".json"));
		Arrays.sort(files);

		for (File file : files) {
			Logs.devLog("Reading edition file: " + file.getAbsolutePath());

			try {
				FileReader reader = new FileReader(file);
				JsonParser parser = new JsonParser();
				JsonObject head = (JsonObject) parser.parse(reader);
				EditionStructure eStruct = new EditionStructure(head.get("id"), head.get("name"), head.get("color"));

				if (!Databank.registerAnEdition(eStruct))
					Logs.errLog("Concerned edition file: " + file.getName());

				reader.close();
			} catch (Exception e) {
				Logs.errLog("An error occurred wile reading an edition file: " + file.getName());
				Logs.errLog(e.getMessage());
			}
		}
	}

	private static void getCards(File folder) {
		Collection<File> fcol = FileUtils.listFiles(folder, new RegexFileFilter("^.*\\.(json|cdf)$"), DirectoryFileFilter.DIRECTORY); // Filter json and cdf only
		List<File> files = new ArrayList<File>(fcol);

		Collections.sort(files);
		for (File file : files) {
			Logs.devLog("Reading card file: " + file.getAbsolutePath());

			if (file.getName().endsWith(".cdf")) {
				try {
					FileInputStream fis = new FileInputStream(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					CDFCardStructure cdfStruct = new CDFCardStructure();
					CardStructure cStruct;
					String line;

					while ((line = br.readLine()) != null)
						cdfStruct.giveArgument(line);

					br.close();
					fis.close();

					cStruct = new CardStructure(cdfStruct.getId(), cdfStruct.getEdition(), cdfStruct.getRarity());

					if (!cStruct.setSecondaryInput(cdfStruct.getName(), cdfStruct.getCategory(), cdfStruct.getWeight(), cdfStruct.getAssetPaths(), cdfStruct.getDescription()))
						Logs.errLog("Concerned card file: " + file.getName());

					if (!Databank.registerACard(cStruct))
						Logs.errLog("Concerned card file: " + file.getName());
				} catch (Exception e) {
					Logs.errLog("An error occurred wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			} else // JSON condition. Not 'else if' since only .cdf and .json should get there
			{ // Warning, json is case sensitive
				try {
					FileReader reader = new FileReader(file);
					JsonParser parser = new JsonParser();
					JsonObject head = (JsonObject) parser.parse(reader);

					reader.close();

					CardStructure cStruct = new CardStructure(head.get("id"), head.get("edition"), head.get("rarity"));

					if (!cStruct.setSecondaryInput(head.get("name"), head.get("category"), head.get("weight"), head.get("asset"), head.get("description")))
						Logs.errLog("Concerned card file: " + file.getName());

					if (!Databank.registerACard(cStruct))
						Logs.errLog("Concerned card file: " + file.getName());
				} catch (Exception e) {
					Logs.errLog("An error occurred wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			}
		}
	}

	private static void getPacks(File folder) {
		File[] files = folder.listFiles(createFilenameFilter(".json"));
		Arrays.sort(files);

		for (File file : files) {
			Logs.devLog("Reading custom pack file: " + file.getAbsolutePath());

			try {
				FileReader reader = new FileReader(file);
				JsonParser parser = new JsonParser();
				JsonObject head = (JsonObject) parser.parse(reader);
				CustomPackStructure customPackStructure = new CustomPackStructure(head.get("id"), head.get("name"), head.get("color"));

				JsonArray categories = head.getAsJsonArray("categories");
				for (int i = 0; i < categories.size(); i++) {
					String[] categoryQuantity = categories.get(i).getAsString().split(":");
					customPackStructure.addCategoryQuantity(categoryQuantity[0], Integer.parseInt(categoryQuantity[1]), Rarity.fromString(categoryQuantity[2]));
				}

				if (!Databank.registerACustomPack(customPackStructure))
					Logs.errLog("Concerned custom pack file: " + file.getName());

				reader.close();
			} catch (Exception e) {
				Logs.errLog("An error occurred wile reading a custom pack file: " + file.getName());
				Logs.errLog(e.getMessage());
			}
		}
	}
}
