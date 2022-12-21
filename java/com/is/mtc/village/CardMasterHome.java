package com.is.mtc.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.is.mtc.MineTradingCards;
import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.displayer.DisplayerBlockTileEntity;
import com.is.mtc.init.MTCItems;
import com.is.mtc.integration.villagenames.VNCompat;
import com.is.mtc.root.Rarity;
import com.is.mtc.util.Functions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;


public class CardMasterHome extends StructureVillagePieces.Village {
	// Stuff to be used in the construction
	public boolean entitiesGenerated = false;
	public ArrayList<Integer> decorHeightY = new ArrayList();
	public int townColor=15; // Black
	public int townColor2=14; // Red
	public int townColor3=13; // Green
	public int townColor4=12; // Brown
	public Biome biome=null;
	
	// Make foundation with blanks as empty air and F as foundation spaces
    private static final String[] foundationPattern = new String[]{
    		"          ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		" FFFFFFFF ",
    		"      P   ",
    };
    // Here are values to assign to the bounding box
	public static final int STRUCTURE_WIDTH = foundationPattern[0].length();
	public static final int STRUCTURE_DEPTH = foundationPattern.length;
	public static final int STRUCTURE_HEIGHT = 7;
	// Values for lining things up
	private static final int GROUND_LEVEL = 2; // Spaces above the bottom of the structure considered to be "ground level"
	private static final int INCREASE_MIN_U = 3;
	private static final int DECREASE_MAX_U = 0;
	private static final int INCREASE_MIN_W = 0;
	private static final int DECREASE_MAX_W = 4;
	
	private int averageGroundLevel = -1;

	public CardMasterHome() {
	}

	public CardMasterHome(StructureVillagePieces.Start start, int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing coordBaseMode) {
        super();
        this.setCoordBaseMode(coordBaseMode);
        this.boundingBox = boundingBox;
        // Additional stuff to be used in the construction
        if (start!=null)
        {
        	this.biome=start.biome;
        }
	}
	
	public static CardMasterHome buildComponent(StructureVillagePieces.Start start, List pieces, Random random, int x, int y, int z, EnumFacing coordBaseMode, int componentType) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, STRUCTURE_WIDTH, STRUCTURE_HEIGHT, STRUCTURE_DEPTH, coordBaseMode);

		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new CardMasterHome(start, componentType, random, structureboundingbox, coordBaseMode) : null;
	}
    
    
    @Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBB) {
    	int[] townColors = VNCompat.getTownColorsVN(world, boundingBox,
    			this.townColor,
    			this.townColor2,
    			this.townColor3,
    			this.townColor4,
    			this.townColor,
    			this.townColor,
    			this.townColor
    			);
    	this.townColor = townColors[0];
    	this.townColor2 = townColors[1];
    	this.townColor3 = townColors[2];
    	this.townColor4 = townColors[3];
    	
    	if (this.averageGroundLevel < 0)
        {
    		this.averageGroundLevel = Functions.getMedianGroundLevel(world,
    				// Set the bounding box version as this bounding box but with Y going from 0 to 512
    				new StructureBoundingBox(
    						this.boundingBox.minX+(new int[]{INCREASE_MIN_U,DECREASE_MAX_W,INCREASE_MIN_U,INCREASE_MIN_W}[this.getCoordBaseMode().getHorizontalIndex()]), this.boundingBox.minZ+(new int[]{INCREASE_MIN_W,INCREASE_MIN_U,DECREASE_MAX_W,INCREASE_MIN_U}[this.getCoordBaseMode().getHorizontalIndex()]),
    						this.boundingBox.maxX-(new int[]{DECREASE_MAX_U,INCREASE_MIN_W,DECREASE_MAX_U,DECREASE_MAX_W}[this.getCoordBaseMode().getHorizontalIndex()]), this.boundingBox.maxZ-(new int[]{DECREASE_MAX_W,DECREASE_MAX_U,INCREASE_MIN_W,DECREASE_MAX_U}[this.getCoordBaseMode().getHorizontalIndex()])),
    				true, (byte)1, this.getCoordBaseMode().getHorizontalIndex());
    		
            if (this.averageGroundLevel < 0) {return true;} // Do not construct in a void

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.minY - GROUND_LEVEL, 0);
        }

    	BiomeProvider biomeProvider = world.getBiomeProvider();
    	int bbCenterX = (this.boundingBox.minX+this.boundingBox.maxX)/2; int bbCenterZ = (this.boundingBox.minZ+this.boundingBox.maxZ)/2;
        Biome biome = biomeProvider.getBiome(new BlockPos(bbCenterX, 64, bbCenterZ));

    	// Reestablish biome if start was null or something
        if (this.biome==null) {this.biome = world.getBiome(new BlockPos((this.boundingBox.minX+this.boundingBox.maxX)/2, 0, (this.boundingBox.minZ+this.boundingBox.maxZ)/2));}
        
        // Set structureType based on biome so that the vanilla hooks will use it
        if (this.biome instanceof BiomeDesert) {this.structureType = 1;}
        else if (this.biome instanceof BiomeSavanna) {this.structureType = 2;}
        else if (this.biome instanceof BiomeTaiga) {this.structureType = 3;}
        
    	IBlockState biomeDirtState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.DIRT.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome));
    	IBlockState biomeGrassState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.GRASS.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome));
    	
    	// Establish top and filler blocks, substituting Grass and Dirt if they're null
    	IBlockState biomeTopState=biomeGrassState; if (this.biome!=null && this.biome.topBlock!=null) {biomeTopState=this.biome.topBlock;}
    	IBlockState biomeFillerState=biomeDirtState; if (this.biome!=null && this.biome.fillerBlock!=null) {biomeFillerState=this.biome.fillerBlock;}

    	// Clear space above
        for (int u = 0; u < STRUCTURE_WIDTH; ++u) {for (int w = 0; w < STRUCTURE_DEPTH; ++w) {
        	this.clearCurrentPositionBlocksUpwards(world, u, GROUND_LEVEL, w, structureBB);
        }}
        
    	// Follow the blueprint to set up the starting foundation
    	for (int w=0; w < foundationPattern.length; w++) {for (int u=0; u < foundationPattern[0].length(); u++) {
    		
    		String unitLetter = foundationPattern[foundationPattern.length-1-w].substring(u, u+1).toUpperCase();
			int posX = this.getXWithOffset(u, w);
			int posY = this.getYWithOffset(GROUND_LEVEL-1);
			int posZ = this.getZWithOffset(u, w);
					
    		if (unitLetter.equals("F"))
    		{
    			// If marked with F: fill with dirt foundation
    			this.replaceAirAndLiquidDownwards(world, biomeFillerState, u, GROUND_LEVEL-1, w, structureBB);
    		}
    		else if (unitLetter.equals("P"))
    		{
    			// If marked with P: fill with dirt foundation and top with block-and-biome-appropriate path
    			this.replaceAirAndLiquidDownwards(world, biomeFillerState, u, GROUND_LEVEL-1+(world.getBlockState(new BlockPos(posX, posY, posZ)).isNormalCube()?-1:0), w, structureBB);
    			if (MineTradingCards.hasVillageNamesInstalled
    					&& astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.newVillageGenerator) {
    				VNCompat.setPathSpecificBlock(world, biomeProvider, bbCenterX, bbCenterZ, this.biome, posX, posY, posZ, false);
    			}
    			else {
    				BlockPos pathpos = new BlockPos(posX, posY, posZ);
					IBlockState pathstate = world.getBlockState(pathpos);
					
	                IBlockState replacement_grasspathstate = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
	                IBlockState replacement_planksstate = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
	                IBlockState replacement_gravelstate = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState());
	                IBlockState replacement_cobblestonestate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
	                
					if ((pathstate.getBlock()==Blocks.GRASS || pathstate.getBlock()==Blocks.DIRT) && world.isAirBlock(pathpos.up())) {
						this.setBlockState(world, replacement_grasspathstate, u, GROUND_LEVEL-1, w, structureBB);
					}
					else if (pathstate.getMaterial().isLiquid()) {
						this.setBlockState(world, replacement_planksstate, u, GROUND_LEVEL-1, w, structureBB);
					}

                    if (pathstate.getBlock() == Blocks.SAND || pathstate.getBlock() == Blocks.SANDSTONE || pathstate.getBlock() == Blocks.RED_SANDSTONE)
                    {
                    	this.setBlockState(world, replacement_gravelstate, u, GROUND_LEVEL-1, w, structureBB);
                    	this.setBlockState(world, replacement_cobblestonestate, u, GROUND_LEVEL-2, w, structureBB);
                    }
    			}
    		}
    		else if (world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock()==biomeFillerState.getBlock())
    		{
    			// If the space is blank and the block itself is dirt, add dirt foundation
    			this.replaceAirAndLiquidDownwards(world, biomeFillerState, u, GROUND_LEVEL-2, w, structureBB);
    		}
    		
    		// Then, if the top is dirt with a non-full cube above it, make it grass
    		if (world.getBlockState(new BlockPos(posX, posY, posZ))!=null && world.getBlockState(new BlockPos(posX, posY+1, posZ))!=null
    				&& world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock()==biomeFillerState.getBlock() && !world.getBlockState(new BlockPos(posX, posY+1, posZ)).isNormalCube())
    		{
    			// If the space is blank and the block itself is dirt, add dirt foundation and then cap with grass:
    			this.setBlockState(world, biomeTopState, u, GROUND_LEVEL-1, w, structureBB);
    		}
        }}
        

        // Cobblestone
    	IBlockState biomeCobblestoneState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
        for(int[] uuvvww : new int[][]{
        	// Foundation
        	{1,0,1, 8,0,9}, 
        	{1,1,1, 8,1,1}, {1,1,9, 8,1,9}, {1,1,1, 2,1,9}, {8,1,1, 8,1,9}, 
        	// Corner pillars
        	{1,2,1, 1,4,1}, {8,2,1, 8,4,1}, 
        	{1,2,9, 1,4,9}, {8,2,9, 8,4,9}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeCobblestoneState, biomeCobblestoneState, false);	
        }
        

        // Terracotta
        for(int[] uuvvwwm : new int[][]{
        	// Loss, lol
        	{2,0,4, 3,0,4, this.townColor}, {2,0,8, 3,0,8, this.townColor}, {6,0,4, 7,0,4, this.townColor}, {6,0,7, 7,0,7, this.townColor}, 
        	{2,0,2, 2,0,3, this.townColor2}, 
        	{2,0,6, 3,0,6, this.townColor3}, 
        	{6,0,2, 7,0,2, this.townColor4}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(uuvvwwm[6]), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Wool
        for(int[] uuvvwwm : new int[][]{
        	{3,1,2, 7,1,8, this.townColor}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.WOOL.getStateFromMeta(uuvvwwm[6]), Blocks.WOOL.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Carpet
        for(int[] uuvvwwm : new int[][]{
        	{4,3,3, 4,3,8, this.townColor2}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.CARPET.getStateFromMeta(uuvvwwm[6]), Blocks.CARPET.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Logs Vertical
    	IBlockState biomeLogVertState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.LOG.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
        for(int[] uuvvww : new int[][]{
        	// Posts beneath displays
        	{2,2,3, 2,2,3}, {2,2,5, 2,2,5}, {2,2,7, 2,2,7}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogVertState, biomeLogVertState, false);	
        }
        
        
        // Logs Across
    	IBlockState biomeLogHorAcrossState = this.getBiomeSpecificBlockState(VNCompat.getHorizontalPillarState(biomeLogVertState, this.getCoordBaseMode().getHorizontalIndex(), true)); // Perpendicular to you
        for(int[] uuvvww : new int[][]{
        	// Front beam
        	{2,4,1, 7,4,1}, 
        	// Rear beam
        	{2,4,9, 7,4,9}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogHorAcrossState, biomeLogHorAcrossState, false);	
        }
        
        
        // Logs Along
    	IBlockState biomeLogHorAlongState = this.getBiomeSpecificBlockState(VNCompat.getHorizontalPillarState(biomeLogVertState, this.getCoordBaseMode().getHorizontalIndex(), false)); // Toward you
        for(int[] uuvvww : new int[][]{
        	// Left wall 
        	{1,4,2, 1,4,8}, 
        	// Right wall 
        	{8,4,2, 8,4,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogHorAlongState, biomeLogHorAlongState, false);	
        }
        
        // Bookshelves
        for(int[] uuvvww : new int[][]{
        	{2,4,2, 2,4,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);	
        }
    	
        
    	// Torches
        for (int[] uvwo : new int[][]{ // Orientation - 0:forward, 1:rightward, 2:backward (toward you), 3:leftward, -1:upright; 
        	// Over door
        	{6,4,2, 0}, 
        	// Over table
        	{7,4,6, 3}, 
        	}) {
        	this.setBlockState(world, Blocks.TORCH.getStateFromMeta(VNCompat.getTorchRotationMeta(uvwo[3])), uvwo[0], uvwo[1], uvwo[2], structureBB);
        }
        
        
        // Planks
    	IBlockState biomePlankState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.PLANKS.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
        for(int[] uuvvww : new int[][]{
        	// Front wall
        	{2,2,1, 2,3,1}, {3,2,1, 3,2,1}, {4,2,1, 5,3,1}, {7,2,1, 7,3,1}, 
        	// Left wall
        	{1,2,2, 1,3,8}, 
        	// Right wall
        	{8,2,2, 8,3,8}, 
        	// Back wall
        	{2,2,9, 2,3,9}, {3,2,9, 3,2,9}, {4,2,9, 5,3,9}, {6,2,9, 6,2,9},  {7,2,9, 7,3,9}, 
        	// Roof
        	{1,5,1, 8,5,1}, {8,5,2, 8,5,8}, {2,5,9, 8,5,9}, {1,5,2, 1,5,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomePlankState, biomePlankState, false);	
        }
        
        
        // Windows
    	for (int[] uvw : new int[][]{
    		{3,3,1}, // Front
    		{3,3,9}, {6,3,9}, // Rear
    		})
        {
    		this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), uvw[0], uvw[1], uvw[2], structureBB);
        }
        
        // Wooden stairs
    	IBlockState biomeWoodStairsState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
    	for (int[] uuvvwwo : new int[][]{ // Orientation - 0:leftward, 1:rightward, 3:backward, 2:forward; +4:inverted
    		// Roof
    		{0,5,0, 8,5,0, 3}, {9,5,0, 9,5,9, 1}, {1,5,10, 9,5,10, 2}, {0,5,1, 0,5,10, 0},
    		
    		// Counter
    		{4,2,3, 4,2,8, 0+4}, 
    		
    		// Shelves
    		{2,2,2, 2,3,2, 1}, {2,2,4, 2,3,4, 1}, {2,2,6, 2,3,6, 1}, {2,2,8, 2,3,8, 1}, 
    		
    		// Seats
    		{7,2,5, 7,2,5, 2}, {7,2,7, 7,2,7, 3}, 
    		})
        {
    		this.fillWithBlocks(world, structureBB, uuvvwwo[0], uuvvwwo[1], uuvvwwo[2], uuvvwwo[3], uuvvwwo[4], uuvvwwo[5], biomeWoodStairsState.getBlock().getStateFromMeta(uuvvwwo[6]%4+(uuvvwwo[6]/4)*4), biomeWoodStairsState.getBlock().getStateFromMeta(uuvvwwo[6]%4+(uuvvwwo[6]/4)*4), false);
        }
    	
    	
        // Table
        IBlockState[] tableComponentBlockstates = VNCompat.chooseModWoodenTable(biomePlankState.getBlock()==Blocks.PLANKS ? biomePlankState.getBlock().getMetaFromState(biomePlankState) : 0);
    	for (int[] uuvvww : new int[][]{
        	{7,2,6}, 
    		})
        {
    		for (int i=1; i>=0; i--)
    		{
    			this.setBlockState(world, this.getBiomeSpecificBlockState(tableComponentBlockstates[i]), uuvvww[0], uuvvww[1]+1-i, uuvvww[2], structureBB);
    		}
        }
        
    	
        // Bottom wood slab
    	IBlockState biomeWoodSlabBottomState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.WOODEN_SLAB.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
        for(int[] uuvvww : new int[][]{
        	// Roof
        	{1,6,1, 8,6,9}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeWoodSlabBottomState, biomeWoodSlabBottomState, false);	
        }
        
        
        // Top wood slab
    	IBlockState biomeWoodSlabTopState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.WOODEN_SLAB.getStateFromMeta(8), biomeProvider, bbCenterX, bbCenterZ, this.biome));
        for(int[] uuvvww : new int[][]{
        	// Ceiling
        	{4,4,2, 4,4,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeWoodSlabTopState, biomeWoodSlabTopState, false);	
        }
        
    	
        // Fence
    	IBlockState biomeFenceState = this.getBiomeSpecificBlockState(VNCompat.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome));
    	for (int[] uvw : new int[][]{
    		// Above counter
    		{4,5,3}, {4,5,5}, {4,5,7}, 
        	})
        {
        	this.setBlockState(world, biomeFenceState, uvw[0], uvw[1], uvw[2], structureBB);
        }
        
    	
        // Fence gate
    	IBlockState biomeFenceGateState = VNCompat.getBiomeSpecificBlockState(Blocks.OAK_FENCE_GATE.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome);
    	for (int[] uvwos : new int[][]{
        	{4,2,2, 1, 0},
        	})
        {
        	this.setBlockState(world, biomeFenceGateState.getBlock().getStateFromMeta(VNCompat.chooseFenceGateMeta(uvwos[3], uvwos[4]==1)), uvwos[0], uvwos[1], uvwos[2], structureBB);
        }
    	
    	
        // Card Display cases
        for (int[] uvwoc : new int[][]{ // 0=fore-facing (away from you); 1=right-facing; 2=back-facing (toward you); 3=left-facing
    		{2,3,3, 1, 0}, {2,3,5, 1, 2}, {2,3,7, 1, 0}, 
        	})
        {
        	int x = this.getXWithOffset(uvwoc[0], uvwoc[2]);
        	int y = this.getYWithOffset(uvwoc[1]);
        	int z = this.getZWithOffset(uvwoc[0], uvwoc[2]);
        	
            // Set contents
        	BlockPos pos = new BlockPos(x, y, z);
            if (structureBB.isVecInside(pos) && world.getBlockState(pos)!=null &&world.getBlockState(pos) != MTCItems.monoDisplayerBlock)
            {
            	world.setBlockState(pos, MTCItems.monoDisplayerBlock.getStateFromMeta(VNCompat.chooseMonoDisplayMeta(uvwoc[3], this.getCoordBaseMode())), 2);
            	TileEntity te = world.getTileEntity(pos);
            	
            	if (te != null && te instanceof DisplayerBlockTileEntity) {
            		// The center card will sometimes be an uncommon
            		boolean cardIsUncommon = uvwoc[4]!=0 && (random.nextInt(100) < uvwoc[4]);
            		
            		ItemStack displaystack = new ItemStack(cardIsUncommon ? MTCItems.cardUncommon : MTCItems.cardCommon, 1);
                	
            		// Turn card into specific type
        			CardStructure cStruct = Databank.generateACard(cardIsUncommon ? Rarity.UNCOMMON : Rarity.COMMON, random);
        			if (cStruct != null) {
        				displaystack.setTagCompound(new NBTTagCompound());
        				displaystack = CardItem.applyCDWDtoStack(displaystack, cStruct, random);
        				
        				((DisplayerBlockTileEntity) te).setStackIntoSlot(displaystack, 0, false);
        			}
            	}
            }
        }
        
        
        // Doors
    	IBlockState biomeWoodDoorState = VNCompat.getBiomeSpecificBlockState(Blocks.OAK_DOOR.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for (int[] uvwoor : new int[][]{ // u, v, w, orientation, isShut (1/0 for true/false), isRightHanded (1/0 for true/false)
        	{6,2,1, 2, 1, 1}, 
        })
        {
        	for (int height=0; height<=1; height++)
        	{
        		this.setBlockState(world, biomeWoodDoorState.getBlock().getStateFromMeta(VNCompat.getDoorMetas(uvwoor[3], this.getCoordBaseMode(), uvwoor[4]==1, uvwoor[5]==1)[height]),
        				uvwoor[0], uvwoor[1]+height, uvwoor[2], structureBB);
        	}
        }
    	
        
        // Clear path for easier entry
        for (int[] uvw : new int[][]{
    		{6, GROUND_LEVEL, -1}, 
       		})
    	{
        	int pathU = uvw[0]; int pathV = uvw[1]; int pathW = uvw[2];
            
            // Clear above and set foundation below
            this.clearCurrentPositionBlocksUpwards(world, pathU, pathV, pathW, structureBB);
        	this.replaceAirAndLiquidDownwards(world, biomeFillerState, pathU, pathV-2, pathW, structureBB);
        	// Top is grass which is converted to path
        	if (world.isAirBlock(new BlockPos(this.getXWithOffset(pathU, pathW), this.getYWithOffset(pathV-1), this.getZWithOffset(pathU, pathW))))
        	{
        		this.setBlockState(world, biomeGrassState, pathU, pathV-1, pathW, structureBB);
        	}
        	
			if (MineTradingCards.hasVillageNamesInstalled
					&& astrotibs.villagenames.config.village.VillageGeneratorConfigHandler.newVillageGenerator) {
				VNCompat.setPathSpecificBlock(world, biomeProvider, bbCenterX, bbCenterZ, this.biome, this.getXWithOffset(pathU, pathW), this.getYWithOffset(pathV-1), this.getZWithOffset(pathU, pathW), false);
			}
			else {
				BlockPos pathpos = new BlockPos(this.getXWithOffset(pathU, pathW), this.getYWithOffset(pathV-1), this.getZWithOffset(pathU, pathW));
				IBlockState pathstate = world.getBlockState(pathpos);
				
                IBlockState replacement_grasspathstate = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState());
                IBlockState replacement_planksstate = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
                IBlockState replacement_gravelstate = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState());
                IBlockState replacement_cobblestonestate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
				
				if ((pathstate.getBlock()==Blocks.GRASS || pathstate.getBlock()==Blocks.DIRT) && world.isAirBlock(pathpos.up())) {
					this.setBlockState(world, replacement_grasspathstate, pathU, pathV-1, pathW, structureBB);
				}
				else if (pathstate.getMaterial().isLiquid()) {
					this.setBlockState(world, replacement_planksstate, pathU, pathV-1, pathW, structureBB);
				}
				else if (pathstate.getMaterial().isLiquid()) {
					this.setBlockState(world, replacement_planksstate, pathU, pathV-1, pathW, structureBB);
				}
                if (pathstate.getBlock() == Blocks.SAND || pathstate.getBlock() == Blocks.SANDSTONE || pathstate.getBlock() == Blocks.RED_SANDSTONE)
                {
                	this.setBlockState(world, replacement_gravelstate, pathU, pathV-1, pathW, structureBB);
                	this.setBlockState(world, replacement_cobblestonestate, pathU, pathV-2, pathW, structureBB);
                }
			}
        }
		
		
		// Villagers
		if (!this.entitiesGenerated)
		{
			this.entitiesGenerated=true;

        	// Card master behind counter
        	int u = 3;
        	int v = 2;
        	int w = 2 + random.nextInt(7);
        	
			EntityVillager entityvillager = new EntityVillager(world);
			entityvillager.setProfession(MineTradingCardVillagers.PROFESSION_CARD_MASTER);
			
			entityvillager.setLocationAndAngles((double)this.getXWithOffset(u, w) + 0.5D, (double)this.getYWithOffset(v) + 0.5D, (double)this.getZWithOffset(u, w) + 0.5D, random.nextFloat()*360F, 0.0F);
			world.spawnEntity(entityvillager);
			
            // Up to two card traders in main area
            for (int i=0; i<2; i++) {
            	if (random.nextBoolean()) {
            		u = 5 + random.nextInt(2);
                	v = 2;
                	w = 3 + random.nextInt(3) + i*3;
                	
                	entityvillager = new EntityVillager(world);
                	entityvillager.setProfession(MineTradingCardVillagers.PROFESSION_CARD_TRADER);
                	
        			entityvillager.setLocationAndAngles((double)this.getXWithOffset(u, w) + 0.5D, (double)this.getYWithOffset(v) + 0.5D, (double)this.getZWithOffset(u, w) + 0.5D, random.nextFloat()*360F, 0.0F);
                    world.spawnEntity(entityvillager);
            	}
            }
		}
		
		
        // Clean items
        Functions.cleanEntityItems(world, this.boundingBox);
    	
		return true;
	}

	@Override
	protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
		return MineTradingCardVillagers.PROFESSION_CARD_MASTER;
	}
}

