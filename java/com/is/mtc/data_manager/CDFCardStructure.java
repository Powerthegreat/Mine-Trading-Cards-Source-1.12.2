package com.is.mtc.data_manager;

import com.is.mtc.root.Tools;

import java.util.ArrayList;
import java.util.List;

public class CDFCardStructure {

	private String id, edition;
	private String rarity;

	private String name, category, /*assetPath, */
			description;
	private List<String> assetPath;
	private int weight;

	public CDFCardStructure() {
		id = "";
		edition = "";
		rarity = "";

		name = "";
		category = "";
		assetPath = new ArrayList<>();
		weight = 0;
	}

	public void giveArgument(String argument) {
		String[] args = argument.split("=");

		if (args.length != 2)
			return;

		args[0] = Tools.clean(args[0]).toLowerCase();
		args[1] = Tools.clean(args[1]);

		if (args[0].equals("cardid"))
			id = args[1];
		else if (args[0].equals("editionid"))
			edition = args[1];
		else if (args[0].equals("raritylevel"))
			rarity = args[1];

		else if (args[0].equals("name"))
			name = args[1];
		else if (args[0].equals("category"))
			category = args[1];
		else if (args[0].equals("dropweight") || args[0].equals("weight"))
			weight = Integer.parseInt(args[1]);
		else if (args[0].equals("illustrationpath") || args[0].equals("asset")) {
			String[] tempPathList = args[1].split(":");
			for (String asset : tempPathList) {
				assetPath.add(Tools.clean(asset));
			}
		} else if (args[0].equals("description"))
			description = args[1];
	}

	public String getId() {
		return id;
	}

	public String getEdition() {
		return edition;
	}

	public String getRarity() {
		return rarity;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public int getWeight() {
		return weight;
	}

	public List<String> getAssetPath() {
		return assetPath;
	}

	public String getDescription() {
		return description;
	}
}
