package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Joints / Filter Joint sample. */
public final class FilterJointSample extends AbstractBox2DSample {
    public FilterJointSample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        B2Body body1 = addDynamicBox(-4.0f, 2.0f, 2.0f, 2.0f);
        B2Body body2 = addDynamicBox(4.0f, 2.0f, 2.0f, 2.0f);
        addFilterJoint(body1, body2);
    }
}
