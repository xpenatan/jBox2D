package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Robustness / HighMassRatio1 sample. */
public final class HighMassRatio1Sample extends AbstractBox2DSample {
    public HighMassRatio1Sample() {
        addStaticBox(0.0f, -1.0f, 50.0f, 1.0f, 0.0f);
        float extent = 1.0f;
        for(int pyramid = 0; pyramid < 3; pyramid++) {
            int count = 10;
            float offset = -20.0f * extent + 2.0f * (count + 1.0f) * extent * pyramid;
            float y = extent;
            while(count > 0) {
                for(int i = 0; i < count; i++) {
                    float coefficient = i - 0.5f * count;
                    float yy = count == 1 ? y + 2.0f : y;
                    float density = count == 1 ? (pyramid + 1.0f) * 100.0f : 1.0f;
                    addDynamicBox(2.0f * coefficient * extent + offset, yy, extent, extent,
                            0.0f, density, 0.6f, 0.0f, 0.0f);
                }
                count--;
                y += 2.0f * extent;
            }
        }
    }
}
