package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Benchmark / Large Pyramid sample. */
public final class BenchmarkLargePyramidSample extends AbstractBox2DSample {
    public BenchmarkLargePyramidSample() {
        world().EnableSleeping(false);
        addStaticBox(0.0f, -1.0f, 100.0f, 1.0f, 0.0f);
        int baseCount = BenchmarkSampleSupport.DEBUG_SIZE ? 20 : 100;
        BenchmarkSampleSupport.createPyramid(this, baseCount, 0.5f,
                0.5f - 0.5f * baseCount, 0.0f);
    }
}
