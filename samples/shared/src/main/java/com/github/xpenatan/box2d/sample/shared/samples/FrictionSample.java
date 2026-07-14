package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Shapes / Friction sample. */
public final class FrictionSample extends AbstractBox2DSample {
    public FrictionSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addSegmentShape(ground, -40.0f, 0.0f, 40.0f, 0.0f, 0.0f, 0.2f, 0.0f);
        addOffsetBoxShape(ground, 13.0f, 0.25f, -4.0f, 22.0f, -0.25f, 0, .2f, 0, 0);
        addOffsetBoxShape(ground, 0.25f, 1.0f, 10.5f, 19.0f, 0, 0, .2f, 0, 0);
        addOffsetBoxShape(ground, 13.0f, 0.25f, 4.0f, 14.0f, 0.25f, 0, .2f, 0, 0);
        addOffsetBoxShape(ground, 0.25f, 1.0f, -10.5f, 11.0f, 0, 0, .2f, 0, 0);
        addOffsetBoxShape(ground, 13.0f, 0.25f, -4.0f, 6.0f, -0.25f, 0, .2f, 0, 0);
        float[] friction = { 0.75f, 0.5f, 0.35f, 0.1f, 0.0f };
        for(int i = 0; i < friction.length; i++) addDynamicBox(-15.0f + 4.0f * i, 28.0f,
                0.5f, 0.5f, 0.0f, 25.0f, friction[i], 0.0f, 0.0f);
    }
}
