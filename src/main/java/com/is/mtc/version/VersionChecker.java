package com.is.mtc.version;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.is.mtc.MineTradingCards;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Reference;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * Adapted from Jabelar's tutorials
 * http://jabelarminecraft.blogspot.com/p/minecraft-forge-1721710-making-mod.html
 * Parallel threading provided by Roadhog360
 * @author AstroTibs
 */
public class VersionChecker extends Thread
{
	public static VersionChecker instance = new VersionChecker();
	
	private static boolean isLatestVersion = false;
	private static boolean warnaboutfailure = false;
    private static String latestVersion = "";
    private static boolean isUpdateCheckFinished = false;
    private static boolean quitChecking = false;
    private static boolean hasThreadStarted = false;
    
    private static final String CHECK_FOR_VERSIONS_AT_URL = "You can check for new versions at "+Reference.URL;
    
	@Override
	public void run()
	{
        InputStream in = null;
        
        try
        {
        	URL url = new URL(Reference.VERSION_CHECKER_URL);
            in = url.openStream();
        } 
        catch (Exception e)
        {
        	if (!warnaboutfailure)
        	{
            	Logs.errLog("Could not connect with server to compare " + Reference.NAME + " version");
        		Logs.errLog(CHECK_FOR_VERSIONS_AT_URL);
            	warnaboutfailure=true;
        	}
        }
        
        try
        {
            latestVersion = IOUtils.readLines(in, Charset.defaultCharset()).get(0);
        }
        catch (Exception e)
        {
        	if (!warnaboutfailure)
        	{
        		Logs.errLog("Failed to compare " + Reference.NAME + " version");
        		Logs.errLog(CHECK_FOR_VERSIONS_AT_URL);
        		warnaboutfailure=true;
        	}
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        
        isLatestVersion = Reference.MOD_VERSION.equals(latestVersion);
        
        if (!this.isLatestVersion() && !latestVersion.equals("") && !latestVersion.equals(null))
        {
        	Logs.stdLog("This version of "+Reference.NAME_COLORIZED+" (" + Reference.MOD_VERSION + ") differs from the latest version: " + latestVersion);
        }
        
        isUpdateCheckFinished = true;
    }
	
    public boolean isLatestVersion()
    {
    	return isLatestVersion;
    }
    
    public String getLatestVersion()
    {
    	return latestVersion;
    }
	
    /**
     * PlayerTickEvent is going to be used for version checking.
     * @param event
     */
    
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onPlayerTickEvent(PlayerTickEvent event)
    {
    	// Used to repeat the version check
    	if (
    			(latestVersion.equals(null) || latestVersion.equals(""))
    			&& !warnaboutfailure // Skip the "run" if a failure was detected
    			&& !hasThreadStarted
    			)
    	{
    		start();
    		hasThreadStarted=true;
    	}
    	
    	if (
    			event.player.ticksExisted>=200
    			&& !quitChecking
    			&& isUpdateCheckFinished) 
    	{
    		Logs.errLog(Reference.NAME+" version check failed.");
    		Logs.errLog(CHECK_FOR_VERSIONS_AT_URL);
    		quitChecking=true;
    	}
    	
        if (
        		event.player.world.isRemote
        		&& event.phase == Phase.END // Stops doubling the checks unnecessarily
        		&& event.player.ticksExisted>=30
        		&& isUpdateCheckFinished
        		&& !quitChecking
        		)
        {
        	// Ordinary version checker
        	if (
            		MineTradingCards.ENABLE_UPDATE_CHECKER
            		&& !instance.isLatestVersion()
            		&& !latestVersion.equals(null)
            		&& !latestVersion.equals("")
            		&& !(Reference.MOD_VERSION).contains("DEV")
        			)
        	{
                quitChecking=true;
                
                event.player.sendMessage(
                		new TextComponentString(
                				Reference.NAME_COLORIZED + 
                				TextFormatting.RESET + " version " + TextFormatting.YELLOW + this.getLatestVersion() + TextFormatting.RESET +
                				" is available! Get it at:"
                		 ));
                event.player.sendMessage(ForgeHooks.newChatWithLinks(Reference.URL));
        	}
        }
        
        if (quitChecking || !MineTradingCards.ENABLE_UPDATE_CHECKER)
        {
            MinecraftForge.EVENT_BUS.unregister(instance);
            return;
        }
    }
}