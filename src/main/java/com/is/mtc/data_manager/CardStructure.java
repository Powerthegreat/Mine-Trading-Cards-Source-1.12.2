package com.is.mtc.data_manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.JsonElement;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

/*
 * Card is identified by id and edition. Same id can be in two different editions
 * Mandatory parameters are id, edition and rarity
 */
public class CardStructure {
	private String id, edition;
	private int rarity;
	public int numeral;

	private String name, category, /*assetPath, */
			desc;
	private int weight;
	private ResourceLocation assetLocation;
	private ResourceLocation relo;

	public CardStructure(Path filePath, JsonElement jsonId, JsonElement jsonEdition, JsonElement jsonRarity) {
		setInput(jsonId != null ? jsonId.getAsString() : null,
				jsonEdition != null ? jsonEdition.getAsString() : null,
				jsonRarity != null ? jsonRarity.getAsString() : null);
		setAssetLocation(filePath);
	}

	public CardStructure(String id, String edition, String rarity) {
		setInput(id, edition, rarity);
	}

	public boolean setSecondaryInput(Path filePath, JsonElement jName, JsonElement jCategory, JsonElement jWeight,
									 JsonElement jAssetPath, JsonElement jDesc) {
		return setSecondaryInput(filePath,jName != null ? jName.getAsString() : null,
				jCategory != null ? jCategory.getAsString() : null,
				jWeight != null ? jWeight.getAsInt() : 0,
				jAssetPath != null ? jAssetPath.getAsString().split(":")[0] : null,
				jDesc != null ? jDesc.getAsString() : null);
	}

	public boolean setSecondaryInput(Path filePath, String name, String category, int weight, String assetPath, String desc) {
		if(assetPath==null){
			setAssetLocation(filePath);
		} else {
				assetLocation = new ResourceLocation("is_mtc", "textures/cards/"+assetPath+".png");
		}

		this.weight = (int) Tools.clamp(0, weight, Integer.MAX_VALUE);
		this.category = Tools.clean(category);

		if (!MineTradingCards.PROXY_IS_REMOTE) // Only weight and category are really needed on server side
			return true;

		this.name = Tools.clean(name);
		this.desc = Tools.clean(desc);
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
		relo = null;
		numeral = 0;
	}

	private void setAssetLocation(Path filePath){
		//Logs.stdLog(filePath.toString());
		String assetPath = filePath.getParent().getFileName().toString() + "/" + filePath.getFileName().toString().split("\\.")[0]+".png";
		Logs.stdLog(assetPath);
		assetLocation = new ResourceLocation("is_mtc","textures/cards/"+assetPath);
	}

	public boolean isValid() {
		return !(id.isEmpty() || edition.isEmpty() || rarity == Rarity.UNSET);
	}

	@Override
	public String toString() {
		return "{id:" + id + " edition:" + edition + " rarity:" + Rarity.toString(rarity) + " numeral:" + numeral + "} " +
				"[name:'" + name + "' category:'" + category + "' weight:" + weight + "]";
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

	public String getCDWD() {
		return id + " " + edition + " " + rarity;
	}

	public ResourceLocation getAssetLocation() {
		return assetLocation;
	}
}
