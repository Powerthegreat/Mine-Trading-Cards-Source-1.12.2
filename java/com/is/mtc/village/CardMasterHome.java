package com.is.mtc.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import net.minecraft.world.biome.BiomeProvider;
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
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    		"FFFFFFFF",
    };
    // Here are values to assign to the bounding box
	public static final int STRUCTURE_WIDTH = foundationPattern[0].length();
	public static final int STRUCTURE_DEPTH = foundationPattern.length;
	public static final int STRUCTURE_HEIGHT = 8;
	// Values for lining things up
	private static final int GROUND_LEVEL = 2; // Spaces above the bottom of the structure considered to be "ground level"
	private static final int INCREASE_MIN_U = 3;
	private static final int DECREASE_MAX_U = 0;
	private static final int INCREASE_MIN_W = -1;
	private static final int DECREASE_MAX_W = 8;
	
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

    	IBlockState biomeDirtState = VNCompat.getBiomeSpecificBlockState(Blocks.DIRT.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome);
    	IBlockState biomeGrassState = VNCompat.getBiomeSpecificBlockState(Blocks.GRASS.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome);
    	
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
    	IBlockState biomeCobblestoneState = VNCompat.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for(int[] uuvvww : new int[][]{
        	// Foundation
        	{0,0,0, 7,0,8}, 
        	{0,1,0, 7,1,0}, {0,1,8, 7,1,8}, {0,1,0, 1,1,8}, {7,1,0, 7,1,8}, 
        	// Corner pillars
        	{0,2,0, 0,4,0}, {7,2,0, 7,4,0}, 
        	{0,2,8, 0,4,8}, {7,2,8, 7,4,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeCobblestoneState, biomeCobblestoneState, false);	
        }
        

        // Terracotta
        for(int[] uuvvwwm : new int[][]{
        	// Loss, lol
        	{1,0,3, 2,0,3, this.townColor}, {1,0,7, 2,0,7, this.townColor}, {5,0,3, 6,0,3, this.townColor}, {5,0,6, 6,0,6, this.townColor}, 
        	{1,0,1, 1,0,2, this.townColor2}, 
        	{1,0,5, 2,0,5, this.townColor3}, 
        	{5,0,1, 6,0,1, this.townColor4}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(uuvvwwm[6]), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Wool
        for(int[] uuvvwwm : new int[][]{
        	{2,1,1, 6,1,7, this.townColor}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.WOOL.getStateFromMeta(uuvvwwm[6]), Blocks.WOOL.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Carpet
        for(int[] uuvvwwm : new int[][]{
        	{3,3,2, 3,3,7, this.townColor2}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvwwm[0], uuvvwwm[1], uuvvwwm[2], uuvvwwm[3], uuvvwwm[4], uuvvwwm[5],
        			Blocks.CARPET.getStateFromMeta(uuvvwwm[6]), Blocks.CARPET.getStateFromMeta(uuvvwwm[6]),
        			false);	
        }
        

        // Logs Vertical
    	IBlockState biomeLogVertState = VNCompat.getBiomeSpecificBlockState(Blocks.LOG.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for(int[] uuvvww : new int[][]{
        	// Posts beneath displays
        	{1,2,2, 1,2,2}, {1,2,4, 1,2,4}, {1,2,6, 1,2,6}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogVertState, biomeLogVertState, false);	
        }
        
        
        // Logs Across
    	IBlockState biomeLogHorAcrossState = VNCompat.getHorizontalPillarState(biomeLogVertState, this.getCoordBaseMode().getHorizontalIndex(), true); // Perpendicular to you
        for(int[] uuvvww : new int[][]{
        	// Front beam
        	{1,4,0, 6,4,0}, 
        	// Rear beam
        	{1,4,8, 6,4,8}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogHorAcrossState, biomeLogHorAcrossState, false);	
        }
        
        
        // Logs Along
    	IBlockState biomeLogHorAlongState = VNCompat.getHorizontalPillarState(biomeLogVertState, this.getCoordBaseMode().getHorizontalIndex(), false); // Toward you
        for(int[] uuvvww : new int[][]{
        	// Left wall 
        	{0,4,1, 0,4,7}, 
        	// Right wall 
        	{7,4,1, 7,4,7}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeLogHorAlongState, biomeLogHorAlongState, false);	
        }
        
        // Bookshelves
        for(int[] uuvvww : new int[][]{
        	{1,4,1, 1,4,7}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);	
        }
    	
        
    	// Torches
        for (int[] uvwo : new int[][]{ // Orientation - 0:forward, 1:rightward, 2:backward (toward you), 3:leftward, -1:upright; 
        	// Over door
        	{5,4,1, 0}, 
        	// Over table
        	{6,4,5, 3}, 
        	}) {
        	this.setBlockState(world, Blocks.TORCH.getStateFromMeta(VNCompat.getTorchRotationMeta(uvwo[3])), uvwo[0], uvwo[1], uvwo[2], structureBB);
        }
        
        
        // Planks
    	IBlockState biomePlankState = VNCompat.getBiomeSpecificBlockState(Blocks.PLANKS.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for(int[] uuvvww : new int[][]{
        	// Front wall
        	{1,2,0, 1,3,0}, {2,2,0, 2,2,0}, {3,2,0, 4,3,0}, {6,2,0, 6,3,0}, 
        	// Left wall
        	{0,2,1, 0,3,7}, 
        	// Right wall
        	{7,2,1, 7,3,7}, 
        	// Back wall
        	{1,2,8, 1,3,8}, {2,2,8, 2,2,8}, {3,2,8, 4,3,8}, {5,2,8, 5,2,8},  {6,2,8, 6,3,8}, 
        	// Roof
        	{1,5,1, 6,5,7}, {2,6,2, 5,6,6}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomePlankState, biomePlankState, false);	
        }
        
        
        // Windows
    	for (int[] uvw : new int[][]{
    		{2,3,0}, // Front
    		{2,3,8}, {5,3,8}, // Rear
    		})
        {
    		this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), uvw[0], uvw[1], uvw[2], structureBB);
        }
        
        // Wooden stairs
    	IBlockState biomeWoodStairsState = VNCompat.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
    	for (int[] uuvvwwo : new int[][]{ // Orientation - 0:leftward, 1:rightward, 3:backward, 2:forward; +4:inverted
    		
    		// Roof
    		{0,5,0, 6,5,0, 3}, {7,5,0, 7,5,7, 1}, {1,5,8, 7,5,8, 2}, {0,5,1, 0,5,8, 0}, 
    		{1,6,1, 5,6,1, 3}, {6,6,1, 6,6,6, 1}, {2,6,7, 6,6,7, 2}, {1,6,2, 1,6,7, 0}, 
    		
    		// Counter
    		{3,2,2, 3,2,7, 0+4}, 
    		
    		// Shelves
    		{1,2,1, 1,3,1, 1}, {1,2,3, 1,3,3, 1}, {1,2,5, 1,3,5, 1}, {1,2,7, 1,3,7, 1}, 
    		
    		// Seats
    		{6,2,4, 6,2,4, 2}, {6,2,6, 6,2,6, 3}, 
    		})
        {
    		this.fillWithBlocks(world, structureBB, uuvvwwo[0], uuvvwwo[1], uuvvwwo[2], uuvvwwo[3], uuvvwwo[4], uuvvwwo[5], biomeWoodStairsState.getBlock().getStateFromMeta(uuvvwwo[6]%4+(uuvvwwo[6]/4)*4), biomeWoodStairsState.getBlock().getStateFromMeta(uuvvwwo[6]%4+(uuvvwwo[6]/4)*4), false);
        }
    	
    	
        // Table
        IBlockState[] tableComponentBlockstates = VNCompat.chooseModWoodenTable(biomePlankState.getBlock()==Blocks.PLANKS ? biomePlankState.getBlock().getMetaFromState(biomePlankState) : 0);
    	for (int[] uuvvww : new int[][]{
        	{6,2,5}, 
    		})
        {
    		for (int i=1; i>=0; i--)
    		{
    			this.setBlockState(world, tableComponentBlockstates[i], uuvvww[0], uuvvww[1]+1-i, uuvvww[2], structureBB);
    		}
        }
        
    	
        // Fence gate
    	IBlockState biomeFenceGateState = VNCompat.getBiomeSpecificBlockState(Blocks.OAK_FENCE_GATE.getDefaultState(), biomeProvider, bbCenterX, bbCenterZ, this.biome);
    	for (int[] uvwos : new int[][]{
        	{3,2,1, 1, 0},
        	})
        {
        	this.setBlockState(world, biomeFenceGateState.getBlock().getStateFromMeta(VNCompat.chooseFenceGateMeta(uvwos[3], uvwos[4]==1)), uvwos[0], uvwos[1], uvwos[2], structureBB);
        }
        
    	
        // Bottom wood slab
    	IBlockState biomeWoodSlabBottomState = VNCompat.getBiomeSpecificBlockState(Blocks.WOODEN_SLAB.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for(int[] uuvvww : new int[][]{
        	// Ceiling
        	{2,7,2, 5,7,6}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeWoodSlabBottomState, biomeWoodSlabBottomState, false);	
        }
        
        
        // Top wood slab
    	IBlockState biomeWoodSlabTopState = VNCompat.getBiomeSpecificBlockState(Blocks.WOODEN_SLAB.getStateFromMeta(8), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for(int[] uuvvww : new int[][]{
        	// Ceiling
        	{3,4,1, 3,4,7}, 
        	})
        {
        	this.fillWithBlocks(world, structureBB, uuvvww[0], uuvvww[1], uuvvww[2], uuvvww[3], uuvvww[4], uuvvww[5], biomeWoodSlabTopState, biomeWoodSlabTopState, false);	
        }
    	
    	
        // Card Display cases
        for (int[] uvwoc : new int[][]{ // 0=fore-facing (away from you); 1=right-facing; 2=back-facing (toward you); 3=left-facing
    		{1,3,2, 1, 0}, {1,3,4, 1, 2}, {1,3,6, 1, 0}, 
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
        			CardStructure cStruct = Databank.generateACard(cardIsUncommon ? Rarity.UNCOMMON : Rarity.COMMON);
        			if (cStruct != null) {
        				displaystack.setTagCompound(new NBTTagCompound());
        				displaystack = CardItem.applyCDWDtoStack(displaystack, cStruct);
        				
        				((DisplayerBlockTileEntity) te).setStackIntoSlot(displaystack, 0, false);
        			}
            	}
            }
        }
        
        
        // Doors
    	IBlockState biomeWoodDoorState = VNCompat.getBiomeSpecificBlockState(Blocks.OAK_DOOR.getStateFromMeta(0), biomeProvider, bbCenterX, bbCenterZ, this.biome);
        for (int[] uvwoor : new int[][]{ // u, v, w, orientation, isShut (1/0 for true/false), isRightHanded (1/0 for true/false)
        	{5,2,0, 2, 1, 1}, 
        })
        {
        	for (int height=0; height<=1; height++)
        	{
        		this.setBlockState(world, biomeWoodDoorState.getBlock().getStateFromMeta(VNCompat.getDoorMetas(uvwoor[3], this.getCoordBaseMode(), uvwoor[4]==1, uvwoor[5]==1)[height]),
        				uvwoor[0], uvwoor[1]+height, uvwoor[2], structureBB);
        	}
        }
		
		
		// Villagers
		if (!this.entitiesGenerated)
		{
			this.entitiesGenerated=true;

        	// Card master behind counter
        	int u = 2;
        	int v = 2;
        	int w = 1 + random.nextInt(7);
        	
			EntityVillager entityvillager = new EntityVillager(world);
			entityvillager.setProfession(MineTradingCardVillagers.PROFESSION_CARD_MASTER);
			
			entityvillager.setLocationAndAngles((double)this.getXWithOffset(u, w) + 0.5D, (double)this.getYWithOffset(v) + 0.5D, (double)this.getZWithOffset(u, w) + 0.5D, random.nextFloat()*360F, 0.0F);
			world.spawnEntity(entityvillager);
			
            // Up to two card traders in main area
            for (int i=0; i<2; i++) {
            	if (random.nextBoolean()) {
            		u = 4 + random.nextInt(2);
                	v = 2;
                	w = 2 + random.nextInt(3) + i*3;
                	
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

