package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Stacking / Capsule Stack sample. */
public final class CapsuleStackSample extends AbstractBox2DSample {
    public CapsuleStackSample() {
        addStaticBox(0.0f, -1.0f, 10.0f, 1.0f, 0.0f);
        float a = 0.25f;
        float y = 2.0f * a;
        for(int i = 0; i < 20; i++) {
            B2Body body = createDynamicBody(0.0f, y, 0.0f);
            addCapsuleShape(body, -4.0f * a, 0.0f, 4.0f * a, 0.0f, a, 1.0f, 0.6f, 0.0f, 0.0f);
            y += 3.0f * a;
        }
    }
}
