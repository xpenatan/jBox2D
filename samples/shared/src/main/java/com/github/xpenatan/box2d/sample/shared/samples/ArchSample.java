package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Stacking / Arch sample. */
public final class ArchSample extends AbstractBox2DSample {
    private static final float SCALE = 0.25f;
    private static final float[][] INNER = {
            { 16.0f, 0.0f }, { 14.938037f, 5.133601f }, { 13.798717f, 10.249281f },
            { 12.562530f, 15.341070f }, { 11.200410f, 20.398565f }, { 9.665212f, 25.403699f },
            { 7.871799f, 30.317934f }, { 5.635200f, 35.038208f }, { 2.405938f, 39.095541f }
    };
    private static final float[][] OUTER = {
            { 24.0f, 0.0f }, { 22.336195f, 6.022998f }, { 20.549369f, 12.009644f },
            { 18.608546f, 17.947032f }, { 16.467693f, 23.813679f }, { 14.053250f, 29.570793f },
            { 11.235510f, 35.137758f }, { 7.752568f, 40.304507f }, { 3.016932f, 44.288916f }
    };

    public ArchSample() {
        addGroundSegment(-100.0f, 0.0f, 100.0f, 0.0f);
        for(int i = 0; i < 8; i++) {
            B2Body right = createDynamicBody(0.0f, 0.0f, 0.0f);
            addPolygonShape(right, quad(INNER[i], OUTER[i], OUTER[i + 1], INNER[i + 1], false),
                    0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
            B2Body left = createDynamicBody(0.0f, 0.0f, 0.0f);
            addPolygonShape(left, quad(OUTER[i], INNER[i], INNER[i + 1], OUTER[i + 1], true),
                    0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        }
        B2Body keystone = createDynamicBody(0.0f, 0.0f, 0.0f);
        addPolygonShape(keystone, new float[] {
                sx(INNER[8][0]), sy(INNER[8][1]), sx(OUTER[8][0]), sy(OUTER[8][1]),
                -sx(OUTER[8][0]), sy(OUTER[8][1]), -sx(INNER[8][0]), sy(INNER[8][1])
        }, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);

        float top = sy(OUTER[8][1]);
        for(int i = 0; i < 4; i++) addDynamicBox(0.0f, 0.5f + top + i, 2.0f, 0.5f,
                0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    private static float[] quad(float[] a, float[] b, float[] c, float[] d, boolean mirror) {
        float sign = mirror ? -1.0f : 1.0f;
        return new float[] { sign * sx(a[0]), sy(a[1]), sign * sx(b[0]), sy(b[1]),
                sign * sx(c[0]), sy(c[1]), sign * sx(d[0]), sy(d[1]) };
    }

    private static float sx(float value) { return value * SCALE; }
    private static float sy(float value) { return value * SCALE; }
}
