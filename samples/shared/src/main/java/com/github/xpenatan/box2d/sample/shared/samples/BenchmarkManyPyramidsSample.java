package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Shape;

/** Java port of Box2D 3.1.1's Benchmark / Many Pyramids sample. */
public final class BenchmarkManyPyramidsSample extends AbstractBox2DSample {
    public BenchmarkManyPyramidsSample() {
        world().EnableSleeping(false);
        int baseCount = 10;
        float extent = 0.5f;
        int rowCount = BenchmarkSampleSupport.DEBUG_SIZE ? 5 : 20;
        int columnCount = BenchmarkSampleSupport.DEBUG_SIZE ? 5 : 20;
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        float groundDeltaY = 2.0f * extent * (baseCount + 1.0f);
        float groundWidth = 2.0f * extent * columnCount * (baseCount + 1.0f);
        for(int row = 0; row < rowCount; row++) {
            float y = row * groundDeltaY;
            B2Shape segment = addSegmentShape(ground, -groundWidth, y, groundWidth, y,
                    0.0f, 0.6f, 0.0f);
            discardHandle(segment);
        }
        float baseWidth = 2.0f * extent * baseCount;
        for(int row = 0; row < rowCount; row++) {
            float baseY = row * groundDeltaY;
            for(int column = 0; column < columnCount; column++) {
                float centerX = -0.5f * groundWidth + column * (baseWidth + 2.0f * extent) + extent;
                BenchmarkSampleSupport.createPyramid(this, baseCount, extent, centerX, baseY);
            }
        }
    }
}
