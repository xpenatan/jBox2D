package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Stacking / Cliff sample. */
public final class CliffSample extends AbstractBox2DSample {
    private final B2Body[] movingBodies = new B2Body[9];
    private boolean flipped;

    public CliffSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addOffsetBoxShape(ground, 100.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f);
        addSegmentShape(ground, -14.0f, 4.0f, -8.0f, 4.0f, 0.0f, 0.6f, 0.0f);
        addOffsetBoxShape(ground, 3.0f, 0.5f, 0.0f, 4.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f);
        addCapsuleShape(ground, 8.5f, 4.0f, 13.5f, 4.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        createBodies();
    }

    private void createBodies() {
        for(int i = 0; i < movingBodies.length; i++) {
            destroyBody(movingBodies[i]);
            movingBodies[i] = null;
        }
        float sign = flipped ? -1.0f : 1.0f;
        float capsuleOffset = flipped ? -4.0f : 0.0f;
        float circleOffset = flipped ? 4.0f : 0.0f;

        float[] capsuleX = { -9.0f + capsuleOffset, 2.0f + capsuleOffset, 13.0f + capsuleOffset };
        float[] capsuleY = { 4.25f, 4.75f, 4.75f };
        for(int i = 0; i < 3; i++) {
            B2Body body = createDynamicBody(capsuleX[i], capsuleY[i], 0.0f);
            addCapsuleShape(body, -0.25f, 0.0f, 0.25f, 0.0f, 0.25f, 1.0f, 0.01f, 0.0f, 0.0f);
            setLinearVelocity(body, 2.0f * sign, 0.0f);
            movingBodies[i] = body;
        }

        float[] boxX = { -11.0f, 0.0f, 11.0f };
        float[] boxY = { 4.5f, 5.0f, 5.0f };
        for(int i = 0; i < 3; i++) {
            B2Body body = addDynamicBox(boxX[i], boxY[i], 0.5f, 0.5f, 0.0f, 1.0f, 0.01f, 0.0f, 0.0f);
            setLinearVelocity(body, 2.5f * sign, 0.0f);
            movingBodies[3 + i] = body;
        }

        float[] circleX = { -13.0f + circleOffset, -2.0f + circleOffset, 9.0f + circleOffset };
        float[] circleY = { 4.5f, 5.0f, 5.0f };
        for(int i = 0; i < 3; i++) {
            B2Body body = addDynamicCircle(circleX[i], circleY[i], 0.5f, 1.0f, 0.2f, 0.0f, 0.0f);
            setLinearVelocity(body, 1.5f * sign, 0.0f);
            movingBodies[6 + i] = body;
        }
    }

    private void flip() {
        flipped = !flipped;
        createBodies();
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.button("Flip", this::flip));
    }
}
