package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Robustness / HighMassRatio3 sample. */
public final class HighMassRatio3Sample extends AbstractBox2DSample {
    public HighMassRatio3Sample() {
        addStaticBox(0.0f, -1.0f, 50.0f, 1.0f, 0.0f);
        float[] triangle = { -0.5f, 0.0f, 0.5f, 0.0f, 0.0f, 1.0f };
        B2Body left = createDynamicBody(-9.0f, 0.5f, 0.0f);
        addPolygonShape(left, triangle, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        B2Body right = createDynamicBody(9.0f, 0.5f, 0.0f);
        addPolygonShape(right, triangle, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        addDynamicBox(0.0f, 14.0f, 10.0f, 10.0f);
    }
}
