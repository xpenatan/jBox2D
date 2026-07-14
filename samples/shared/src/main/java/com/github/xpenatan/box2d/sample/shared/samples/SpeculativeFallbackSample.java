package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Speculative Fallback sample. */
public final class SpeculativeFallbackSample extends AbstractBox2DSample {
    public SpeculativeFallbackSample() {
        B2Body ground = addGroundSegment(-10,0,10,0);
        addPolygonShape(ground,new float[]{-2,4,2,4,2,4.1f,-.5f,4.2f,-2,4.2f},0,0,.6f,0,0);
        B2Body body = createDynamicBody(8,12,0); setLinearVelocity(body,0,-100);
        addOffsetBoxShape(body,2,.05f,-8,0,PI,1,.6f,0,0);
    }
}
