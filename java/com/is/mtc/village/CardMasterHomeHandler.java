package com.is.mtc.village;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import cpw.mods.fml.common.registry.VillagerRegistry;

public class CardMasterHomeHandler
implements VillagerRegistry.IVillageCreationHandler {
	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(CardMasterHome.class, 9, 1);
	}

	@Override
	public Class<?> getComponentClass() {
		return CardMasterHome.class;
	}

	@Override
	public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
		return CardMasterHome.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}
}