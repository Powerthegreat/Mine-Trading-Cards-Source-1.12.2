package com.is.mtc.integration.villagenames;

import java.util.ArrayList;
import java.util.Map;

import com.is.mtc.MineTradingCards;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class VNCompat {
	
	// Indexed by [orientation]
	public static final int[] FENCE_GATE_META_ARRAY = new int[]
	{0,1,2,3,};
	
	// Indexed by [orientation][horizIndex]
	public static final int[][] MONO_DISPLAY_BLOCK_META_ARRAY = new int[][]{
		{0,1,2,3},
		{3,0,3,0},
		{2,3,0,1},
		{1,2,1,2},
	};
	
	// Indexed by [orientation]
	public static final int[] DOOR_META_ARRAY = new int[]
	// --- LOWER HALF --- //
	// Shut
	{1,2,3,0};
	
		
    /**
     * Biome-specific block replacement
     */
    public static IBlockState getBiomeSpecificBlockState(IBlockState blockstate, BiomeProvider biomeProvider, int bbCenterX, int bbCenterZ, Biome biome)
    {
    	if (MineTradingCards.hasVillageNamesInstalled
				&& astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.newVillageGenerator) {

			// Determine MaterialType
    		astrotibs.villagenames.utility.FunctionsVN.MaterialType materialType = astrotibs.villagenames.utility.FunctionsVN.MaterialType.OAK;
			Map<String, ArrayList<String>> mappedBiomes = astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.unpackBiomes(
					astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.spawnBiomesNames
					);
			
			// Determine whether mod subs are allowed
			boolean disallowModSubs = false;
			String mappeddisallowModSubs = (String) (mappedBiomes.get("DisallowModSubs")).get(mappedBiomes.get("BiomeNames").indexOf((String)(ReflectionHelper.getPrivateValue(Biome.class, biome, new String[]{"biomeName","field_76791_y"}))));
			if (mappeddisallowModSubs.toLowerCase().trim().equals("nosub")) {
				disallowModSubs = true;
			}
			
			try {
            	String mappedMaterialType = (String) (mappedBiomes.get("MaterialTypes")).get(mappedBiomes.get("BiomeNames").indexOf((String)(ReflectionHelper.getPrivateValue(Biome.class, biome, new String[]{"biomeName","field_76791_y"}))));
            	if (mappedMaterialType.equals("")) {
            		materialType = astrotibs.villagenames.utility.FunctionsVN.MaterialType.getMaterialTemplateForBiome(biomeProvider, bbCenterX, bbCenterZ);
            	} else {
            		materialType = astrotibs.villagenames.utility.FunctionsVN.MaterialType.getMaterialTypeFromName(mappedMaterialType, astrotibs.villagenames.utility.FunctionsVN.MaterialType.OAK);
            	}
            }
			catch (Exception e) {
				materialType = astrotibs.villagenames.utility.FunctionsVN.MaterialType.getMaterialTemplateForBiome(biomeProvider, bbCenterX, bbCenterZ);
			}
			return astrotibs.villagenames.village.StructureVillageVN.getBiomeSpecificBlockState(blockstate, materialType, biome, disallowModSubs);
    	} else {
			return blockstate;
		}
    }
	
	public static int[] getTownColorsVN(World world, StructureBoundingBox boundingBox, int color1, int color2, int color3, int color4, int color5, int color6, int color7) {
		
		int[] color_a = new int[] {color1, color2, color3, color4, color5, color6, color7};
		
		if (MineTradingCards.hasVillageNamesInstalled
				&& astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.newVillageGenerator
				&& astrotibs.villagenames.config.GeneralConfig.useVillageColors) {
			
			NBTTagCompound villageNBTtag = astrotibs.villagenames.village.StructureVillageVN.getOrMakeVNInfo(world, 
        			(boundingBox.minX+boundingBox.maxX)/2,
        			(boundingBox.minY+boundingBox.maxY)/2,
        			(boundingBox.minZ+boundingBox.maxZ)/2);
			
        	// Load the values of interest into memory
			color_a = new int[] {
					villageNBTtag.getInteger("townColor"),
					villageNBTtag.getInteger("townColor2"),
					villageNBTtag.getInteger("townColor3"),
					villageNBTtag.getInteger("townColor4"),
					villageNBTtag.getInteger("townColor5"),
					villageNBTtag.getInteger("townColor6"),
					villageNBTtag.getInteger("townColor7")
					};
		}
		
		return color_a;
	}
    
    /**
     * Give this method a pillar-like blockstate, the horizontal orientation index of a structure component,
     * and use isAlong to determine whether the log should be oriented "down the barrel" (false) or laterally (true)
     * from the perspective of a player entering the structure component; get back the new blockstate.
     */
    public static IBlockState getHorizontalPillarState(IBlockState blockstate, int coordBaseMode, boolean isAcross)
    {
    	return blockstate.getBlock().getStateFromMeta(
    			blockstate.getBlock().getMetaFromState(blockstate)%4 // Material meta value
    			+ 4 // Horizontal is either 4 or 8
    			//+ (coordBaseMode%2==(isAcross? 0 : 1)? 0 : 1) *4
    			+ (isAcross? 0 : 4)
   					);
    }
    
    /**
     * Give this method the orientation of a hanging torch and the base mode of the structure it's in,
     * and it'll give you back the required meta value for construction.
     * For relative orientations, use:
     * 0=fore-facing (away from you); 1=right-facing; 2=back-facing (toward you); 3=left-facing
     */
    public static int getTorchRotationMeta(int relativeOrientation)
    {
		switch (relativeOrientation)
		{
		case 0: // Facing away
			return 4;
		case 1: // Facing right
			return 1;
		case 2: // Facing you
			return 3;
		case 3: // Facing left
			return 2;
		}
    	return 0; // Torch will be standing upright, hopefully
    }
	
	// Wooden table (Vanilla is a fence with a pressure plate on top)
	public static IBlockState[] chooseModWoodenTable(int materialMeta)
	{
		if (MineTradingCards.hasVillageNamesInstalled
				&& astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.newVillageGenerator) {
			return astrotibs.villagenames.integration.ModObjects.chooseModWoodenTable(materialMeta);
		}
		else {
			return new IBlockState[] {
				Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(),
				chooseVanillaWoodenFence(materialMeta)
				};
		}
	}
	
	public static IBlockState chooseVanillaWoodenFence(int materialMeta)
	{
		IBlockState fenceState;
		
		switch(materialMeta)
		{
		default:
		case 0:
			fenceState = Blocks.OAK_FENCE.getDefaultState(); break;
		case 1:
			fenceState = Blocks.SPRUCE_FENCE.getDefaultState(); break;
		case 2:
			fenceState = Blocks.BIRCH_FENCE.getDefaultState(); break;
		case 3:
			fenceState = Blocks.JUNGLE_FENCE.getDefaultState(); break;
		case 4:
			fenceState = Blocks.ACACIA_FENCE.getDefaultState(); break;
		case 5:
			fenceState = Blocks.DARK_OAK_FENCE.getDefaultState(); break;
		}
		
		return fenceState;
	}

	public static int chooseFenceGateMeta(int orientation, boolean isOpen)
	{
		return FENCE_GATE_META_ARRAY[orientation] + (isOpen?4:0);
	}
	
	public static int chooseMonoDisplayMeta(int orientation, EnumFacing coordBaseMode)
	{
		if (orientation<0) {return -orientation;}
		return MONO_DISPLAY_BLOCK_META_ARRAY[orientation][coordBaseMode.getHorizontalIndex()];
	}
	
	/**
     * Returns meta values for lower and upper halves of a door
     * 
	 * orientation - Direction the outside of the door faces when closed:
	 * 0=fore-facing (away from you); 1=right-facing; 2=back-facing (toward you); 3=left-facing
	 * 
	 * isShut - doors are "shut" by default when placed by a player
	 * rightHandRule - whether the door opens counterclockwise when viewed from above. This is default state when placed by a player
	 */
	public static int[] getDoorMetas(int orientation, EnumFacing coordBaseMode, boolean isShut, boolean isRightHanded)
	{
		int horizIndex = coordBaseMode.getHorizontalIndex();
		
		return new int[] {
				DOOR_META_ARRAY[orientation] + (isShut?0:4),
				isRightHanded ? 8 : 9
						};
	}
}
