package com.is.mtc.binder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.is.mtc.Reference;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Tools;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector2f;

import com.is.mtc.packet.MTCMessage;
import com.is.mtc.MineTradingCards;

public class BinderItemInterfaceContainer extends GuiContainer {
	/** The X size of the inventory window in pixels. */
	protected int xSize = 242;
	/** The Y size of the inventory window in pixels. */
	protected int ySize = 222;
	/** A list of the players inventory slots */
	public Container inventorySlots;
	/** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
	protected int guiLeft;
	/** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
	protected int guiTop;
	private Slot theSlot;
	/** Used when touchscreen is enabled. */
	private Slot clickedSlot;
	/** Used when touchscreen is enabled. */
	private boolean isRightMouseClick;
	/** Used when touchscreen is enabled */
	private ItemStack draggedStack;
	private int touchUpX;
	private int touchUpY;
	private Slot returningStackDestSlot;
	private long returningStackTime;
	/** Used when touchscreen is enabled */
	private ItemStack returningStack;
	private Slot currentDragTargetSlot;
	private long dragItemDropDelay;
	protected final Set dragSplittingSlots = new HashSet();
	protected boolean dragSplitting;
	private int dragSplittingLimit;
	private int dragSplittingButton;
	private boolean ignoreMouseUp;
	private int dragSplittingRemnant;
	private long lastClickTime;
	private Slot lastClickSlot;
	private int lastClickButton;
	private boolean doubleClick;
	private ItemStack shiftClickedSlot;

	/*-*/
	private static final Vector2f UI_SIZE = new Vector2f(242, 222);
	private BinderItemContainer binderItemContainer;

	public static final int LESS1 = 0, LESS2 = 1, LESS3 = 2;
	public static final int MORE1 = 3, MORE2 = 4, MORE3 = 5;
	public static final int MODE_SWITCH = 6;

	/*-*/

	public BinderItemInterfaceContainer(BinderItemContainer binderItemContainer) {
		super(binderItemContainer);
		this.inventorySlots = binderItemContainer;
		this.ignoreMouseUp = true;

		xSize = (int)UI_SIZE.x;
		ySize = (int)UI_SIZE.y;
		this.binderItemContainer = binderItemContainer;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		Vector2f drawPos = new Vector2f((width - UI_SIZE.x) / 2, (height - UI_SIZE.y) / 2);

		super.initGui();

		this.mc.player.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		//make buttons
		//id, x, y, width, height, text
		buttonList.add(new GuiButton(LESS1, (int)drawPos.x + 11, (int)drawPos.y + 144, 20, 20, "-"));
		buttonList.add(new GuiButton(LESS2, (int)drawPos.x + 11, (int)drawPos.y + 167, 20, 20, "--"));
		buttonList.add(new GuiButton(LESS3, (int)drawPos.x + 11, (int)drawPos.y + 190, 20, 20, "---"));

		buttonList.add(new GuiButton(MORE1, (int)drawPos.x + 210, (int)drawPos.y + 144, 20, 20, "+"));
		buttonList.add(new GuiButton(MORE2, (int)drawPos.x + 210, (int)drawPos.y + 167, 20, 20, "++"));
		buttonList.add(new GuiButton(MORE3, (int)drawPos.x + 210, (int)drawPos.y + 190, 20, 20, "+++"));

		buttonList.add(new GuiButton(MODE_SWITCH, (int)drawPos.x + 27, (int)drawPos.y + 126, 52, 12, BinderItem.MODE_STR[binderItemContainer.getBinderStack().getTagCompound().getInteger("mode_mtc")]));
	}

	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case LESS1:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS1));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), -1);
			break;
		case LESS2:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS2));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), -4);
			break;
		case LESS3:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(LESS3));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), -8);
			break;

		case MORE1:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE1));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), 1);
			break;
		case MORE2:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE2));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), 4);
			break;
		case MORE3:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MORE3));
			BinderItem.changePageBy(binderItemContainer.getBinderStack(), 8);
			break;

		case MODE_SWITCH:
			MineTradingCards.simpleNetworkWrapper.sendToServer(new MTCMessage(MODE_SWITCH));
			Vector2f drawPos = new Vector2f((width - UI_SIZE.x) / 2, (height - UI_SIZE.y) / 2);
			int mode = binderItemContainer.getBinderStack().getTagCompound().getInteger("mode_mtc");

			binderItemContainer.getBinderStack().getTagCompound().setInteger("mode_mtc", mode == BinderItem.MODE_STD ? BinderItem.MODE_FIL : BinderItem.MODE_STD);
			buttonList.remove(6);
			buttonList.add(new GuiButton(MODE_SWITCH, (int)drawPos.x + 27, (int)drawPos.y + 126, 52, 12, BinderItem.MODE_STR[binderItemContainer.getBinderStack().getTagCompound().getInteger("mode_mtc")]));
			break;
		}
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}

	// Adapted drawing
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY) // Custom for 01 size
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferBuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferBuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		bufferBuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		int k = this.guiLeft;
		int l = this.guiTop;
		this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef(k, l, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		this.theSlot = null;
		short short1 = 240;
		short short2 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k1;

		//for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
		for (int i1 = 0; i1 < 36; ++i1) // Draw inventory
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
			this.func_146977_a(slot);

			if (this.isMouseOverSlot(slot, mouseX, mouseY))
			{
				this.theSlot = slot;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int j1 = slot.xPos;
				k1 = slot.yPos;
				GL11.glColorMask(true, true, true, false);
				this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		for (int i1 = 0; i1 < 8; ++i1) // Draw binder
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1 + 36 + (binderItemContainer.getCurrentPage() * BinderItemInventory.getStacksPerPage()));
			this.func_146977_a(slot);

			if (this.isMouseOverSlot(slot, mouseX, mouseY))
			{
				this.theSlot = slot;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int j1 = slot.xPos;
				k1 = slot.yPos;
				GL11.glColorMask(true, true, true, false);
				this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		//Forge: Force lighting to be disabled as there are some issue where lighting would
		//incorrectly be applied based on items that are in the inventory.
		GL11.glDisable(GL11.GL_LIGHTING);
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		GL11.glEnable(GL11.GL_LIGHTING);
		InventoryPlayer inventoryplayer = this.mc.player.inventory;
		ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

		if (itemstack != null)
		{
			byte b0 = 8;
			k1 = this.draggedStack == null ? 8 : 16;
			String s = null;

			if (this.draggedStack != null && this.isRightMouseClick)
			{
				itemstack = itemstack.copy();
				itemstack.setCount(MathHelper.ceil(itemstack.getCount() / 2.0F));
			}
			else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
			{
				itemstack = itemstack.copy();
				itemstack.setCount(this.dragSplittingRemnant);

				if (itemstack.getCount() == 0)
				{
					s = "" + ChatFormatting.YELLOW + "0";
				}
			}

			this.drawItemStack(itemstack, mouseX - k - b0, mouseY - l - k1, s);
		}

		if (this.returningStack != null)
		{
			float f1 = (Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

			if (f1 >= 1.0F)
			{
				f1 = 1.0F;
				this.returningStack = null;
			}

			k1 = this.returningStackDestSlot.xPos - this.touchUpX;
			int j2 = this.returningStackDestSlot.yPos - this.touchUpY;
			int l1 = this.touchUpX + (int)(k1 * f1);
			int i2 = this.touchUpY + (int)(j2 * f1);
			this.drawItemStack(this.returningStack, l1, i2, (String)null);
		}

		GL11.glPopMatrix();

		if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
		{
			ItemStack itemstack1 = this.theSlot.getStack();
			this.renderToolTip(itemstack1, mouseX, mouseY);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	private void drawItemStack(ItemStack item, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
		GL11.glTranslatef(0.0F, 0.0F, 32.0F);
		this.zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		FontRenderer font = null;
		if (item != null) font = item.getItem().getFontRenderer(item);
		if (font == null) font = fontRenderer;
		itemRender.renderItemAndEffectIntoGUI(item, p_146982_2_, p_146982_3_);
		itemRender.renderItemOverlayIntoGUI(font, item, p_146982_2_, p_146982_3_ - (this.draggedStack == null ? 0 : 8), p_146982_4_);
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {}

	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		Vector2f drawPos = new Vector2f((width - UI_SIZE.x) / 2, (height - UI_SIZE.y) / 2);

		drawDefaultBackground();
		mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MODID + ":textures/gui/ui_binder_3.png"));
		drawTexturedModalRect((int)drawPos.x, (int)drawPos.y, 0, 0, (int)UI_SIZE.x, (int)UI_SIZE.y);

		for (int i = 0; binderItemContainer != null && i < 2; ++i) { // Display cards illustrations
			for (int j = 0; j < 4; ++j) {
				int x = j + i * 4 + (binderItemContainer.getCurrentPage() * BinderItemInventory.getStacksPerPage());
				ItemStack stack = binderItemContainer.getCardStackAtIndex(x);

				if (Tools.isValidCard(stack)) { // Is a valid itemstack for card
					CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

					if (cStruct != null && cStruct.getDynamicTexture() != null) { // Card data and illustration are corrects

						cStruct.preloadResource(mc.getTextureManager());
						mc.getTextureManager().bindTexture(cStruct.getResourceLocation());
						drawTexturedModalRect((int)drawPos.x + 8 + j * 58, (int)drawPos.y + 8 + i * 64, 52, 52);
					}
					else
					{
						drawString(fontRenderer, "s" + (x + 1),
								(int)drawPos.x + 8 + j * 58, (int)drawPos.y + 8 + i * 64, 0xFFFFFF);
					}
				}
				else
				{
					drawString(fontRenderer, "s" + (x + 1),
							(int)drawPos.x + 8 + j * 58, (int)drawPos.y + 8 + i * 64, 0xFFFFFF);
				}
			}
		}
		drawCenteredString(fontRenderer, "Page: " + (binderItemContainer.getCurrentPage() + 1) + "/" + BinderItemInventory.getTotalPages(), (int)drawPos.x + 120, (int)drawPos.y + 127, 0xFFFFFF);
	}

	private void func_146977_a(Slot p_146977_1_)
	{
		int i = p_146977_1_.xPos;
		int j = p_146977_1_.yPos;
		ItemStack itemstack = p_146977_1_.getStack();
		boolean flag = false;
		boolean flag1 = p_146977_1_ == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
		ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
		String s = null;

		if (p_146977_1_ == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null)
		{
			itemstack = itemstack.copy();
			itemstack.setCount(itemstack.getCount() / 2);
		}
		else if (this.dragSplitting && this.dragSplittingSlots.contains(p_146977_1_) && itemstack1 != null)
		{
			if (this.dragSplittingSlots.size() == 1)
			{
				return;
			}

			if (Container.canAddItemToSlot(p_146977_1_, itemstack1, true) && this.inventorySlots.canDragIntoSlot(p_146977_1_))
			{
				itemstack = itemstack1.copy();
				flag = true;
				Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, p_146977_1_.getStack() == null ? 0 : p_146977_1_.getStack().getCount());

				if (itemstack.getCount() > itemstack.getMaxStackSize())
				{
					s = ChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
					itemstack.setCount(itemstack.getMaxStackSize());
				}

				if (itemstack.getCount() > p_146977_1_.getSlotStackLimit())
				{
					s = ChatFormatting.YELLOW + "" + p_146977_1_.getSlotStackLimit();
					itemstack.setCount(p_146977_1_.getSlotStackLimit());
				}
			}
			else
			{
				this.dragSplittingSlots.remove(p_146977_1_);
				this.func_146980_g();
			}
		}

		this.zLevel = 100.0F;
		itemRender.zLevel = 100.0F;

		/*if (itemstack == null)
		{
			IIcon iicon = p_146977_1_.getBackgroundIconIndex();

			if (iicon != null)
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND); // Forge: Blending needs to be enabled for this.
				this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
				this.drawTexturedModelRectFromIcon(i, j, iicon, 16, 16);
				GL11.glDisable(GL11.GL_BLEND); // Forge: And clean that up
				GL11.glEnable(GL11.GL_LIGHTING);
				flag1 = true;
			}
		}*/

		if (!flag1)
		{
			if (flag)
			{
				drawRect(i, j, i + 16, j + 16, -2130706433);
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			itemRender.renderItemAndEffectIntoGUI(itemstack, i, j);
			itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, i, j, s);
		}

		itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	private void func_146980_g()
	{
		ItemStack itemstack = this.mc.player.inventory.getItemStack();

		if (itemstack != null && this.dragSplitting)
		{
			this.dragSplittingRemnant = itemstack.getCount();
			ItemStack itemstack1;
			int i;

			for (Iterator iterator = this.dragSplittingSlots.iterator(); iterator.hasNext(); this.dragSplittingRemnant -= itemstack1.getCount() - i)
			{
				Slot slot = (Slot)iterator.next();
				itemstack1 = itemstack.copy();
				i = slot.getStack() == null ? 0 : slot.getStack().getCount();
				Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);

				if (itemstack1.getCount() > itemstack1.getMaxStackSize())
				{
					itemstack1.setCount(itemstack1.getMaxStackSize());
				}

				if (itemstack1.getCount() > slot.getSlotStackLimit())
				{
					itemstack1.setCount(slot.getSlotStackLimit());
				}
			}
		}
	}

	/**
	 * Returns the slot at the given coordinates or null if there is none.
	 */
	private Slot getSlotAtPosition(int p_146975_1_, int p_146975_2_)
	{
		for (int k = 0; k < this.inventorySlots.inventorySlots.size(); ++k)
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(k);

			if (this.isMouseOverSlot(slot, p_146975_1_, p_146975_2_))
			{
				return slot;
			}
		}

		return null;
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException {
		super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
		boolean flag = p_73864_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
		Slot slot = this.getSlotAtPosition(p_73864_1_, p_73864_2_);
		long l = Minecraft.getSystemTime();
		this.doubleClick = this.lastClickSlot == slot && l - this.lastClickTime < 250L && this.lastClickButton == p_73864_3_;
		this.ignoreMouseUp = false;

		if (p_73864_3_ == 0 || p_73864_3_ == 1 || flag)
		{
			int i1 = this.guiLeft;
			int j1 = this.guiTop;
			boolean flag1 = p_73864_1_ < i1 || p_73864_2_ < j1 || p_73864_1_ >= i1 + this.xSize || p_73864_2_ >= j1 + this.ySize;
			int k1 = -1;

			if (slot != null)
			{
				k1 = slot.slotNumber;
			}

			if (flag1)
			{
				k1 = -999;
			}

			if (this.mc.gameSettings.touchscreen && flag1 && this.mc.player.inventory.getItemStack() == null)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				return;
			}

			if (k1 != -1)
			{
				if (this.mc.gameSettings.touchscreen)
				{
					if (slot != null && slot.getHasStack())
					{
						this.clickedSlot = slot;
						this.draggedStack = null;
						this.isRightMouseClick = p_73864_3_ == 1;
					}
					else
					{
						this.clickedSlot = null;
					}
				}
				else if (!this.dragSplitting)
				{
					if (this.mc.player.inventory.getItemStack() == null)
					{
						if (p_73864_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
						{
							this.handleMouseClick(slot, k1, p_73864_3_, 3);
						}
						else
						{
							boolean flag2 = k1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
							byte b0 = 0;

							if (flag2)
							{
								this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
								b0 = 1;
							}
							else if (k1 == -999)
							{
								b0 = 4;
							}

							this.handleMouseClick(slot, k1, p_73864_3_, b0);
						}

						this.ignoreMouseUp = true;
					}
					else
					{
						this.dragSplitting = true;
						this.dragSplittingButton = p_73864_3_;
						this.dragSplittingSlots.clear();

						if (p_73864_3_ == 0)
						{
							this.dragSplittingLimit = 0;
						}
						else if (p_73864_3_ == 1)
						{
							this.dragSplittingLimit = 1;
						}
					}
				}
			}
		}

		this.lastClickSlot = slot;
		this.lastClickTime = l;
		this.lastClickButton = p_73864_3_;
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
	 * lastButtonClicked & timeSinceMouseClick.
	 */
	@Override
	protected void mouseClickMove(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_)
	{
		Slot slot = this.getSlotAtPosition(p_146273_1_, p_146273_2_);
		ItemStack itemstack = this.mc.player.inventory.getItemStack();

		if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
		{
			if (p_146273_3_ == 0 || p_146273_3_ == 1)
			{
				if (this.draggedStack == null)
				{
					if (slot != this.clickedSlot)
					{
						this.draggedStack = this.clickedSlot.getStack().copy();
					}
				}
				else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false))
				{
					long i1 = Minecraft.getSystemTime();

					if (this.currentDragTargetSlot == slot)
					{
						if (i1 - this.dragItemDropDelay > 500L)
						{
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
							this.handleMouseClick(slot, slot.slotNumber, 1, 0);
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
							this.dragItemDropDelay = i1 + 750L;
							this.draggedStack.setCount(draggedStack.getCount() - 1);
						}
					}
					else
					{
						this.currentDragTargetSlot = slot;
						this.dragItemDropDelay = i1;
					}
				}
			}
		}
		else if (this.dragSplitting && slot != null && itemstack != null && itemstack.getCount() > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot))
		{
			this.dragSplittingSlots.add(slot);
			this.func_146980_g();
		}
	}

	/**
	 * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
	 * mouseMove, which==0 or which==1 is mouseUp
	 */

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		Slot slot = this.getSlotAtPosition(mouseX, mouseY);
		int l = this.guiLeft;
		int i1 = this.guiTop;
		boolean flag = mouseX < l || mouseY < i1 || mouseX >= l + this.xSize || mouseY >= i1 + this.ySize;
		int j1 = -1;

		if (slot != null)
		{
			j1 = slot.slotNumber;
		}

		if (flag)
		{
			j1 = -999;
		}

		Slot slot1;
		Iterator iterator;

		if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot(null, slot))
		{
			if (isShiftKeyDown())
			{
				if (slot != null && slot.inventory != null && this.shiftClickedSlot != null)
				{
					iterator = this.inventorySlots.inventorySlots.iterator();

					while (iterator.hasNext())
					{
						slot1 = (Slot)iterator.next();

						if (slot1 != null && slot1.canTakeStack(this.mc.player) && slot1.getHasStack() && slot1.inventory == slot.inventory && Container.canAddItemToSlot(slot1, this.shiftClickedSlot, true))
						{
							this.handleMouseClick(slot1, slot1.slotNumber, state, 1);
						}
					}
				}
			}
			else
			{
				this.handleMouseClick(slot, j1, state, 6);
			}

			this.doubleClick = false;
			this.lastClickTime = 0L;
		}
		else
		{
			if (this.dragSplitting && this.dragSplittingButton != state)
			{
				this.dragSplitting = false;
				this.dragSplittingSlots.clear();
				this.ignoreMouseUp = true;
				return;
			}

			if (this.ignoreMouseUp)
			{
				this.ignoreMouseUp = false;
				return;
			}

			boolean flag1;

			if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
			{
				if (state == 0 || state == 1)
				{
					if (this.draggedStack == null && slot != this.clickedSlot)
					{
						this.draggedStack = this.clickedSlot.getStack();
					}

					flag1 = Container.canAddItemToSlot(slot, this.draggedStack, false);

					if (j1 != -1 && this.draggedStack != null && flag1)
					{
						this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
						this.handleMouseClick(slot, j1, 0, 0);

						if (this.mc.player.inventory.getItemStack() != null)
						{
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
							this.touchUpX = mouseX - l;
							this.touchUpY = mouseY - i1;
							this.returningStackDestSlot = this.clickedSlot;
							this.returningStack = this.draggedStack;
							this.returningStackTime = Minecraft.getSystemTime();
						}
						else
						{
							this.returningStack = null;
						}
					}
					else if (this.draggedStack != null)
					{
						this.touchUpX = mouseX - l;
						this.touchUpY = mouseY - i1;
						this.returningStackDestSlot = this.clickedSlot;
						this.returningStack = this.draggedStack;
						this.returningStackTime = Minecraft.getSystemTime();
					}

					this.draggedStack = null;
					this.clickedSlot = null;
				}
			}
			else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
			{
				this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), 5);
				iterator = this.dragSplittingSlots.iterator();

				while (iterator.hasNext())
				{
					slot1 = (Slot)iterator.next();
					this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), 5);
				}

				this.handleMouseClick(null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), 5);
			}
			else if (this.mc.player.inventory.getItemStack() != null)
			{
				if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
				{
					this.handleMouseClick(slot, j1, state, 3);
				}
				else
				{
					flag1 = j1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

					if (flag1)
					{
						this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
					}

					this.handleMouseClick(slot, j1, state, flag1 ? 1 : 0);
				}
			}
		}

		if (this.mc.player.inventory.getItemStack() == null)
		{
			this.lastClickTime = 0L;
		}

		this.dragSplitting = false;
	}

	/*@Override
	protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
	{
		super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_); //Forge, Call parent to release buttons
		Slot slot = this.getSlotAtPosition(p_146286_1_, p_146286_2_);
		int l = this.guiLeft;
		int i1 = this.guiTop;
		boolean flag = p_146286_1_ < l || p_146286_2_ < i1 || p_146286_1_ >= l + this.xSize || p_146286_2_ >= i1 + this.ySize;
		int j1 = -1;

		if (slot != null)
		{
			j1 = slot.slotNumber;
		}

		if (flag)
		{
			j1 = -999;
		}

		Slot slot1;
		Iterator iterator;

		if (this.field_146993_M && slot != null && p_146286_3_ == 0 && this.inventorySlots.func_94530_a((ItemStack)null, slot))
		{
			if (isShiftKeyDown())
			{
				if (slot != null && slot.inventory != null && this.field_146994_N != null)
				{
					iterator = this.inventorySlots.inventorySlots.iterator();

					while (iterator.hasNext())
					{
						slot1 = (Slot)iterator.next();

						if (slot1 != null && slot1.canTakeStack(this.mc.player) && slot1.getHasStack() && slot1.inventory == slot.inventory && Container.func_94527_a(slot1, this.field_146994_N, true))
						{
							this.handleMouseClick(slot1, slot1.slotNumber, p_146286_3_, 1);
						}
					}
				}
			}
			else
			{
				this.handleMouseClick(slot, j1, p_146286_3_, 6);
			}

			this.field_146993_M = false;
			this.field_146997_J = 0L;
		}
		else
		{
			if (this.field_147007_t && this.field_146988_G != p_146286_3_)
			{
				this.field_147007_t = false;
				this.field_147008_s.clear();
				this.field_146995_H = true;
				return;
			}

			if (this.field_146995_H)
			{
				this.field_146995_H = false;
				return;
			}

			boolean flag1;

			if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
			{
				if (p_146286_3_ == 0 || p_146286_3_ == 1)
				{
					if (this.draggedStack == null && slot != this.clickedSlot)
					{
						this.draggedStack = this.clickedSlot.getStack();
					}

					flag1 = Container.canAddItemToSlot(slot, this.draggedStack, false);

					if (j1 != -1 && this.draggedStack != null && flag1)
					{
						this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_146286_3_, 0);
						this.handleMouseClick(slot, j1, 0, 0);

						if (this.mc.player.inventory.getItemStack() != null)
						{
							this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_146286_3_, 0);
							this.field_147011_y = p_146286_1_ - l;
							this.field_147010_z = p_146286_2_ - i1;
							this.returningStackDestSlot = this.clickedSlot;
							this.returningStack = this.draggedStack;
							this.returningStackTime = Minecraft.getSystemTime();
						}
						else
						{
							this.returningStack = null;
						}
					}
					else if (this.draggedStack != null)
					{
						this.field_147011_y = p_146286_1_ - l;
						this.field_147010_z = p_146286_2_ - i1;
						this.returningStackDestSlot = this.clickedSlot;
						this.returningStack = this.draggedStack;
						this.returningStackTime = Minecraft.getSystemTime();
					}

					this.draggedStack = null;
					this.clickedSlot = null;
				}
			}
			else if (this.field_147007_t && !this.field_147008_s.isEmpty())
			{
				this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.field_146987_F), 5);
				iterator = this.field_147008_s.iterator();

				while (iterator.hasNext())
				{
					slot1 = (Slot)iterator.next();
					this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.field_146987_F), 5);
				}

				this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.field_146987_F), 5);
			}
			else if (this.mc.player.inventory.getItemStack() != null)
			{
				if (p_146286_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100)
				{
					this.handleMouseClick(slot, j1, p_146286_3_, 3);
				}
				else
				{
					flag1 = j1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

					if (flag1)
					{
						this.field_146994_N = slot != null && slot.getHasStack() ? slot.getStack() : null;
					}

					this.handleMouseClick(slot, j1, p_146286_3_, flag1 ? 1 : 0);
				}
			}
		}

		if (this.mc.player.inventory.getItemStack() == null)
		{
			this.field_146997_J = 0L;
		}

		this.field_147007_t = false;
	}*/

	/**
	 * Returns if the passed mouse position is over the specified slot.
	 */
	private boolean isMouseOverSlot(Slot p_146981_1_, int p_146981_2_, int p_146981_3_)
	{
		return this.func_146978_c(p_146981_1_.xPos, p_146981_1_.yPos, 16, 16, p_146981_2_, p_146981_3_);
	}

	protected boolean func_146978_c(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_)
	{
		int k1 = this.guiLeft;
		int l1 = this.guiTop;
		p_146978_5_ -= k1;
		p_146978_6_ -= l1;
		return p_146978_5_ >= p_146978_1_ - 1 && p_146978_5_ < p_146978_1_ + p_146978_3_ + 1 && p_146978_6_ >= p_146978_2_ - 1 && p_146978_6_ < p_146978_2_ + p_146978_4_ + 1;
	}

	protected void handleMouseClick(Slot p_146984_1_, int p_146984_2_, int p_146984_3_, int p_146984_4_)
	{
		if (p_146984_1_ != null)
		{
			p_146984_2_ = p_146984_1_.slotNumber;
		}

		this.mc.playerController.windowClick(this.inventorySlots.windowId, p_146984_2_, p_146984_3_, ClickType.values()[p_146984_4_], this.mc.player);
	}

	/**
	 * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_)
	{
		if (p_73869_2_ == 1 || p_73869_2_ == this.mc.gameSettings.keyBindInventory.getKeyCode())
		{
			this.mc.player.closeScreen();
		}

		this.checkHotbarKeys(p_73869_2_);

		if (this.theSlot != null && this.theSlot.getHasStack())
		{
			if (p_73869_2_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode())
			{
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
			}
			else if (p_73869_2_ == this.mc.gameSettings.keyBindDrop.getKeyCode())
			{
				this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
			}
		}
	}

	/**
	 * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack.
	 */
	protected boolean checkHotbarKeys(int p_146983_1_)
	{
		if (this.mc.player.inventory.getItemStack() == null && this.theSlot != null)
		{
			for (int j = 0; j < 9; ++j)
			{
				if (p_146983_1_ == this.mc.gameSettings.keyBindsHotbar[j].getKeyCode())
				{
					this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, j, 2);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override
	public void onGuiClosed()
	{
		if (this.mc.player != null)
		{
			this.inventorySlots.onContainerClosed(this.mc.player);
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if (!this.mc.player.isEntityAlive() || this.mc.player.isDead)
		{
			this.mc.player.closeScreen();
		}
	}
}