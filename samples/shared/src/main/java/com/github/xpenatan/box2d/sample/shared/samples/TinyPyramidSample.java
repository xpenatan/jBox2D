package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Robustness / Tiny Pyramid sample. */
public final class TinyPyramidSample extends AbstractBox2DSample {
    private final float extent = 0.025f;

    public TinyPyramidSample() {
        addStaticBox(0.0f, -1.0f, 5.0f, 1.0f, 0.0f);
        int baseCount = 30;
        for(int row = 0; row < baseCount; row++) {
            float y = (2.0f * row + 1.0f) * extent;
            for(int column = row; column < baseCount; column++) {
                float x = (row + 1.0f) * extent + 2.0f * (column - row) * extent - baseCount * extent;
                addDynamicBox(x, y, extent, extent);
            }
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.text("5.0cm squares"));
    }
}
