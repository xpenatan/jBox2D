package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's intentionally invalid Bodies / Bad sample. */
public final class BadBodySample extends AbstractBox2DSample {
    private final B2Body badBody;

    public BadBodySample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        badBody = createDynamicBody(0.0f, 3.0f, 0.25f * PI);
        badBody.SetAngularVelocity(0.5f);
        addCapsuleShape(badBody, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.6f, 0.0f, 0.0f);
        B2Body normal = createDynamicBody(2.0f, 3.0f, 0.25f * PI);
        addCapsuleShape(normal, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.6f, 0.0f, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        applyForceToCenter(badBody, 0.0f, 10.0f);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.text("A bad body is a dynamic body with no mass and behaves like a kinematic body."),
                Box2DSampleControl.text("Bad bodies are invalid; behavior is not guaranteed."));
    }
}
