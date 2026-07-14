package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / CreateDestroy sample. */
public final class BenchmarkCreateDestroySample extends AbstractBox2DSample {
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final int baseCount = BenchmarkSampleSupport.DEBUG_SIZE ? 40 : 100;
    private final int iterations = BenchmarkSampleSupport.DEBUG_SIZE ? 1 : 10;
    private double createMilliseconds;
    private double destroyMilliseconds;

    public BenchmarkCreateDestroySample() {
        addStaticBox(0.0f, 0.0f, 100.0f, 1.0f, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        createMilliseconds = 0.0;
        destroyMilliseconds = 0.0;
        for(int i = 0; i < iterations; i++) createScene();
    }

    private void createScene() {
        long start = System.nanoTime();
        for(B2Body body : bodies) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        bodies.clear();
        destroyMilliseconds += (System.nanoTime() - start) * 1.0e-6;

        start = System.nanoTime();
        float radius = 0.5f;
        float shift = 2.0f * radius;
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
        createMilliseconds += (System.nanoTime() - start) * 1.0e-6;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.dynamicText(() -> String.format("total: create = %.3f ms, destroy = %.3f ms",
                        createMilliseconds, destroyMilliseconds)),
                Box2DSampleControl.dynamicText(() -> {
                    int count = Math.max(1, bodies.size());
                    return String.format("body: create = %.3f us, destroy = %.3f us",
                            1000.0 * createMilliseconds / iterations / count,
                            1000.0 * destroyMilliseconds / iterations / count);
                }));
    }
}
