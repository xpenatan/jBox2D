package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Explosion projected-perimeter sample. */
public final class ExplosionSample extends AbstractBox2DSample {
    private final List<B2Joint> joints = new ArrayList<B2Joint>();
    private float radius = 7.0f;
    private float falloff = 3.0f;
    private float impulse = 10.0f;
    private float referenceAngle;

    public ExplosionSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        float r = 8.0f;
        for(int degrees = 0; degrees < 360; degrees += 30) {
            float angle = radians(degrees);
            B2Rot rotation = new B2Rot(angle);
            float x = r * rotation.GetCosine();
            float y = r * rotation.GetSine();
            B2BodyDef def = new B2BodyDef();
            B2Vec2 p = vector(x, y);
            def.SetType(B2.DynamicBody());
            def.SetPosition(p);
            def.SetGravityScale(0.0f);
            B2Body body = createBody(def);
            addBoxShape(body, 1.0f, 0.1f, 1, .6f, 0, 0);
            joints.add(addWeldJoint(ground, body, x, y, 0.5f, 0.7f));
            release(p, def, rotation);
        }
    }

    private void explode() {
        explode(0.0f, 0.0f, radius, falloff, impulse);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        referenceAngle += radians(60.0f) * deltaSeconds;
        if(referenceAngle > PI) referenceAngle -= 2.0f * PI;
        for(B2Joint joint : joints) joint.SetReferenceAngle(referenceAngle);
    }

    @Override public void draw(Box2DSampleDraw draw) {
        draw.circle(0, 0, radius + falloff, 0x00A0FFFF);
        draw.circle(0, 0, radius, 0xFFD000FF);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("Explode", this::explode),
                Box2DSampleControl.slider("radius", 0, 20, .1f, () -> radius, v -> radius = v),
                Box2DSampleControl.slider("falloff", 0, 20, .1f, () -> falloff, v -> falloff = v),
                Box2DSampleControl.slider("impulse", -20, 20, .1f, () -> impulse, v -> impulse = v),
                Box2DSampleControl.dynamicText(() -> String.format(java.util.Locale.US,
                        "reference angle = %.3f", referenceAngle)));
    }
}
