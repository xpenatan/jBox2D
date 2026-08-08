package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2MassData;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Bodies / Weeble sample. */
public final class WeebleSample extends AbstractBox2DSample {
    private final B2Body weeble;
    private float explosionMagnitude = 8.0f;

    public WeebleSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addSegmentShape(ground, -20.0f, 0.0f, 20.0f, 0.0f, 0.0f, 0.1f, 0.0f);
        weeble = createDynamicBody(0.0f, 3.0f, 0.25f * PI);
        addCapsuleShape(weeble, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.1f, 1.0f, 0.0f);
        float mass = weeble.GetMass();
        float offset = 1.5f;
        B2MassData massData = new B2MassData();
        B2Vec2 center = vector(0.0f, -offset);
        massData.SetMass(mass);
        massData.SetCenter(center);
        massData.SetRotationalInertia(weeble.GetRotationalInertia() + mass * offset * offset);
        weeble.SetMassData(massData);
        release(center, massData);
    }

    private void explode() {
        explode(0.0f, 0.0f, 2.0f, 0.1f, explosionMagnitude);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.circle(0.0f, 0.0f, 2.0f, 0xFF00FFFF);

        B2Vec2 localPoint = vector(0.0f, 2.0f);
        B2Vec2 worldPoint = weeble.GetWorldPoint(localPoint);
        B2Vec2 center = weeble.GetWorldCenterOfMass();
        B2Vec2 linearVelocity = weeble.GetLinearVelocity();
        float rx = worldPoint.GetX() - center.GetX();
        float ry = worldPoint.GetY() - center.GetY();
        float omega = weeble.GetAngularVelocity();
        float vx = linearVelocity.GetX() - omega * ry;
        float vy = linearVelocity.GetY() + omega * rx;
        draw.segment(worldPoint.GetX(), worldPoint.GetY(), worldPoint.GetX() + vx, worldPoint.GetY() + vy,
                0xFF0000FF);
        draw.segment(worldPoint.GetX() + 0.05f, worldPoint.GetY(),
                worldPoint.GetX() + vx + 0.05f, worldPoint.GetY() + vy, 0x00FF00FF);
        release(linearVelocity, center, worldPoint, localPoint);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("Teleport", () -> setTransform(weeble, 0.0f, 5.0f, 0.95f * PI)),
                Box2DSampleControl.button("Explode", this::explode),
                Box2DSampleControl.slider("Magnitude", -100.0f, 100.0f, 0.5f,
                        () -> explosionMagnitude, value -> explosionMagnitude = value));
    }
}
