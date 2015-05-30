package com.dynious.refinedrelocation.client.gui.widget;

import com.dynious.refinedrelocation.grid.filter.CustomUserFilter;
import com.dynious.refinedrelocation.lib.Strings;
import net.minecraft.util.StatCollector;

public class GuiUserFilter extends GuiWidgetBase {

    public static final int ICON_X = 116;
    public static final int ICON_Y = 238;

    public int inputHeight = 76;
    protected CustomUserFilter filter;
    protected boolean drawLabel = true;
    protected GuiLabel userFilterLabel;
    protected GuiButton userFilterHelp;
    protected GuiTextInputUserFilter userFilter;

    public GuiUserFilter(int x, int y, int w, int h, boolean drawLabel, CustomUserFilter filter) {
        super(x, y, w, h);
        this.drawLabel = drawLabel;
        this.filter = filter;

        userFilterLabel = new GuiLabel(this, x, y, StatCollector.translateToLocal(Strings.CUSTOM_FILTER));
        userFilterLabel.drawCentered = false;

        userFilterHelp = new GuiButton(this, userFilterLabel.x + userFilterLabel.w + 5, userFilterLabel.y - 1, 10, 10, 0, 128, null);
        userFilterHelp.setTooltipString(StatCollector.translateToLocal(Strings.NAME_MATCHING) + "\n\u00A77"
                + StatCollector.translateToLocal(Strings.WILDCARD_CHARACTER) + ": \u00A73*\n\u00A77"
                + StatCollector.translateToLocal(Strings.OREDICT_CHARACTER) + ": \u00A72!\n\u00A77"
                + StatCollector.translateToLocal(Strings.COMMA_SEPARATION));

        userFilter = new GuiTextInputUserFilter(this, x, y + h - inputHeight - 9, w, inputHeight, filter);
    }

}
