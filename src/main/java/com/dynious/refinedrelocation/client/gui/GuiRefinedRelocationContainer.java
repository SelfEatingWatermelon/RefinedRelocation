package com.dynious.refinedrelocation.client.gui;

import com.dynious.refinedrelocation.client.gui.widget.GuiWidgetBase;
import com.dynious.refinedrelocation.container.ContainerRefinedRelocation;
import com.dynious.refinedrelocation.lib.Settings;
import com.dynious.refinedrelocation.lib.Strings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiRefinedRelocationContainer extends GuiContainer implements IGuiParent {

    protected IGuiWidgetBase rootNode;
    private final List<String> tmpTooltip = new ArrayList<>();

    public GuiRefinedRelocationContainer(ContainerRefinedRelocation container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();

        rootNode = new GuiWidgetBase(0, 0, width, height) {
        };

        this.clearChildren();
    }

    public int getLeft() {
        return guiLeft;
    }

    public int getTop() {
        return guiTop;
    }

    @Override
    public void addChild(IGuiWidgetBase child) {
        this.rootNode.addChild(child);
    }

    @Override
    public void addChildren(List<IGuiWidgetBase> children) {
        this.rootNode.addChildren(children);
    }

    @Override
    public void clearChildren() {
        this.rootNode.clearChildren();
    }

    @Override
    public boolean removeChild(IGuiWidgetBase child) {
        return this.rootNode.removeChild(child);
    }

    @Override
    public void removeChildren(List<IGuiWidgetBase> children) {
        this.rootNode.removeChildren(children);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glPushMatrix();

        GL11.glColor4f(1, 1, 1, 1);
        rootNode.drawBackground(mouseX, mouseY);

        GL11.glPopMatrix();
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) -guiLeft, (float) -guiTop, 0.0F);

        GL11.glColor4f(1, 1, 1, 1);
        rootNode.drawForeground(mouseX, mouseY);

        GL11.glPopMatrix();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        IGuiWidgetBase child = rootNode.getChildAt(mouseX, mouseY);
        if (child != null) {
            tmpTooltip.clear();
            rootNode.getTooltip(tmpTooltip, mouseX, mouseY);
            if (!tmpTooltip.isEmpty()) {
                this.drawHoveringText(tmpTooltip, mouseX, mouseY, fontRendererObj);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        rootNode.update();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int type) {
        IGuiWidgetBase child = rootNode.getChildAt(mouseX, mouseY);
        if (child != null) {
            child.mouseClicked(mouseX, mouseY, type, isShiftKeyDown());
        }
        super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    public void handleMouseInput() {
        rootNode.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void keyTyped(char c, int i) {
        if (!rootNode.keyTyped(c, i)) {
            super.keyTyped(c, i);
        }
    }

    @Override
    public void mouseMovedOrUp(int par1, int par2, int par3) {
        rootNode.mouseMovedOrUp(par1, par2, par3);
        super.mouseMovedOrUp(par1, par2, par3);
    }

    @Override
    public ContainerRefinedRelocation getContainer() {
        return (ContainerRefinedRelocation) inventorySlots;
    }

    public static boolean isRestrictedAccess() {
        return Settings.ENABLE_ADVENTURE_MODE_RESTRICTION && Minecraft.getMinecraft().playerController.currentGameType.isAdventure();
    }

    public static boolean isRestrictedAccessWithError() {
        if (isRestrictedAccess()) {
            ChatComponentText chatComponent = new ChatComponentText(StatCollector.translateToLocal(Strings.ADVENTURE_MODE));
            chatComponent.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
            Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent);
            return true;
        }
        return false;
    }

}
