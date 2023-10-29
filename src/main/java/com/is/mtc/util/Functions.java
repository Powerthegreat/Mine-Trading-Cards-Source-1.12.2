package com.is.mtc.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.is.mtc.root.Logs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Functions {
    
    
    /**
     * Discover the y coordinate that will serve as the ground level of the supplied BoundingBox.
     * (An ACTUAL median of all the levels in the BB's horizontal rectangle).
     * 
     * Use outlineOnly if you'd like to tally only the boundary values.
     * 
     * If outlineOnly is true, use sideFlag to specify which boundaries:
     * +1: front
     * +2: left (wrt coordbase 0 or 1)
     * +4: back
     * +8: right (wrt coordbase 0 or 1)
     * 
     * horizIndex is the integer that represents the orientation of the structure.
     */
    public static int getMedianGroundLevel(World world, StructureBoundingBox boundingBox, boolean outlineOnly, byte sideFlag, int horizIndex)
    {
    	ArrayList<Integer> i = new ArrayList<Integer>();
    	
        for (int k = boundingBox.minZ; k <= boundingBox.maxZ; ++k)
        {
            for (int l = boundingBox.minX; l <= boundingBox.maxX; ++l)
            {
                if (boundingBox.isVecInside(new BlockPos(l, 64, k)))
                {
                	if (!outlineOnly || (outlineOnly &&
                			(
                					(k==boundingBox.minZ && (sideFlag&(new int[]{1,2,4,2}[horizIndex]))>0) ||
                					(k==boundingBox.maxZ && (sideFlag&(new int[]{4,8,1,8}[horizIndex]))>0) ||
                					(l==boundingBox.minX && (sideFlag&(new int[]{2,4,2,1}[horizIndex]))>0) ||
                					(l==boundingBox.maxX && (sideFlag&(new int[]{8,1,8,4}[horizIndex]))>0) ||
                					false
                					)
                			))
                	{
                		int aboveTopLevel = getAboveTopmostSolidOrLiquidBlockVN(world, new BlockPos(l, 64, k)).getY();
                		if (aboveTopLevel != -1) {i.add(aboveTopLevel);}
                	}
                }
            }
        }
        
        return medianIntArray(i, true);
    }
    
    
    /**
     * Returns the space above the topmost block that is solid or liquid. Does not count leaves or other foliage
     */
    public static BlockPos getAboveTopmostSolidOrLiquidBlockVN(World world, BlockPos pos)
    {
        
        Chunk chunk = world.getChunk(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.down();
            IBlockState blockstate = chunk.getBlockState(blockpos1);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            
            if (
            		// If it's a solid, full block that isn't one of these particular types
            		(material.blocksMovement()
    				&& !block.isLeaves(world.getBlockState(blockpos1), world, blockpos1)
    				&& material != Material.LEAVES
					&& material != Material.PLANTS
					&& material != Material.VINE
					&& material != Material.AIR
    				&& !block.isFoliage(world, blockpos1))
            		&& blockstate.isNormalCube()
            		// If the block is liquid, return the value above it
            		|| material.isLiquid()
            		)
            {
                break;
            }
        }
        
        return blockpos;
    }

	/**
	 * Returns the median value of an int array.
	 * If the returned value is a halfway point, round up or down depending on if the average value is higher or lower than the median.
	 * If it's the same, specify based on roundup parameter.
	 */
	public static int medianIntArray(ArrayList<Integer> array, boolean roundup)
	{
		if (array.size() <=0) return -1;
		
		Collections.sort(array);
		
		//if (GeneralConfig.debugMessages) {LogHelper.info("array: " + array);}
		
		if (array.size() % 2 == 0)
		{
			// Array is even-length. Find average of the middle two values.
			int totalElements = array.size();
			int sumOfMiddleTwo = array.get(totalElements / 2) + array.get(totalElements / 2 - 1);
			
			if (sumOfMiddleTwo%2==0)
			{
				// Average of middle two values is integer
				//LogHelper.info("Median chosen type A: " + sumOfMiddleTwo/2);
				return sumOfMiddleTwo/2;
			}
			else
			{
				// Average of middle two is half-integer.
				// Round this based on whether the average is higher.
				double median = (double)sumOfMiddleTwo/2;
				
				double average = 0;
				for (int i : array) {average += i;}
				average /= array.size();
				
				if (average < median)
				{
					//LogHelper.info("Median chosen type B: " + MathHelper.floor_double(median) );
					return MathHelper.floor(median);
				}
				else if (average > median)
				{
					//LogHelper.info("Median chosen type C: " + MathHelper.ceil(median) );
					return MathHelper.ceil(median);
				}
				else
				{
					//LogHelper.info("Median chosen type D: " + (roundup ? MathHelper.ceil(median) : MathHelper.floor(median)));
					return roundup ? MathHelper.ceil(median) : MathHelper.floor(median);
				}
			}
		}
		else
		{
			// Array is odd-length. Take the middle value.
			//LogHelper.info("Median chosen type E: " + array.get(array.size()/2));
			return array.get(array.size()/2);
		}
	}
	
	
	/**
	 * Deletes EntityItems within a given structure bounding box
	 */
	public static void cleanEntityItems(World world, StructureBoundingBox boundingBox)
	{
		// selectEntitiesWithinAABB is an AABB method
		AxisAlignedBB aabb = (new AxisAlignedBB(
				// Modified to center onto front of house
				boundingBox.minX, boundingBox.minY, boundingBox.minZ,
				boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)).expand(3, 8, 3);
        
        List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb, null);
        
		if (!list.isEmpty())
        {
			Iterator iterator = list.iterator();
					
			while (iterator.hasNext())
			{
				EntityItem entityitem = (EntityItem) iterator.next();
				entityitem.setDead();
			}
			
			if (Logs.ENABLE_DEV_LOGS) {Logs.devLog("Cleaned "+list.size()+" Entity items within " + aabb.toString());}
        }
	}
    
    /**
     * Inputs a color string, either as a decimal integer or a hex integer
     * as signified by # in the front, and returns it as the proper integer.
     */
    public static int parseColorInteger(String colorstring, int defaultcolor) {
		boolean ishexformat;
		try {
			ishexformat=colorstring.indexOf("#")==0;
			String colorstring_substring=ishexformat?colorstring.substring(1):colorstring;
			return Integer.parseInt(colorstring_substring, ishexformat ? 16 : 10);
		}
		catch (Exception e) {
			Logs.errLog("Color integer " + colorstring + " is not properly formatted!");
			return defaultcolor;
		}
    }
    
    private static final int MINECRAFT_ID_HASH = "minecraft".hashCode();
    private static final int MINECRAFT_ID_TARGET_HASH = 0x5e8f51;
    // base 16777216 plus target color int, minus "minecraft" hash 7207341
    private static final int HASH_COLOR_OFFSET = MINECRAFT_ID_TARGET_HASH - MINECRAFT_ID_HASH + (MINECRAFT_ID_TARGET_HASH - MINECRAFT_ID_HASH >= 0 ? 0 : Reference.COLOR_WHITE);
    
    /**
     * Uses a hash conversion to set a name into a color int
     * @return
     */
    public static int string_to_color_code(String id) {
    	// Convert into a "safe" int (positive and within 0xFFFFFF)
    	int safe_int = MathHelper.abs(id.hashCode() + HASH_COLOR_OFFSET)%Reference.COLOR_WHITE;
    	
//    	// Decompose into r, g, b
//    	int r = safe_int>>16;
//		int g = (safe_int>>8)&255;
//		int b = safe_int&255;
    			
		return safe_int;
    }
    
    /**
     * Inputs an array of objects and a corresponding array of weights, and returns a randomly-selected element
     * with a probability proportional to its weight.
     * 
     * These inputs must be equal length. If they are not, you get back null.
     * Additionally, and this goes without saying: the individual weights must be non-negative and their sum must be positive.
     * 
     * Adapted from https://stackoverflow.com/questions/6737283/weighted-randomness-in-java
     */
    public static Object weightedRandom(Object elementArray, double[] weightArray, Random random)
    {
    	if (Array.getLength(elementArray) != weightArray.length) {return null;}
    	else
    	{
    		// Compute the total weight of all items together
    		double totalWeight = 0D;
    		for (int i=0; i<weightArray.length; i++ )
    		{
    			totalWeight += weightArray[i];
    		}
    		if (totalWeight <= 0) {return null;}
    		
    		// Now choose a random item
    		int randomIndex = -1;
    		double randomObject = random.nextDouble() * totalWeight;
    		for (int i = 0; i < Array.getLength(elementArray); ++i)
    		{
    			randomObject -= weightArray[i];
    		    if (randomObject <= 0.0d)
    		    {
    		        randomIndex = i;
    		        break;
    		    }
    		}
    		
    		return Array.get(elementArray, randomIndex);
    	}
    }
}
