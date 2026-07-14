package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / User Constraint sample. */
public final class UserConstraintSample extends AbstractBox2DSample {
    private final B2Body body;
    private final float[] anchors = new float[8];
    private final float[] forces = new float[2];

    public UserConstraintSample() {
        body = createDynamicBody(0.0f, 0.0f, 0.0f);
        body.SetAngularDamping(0.5f);
        body.SetLinearDamping(0.2f);
        addBoxShape(body, 1.0f, 0.5f, 20.0f, 0.6f, 0.0f, 0.0f);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        float[] localY = {-0.5f, 0.5f};
        for(int i = 0; i < 2; i++) {
            B2Vec2 local = vector(1.0f, localY[i]);
            B2Vec2 world = body.GetWorldPoint(local);
            float ax = 3.0f, ay = 0.0f;
            float dx = world.GetX() - ax, dy = world.GetY() - ay;
            float length = (float)Math.sqrt(dx * dx + dy * dy);
            anchors[4 * i] = ax; anchors[4 * i + 1] = ay;
            anchors[4 * i + 2] = world.GetX(); anchors[4 * i + 3] = world.GetY();
            if(length > 1.0f && length > 0.001f) {
                float stretch = length - 1.0f;
                float magnitude = Math.min(1000.0f * deltaSeconds, 30.0f * stretch);
                float ix = -magnitude * dx / length, iy = -magnitude * dy / length;
                applyLinearImpulse(body, ix, iy, world.GetX(), world.GetY());
                forces[i] = magnitude / Math.max(deltaSeconds, 0.0001f);
            }
            else forces[i] = 0.0f;
            release(world, local);
        }
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.segment(0, 0, 1, 0, 0xFF0000FF);
        draw.segment(0, 0, 0, 1, 0x00FF00FF);
        for(int i = 0; i < 2; i++) {
            int color = forces[i] > 0 ? 0xEE82EEFF : 0xE0FFFFFF;
            draw.segment(anchors[4 * i], anchors[4 * i + 1], anchors[4 * i + 2], anchors[4 * i + 3], color);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.dynamicText(
                () -> String.format("forces = %.1f, %.1f", forces[0], forces[1])));
    }
}
