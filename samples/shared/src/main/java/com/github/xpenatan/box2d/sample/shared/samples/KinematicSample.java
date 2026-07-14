package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;

/** Java port of Box2D 3.1.1's Bodies / Kinematic target-transform sample. */
public final class KinematicSample extends AbstractBox2DSample {
    private final B2Body body;
    private final float amplitude = 2.0f;
    private float time;
    private float targetX = 4.0f;
    private float targetY;
    private float axisX;
    private float axisY = 1.0f;

    public KinematicSample() {
        body = createKinematicBody(2.0f * amplitude, 0.0f, 0.0f);
        addBoxShape(body, 0.1f, 1.0f, 0.0f, 0.6f, 0.0f, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        targetX = 2.0f * amplitude * (float)Math.cos(time);
        targetY = amplitude * (float)Math.sin(2.0f * time);
        float angle = 2.0f * time;
        axisX = -(float)Math.sin(angle);
        axisY = (float)Math.cos(angle);
        B2Vec2 point = vector(targetX, targetY);
        B2Rot rotation = new B2Rot(angle);
        B2Transform target = new B2Transform(point, rotation);
        body.SetTargetTransform(target, deltaSeconds);
        release(target, rotation, point);
        time += deltaSeconds;
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.segment(targetX - 0.5f * axisX, targetY - 0.5f * axisY,
                targetX + 0.5f * axisX, targetY + 0.5f * axisY, 0xDDA0DDFF);
        draw.point(targetX, targetY, 10.0f, 0xDDA0DDFF);
    }
}
