package com.is.mtc.data_manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import net.minecraft.util.Tuple;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class DataLoader {
	private static final JsonParser parser = new JsonParser();

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
		List<String> resourcesZips = new ArrayList<>();
		List<File> editions_folders = new ArrayList<>();
		List<File> cards_folders = new ArrayList<>();
		List<File> custom_packs_folders = new ArrayList<>();

		if (resourcesFolder.exists() && resourcesFolder.isDirectory()) {
			for (File resourcePack : resourcesFolder.listFiles()) { // Iterate through resource packs for valid MTC folders
				if (resourcePack.exists() && (resourcePack.isDirectory())) {
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
				} else if (resourcePack.exists() && resourcePack.toString().endsWith(".zip")) {
					resourcesZips.add(resourcePack.getPath());
				}
			}
		}

		Logs.stdLog("MTC is now reading and loading data");
		if (editions_folders.isEmpty()) {
			Logs.errLog("Editions folder not found. Cards and informations will be missing unless zipped resource packs contain editions");
			Logs.errLog("Expected path: <Resource Pack>/assets/is_mtc/mtc/editions/");

			if (resourcesZips.isEmpty()) {
				return;
			}
		}

		if (cards_folders.isEmpty()) {
			Logs.errLog("Cards folder not found. Cards data and informations will be missing unless zipped resource packs contain cards");
			Logs.errLog("Expected path: <Resource Pack>/assets/is_mtc/mtc/cards/");

			if (resourcesZips.isEmpty()) {
				return;
			}
		}

		Logs.stdLog("Loading editions");
		for (File editions_folder : editions_folders) {
			getEditions(editions_folder);
		}
		Logs.stdLog("Done loading editions");

		Logs.stdLog("Loading editions, cards, and custom packs from zipped resource packs");
		for (String resourcesZip : resourcesZips) {
			Logs.stdLog("Found zipped resource pack again: " + resourcesZip);
			File resourcesZipFile = new File(resourcesZip);
			try (ZipFile file = new ZipFile(resourcesZipFile.toString())) {
				List<Tuple<ZipEntry, Boolean>> cardZipEntries = new ArrayList<>();
				List<ZipEntry> packZipEntries = new ArrayList<>();
				for (Enumeration<? extends ZipEntry> e = file.entries(); e.hasMoreElements(); ) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory()) {
						if (FilenameUtils.getExtension(entry.getName()).equals("cdf")) {
							if (entry.toString().startsWith("assets/is_mtc/mtc/cards/")) {
								cardZipEntries.add(new Tuple<>(entry, true));
							}
						} else if (FilenameUtils.getExtension(entry.getName()).equals("json")) {
							if (entry.toString().startsWith("assets/is_mtc/mtc/editions/")) {
								getSingleEdition((JsonObject) parser.parse(loadInputStreamJson(file.getInputStream(entry))), entry.getName());
							} else if (entry.toString().startsWith("assets/is_mtc/mtc/cards/")) {
								cardZipEntries.add(new Tuple<>(entry, false));
							} else if (entry.toString().startsWith("assets/is_mtc/mtc/packs/")) {
								packZipEntries.add(entry);
							}
						}
					}
				}
				if (Databank.getEditionsCount() == 0) {
					Logs.errLog("No editions were registered in resource pack " + resourcesZip);
					continue;
				}
				for (Tuple<ZipEntry, Boolean> entry : cardZipEntries) { // Load cards after editions
					if (entry.getB()) {
						InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream(entry.getA()));
						BufferedReader br = new BufferedReader(inputStreamReader);
						getSingleCardCdf(br, entry.getA().getName());
						br.close();
						inputStreamReader.close();
					} else {
						getSingleCardJson((JsonObject) parser.parse(loadInputStreamJson(file.getInputStream(entry.getA()))), entry.getA().getName());
					}
				}
				for (ZipEntry entry : packZipEntries) { // Load packs last
					getSinglePack((JsonObject) parser.parse(loadInputStreamJson(file.getInputStream(entry))), entry.getName());
				}
			} catch (IOException ignored) {
			}
		}
		if (Databank.getEditionsCount() == 0) {
			Logs.errLog("No editions were registered");
			return;
		}
		Logs.stdLog("Done loading editions, cards, and custom packs from zipped resource packs");

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
			Logs.errLog("Custom packs folder not found. No custom packs will be loaded unless zipped resource packs contain packs");
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
				JsonObject head = (JsonObject) parser.parse(reader);
				getSingleEdition(head, file.getName());

				reader.close();
			} catch (Exception e) {
				Logs.errLog("An error occurred wile reading an edition file: " + file.getName());
				Logs.errLog(e.getMessage());
			}
		}
	}

	private static void getSingleEdition(JsonObject data, String fileName) {
		EditionStructure eStruct = new EditionStructure(data.get("id"), data.get("name"), data.get("color"));

		if (!Databank.registerAnEdition(eStruct))
			Logs.errLog("Concerned edition file: " + fileName);
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
					getSingleCardCdf(br, file.getName());

					br.close();
					fis.close();
				} catch (Exception e) {
					Logs.errLog("An error occurred wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			} else // JSON condition. Not 'else if' since only .cdf and .json should get there
			{ // Warning, json is case sensitive
				try {
					FileReader reader = new FileReader(file);
					JsonObject head = (JsonObject) parser.parse(reader);

					reader.close();

					getSingleCardJson(head, file.getName());
				} catch (Exception e) {
					Logs.errLog("An error occurred wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			}
		}
	}

	private static void getSingleCardCdf(BufferedReader data, String fileName) {
		try {
			CDFCardStructure cdfStruct = new CDFCardStructure();
			CardStructure cStruct;
			String line;

			while ((line = data.readLine()) != null)
				cdfStruct.giveArgument(line);

			cStruct = new CardStructure(cdfStruct.getId(), cdfStruct.getEdition(), cdfStruct.getRarity());

			if (!cStruct.setSecondaryInput(cdfStruct.getName(), cdfStruct.getCategory(), cdfStruct.getWeight(), cdfStruct.getAssetPaths(), cdfStruct.getDescription()))
				Logs.errLog("Concerned card file: " + fileName);

			if (!Databank.registerACard(cStruct))
				Logs.errLog("Concerned card file: " + fileName);
		} catch (Exception e) {
			Logs.errLog("An error occurred wile reading a card file: " + fileName);
			Logs.errLog(e.getMessage());
		}
	}

	private static void getSingleCardJson(JsonObject data, String fileName) {
		CardStructure cStruct = new CardStructure(data.get("id"), data.get("edition"), data.get("rarity"));

		if (!cStruct.setSecondaryInput(data.get("name"), data.get("category"), data.get("weight"), data.get("asset"), data.get("description")))
			Logs.errLog("Concerned card file: " + fileName);

		if (!Databank.registerACard(cStruct))
			Logs.errLog("Concerned card file: " + fileName);
	}

	private static void getPacks(File folder) {
		File[] files = folder.listFiles(createFilenameFilter(".json"));
		Arrays.sort(files);

		for (File file : files) {
			Logs.devLog("Reading custom pack file: " + file.getAbsolutePath());

			try {
				FileReader reader = new FileReader(file);
				JsonObject head = (JsonObject) parser.parse(reader);
				getSinglePack(head, file.getName());

				reader.close();
			} catch (Exception e) {
				Logs.errLog("An error occurred wile reading a custom pack file: " + file.getName());
				Logs.errLog(e.getMessage());
			}
		}
	}

	private static void getSinglePack(JsonObject data, String fileName) {
		CustomPackStructure customPackStructure = new CustomPackStructure(data.get("id"), data.get("name"), data.get("color"));

		JsonArray categories = data.getAsJsonArray("categories");
		for (int i = 0; i < categories.size(); i++) {
			String[] categoryQuantity = categories.get(i).getAsString().split(":");
			customPackStructure.addCategoryQuantity(categoryQuantity[0], Integer.parseInt(categoryQuantity[1]), Rarity.fromString(categoryQuantity[2]));
		}

		if (!Databank.registerACustomPack(customPackStructure))
			Logs.errLog("Concerned custom pack file: " + fileName);
	}

	private static String loadInputStreamJson(InputStream in) {
		StringBuilder out = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}
}
