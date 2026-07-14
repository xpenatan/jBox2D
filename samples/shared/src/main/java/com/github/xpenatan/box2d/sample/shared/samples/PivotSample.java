package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Bodies / Pivot sample. */
public final class PivotSample extends AbstractBox2DSample {
    private final B2Body body;
    private final float lever = 3.0f;
    private float pivotVelocityX;
    private float pivotVelocityY;

    public PivotSample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        body = createDynamicBody(0.0f, 3.0f, 0.0f);
        setLinearVelocity(body, 5.0f, 0.0f);
        body.SetAngularVelocity(5.0f / lever);
        addBoxShape(body, 0.1f, lever, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2Vec2 velocity = body.GetLinearVelocity();
        B2Rot rotation = body.GetRotation();
        B2Vec2 localLever = vector(0.0f, -lever);
        B2Vec2 r = rotation.RotateVector(localLever);
        float omega = body.GetAngularVelocity();
        pivotVelocityX = velocity.GetX() - omega * r.GetY();
        pivotVelocityY = velocity.GetY() + omega * r.GetX();
        release(r, localLever, rotation, velocity);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.dynamicText(
                () -> String.format(java.util.Locale.US, "pivot velocity = (%.3f, %.3f)", pivotVelocityX, pivotVelocityY)));
    }
}
