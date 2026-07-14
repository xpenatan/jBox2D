package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Speculative Sliver sample. */
public final class SpeculativeSliverSample extends AbstractBox2DSample {
    public SpeculativeSliverSample() {
        addGroundSegment(-10,0,10,0);
        B2Body body=createDynamicBody(0,12,0); setLinearVelocity(body,0,-100);
        addPolygonShape(body,new float[]{-2,0,-1,0,2,.5f},0,1,.6f,0,0);
    }
}
