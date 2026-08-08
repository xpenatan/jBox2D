package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Speculative Ghost sample. */
public final class SpeculativeGhostSample extends AbstractBox2DSample {
    private final B2Body movingBody;
    private boolean velocityInitialized;

    public SpeculativeGhostSample() {
        B2Body ground=addGroundSegment(-10,0,10,0); addOffsetBoxShape(ground,1,.1f,0,.9f,0,0,.6f,0,0);
        movingBody=createDynamicBody(.015f,2.515f,0); movingBody.SetGravityScale(0); setLinearVelocity(movingBody,7.5f,-7.5f);
        addBoxShape(movingBody,.25f,.25f,1,.6f,0,0);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        if(!velocityInitialized) {
            float speed = 0.125f / deltaSeconds;
            setLinearVelocity(movingBody, speed, -speed);
            velocityInitialized = true;
        }
    }
}
