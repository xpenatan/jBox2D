package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Chain;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Tangent Speed multi-material chain sample. */
public final class TangentSpeedSample extends AbstractBox2DSample {
    private static final float[] PATH = {
            113.29167938f, -37.09166718f, 104.82500458f, -37.09166718f,
            97.41666412f, -37.09166718f, 90.53749847f, -37.09166718f,
            84.71666718f, -36.56250000f, 79.42500305f, -34.97500229f,
            74.13333130f, -32.32916641f, 69.37083435f, -28.09583473f,
            66.72499847f, -28.09583473f, 66.72499847f, -37.09166718f,
            4.28334141f, -37.09166718f, 4.28334141f, -0.05000000f,
            0.04999924f, -0.05000000f, 0.04999924f, -41.32500076f,
            113.29167938f, -41.32499695f
    };
    private static final int[] COLORS = {
            0x00008B, 0x008B8B, 0xB8860B, 0xA9A9A9, 0x006400, 0xBDB76B, 0x8B008B
    };
    private final List<B2Body> balls = new ArrayList<B2Body>();
    private float friction = 0.6f;
    private float rollingResistance = 0.3f;
    private int step;

    public TangentSpeedSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        int materialCount = PATH.length / 2;
        B2SurfaceMaterial[] materials = new B2SurfaceMaterial[materialCount];
        for(int i = 0; i < materialCount; i++) {
            B2SurfaceMaterial material = new B2SurfaceMaterial();
            material.SetFriction(0.6f);
            if(i < COLORS.length) {
                material.SetTangentSpeed(-10.0f * (i + 1));
                material.SetCustomColor(COLORS[i]);
            }
            materials[i] = material;
        }
        B2Chain chain = addChain(ground, PATH, true, materials);
        discardHandle(chain);
        release(materials);
    }

    private void dropBall() {
        balls.add(addDynamicCircle(110.0f, -30.0f, 0.5f, 1.0f, friction, 0.0f, rollingResistance));
    }

    private void reset() {
        for(B2Body body : balls) destroyBody(body);
        balls.clear();
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
