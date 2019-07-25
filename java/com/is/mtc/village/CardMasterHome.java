
package com.is.mtc.village;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import com.is.mtc.root.Logs;

public class CardMasterHome extends StructureVillagePieces.Village {
	private int averageGroundLevel = -1;

	public CardMasterHome() {
	}

	public CardMasterHome(StructureVillagePieces.Start villagePiece, int type, Random random, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
		super(villagePiece, type);
		this.setCoordBaseMode(facing);
		this.boundingBox = structureBoundingBox;
	}

	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
		int i1;
		int l;

		if (averageGroundLevel < 0) {
			averageGroundLevel = this.getAverageGroundLevel(world, sbb);
			if (averageGroundLevel < 0) {
				return true;
			}
			boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.minY + 2, 0);
		}

		spawnVillagers(world, sbb, 2, 1, 2, 1);
		Logs.devLog("Card master spawned");
		for (l = 1; l < 6; ++l) {
			for (i1 = 0; i1 < 9; ++i1) {
				clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				fillWithAir(world, sbb, 0, -1, 1, 8, -1, 5);
			}
		}

		for (l = 0; l < 7; ++l) {
			for (i1 = 1; i1 < 8; ++i1) {
				clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				fillWithAir(world, sbb, 0, -1, 1, 8, -1, 5);
			}
		}

		return true;
	}

	public static CardMasterHome buildComponent(StructureVillagePieces.Start villagePiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing p4, int p5) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 3, 7, p4);

		return CardMasterHome.canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new CardMasterHome(villagePiece, p5, random, structureboundingbox, p4) : null;
	}

	protected int getVillagerType(int p_74888_1_) {
		return VillageHandler.TRADER_ID;
	}
}

