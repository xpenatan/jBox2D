package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Chain;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Tangent Speed multi-material chain sample. */
public final class TangentSpeedSample extends AbstractBox2DSample {
    private static final float[] PATH = {
            113.0f, -30.0f, 105.0f, -30.0f, 97.0f, -29.5f, 89.0f, -28.0f,
            81.0f, -25.0f, 74.0f, -21.0f, 69.0f, -16.0f, 67.0f, -10.0f,
            67.0f, 0.0f, 4.0f, 0.0f, 4.0f, -41.0f, 0.0f, -41.0f,
            0.0f, 10.0f, 113.0f, 10.0f, 113.0f, -30.0f
    };
    private final List<B2Body> balls = new ArrayList<B2Body>();
    private float friction = 0.6f;
    private float rollingResistance = 0.3f;
    private int step;

    public TangentSpeedSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Chain chain = addChain(ground, PATH, true, 0.6f);
        int count = Math.min(7, chain.GetSegmentCount());
        for(int i = 0; i < count; i++) {
            B2Shape segment = chain.GetSegment(i);
            B2SurfaceMaterial material = segment.GetSurfaceMaterial();
            material.SetFriction(0.6f);
            material.SetTangentSpeed(-10.0f * (i + 1));
            segment.SetSurfaceMaterial(material);
            release(material, segment);
        }
    }

    private void dropBall() {
        balls.add(addDynamicCircle(110.0f, -30.0f, 0.5f, 1.0f, friction, 0.0f, rollingResistance));
    }

    private void reset() {
        for(B2Body body : balls) destroyBody(body);
        balls.clear();
        step = 0;
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        if(step % 25 == 0 && balls.size() < 200) dropBall();
        step++;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Friction", 0.0f, 2.0f, 0.01f,
                        () -> friction, value -> { friction = value; reset(); }),
                Box2DSampleControl.slider("Rolling Resistance", 0.0f, 1.0f, 0.01f,
                        () -> rollingResistance, value -> { rollingResistance = value; reset(); }));
    }
}
