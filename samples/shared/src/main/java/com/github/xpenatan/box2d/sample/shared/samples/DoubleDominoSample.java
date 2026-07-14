package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Stacking / Double Domino sample. */
public final class DoubleDominoSample extends AbstractBox2DSample {
    public DoubleDominoSample() {
        addStaticBox(0.0f, -1.0f, 100.0f, 1.0f, 0.0f);
        int count = 15;
        float x = -0.5f * count;
        for(int i = 0; i < count; i++) {
            B2Body body = addDynamicBox(x, 0.5f, 0.125f, 0.5f, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
            if(i == 0) applyLinearImpulse(body, 0.2f, 0.0f, x, 1.0f);
            x += 1.0f;
        }
    }
}
