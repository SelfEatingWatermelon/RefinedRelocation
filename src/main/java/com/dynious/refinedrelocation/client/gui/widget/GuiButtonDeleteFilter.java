package com.dynious.refinedrelocation.client.gui.widget;

import com.dynious.refinedrelocation.client.gui.GuiFiltered;
import com.dynious.refinedrelocation.client.gui.IGuiParent;
import com.dynious.refinedrelocation.lib.Strings;
import com.dynious.refinedrelocation.network.NetworkHandler;
import com.dynious.refinedrelocation.network.packet.filter.MessageSetFilterType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.List;

public class GuiButtonDeleteFilter extends GuiButton {

    private final GuiFiltered parent;

    public GuiButtonDeleteFilter(GuiFiltered parent, int x, int y) {
        super(parent, x, y, 16, 16, 160, 80, "");
        this.parent = parent;
        setTooltipString(StatCollector.translateToLocal(Strings.DELETE_FILTER));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int type, boolean isShiftKeyDown) {
        if (isInsideBounds(mouseX, mouseY)) {
            int selectedFilterIndex = parent.getSelectedFilterIndex();
            NetworkHandler.INSTANCE.sendToServer(new MessageSetFilterType(selectedFilterIndex, ""));
            parent.getFilter().setFilterType(selectedFilterIndex, "");
        }
        super.mouseClicked(mouseX, mouseY, type, isShiftKeyDown);
    }

    @Override
    public boolean isInsideBounds(int x, int y) {
        return parent.hasFilterSelected() && super.isInsideBounds(x, y);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        if(parent.hasFilterSelected()) {
            super.drawBackground(mouseX, mouseY);
        }
    }
}
