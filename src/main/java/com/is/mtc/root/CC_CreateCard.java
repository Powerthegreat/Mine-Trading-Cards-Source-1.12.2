package com.is.mtc.root;

import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.util.Reference;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CC_CreateCard { // Command to create an existing card
	protected static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands." + Reference.MODID + ".mtc_card.failed"));

	public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(Commands.literal("mtc_card").requires((commandSource) -> commandSource.hasPermission(1))
				.then(Commands.argument("id", StringArgumentType.string())
						.then(Commands.argument("edition", StringArgumentType.string())
								.then(Commands.argument("rarity", IntegerArgumentType.integer())
										.executes((commandContext) -> dropCard(commandContext, StringArgumentType.getString(commandContext, "id"), StringArgumentType.getString(commandContext, "edition"), IntegerArgumentType.getInteger(commandContext, "rarity"), -1))
										.then(Commands.argument("asset number", IntegerArgumentType.integer())
												.executes((commandContext) -> dropCard(commandContext, StringArgumentType.getString(commandContext, "id"), StringArgumentType.getString(commandContext, "edition"), IntegerArgumentType.getInteger(commandContext, "rarity"), IntegerArgumentType.getInteger(commandContext, "asset number")))
										)
								)
						)
				)
		);
	}

	protected static int dropCard(CommandContext<CommandSource> commandContext, String id, String edition, int rarity, int assetNumber) throws CommandSyntaxException {
		ServerWorld serverWorld = commandContext.getSource().getLevel();
		CommandSource commandSource = commandContext.getSource();

		String cdwd = id.toLowerCase() + ' ' + edition + ' ' + rarity;
		CardStructure cStruct = Databank.getCardByCDWD(cdwd);

		if (cStruct == null) {
			throw ERROR_FAILED.create();
		}

		ItemStack gennedStack = new ItemStack(Rarity.getAssociatedCardItem(cStruct.getRarity()));
		CompoundNBT tag = new CompoundNBT();
		tag.putString("cdwd", cdwd);

		if (assetNumber > -1 && assetNumber < cStruct.getResourceLocations().size()) {
			tag.putInt("assetnumber", assetNumber);
		}

		gennedStack.setTag(tag);

		ItemEntity spawnedItem = new ItemEntity(serverWorld,
				commandContext.getSource().getPosition().x,
				commandContext.getSource().getPosition().y,
				commandContext.getSource().getPosition().z,
				gennedStack);

		serverWorld.addFreshEntity(spawnedItem);
		commandSource.sendSuccess(new TranslationTextComponent("commands." + Reference.MODID + ".mtc_card.success",
				commandContext.getSource().getPosition().x,
				commandContext.getSource().getPosition().y,
				commandContext.getSource().getPosition().z), true);
		return 1;
	}
}
