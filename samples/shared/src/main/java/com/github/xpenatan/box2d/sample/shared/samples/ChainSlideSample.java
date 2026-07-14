package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Chain Slide sample. */
public final class ChainSlideSample extends AbstractBox2DSample {
    public ChainSlideSample() {
        float[] points = new float[160]; int p = 0; float x = 20, y = 0;
        for(int i = 0; i < 20; i++) { points[p++] = x; points[p++] = y; x -= 2; }
        for(int i = 20; i < 40; i++) { points[p++] = x; points[p++] = y; y += 1; }
        for(int i = 40; i < 60; i++) { points[p++] = x; points[p++] = y; x += 2; }
        for(int i = 60; i < 80; i++) { points[p++] = x; points[p++] = y; y -= 1; }
        B2Body ground = createStaticBody(0, 0, 0); addChain(ground, points, true, .6f);
        B2Body ball = addDynamicCircle(-19.5f, .5f, .5f, 1, 0, 0, 0);
        setLinearVelocity(ball, 100, 0);
    }
}
