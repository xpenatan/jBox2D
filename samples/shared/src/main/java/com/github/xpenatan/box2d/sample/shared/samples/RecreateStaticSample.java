package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Shapes / Recreate Static sample. */
public final class RecreateStaticSample extends AbstractBox2DSample {
    private B2Body ground;

    public RecreateStaticSample() {
        addDynamicBox(0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        destroyBody(ground);
        ground = createStaticBody(0, 0, 0);
        B2ShapeDef def = shapeDef(0, .6f, 0, 0);
        def.SetInvokeContactCreation(true);
        B2Vec2 a = vector(-10, 0);
        B2Vec2 b = vector(10, 0);
        B2Segment segment = new B2Segment(a, b);
        createSegmentShape(ground, def, segment);
        release(segment, b, a, def);
    }
}
