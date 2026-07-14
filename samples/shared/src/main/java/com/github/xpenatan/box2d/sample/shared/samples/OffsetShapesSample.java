package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;

/** Java port of Box2D 3.1.1's Shapes / Offset sample. */
public final class OffsetShapesSample extends AbstractBox2DSample {
    public OffsetShapesSample() {
        B2Body ground = createStaticBody(-1.0f, 1.0f, 0.0f);
        addOffsetBoxShape(ground, 1, 1, 10, -2, 0.5f * PI, 0, .6f, 0, 0);
        B2Body capsule = createDynamicBody(13.5f, -0.75f, 0.0f);
        addCapsuleShape(capsule, -5, 1, -4, 1, .25f, 1, .6f, 0, 0);
        B2Body box = createDynamicBody(0, 0, 0);
        addOffsetBoxShape(box, .75f, .5f, 9, 2, .5f * PI, 1, .6f, 0, 0);
    }

    @Override public void draw(Box2DSampleDraw draw) {
        draw.segment(0, 0, .5f, 0, 0xFF0000FF);
        draw.segment(0, 0, 0, .5f, 0x00FF00FF);
    }
}
