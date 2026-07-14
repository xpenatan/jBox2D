package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / Sleep sample. */
public final class BenchmarkSleepSample extends AbstractBox2DSample {
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final int iterations = BenchmarkSampleSupport.DEBUG_SIZE ? 1 : 41;
    private double wakeTotal;
    private double sleepTotal;
    private int wakeCount;
    private int sleepCount;
    private boolean awake;

    public BenchmarkSleepSample() {
        addStaticBox(0.0f, 0.0f, 100.0f, 1.0f, 0.0f);
        int baseCount = BenchmarkSampleSupport.DEBUG_SIZE ? 40 : 100;
        float shift = 1.0f;
        float centerX = shift * baseCount / 2.0f;
        float centerY = shift / 2.0f + 1.0f;
        B2Polygon box = B2Polygon.CreateRoundedBox(0.5f, 0.5f, 0.0f);
        B2ShapeDef shapeDef = shapeDef(1.0f, 0.5f, 0.0f, 0.0f);
        for(int row = 0; row < baseCount; row++) {
            float y = row * shift + centerY;
            for(int column = row; column < baseCount; column++) {
                float x = 0.5f * row * shift + (column - row) * shift - centerX;
                B2Body body = createDynamicBody(x, y, 0.0f);
                B2Shape shape = createPolygonShape(body, shapeDef, box);
                discardHandle(shape);
                bodies.add(body);
            }
        }
        release(shapeDef, box);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        if(bodies.isEmpty()) return;
        for(int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            bodies.get(0).SetAwake(awake);
            double elapsed = (System.nanoTime() - start) * 1.0e-6;
            if(awake) {
                wakeTotal += elapsed;
                wakeCount++;
            }
            else {
                sleepTotal += elapsed;
                sleepCount++;
            }
            awake = !awake;
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.dynamicText(() -> wakeCount == 0 ? "wake ave = n/a"
                        : String.format("wake ave = %.3f ms", wakeTotal / wakeCount)),
                Box2DSampleControl.dynamicText(() -> sleepCount == 0 ? "sleep ave = n/a"
                        : String.format("sleep ave = %.3f ms", sleepTotal / sleepCount)));
    }
}
