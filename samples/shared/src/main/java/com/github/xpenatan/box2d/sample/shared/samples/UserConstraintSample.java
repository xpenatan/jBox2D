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
    private final float[] impulses = new float[2];

    public UserConstraintSample() {
        body = createDynamicBody(0.0f, 0.0f, 0.0f);
        body.SetAngularDamping(0.5f);
        body.SetLinearDamping(0.2f);
        addBoxShape(body, 1.0f, 0.5f, 20.0f, 0.6f, 0.0f, 0.0f);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        if(deltaSeconds == 0.0f) return;
        float invTimeStep = 1.0f / deltaSeconds;
        float omega = 2.0f * PI * 3.0f;
        float sigma = 2.0f * 0.7f + deltaSeconds * omega;
        float s = deltaSeconds * omega * sigma;
        float impulseCoefficient = 1.0f / (1.0f + s);
        float massCoefficient = s * impulseCoefficient;
        float biasCoefficient = omega / sigma;

        float mass = body.GetMass();
        float invMass = mass < 0.0001f ? 0.0f : 1.0f / mass;
        float inertia = body.GetRotationalInertia();
        float invI = inertia < 0.0001f ? 0.0f : 1.0f / inertia;
        B2Vec2 velocity = body.GetLinearVelocity();
        float velocityX = velocity.GetX(), velocityY = velocity.GetY();
        float angularVelocity = body.GetAngularVelocity();
        B2Vec2 centerOfMass = body.GetWorldCenterOfMass();

        float[] localY = {-0.5f, 0.5f};
        for(int i = 0; i < 2; i++) {
            B2Vec2 local = vector(1.0f, localY[i]);
            B2Vec2 world = body.GetWorldPoint(local);
            float ax = 3.0f, ay = 0.0f;
            float dx = world.GetX() - ax, dy = world.GetY() - ay;
            float length = (float)Math.sqrt(dx * dx + dy * dy);
            anchors[4 * i] = ax; anchors[4 * i + 1] = ay;
            anchors[4 * i + 2] = world.GetX(); anchors[4 * i + 3] = world.GetY();
            float c = length - 1.0f;
            if(c >= 0.0f && length >= 0.001f) {
                float axisX = dx / length, axisY = dy / length;
                float rx = world.GetX() - centerOfMass.GetX();
                float ry = world.GetY() - centerOfMass.GetY();
                float jb = rx * axisY - ry * axisX;
                float k = invMass + jb * invI * jb;
                float invK = k < 0.0001f ? 0.0f : 1.0f / k;
                float cDot = velocityX * axisX + velocityY * axisY + jb * angularVelocity;
                float impulse = -massCoefficient * invK * (cDot + biasCoefficient * c);
                float appliedImpulse = Math.max(-1000.0f * deltaSeconds, Math.min(impulse, 0.0f));
                velocityX += invMass * appliedImpulse * axisX;
                velocityY += invMass * appliedImpulse * axisY;
                angularVelocity += appliedImpulse * invI * jb;
                impulses[i] = appliedImpulse;
            }
            else impulses[i] = 0.0f;
            release(world, local);
        }
        velocity.Set(velocityX, velocityY);
        body.SetLinearVelocity(velocity);
        body.SetAngularVelocity(angularVelocity);
        forces[0] = impulses[0] * invTimeStep;
        forces[1] = impulses[1] * invTimeStep;
        release(centerOfMass, velocity);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.segment(0, 0, 1, 0, 0xFF0000FF);
        draw.segment(0, 0, 0, 1, 0x00FF00FF);
        for(int i = 0; i < 2; i++) {
            int color = impulses[i] < 0 ? 0xEE82EEFF : 0xE0FFFFFF;
            draw.segment(anchors[4 * i], anchors[4 * i + 1], anchors[4 * i + 2], anchors[4 * i + 3], color);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.dynamicText(
                () -> String.format("forces = %.1f, %.1f", forces[0], forces[1])));
    }

    private final float[] forces = new float[2];
}
