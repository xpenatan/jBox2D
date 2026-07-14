package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Segment Slide sample. */
public final class SegmentSlideSample extends AbstractBox2DSample {
    public SegmentSlideSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addSegmentShape(ground, -40, 0, 40, 0, 0, .6f, 0);
        addSegmentShape(ground, 40, 0, 40, 10, 0, .6f, 0);
        B2Body ball = addDynamicCircle(-20, .7f, .5f); setLinearVelocity(ball, 100, 0);
    }
}
