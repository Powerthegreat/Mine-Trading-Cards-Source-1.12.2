package com.is.mtc.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class CardMasterHomeHandler implements VillagerRegistry.IVillageCreationHandler {
	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(CardMasterHome.class, 9, 1);
	}

	@Override
	public Class<?> getComponentClass() {
		return CardMasterHome.class;
	}

	@Override
	public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		return new CardMasterHome(startPiece, 1, random, startPiece.getBoundingBox(), facing);
	}
	/*@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
	}

	@Override
	public Class<?> getComponentClass() {
		return CardMasterHome.class;
	}

	@Override
	public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		return CardMasterHome.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
	}*/
}