package com.is.mtc.binder;

import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BinderItemGuiContainer extends ContainerScreen<BinderItemContainer> {
	public static final int LESS1 = 0, LESS2 = 1, LESS3 = 2;
	public static final int MORE1 = 3, MORE2 = 4, MORE3 = 5;
	public static final int MODE_SWITCH = 6;
	private static final int WIDTH = 242, HEIGHT = 222;
	private static final ResourceLocation BINDER_ITEM_BACKGROUND = new ResourceLocation(Reference.MODID, "textures/gui/ui_binder_3.png");

	public BinderItemGuiContainer(BinderItemContainer screenContainer, PlayerInventory player, ITextComponent title) {
		super(screenContainer, player, title);

		leftPos = 0;
		topPos = 0;
		imageWidth = WIDTH;
		imageHeight = HEIGHT;
	}

	@Override
	protected void init() {
		super.init();
		int x = (width - imageWidth) / 2, y = (height - imageHeight) / 2;

		addButton(new Button(
				/* Position */ x + 11, y + 144,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("-"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(LESS1));
			BinderItem.changePageBy(getMenu().getBinderStack(), -1);
		}
		));
		addButton(new Button(
				/* Position */ x + 11, y + 167,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("--"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(LESS2));
			BinderItem.changePageBy(getMenu().getBinderStack(), -4);
		}
		));
		addButton(new Button(
				/* Position */ x + 11, y + 190,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("---"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(LESS3));
			BinderItem.changePageBy(getMenu().getBinderStack(), -8);
		}
		));

		addButton(new Button(
				/* Position */ x + 210, y + 144,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("+"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(MORE1));
			BinderItem.changePageBy(getMenu().getBinderStack(), 1);
		}
		));
		addButton(new Button(
				/* Position */ x + 210, y + 167,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("++"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(MORE2));
			BinderItem.changePageBy(getMenu().getBinderStack(), 4);
		}
		));
		addButton(new Button(
				/* Position */ x + 210, y + 190,
				/* Size */ 20, 20,
				/* Text */ new StringTextComponent("+++"),
				/* Effect */ (Button button) -> {
			MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(MORE3));
			BinderItem.changePageBy(getMenu().getBinderStack(), 8);
		}
		));


		addButton(new Button(
				/* Position */ x + 27, y + 126,
				/* Size */ 52, 12,
				/* Text */ new TranslationTextComponent(BinderItem.MODE_STR[getMenu().getBinderStack().getTag().getInt("mode_mtc")]),
				/* Effect */ this::modeButtonPressed
		));
	}

	private void modeButtonPressed(Button button) {
		MineTradingCards.NETWORK_CHANNEL.sendToServer(new MTCMessage(MODE_SWITCH));
		int mode = getMenu().getBinderStack().getTag().getInt("mode_mtc");
		int x = (width - imageWidth) / 2, y = (height - imageHeight) / 2;

		CompoundNBT nbtTag = getMenu().getBinderStack().getTag();
		nbtTag.putInt("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
		buttons.remove(6);
		addButton(new Button(
				/* Position */ x + 27, y + 126,
				/* Size */ 52, 12,
				/* Text */ new TranslationTextComponent(BinderItem.MODE_STR[getMenu().getBinderStack().getTag().getInt("mode_mtc")]),
				/* Effect */ this::modeButtonPressed));
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.textureManager.bind(BINDER_ITEM_BACKGROUND);

		int x = (width - imageWidth) / 2, y = (height - imageHeight) / 2;
		blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);

		for (int i = 0; i < 2; ++i) { // Display card illustration
			for (int j = 0; j < 4; ++j) {
				int cardNumber = j + i * 4 + (getMenu().getCurrentPage() * BinderItemInventory.getStacksPerPage());
				ItemStack stack = getMenu().getCardStackAtIndex(cardNumber);

				if (Tools.isValidCard(stack)) { // Is a valid itemstack for card
					CardStructure cStruct = Databank.getCardByCDWD(stack.getTag().getString("cdwd"));

					if (CardStructure.isValidCStructAsset(cStruct, stack)) { // Card data and illustration are correct

						minecraft.textureManager.bind(cStruct.getResourceLocations().get(stack.getTag().getInt("assetnumber")));
						blit(matrixStack,
								/* Start position on screen */ x + 8 + j * 58, y + 8 + i * 64,
								/* Start position in source image */ 0, 0,
								/* Size on screen */ 52, 52,
								/* Scale within drawing */ 52, 52);
					} else {
						drawString(matrixStack, font, "s" + (cardNumber + 1),
								x + 8 + j * 58, y + 8 + i * 64, 0xFFFFFF);
					}
				} else {
					drawString(matrixStack, font, "s" + (cardNumber + 1),
							x + 8 + j * 58, y + 8 + i * 64, 0xFFFFFF);
				}
			}
		}
		drawCenteredString(matrixStack, font, "Page: " + (getMenu().getCurrentPage() + 1) + "/" + BinderItemInventory.getTotalPages(),
				x + 120, y + 127, 0xFFFFFF);
	}
}