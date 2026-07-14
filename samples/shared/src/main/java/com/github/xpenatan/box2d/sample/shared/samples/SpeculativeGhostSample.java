package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Speculative Ghost sample. */
public final class SpeculativeGhostSample extends AbstractBox2DSample {
    public SpeculativeGhostSample() {
        B2Body ground=addGroundSegment(-10,0,10,0); addOffsetBoxShape(ground,1,.1f,0,.9f,0,0,.6f,0,0);
        B2Body body=createDynamicBody(.015f,2.515f,0); body.SetGravityScale(0); setLinearVelocity(body,7.5f,-7.5f);
        addBoxShape(body,.25f,.25f,1,.6f,0,0);
    }
}
