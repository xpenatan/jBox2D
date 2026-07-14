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
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
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
        B2Vec2 position = weeble.GetPosition();
        float dx = position.GetX();
        float dy = position.GetY();
        float length = Math.max(0.1f, (float)Math.sqrt(dx * dx + dy * dy));
        applyLinearImpulseToCenter(weeble, explosionMagnitude * dx / length, explosionMagnitude * dy / length);
        release(position);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.circle(0.0f, 0.0f, 2.0f, 0xFF00FFFF);
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
