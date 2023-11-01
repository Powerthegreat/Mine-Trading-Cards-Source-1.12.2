package com.is.mtc.data_manager;

import com.google.gson.JsonElement;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Card is identified by id and edition. Same id can be in two different editions
 * Mandatory parameters are id, edition and rarity
 */
public class CardStructure {
	public int numeral;
	private String id, edition;
	private int rarity;
	private String name, category, desc;
	private int weight;

	private List<ResourceLocation> resourceLocations;

	public CardStructure(JsonElement jsonId, JsonElement jsonEdition, JsonElement jsonRarity) {
		setInput(jsonId != null ? jsonId.getAsString() : null,
				jsonEdition != null ? jsonEdition.getAsString() : null,
				jsonRarity != null ? jsonRarity.getAsString() : null);
	}

	public CardStructure(String id, String edition, String rarity) {
		setInput(id, edition, rarity);
	}

	public static boolean isValidCStructAsset(CardStructure cStruct, ItemStack stack) {
		if (stack.hasTagCompound() &&
				cStruct != null &&
				cStruct.getResourceLocations() != null &&
				!cStruct.getResourceLocations().isEmpty() &&
				stack.getTagCompound().getInteger("assetnumber") < cStruct.getResourceLocations().size() &&
				cStruct.getResourceLocations().get(stack.getTagCompound().getInteger("assetnumber")) != null) {
			try {
				Minecraft.getMinecraft().getResourceManager().getResource(cStruct.getResourceLocations().get(stack.getTagCompound().getInteger("assetnumber")));
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean setSecondaryInput(JsonElement jName, JsonElement jCategory, JsonElement jWeight,
									 JsonElement jAssetPath, JsonElement jDesc) {
		return setSecondaryInput(jName != null ? jName.getAsString() : null,
				jCategory != null ? jCategory.getAsString() : null,
				jWeight != null ? jWeight.getAsInt() : 0,
				jAssetPath != null ? Arrays.asList(jAssetPath.getAsString().split(":")) : null,
				jDesc != null ? jDesc.getAsString() : null);
	}

	public boolean setSecondaryInput(String name, String category, int weight, List<String> assetPath, String desc) {
		this.weight = (int) Tools.clamp(0, weight, Integer.MAX_VALUE);
		this.category = Tools.clean(category);

		if (!MineTradingCards.PROXY_IS_REMOTE) // Only weight and category are really needed on server side
			return true;

		this.name = Tools.clean(name);
		this.desc = Tools.clean(desc);

		if (!assetPath.isEmpty()) {
			resourceLocations = new ArrayList<>();

			for (String asset : assetPath) {
				String tempAsset = Tools.clean(asset);
				if (!tempAsset.isEmpty()) {
					resourceLocations.add(new ResourceLocation(Reference.MODID, "mtc/assets/" + tempAsset + ".png"));
				}
			}

			return !resourceLocations.isEmpty();
		}

		return true;
	}

	private void setInput(String id, String edition, String rarity) {
		this.id = Tools.clean(id).toLowerCase();
		this.edition = Tools.clean(edition).toLowerCase();

		if (rarity.toLowerCase().equals("artifact") || rarity.toLowerCase().equals("art")) {
			Logs.errLog("An outdated rarity is used: Artifact. The rarity will be set to Ancient");
			Logs.errLog("Concerned card cdwd (raw): " + getCDWD());
			rarity = "ancient";
		}

		this.rarity = Rarity.fromString(Tools.clean(rarity));

		if (!Tools.isValidID(this.id))
			this.id = "";
		if (!Tools.isValidID(this.edition))
			this.edition = "";

		resourceLocations = new ArrayList<>();
		numeral = 0;
	}

	public boolean isValid() {
		return !(id.isEmpty() || edition.isEmpty() || rarity == Rarity.UNSET);
	}

	@Override
	public String toString() {
		return "{id:" + id + " edition:" + edition + " rarity:" + Rarity.toString(rarity) + " numeral:" + numeral + "} " +
				"[name:'" + name + "' category:'" + category + "' weight:" + weight + " resource_locations:" + resourceLocations + "]";
	}

	public String getId() {
		return id;
	}

	public String getEdition() {
		return edition;
	}

	public int getRarity() {
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

	public String getDescription() {
		return desc;
	}

	public List<ResourceLocation> getResourceLocations() {
		return resourceLocations;
	}

	public String getCDWD() {
		return id + " " + edition + " " + rarity;
	}
}
