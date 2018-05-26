
package com.is.mtc.village;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import com.is.mtc.root.Logs;

public class CardMasterHome extends StructureVillagePieces.House1 {
	private int averageGroundLevel = -1;

	public CardMasterHome() {
	}

	public CardMasterHome(StructureVillagePieces.Start villagePiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5) {
		super(villagePiece, par2, par3Random, par4StructureBoundingBox, par5);
	}

	public boolean func_74875_a(World world, Random random, StructureBoundingBox sbb) {
		int i1;
		int l;

		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
			if (this.averageGroundLevel < 0) {
				return true;
			}
			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.minY + 2, 0);
		}

		this.spawnVillagers(world, sbb, 2, 1, 2, 1);
		Logs.devLog("Card master spawned");
		for (l = 1; l < 6; ++l) {
			for (i1 = 0; i1 < 9; ++i1) {
				this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				this.placeBlockAtCurrentPosition(world, Blocks.air, 0, i1, -1, l, sbb);
			}
		}

		for (l = 0; l < 7; ++l) {
			for (i1 = 1; i1 < 8; ++i1) {
				this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
				this.placeBlockAtCurrentPosition(world, Blocks.air, 0, i1, -1, l, sbb);
			}
		}

		return true;
	}

	public static CardMasterHome buildComponent(StructureVillagePieces.Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 3, 7, p4);

		return CardMasterHome.canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new CardMasterHome(villagePiece, p5, random, structureboundingbox, p4) : null;
	}

	protected int func_74888_b(int p_74888_1_) {
		return VillageHandler.TRADER_ID;
	}
}

