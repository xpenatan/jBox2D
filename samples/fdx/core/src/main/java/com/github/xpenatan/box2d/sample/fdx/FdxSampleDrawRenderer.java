package com.github.xpenatan.box2d.sample.fdx;

import com.github.xpenatan.box2d.fdx.FdxDebugRenderer;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import io.github.libfdx.graphics.GraphicsContext;

/** Adapts the reusable libfdx debug renderer to the shared sample drawing interface. */
public final class FdxSampleDrawRenderer extends FdxDebugRenderer implements Box2DSampleDraw {

    public FdxSampleDrawRenderer(GraphicsContext graphics) {
        super(graphics);
    }
}
