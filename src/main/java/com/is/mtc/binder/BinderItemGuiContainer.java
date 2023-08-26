package com.is.mtc.binder;

import java.io.IOException;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import com.google.common.collect.Sets;
import com.is.mtc.MineTradingCards;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.packet.MTCMessage;
import com.is.mtc.root.Tools;
import com.is.mtc.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class BinderItemGuiContainer extends GuiContainer {
	protected int xSize = 242;
	protected int ySize = 222;
	private BinderItemContainer binderContainer;
	protected int guiLeft;
	protected int guiTop;
	private Slot hoveredSlot;
	private Slot clickedSlot;
	private boolean isRightMouseClick;
	private ItemStack draggedStack = ItemStack.EMPTY;
	private int touchUpX;
	private int touchUpY;
	private Slot returningStackDestSlot;
	private long returningStackTime;
	private ItemStack returningStack = ItemStack.EMPTY;
	private Slot currentDragTargetSlot;
	private long dragItemDropDelay;
	protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
	protected boolean dragSplitting;
	private int dragSplittingLimit;
	private int dragSplittingButton;
	private boolean ignoreMouseUp;
	private int dragSplittingRemnant;
	private long lastClickTime;
	private Slot lastClickSlot;
	private int lastClickButton;
	private boolean doubleClick;
	private ItemStack shiftClickedSlot = ItemStack.EMPTY;

	public static final int LESS1 = 0, LESS2 = 1, LESS3 = 2;
	public static final int MORE1 = 3, MORE2 = 4, MORE3 = 5;
	public static final int MODE_SWITCH = 6;

	public BinderItemGuiContainer(BinderItemContainer inventorySlots) {
		super(inventorySlots);

		ignoreMouseUp = true;
		binderContainer = inventorySlots;
	}

	public void initGui() {
		Vector2f drawPos = new Vector2f((width - xSize) / 2f, (height - ySize) / 2f);

		mc.player.openContainer = binderContainer;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;

		//make buttons
		//id, x, y, width, height, text
		buttonList.add(new GuiButton(LESS1, (int) drawPos.x + 11, (int) drawPos.y + 144, 20, 20, "-"));
		buttonList.add(new GuiButton(LESS2, (int) drawPos.x + 11, (int) drawPos.y + 167, 20, 20, "--"));
		buttonList.add(new GuiButton(LESS3, (int) drawPos.x + 11, (int) drawPos.y + 190, 20, 20, "---"));

		buttonList.add(new GuiButton(MORE1, (int) drawPos.x + 210, (int) drawPos.y + 144, 20, 20, "+"));
		buttonList.add(new GuiButton(MORE2, (int) drawPos.x + 210, (int) drawPos.y + 167, 20, 20, "++"));
		buttonList.add(new GuiButton(MORE3, (int) drawPos.x + 210, (int) drawPos.y + 190, 20, 20, "+++"));

		buttonList.add(new GuiButton(MODE_SWITCH, (int) drawPos.x + 27, (int) drawPos.y + 126, 52, 12, BinderItem.MODE_STR[binderContainer.getBinderStack().getTagCompound().getInteger("mode_mtc")]));
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		for (GuiButton guiButton : buttonList) {
			guiButton.drawButton(mc, mouseX, mouseY, partialTicks);
		}
		for (GuiLabel guiLabel : labelList) {
			guiLabel.drawLabel(mc, mouseX, mouseY);
		}
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) guiLeft, (float) guiTop, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		hoveredSlot = null;
		int k = 240;
		int l = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for (int i1 = 0; i1 < 36; ++i1) {
			Slot slot = binderContainer.inventorySlots.get(i1);

			if (slot.isEnabled()) {
				drawSlot(slot);
			}

			if (isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = slot.xPos;
				int k1 = slot.yPos;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}

		for (int i1 = 0; i1 < 8; i1++) /* Draw binder */ {
			Slot slot = binderContainer.inventorySlots.get(i1 + 36 + (binderContainer.getCurrentPage() * BinderItemInventory.getStacksPerPage()));
			drawSlot(slot);

			if (isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
				hoveredSlot = slot;
				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				int j1 = slot.xPos;
				int k1 = slot.yPos;
				GlStateManager.colorMask(true, true, true, false);
				drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}

		RenderHelper.disableStandardItemLighting();
		drawGuiContainerForegroundLayer(mouseX, mouseY);
		RenderHelper.enableGUIStandardItemLighting();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
		InventoryPlayer inventoryplayer = mc.player.inventory;
		ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;

		if (!itemstack.isEmpty()) {
			int j2 = 8;
			int k2 = draggedStack.isEmpty() ? 8 : 16;
			String s = null;

			if (!draggedStack.isEmpty() && isRightMouseClick) {
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2.0F));
			} else if (dragSplitting && dragSplittingSlots.size() > 1) {
				itemstack = itemstack.copy();
				itemstack.setCount(dragSplittingRemnant);

				if (itemstack.isEmpty()) {
					s = "" + TextFormatting.YELLOW + "0";
				}
			}

			drawItemStack(itemstack, mouseX - guiLeft - 8, mouseY - guiTop - k2, s);
		}

		if (!returningStack.isEmpty()) {
			float f = (float) (Minecraft.getSystemTime() - returningStackTime) / 100.0F;

			if (f >= 1.0F) {
				f = 1.0F;
				returningStack = ItemStack.EMPTY;
			}

			int l2 = returningStackDestSlot.xPos - touchUpX;
			int i3 = returningStackDestSlot.yPos - touchUpY;
			int l1 = touchUpX + (int) ((float) l2 * f);
			int i2 = touchUpY + (int) ((float) i3 * f);
			drawItemStack(returningStack, l1, i2, null);
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();

		renderHoveredToolTip(mouseX, mouseY);
	}

	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case LESS1:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS1));
				BinderItem.changePageBy(binderContainer.getBinderStack(), -1);
				break;
			case LESS2:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS2));
				BinderItem.changePageBy(binderContainer.getBinderStack(), -4);
				break;
			case LESS3:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS3));
				BinderItem.changePageBy(binderContainer.getBinderStack(), -8);
				break;

			case MORE1:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE1));
				BinderItem.changePageBy(binderContainer.getBinderStack(), 1);
				break;
			case MORE2:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE2));
				BinderItem.changePageBy(binderContainer.getBinderStack(), 4);
				break;
			case MORE3:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE3));
				BinderItem.changePageBy(binderContainer.getBinderStack(), 8);
				break;

			case MODE_SWITCH:
				MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MODE_SWITCH));
				Vector2f drawPos = new Vector2f((width - xSize) / 2, (height - ySize) / 2);
				int mode = binderContainer.getBinderStack().getTagCompound().getInteger("mode_mtc");

				NBTTagCompound nbtTag = binderContainer.getBinderStack().getTagCompound();
				nbtTag.setInteger("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
				//binderContainer.getBinderStack().setTagCompound(nbtTag);
				//binderContainer.getBinderStack().writeToNBT(nbtTag);
				buttonList.remove(6);
				buttonList.add(new GuiButton(MODE_SWITCH, (int) drawPos.x + 27, (int) drawPos.y + 126, 52, 12, BinderItem.MODE_STR[binderContainer.getBinderStack().getTagCompound().getInteger("mode_mtc")]));
				break;
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Vector2f drawPos = new Vector2f((width - xSize) / 2f, (height - ySize) / 2f);

		drawDefaultBackground();
		mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_binder_3.png"));
		drawTexturedModalRect((int) drawPos.x, (int) drawPos.y, 0, 0, xSize, ySize);

		for (int i = 0; binderContainer != null && i < 2; ++i) { // Display card illustration
			for (int j = 0; j < 4; ++j) {
				int x = j + i * 4 + (binderContainer.getCurrentPage() * BinderItemInventory.getStacksPerPage());
				ItemStack stack = binderContainer.getCardStackAtIndex(x);

				if (Tools.isValidCard(stack)) { // Is a valid itemstack for card
					CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

					//TODO check if card texture file exists
					if (cStruct != null && cStruct.getAssetLocation() != null) { // Card data and illustration are correct
							//TODO load image from file when its called
							mc.getTextureManager().bindTexture(cStruct.getAssetLocation());
							drawTexturedModalRect((int) drawPos.x + 8 + j * 58, (int) drawPos.y + 8 + i * 64);
					} else {
						drawString(fontRenderer, "s" + (x + 1),
								(int) drawPos.x + 8 + j * 58, (int) drawPos.y + 8 + i * 64, 0xFFFFFF);
					}
				} else {
					drawString(fontRenderer, "s" + (x + 1),
							(int) drawPos.x + 8 + j * 58, (int) drawPos.y + 8 + i * 64, 0xFFFFFF);
				}
			}
		}
		drawCenteredString(fontRenderer, "Page: " + (binderContainer.getCurrentPage() + 1) + "/" + BinderItemInventory.getTotalPages(), (int) drawPos.x + 120, (int) drawPos.y + 127, 0xFFFFFF);
	}

	public void drawTexturedModalRect(int x, int y) {
		int size = 52;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + size, zLevel).tex(0, 1).endVertex();
		bufferbuilder.pos(x + size, y + size, zLevel).tex(1, 1).endVertex();
		bufferbuilder.pos(x + size, y, zLevel).tex(1, 0).endVertex();
		bufferbuilder.pos(x, y, zLevel).tex(0, 0).endVertex();
		tessellator.draw();
	}

	public void onGuiClosed() {
		if (mc.player != null) {
			binderContainer.onContainerClosed(mc.player);
		}
	}

	private void drawSlot(Slot slot) {
		int i = slot.xPos;
		int j = slot.yPos;
		ItemStack itemStack = slot.getStack();
		boolean flag = false;
		boolean flag1 = slot == clickedSlot && !draggedStack.isEmpty() && !isRightMouseClick;
		ItemStack itemStack1 = mc.player.inventory.getItemStack();
		String s = null;

		if (slot == clickedSlot && !draggedStack.isEmpty() && isRightMouseClick && !itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setCount(itemStack.getCount() / 2);
		} else if (dragSplitting && dragSplittingSlots.contains(slot) && !itemStack1.isEmpty()) {
			if (dragSplittingSlots.size() == 1) {
				return;
			}

			if (Container.canAddItemToSlot(slot, itemStack1, true) && binderContainer.canDragIntoSlot(slot)) {
				itemStack = itemStack1.copy();
				flag = true;
				Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemStack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
				int k = Math.min(itemStack.getMaxStackSize(), slot.getItemStackLimit(itemStack));

				if (itemStack.getCount() > k) {
					s = TextFormatting.YELLOW.toString() + k;
					itemStack.setCount(k);
				}
			} else {
				dragSplittingSlots.remove(slot);
				updateDragSplitting();
			}
		}

		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;

		if (itemStack.isEmpty() && slot.isEnabled()) {
			TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();

			if (textureatlassprite != null) {
				GlStateManager.disableLighting();
				mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
				drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
				GlStateManager.enableLighting();
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) {
				drawRect(i, j, i + 16, j + 16, -2130706433);
			}

			GlStateManager.enableDepth();
			itemRender.renderItemAndEffectIntoGUI(mc.player, itemStack, i, j);
			itemRender.renderItemOverlayIntoGUI(fontRenderer, itemStack, i, j, s);
		}

		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	private void updateDragSplitting() {
		ItemStack itemstack = mc.player.inventory.getItemStack();

		if (!itemstack.isEmpty() && dragSplitting) {
			if (dragSplittingLimit == 2) {
				dragSplittingRemnant = itemstack.getMaxStackSize();
			} else {
				dragSplittingRemnant = itemstack.getCount();

				for (Slot slot : dragSplittingSlots) {
					ItemStack itemStack1 = itemstack.copy();
					ItemStack itemStack2 = slot.getStack();
					int i = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
					Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemStack1, i);
					int j = Math.min(itemStack1.getMaxStackSize(), slot.getItemStackLimit(itemStack1));

					if (itemStack1.getCount() > j) {
						itemStack1.setCount(j);
					}

					dragSplittingRemnant -= itemStack1.getCount() - i;
				}
			}
		}
	}

	private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
		return isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY);
	}

	private void drawItemStack(ItemStack stack, int x, int y, String altText) {
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null)
			font = fontRenderer;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
		zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack()) {
			renderToolTip(hoveredSlot.getStack(), mouseX, mouseY);
		}
	}

	protected void renderToolTip(ItemStack stack, int x, int y) {
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		drawHoveringText(getItemToolTip(stack), x, y, (font == null ? fontRenderer : font));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

	protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
		return p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + xSize || p_193983_2_ >= p_193983_4_ + ySize;
	}

	private Slot getSlotAtPosition(int x, int y) {
		for (int i = 0; i < 36; ++i) {
			Slot slot = binderContainer.inventorySlots.get(i);

			if (isMouseOverSlot(slot, x, y) && slot.isEnabled()) {
				return slot;
			}
		}

		for (int i = 0; i < 8; ++i) {
			Slot slot = binderContainer.inventorySlots.get(i + 36 + binderContainer.getCurrentPage() * BinderItemInventory.getStacksPerPage());

			if (isMouseOverSlot(slot, x, y) && slot.isEnabled()) {
				return slot;
			}
		}

		return null;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (int i = 0; i < buttonList.size(); ++i) {
				GuiButton guibutton = buttonList.get(i);

				if (guibutton.mousePressed(mc, mouseX, mouseY)) {
					net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, buttonList);
					if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
						break;
					guibutton = event.getButton();
					selectedButton = guibutton;
					guibutton.playPressSound(mc.getSoundHandler());
					actionPerformed(guibutton);
					if (equals(mc.currentScreen))
						net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
				}
			}
		}
		boolean flag = mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100);
		Slot slot = getSlotAtPosition(mouseX, mouseY);
		long i = Minecraft.getSystemTime();
		doubleClick = lastClickSlot == slot && i - lastClickTime < 250L && lastClickButton == mouseButton;
		ignoreMouseUp = false;

		if (mouseButton == 0 || mouseButton == 1 || flag) {
			int j = guiLeft;
			int k = guiTop;
			boolean flag1 = hasClickedOutside(mouseX, mouseY, j, k);
			if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
			int l = -1;

			if (slot != null) {
				l = slot.slotNumber;
			}

			if (flag1) {
				l = -999;
			}

			if (mc.gameSettings.touchscreen && flag1 && mc.player.inventory.getItemStack().isEmpty()) {
				mc.displayGuiScreen(null);
				return;
			}

			if (l != -1) {
				if (mc.gameSettings.touchscreen) {
					if (slot != null && slot.getHasStack()) {
						clickedSlot = slot;
						draggedStack = ItemStack.EMPTY;
						isRightMouseClick = mouseButton == 1;
					} else {
						clickedSlot = null;
					}
				} else if (!dragSplitting) {
					if (mc.player.inventory.getItemStack().isEmpty()) {
						if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
							handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
						} else {
							boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
							ClickType clicktype = ClickType.PICKUP;

							if (flag2) {
								shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
								clicktype = ClickType.QUICK_MOVE;
							} else if (l == -999) {
								clicktype = ClickType.THROW;
							}

							handleMouseClick(slot, l, mouseButton, clicktype);
						}

						ignoreMouseUp = true;
					} else {
						dragSplitting = true;
						dragSplittingButton = mouseButton;
						dragSplittingSlots.clear();

						if (mouseButton == 0) {
							dragSplittingLimit = 0;
						} else if (mouseButton == 1) {
							dragSplittingLimit = 1;
						} else if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
							dragSplittingLimit = 2;
						}
					}
				}
			}
		}

		lastClickSlot = slot;
		lastClickTime = i;
		lastClickButton = mouseButton;
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
	 * lastButtonClicked & timeSinceMouseClick.
	 */
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		Slot slot = getSlotAtPosition(mouseX, mouseY);
		ItemStack itemstack = mc.player.inventory.getItemStack();

		if (clickedSlot != null && mc.gameSettings.touchscreen) {
			if (clickedMouseButton == 0 || clickedMouseButton == 1) {
				if (draggedStack.isEmpty()) {
					if (slot != clickedSlot && !clickedSlot.getStack().isEmpty()) {
						draggedStack = clickedSlot.getStack().copy();
					}
				} else if (draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, draggedStack, false)) {
					long i = Minecraft.getSystemTime();

					if (currentDragTargetSlot == slot) {
						if (i - dragItemDropDelay > 500L) {
							handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, ClickType.PICKUP);
							handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
							handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, ClickType.PICKUP);
							dragItemDropDelay = i + 750L;
							draggedStack.shrink(1);
						}
					} else {
						currentDragTargetSlot = slot;
						dragItemDropDelay = i;
					}
				}
			}
		} else if (dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > dragSplittingSlots.size() || dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && binderContainer.canDragIntoSlot(slot)) {
			dragSplittingSlots.add(slot);
			updateDragSplitting();
		}
	}

	/**
	 * Called when a mouse button is released.
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (selectedButton != null && state == 0) {
			selectedButton.mouseReleased(mouseX, mouseY);
			selectedButton = null;
		} //Forge, Call parent to release buttons
		Slot slot = getSlotAtPosition(mouseX, mouseY);
		int i = guiLeft;
		int j = guiTop;
		boolean flag = hasClickedOutside(mouseX, mouseY, i, j);
		if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		int k = -1;

		if (slot != null) {
			k = slot.slotNumber;
		}

		if (flag) {
			k = -999;
		}

		if (doubleClick && slot != null && state == 0 && binderContainer.canMergeSlot(ItemStack.EMPTY, slot)) {
			if (isShiftKeyDown()) {
				if (!shiftClickedSlot.isEmpty()) {
					for (Slot slot2 : binderContainer.inventorySlots) {
						if (slot2 != null && slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, shiftClickedSlot, true)) {
							handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
						}
					}
				}
			} else {
				handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
			}

			doubleClick = false;
			lastClickTime = 0L;
		} else {
			if (dragSplitting && dragSplittingButton != state) {
				dragSplitting = false;
				dragSplittingSlots.clear();
				ignoreMouseUp = true;
				return;
			}

			if (ignoreMouseUp) {
				ignoreMouseUp = false;
				return;
			}

			if (clickedSlot != null && mc.gameSettings.touchscreen) {
				if (state == 0 || state == 1) {
					if (draggedStack.isEmpty() && slot != clickedSlot) {
						draggedStack = clickedSlot.getStack();
					}

					boolean flag2 = Container.canAddItemToSlot(slot, draggedStack, false);

					if (k != -1 && !draggedStack.isEmpty() && flag2) {
						handleMouseClick(clickedSlot, clickedSlot.slotNumber, state, ClickType.PICKUP);
						handleMouseClick(slot, k, 0, ClickType.PICKUP);

						if (mc.player.inventory.getItemStack().isEmpty()) {
							returningStack = ItemStack.EMPTY;
						} else {
							handleMouseClick(clickedSlot, clickedSlot.slotNumber, state, ClickType.PICKUP);
							touchUpX = mouseX - i;
							touchUpY = mouseY - j;
							returningStackDestSlot = clickedSlot;
							returningStack = draggedStack;
							returningStackTime = Minecraft.getSystemTime();
						}
					} else if (!draggedStack.isEmpty()) {
						touchUpX = mouseX - i;
						touchUpY = mouseY - j;
						returningStackDestSlot = clickedSlot;
						returningStack = draggedStack;
						returningStackTime = Minecraft.getSystemTime();
					}

					draggedStack = ItemStack.EMPTY;
					clickedSlot = null;
				}
			} else if (dragSplitting && !dragSplittingSlots.isEmpty()) {
				handleMouseClick(null, -999, Container.getQuickcraftMask(0, dragSplittingLimit), ClickType.QUICK_CRAFT);

				for (Slot slot1 : dragSplittingSlots) {
					handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, dragSplittingLimit), ClickType.QUICK_CRAFT);
				}

				handleMouseClick(null, -999, Container.getQuickcraftMask(2, dragSplittingLimit), ClickType.QUICK_CRAFT);
			} else if (!mc.player.inventory.getItemStack().isEmpty()) {
				if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100)) {
					handleMouseClick(slot, k, state, ClickType.CLONE);
				} else {
					boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

					if (flag1) {
						shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
				}
			}
		}

		if (mc.player.inventory.getItemStack().isEmpty()) {
			lastClickTime = 0L;
		}

		dragSplitting = false;
	}

	/**
	 * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
	 * pointY
	 */
	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		int i = guiLeft;
		int j = guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1 && pointY < rectY + rectHeight + 1;
	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null) {
			slotId = slotIn.slotNumber;
		}

		mc.playerController.windowClick(binderContainer.windowId, slotId, mouseButton, type, mc.player);
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			mc.player.closeScreen();
		}

		checkHotbarKeys(keyCode);

		if (hoveredSlot != null && hoveredSlot.getHasStack()) {
			if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
			} else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
				handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
			}
		}
	}

	/**
	 * Checks whether a hotbar key (to swap the hovered item with an item in the hotbar) has been pressed. If so, it
	 * swaps the given items.
	 * Returns true if a hotbar key was pressed.
	 */
	protected boolean checkHotbarKeys(int keyCode) {
		if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null) {
			for (int i = 0; i < 9; ++i) {
				if (mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode)) {
					handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, i, ClickType.SWAP);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (!mc.player.isEntityAlive() || mc.player.isDead) {
			mc.player.closeScreen();
		}
	}

	/* ======================================== FORGE START =====================================*/

	/**
	 * Returns the slot that is currently displayed under the mouse.
	 */
	@javax.annotation.Nullable
	public Slot getSlotUnderMouse() {
		return hoveredSlot;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	/* ======================================== FORGE END   =====================================*/

}