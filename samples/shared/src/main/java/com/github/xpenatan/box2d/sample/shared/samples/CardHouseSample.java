package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Stacking / Card House sample (originally from PEEL). */
public final class CardHouseSample extends AbstractBox2DSample {
    public CardHouseSample() {
        addStaticBox(0.0f, -2.0f, 40.0f, 2.0f, 0.0f);
        final float height = 0.2f;
        final float thickness = 0.001f;
        final float angle0 = radians(25.0f);
        final float angle1 = radians(-25.0f);
        final float angle2 = 0.5f * PI;
        int count = 5;
        float xRoot = 0.0f;
        float y = height - 0.02f;
        while(count > 0) {
            float x = xRoot;
            for(int i = 0; i < count; i++) {
                if(i != count - 1) addDynamicBox(x + 0.25f, y + height - 0.015f, thickness, height,
                        angle2, 1.0f, 0.7f, 0.0f, 0.0f);
                addDynamicBox(x, y, thickness, height, angle1, 1.0f, 0.7f, 0.0f, 0.0f);
                x += 0.175f;
                addDynamicBox(x, y, thickness, height, angle0, 1.0f, 0.7f, 0.0f, 0.0f);
                x += 0.175f;
            }
            y += height * 2.0f - 0.03f;
            xRoot += 0.175f;
            count--;
        }
    }
}
