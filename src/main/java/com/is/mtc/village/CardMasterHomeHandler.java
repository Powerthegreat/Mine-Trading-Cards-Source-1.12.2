package com.is.mtc.village;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class CardMasterHomeHandler implements VillagerRegistry.IVillageCreationHandler {

	public static int SHOP_WEIGHT = 5;
	public static int SHOP_MAX_NUMBER = 1;
	
	@Override
	public PieceWeight getVillagePieceWeight(Random random, int villageSize) {
		return new PieceWeight(CardMasterHome.class, SHOP_WEIGHT, SHOP_MAX_NUMBER);
	}
	
	@Override
	public Class<?> getComponentClass() {
		return CardMasterHome.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing coordBaseMode, int componentType) {
		return CardMasterHome.buildComponent(startPiece, pieces, random, x, y, z, coordBaseMode, componentType);
	}
}