package com.is.mtc.data_manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.is.mtc.root.Logs;
import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;

/*
 * Card is identified by id and edition. Same id can be in two differents editions
 * Mandatory parameters are id, edition and rarity
 */
public class CardStructure {
	private String id, edition;
	private int rarity;
	public int numeral;

	private String name, category, assetPath, desc;
	private int weight;

	private DynamicTexture dytex;
	private ResourceLocation relo;

	public CardStructure(JsonElement jId, JsonElement jEdit, JsonElement jRar)
	{
		setInput(jId != null ? jId.getAsString() : null,
				jEdit != null ? jEdit.getAsString() : null,
						jRar != null ? jRar.getAsString() : null);
	}

	public CardStructure(String id, String edition, String rarity)
	{
		setInput(id, edition, rarity);
	}

	public boolean setSecondaryInput(JsonElement jName, JsonElement jCategory, JsonElement jWeight,
			JsonElement jAssetPath, JsonElement jDesc)
	{
		return setSecondaryInput(jName != null ? jName.getAsString() : null,
				jCategory != null ? jCategory.getAsString() : null,
						jWeight != null ? jWeight.getAsInt() : 0,
								jAssetPath != null ? jAssetPath.getAsString() : null,
										jDesc != null ? jDesc.getAsString() : null);
	}

	public boolean setSecondaryInput(String name, String category, int weight, String assetPath, String desc)
	{
		this.weight = (int)Tools.clamp(0, weight, Integer.MAX_VALUE);

		if (!MineTradingCards.PROXY_IS_REMOTE) // Only weight is really needed on server side
			return true;

		this.name = Tools.clean(name);
		this.category = Tools.clean(category);
		this.assetPath = Tools.clean(assetPath);
		this.desc = Tools.clean(desc);

		if (!this.assetPath.isEmpty()) {
			File asset = new File(MineTradingCards.getDataDir() + "assets/", this.assetPath + ".png");

			try {
				BufferedImage image = ImageIO.read(asset);
				dytex = new DynamicTexture(image);
			}
			catch (IOException e) {
				Logs.errLog("Missing texture at: '" + asset.getAbsolutePath() + "'");
				dytex = null;

				return false;
			}
		}

		return true;
	}

	private void setInput(String id, String edition, String rarity)
	{
		this.id = Tools.clean(id).toLowerCase();
		this.edition = Tools.clean(edition);

		if (rarity.toLowerCase().equals("artifact") || rarity.toLowerCase().equals("art"))
		{
			Logs.errLog("An outdated rarity is used: Artifact. The rarity will be set to Ancient");
			Logs.errLog("Concerned card cdwd (raw): " + getCDWD());
			rarity = "ancient";
		}

		this.rarity = Rarity.fromString(Tools.clean(rarity));

		if (!Tools.isValidID(this.id))
			this.id = "";
		if (!Tools.isValidID(this.edition))
			this.edition = "";

		dytex = null;
		relo = null;
		numeral = 0;
	}

	public boolean isValid()
	{
		return !(id.isEmpty() || edition.isEmpty() || rarity == Rarity.UNSET);
	}

	@Override
	public String toString() {
		return "{id:" + id + " edition:" + edition + " rarity:" + Rarity.toString(rarity) + " numeral:" + numeral + "} " +
				"[name:'" + name + "' category:'" + category + "' weight:" + weight + " asset_path:" + assetPath + "]";
	}

	public void preloadRessource(TextureManager tema) {
		if (dytex == null)
			return;

		relo = tema.getDynamicTextureLocation("mtc_dytex", dytex);
	}

	public String getId() { return id; }
	public String getEdition() { return edition; }
	public int getRarity() { return rarity; }

	public String getName() { return name; }
	public String getCategory() { return category; }
	public int getWeight() { return weight; }
	public String getAssetPath() { return assetPath; }
	public String getDescription() { return desc; }

	public DynamicTexture getDynamicTexture() { return dytex; }
	public ResourceLocation getResourceLocation() { return relo; }

	public String getCDWD() { return id + " " + edition + " " + rarity; }
}
