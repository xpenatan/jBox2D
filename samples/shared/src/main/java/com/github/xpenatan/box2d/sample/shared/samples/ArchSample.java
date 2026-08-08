package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Stacking / Arch sample. */
public final class ArchSample extends AbstractBox2DSample {
    private static final float SCALE = 0.25f;
    private static final float[][] INNER = {
            { 16.0f, 0.0f }, { 14.93803712795643f, 5.133601056842984f },
            { 13.79871746027416f, 10.24928069555078f },
            { 12.56252963284711f, 15.34107019122473f },
            { 11.20040987372525f, 20.39856541571217f },
            { 9.66521217819836f, 25.40369899225096f },
            { 7.87179930638133f, 30.3179337000085f },
            { 5.635199558196225f, 35.03820717801641f },
            { 2.405937953536585f, 39.09554102558315f }
    };
    private static final float[][] OUTER = {
            { 24.0f, 0.0f }, { 22.33619528222415f, 6.02299846205841f },
            { 20.54936888969905f, 12.00964361211476f },
            { 18.60854610798073f, 17.9470321677465f },
            { 16.46769273811807f, 23.81367936585418f },
            { 14.05325025774858f, 29.57079353071012f },
            { 11.23551045834022f, 35.13775818285372f },
            { 7.752568160730571f, 40.30450679009583f },
            { 3.016931552701656f, 44.28891593799322f }
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
