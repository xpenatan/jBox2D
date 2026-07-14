package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Shapes / Ellipse sample. */
public final class EllipseSample extends AbstractBox2DSample {
    public EllipseSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addOffsetBoxShape(ground, 20, 1, 0, -1, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, 19, 5, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, -19, 5, 0, 0, .6f, 0, 0);
        float[] diamond = { 0, -.25f, .05f, -.075f, .05f, .075f, 0, .25f, -.05f, .075f, -.05f, -.075f };
        for(int row = 0; row < 10; row++) for(int column = 0; column < 10; column++) {
            B2Body body = createDynamicBody(-5.0f + column, 2.0f + row, 0.0f);
            addPolygonShape(body, diamond, 0.2f, 1, .6f, 0, .2f);
        }
    }
}
