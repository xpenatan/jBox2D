package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Separation sample. */
public final class JointSeparationSample extends AbstractBox2DSample {
    private final JointShowcase showcase;
    private final List<String> labels = new ArrayList<String>();
    private float impulse = 500.0f;
    private float gravity = -10.0f;

    public JointSeparationSample() {
        B2Body ground = addGroundSegment(-40.0f, 0.0f, 40.0f, 0.0f);
        showcase = JointShowcase.create(this, ground, -20.0f, 10.0f, 10.0f,
                false, false, true, false, 0.0f, 0.0f);
    }

    private void impulse() {
        for(B2Body body : showcase.bodies) {
            B2Vec2 local = vector(1.0f, 1.0f);
            B2Vec2 point = body.GetWorldPoint(local);
            applyLinearImpulse(body, impulse, -impulse, point.GetX(), point.GetY());
            release(point, local);
        }
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        labels.clear();
        for(B2Joint joint : showcase.joints) {
            labels.add(String.format("%.2f m, %.1f deg", joint.GetLinearSeparation(),
                    joint.GetAngularSeparation() * 180.0f / PI));
        }
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(int i = 0; i < labels.size(); i++) draw.worldText(-20.0f + 10.0f * i, 13.0f, labels.get(i), 0xFFFFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("gravity", -500, 500, 1, () -> gravity,
                        value -> { gravity = value; setGravity(0.0f, value); }),
                Box2DSampleControl.button("impulse", this::impulse),
                Box2DSampleControl.slider("magnitude", 0, 1000, 1, () -> impulse, value -> impulse = value));
    }
}
