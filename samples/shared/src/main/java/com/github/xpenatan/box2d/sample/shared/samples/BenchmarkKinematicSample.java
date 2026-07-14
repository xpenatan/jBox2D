package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;

/** Java port of Box2D 3.1.1's Benchmark / Kinematic sample. */
public final class BenchmarkKinematicSample extends AbstractBox2DSample {
    public BenchmarkKinematicSample() {
        int span = BenchmarkSampleSupport.DEBUG_SIZE ? 20 : 100;
        B2Body body = BenchmarkSampleSupport.createBody(this, B2.KinematicBody(), 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, true, true);
        B2ShapeDef shapeDef = BenchmarkSampleSupport.filteredShapeDef(this, 0.0f, 0.6f,
                1L, 2L, false);
        for(int row = -span; row < span; row++) {
            for(int column = -span; column < span; column++) {
                B2Shape shape = BenchmarkSampleSupport.addPolygon(this, body, shapeDef,
                        0.5f, 0.5f, column, row, 0.0f);
                discardHandle(shape);
            }
        }
        body.ApplyMassFromShapes();
        release(shapeDef);
    }
}
