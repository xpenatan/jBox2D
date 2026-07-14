package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Explosion projected-perimeter sample. */
public final class ExplosionSample extends AbstractBox2DSample {
    private final List<B2Body> ring = new ArrayList<B2Body>();
    private float radius = 7.0f;
    private float falloff = 3.0f;
    private float impulse = 10.0f;
    private float referenceAngle;

    public ExplosionSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        float r = 8.0f;
        for(int degrees = 0; degrees < 360; degrees += 30) {
            float angle = radians(degrees);
            float x = r * (float)Math.cos(angle);
            float y = r * (float)Math.sin(angle);
            B2BodyDef def = new B2BodyDef();
            B2Vec2 p = vector(x, y);
            def.SetType(B2.DynamicBody());
            def.SetPosition(p);
            def.SetGravityScale(0.0f);
            B2Body body = createBody(def);
            addBoxShape(body, 1.0f, 0.1f, 1, .6f, 0, 0);
            addWeldJoint(ground, body, x, y, 0.5f, 0.7f);
            ring.add(body);
            release(p, def);
        }
    }

    private void explode() {
        for(B2Body body : ring) {
            B2Vec2 p = body.GetPosition();
            float x = p.GetX();
            float y = p.GetY();
            float distance = Math.max(0.001f, (float)Math.sqrt(x * x + y * y));
            if(distance <= radius + falloff) {
                float strength = distance <= radius ? impulse : impulse * (radius + falloff - distance) / falloff;
                applyLinearImpulseToCenter(body, strength * x / distance, strength * y / distance);
            }
            release(p);
        }
    }

    @Override protected void beforeStep(float deltaSeconds) {
        referenceAngle += radians(60.0f) * deltaSeconds;
        if(referenceAngle > PI) referenceAngle -= 2.0f * PI;
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
