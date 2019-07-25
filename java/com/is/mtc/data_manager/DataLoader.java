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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.is.mtc.root.Logs;
import com.is.mtc.MineTradingCards;


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

	//-

	public static void readAndLoad() {
		File editions_folder = new File(MineTradingCards.getDataDir() + "editions/");
		File cards_folder = new File(MineTradingCards.getDataDir() + "cards/");

		Logs.stdLog("MTC is now reading and loading data");
		if (!editions_folder.exists()) {
			Logs.errLog("Editions folder not found. Cards and informations will be missing");
			Logs.errLog("Expected path: " + editions_folder.getAbsolutePath());

			return;
		}

		if (!cards_folder.exists()) {
			Logs.errLog("Cards folder not found. Cards data and informations will be missing");
			Logs.errLog("Expected path: " + cards_folder.getAbsolutePath());

			return;
		}

		Logs.stdLog("Loading editions");
		getEditions(editions_folder);
		Logs.stdLog("Done loading editions");

		Logs.stdLog("Loading cards");
		getCards(cards_folder);
		Logs.stdLog("Done loading cards");

		for (int i = 0; i < Databank.getEditionsCount(); ++i) {
			EditionStructure eStruct = Databank.getEditionWithNumeralId(i);

			Logs.stdLog(eStruct.toString());
		}

		Logs.stdLog("MTC data loading is done");
	}

	// -

	private static void getEditions(File folder)
	{
		File[] files = folder.listFiles(createFilenameFilter(".json"));
		Arrays.sort(files);

		for (File file : files) {
			Logs.devLog("Reading edition file: " + file.getAbsolutePath());

			try {
				FileReader reader = new FileReader(file);
				JsonParser parser = new JsonParser();
				JsonObject head = (JsonObject)parser.parse(reader);
				EditionStructure eStruct = new EditionStructure(head.get("id"), head.get("name"));

				if (!Databank.registerAnEdition(eStruct))
					Logs.errLog("Concerned edition file: " + file.getName());

				reader.close();
			}
			catch (Exception e) {
				Logs.errLog("An error occured wile reading an edition file: " + file.getName());
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

			if (file.getName().endsWith(".cdf"))
			{
				try {
					FileInputStream fis = new FileInputStream(file);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					CDFCardStructure cdfStruct = new CDFCardStructure();
					CardStructure cStruct;
					String line;

					while ((line = br.readLine()) != null) {
						cdfStruct.giveArgument(line);
					}
					cStruct = new CardStructure(cdfStruct.getId(), cdfStruct.getEdition(), cdfStruct.getRarity());

					if (!cStruct.setSecondaryInput(cdfStruct.getName(), cdfStruct.getCategory(), cdfStruct.getWeight(), cdfStruct.getAssetPath(), cdfStruct.getDescription()))
						Logs.errLog("Concerned card file: " + file.getName());

					if (!Databank.registerACard(cStruct))
						Logs.errLog("Concerned card file: " + file.getName());

					br.close();
					fis.close();
				}
				catch (Exception e) {
					Logs.errLog("An error occured wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			}
			else // JSON condition. Not 'else if' since only .cdf and .json should get there
			{ // Warning, json is case sensitive
				try {
					FileReader reader = new FileReader(file);
					JsonParser parser = new JsonParser();
					JsonObject head = (JsonObject)parser.parse(reader);
					CardStructure cStruct = new CardStructure(head.get("id"), head.get("edition"), head.get("rarity"));

					if (!cStruct.setSecondaryInput(head.get("name"), head.get("category"), head.get("weight"), head.get("asset"), head.get("description")))
						Logs.errLog("Concerned card file: " + file.getName());

					if (!Databank.registerACard(cStruct))
						Logs.errLog("Concerned card file: " + file.getName());

					reader.close();
				}
				catch (Exception e) {
					Logs.errLog("An error occured wile reading a card file: " + file.getName());
					Logs.errLog(e.getMessage());
				}
			}
		}
	}
}
