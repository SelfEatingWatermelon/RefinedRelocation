package com.dynious.refinedrelocation.repack.codechicken.lib.render.uv;

import com.dynious.refinedrelocation.repack.codechicken.lib.vec.IrreversibleTransformationException;
import net.minecraft.util.IIcon;

public class MultiIconTransformation extends UVTransformation {
    public IIcon[] icons;

    public MultiIconTransformation(IIcon... icons) {
        this.icons = icons;
    }

    @Override
    public void apply(UV uv) {
        IIcon icon = icons[uv.tex % icons.length];
        uv.u = icon.getInterpolatedU(uv.u * 16);
        uv.v = icon.getInterpolatedV(uv.v * 16);
    }

    @Override
    public UVTransformation inverse() {
        throw new IrreversibleTransformationException(this);
    }
}
