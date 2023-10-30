package com.is.mtc.gui;

import java.util.ArrayList;
import java.util.List;

import com.is.mtc.MineTradingCards;
import com.is.mtc.handler.ConfigHandler;
import com.is.mtc.util.Reference;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiMessageDialog;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

/**
 * @author AstroTibs
 * Adapted from Jabelar's Magic Beans:
 * https://github.com/jabelar/MagicBeans-1.7.10/blob/52dc91bfa2e515dcd6ebe116453dc98951f03dcb/src/main/java/com/blogspot/jabelarminecraft/magicbeans/gui/GuiConfig.java
 * and FunWayGuy's EnviroMine:
 * https://github.com/EnviroMine/EnviroMine-1.7/blob/1652062539adba36563450caefa1879127ccb950/src/main/java/enviromine/client/gui/menu/config/EM_ConfigMenu.java
 */
public class MTCGuiConfig extends GuiConfig 
{
	public MTCGuiConfig(GuiScreen guiScreen)
	{
		super(
				guiScreen,         // parentScreen: the parent GuiScreen object
				getElements(),     // configElements: a List of IConfigProperty objects
                Reference.MODID,  // modID: the mod ID for the mod whose config settings will be edited
				false,             // allRequireWorldRestart: send true if all configElements on this screen require a world restart
				false,             // allRequireMcRestart: send true if all configElements on this screen require MC to be restarted
				getHeader()        // title: the desired title for this screen. For consistency it is recommended that you pass the path of the config file being edited.
				);
	}
	
	private static String getHeader() {
		return TextFormatting.YELLOW + MineTradingCards.CONF_DIR;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static List<IConfigElement> getElements()
	{
		List<IConfigElement> subCats = new ArrayList<IConfigElement>();
		ConfigCategory cc;

		// General config
		subCats = new ArrayList<IConfigElement>();
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_RECIPES);
		cc.setComment("Toggle crafting recipes");
		cc.setRequiresMcRestart(true);
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_DROPS);
		cc.setComment("Change or toggle drop rates for cards and packs");
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_PACK_CONTENTS);
		cc.setComment("Modify the card rarity distribution for packs");
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_COLORS);
		cc.setComment("Change the colors of cards, packs, and tooltips");
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_VILLAGERS);
		cc.setComment("Change or toggle village and villager components");
		cc.setRequiresMcRestart(true);
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_UPDATES);
		cc.setComment("Toggle update checker");
		cc.setRequiresMcRestart(true);
		subCats.add(new ConfigElement(cc));
		
		cc = ConfigHandler.config.getCategory(ConfigHandler.CONFIG_CAT_LOGS);
		cc.setComment("Turn on debug logging");
		subCats.add(new ConfigElement(cc));
		
		return subCats;
	}
	
	
	@Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2000) // The topmost "Done" button
        {
            boolean flag = true;
            
            try
            {
                if ((configID != null
                		|| this.parentScreen == null ||
                		!(this.parentScreen instanceof MTCGuiConfig)) 
                        && (this.entryList.hasChangedEntry(true))
                        )
                {
                    boolean requiresMcRestart = this.entryList.saveConfigElements();
                    
                    if (Loader.isModLoaded(modID))
                    {
                        ConfigChangedEvent event = new OnConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart);
                        MinecraftForge.EVENT_BUS.post(event);
                        
                        if (!event.getResult().equals(Result.DENY))
                        {
                        	MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(modID, configID, isWorldRunning, requiresMcRestart));
                            ConfigHandler.saveConfig(); // To force-sync the config options
                        }
                        if (requiresMcRestart)
                        {
                            flag = false;
//                            mc.displayGuiScreen(new GuiMessageDialog(parentScreen, Reference.FML_GAME_RESTART_TITLE_GUI_ADDRESS, new TextComponentString(I18n.format(Reference.FML_GAME_RESTART_REQUIRED_GUI_ADDRESS)), Reference.FML_GAME_CONFIRM_RESTART_GUI_ADDRESS));
                            mc.displayGuiScreen(new GuiMessageDialog(parentScreen, "fml.configgui.gameRestartTitle", new TextComponentString(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                        }
                        
                        if (this.parentScreen instanceof MTCGuiConfig) {
                            ((MTCGuiConfig) this.parentScreen).needsRefresh = true;
                        }
                    }
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            
            if (flag) {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }
}
