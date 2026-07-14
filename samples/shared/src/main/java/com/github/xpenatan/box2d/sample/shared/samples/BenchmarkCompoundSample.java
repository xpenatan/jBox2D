package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;

/** Java port of Box2D 3.1.1's Benchmark / Compound sample. */
public final class BenchmarkCompoundSample extends AbstractBox2DSample {
    public BenchmarkCompoundSample() {
        float grid = 1.0f;
        int height = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 200;
        int width = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 200;
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2ShapeDef staticDef = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
        for(int row = 0; row < height; row++) {
            float y = grid * row;
            for(int column = row; column < width; column++) {
                float x = grid * column;
                B2Shape right = BenchmarkSampleSupport.addPolygon(this, ground, staticDef,
                        0.5f * grid, 0.5f * grid, x, y, 0.0f);
                B2Shape left = BenchmarkSampleSupport.addPolygon(this, ground, staticDef,
                        0.5f * grid, 0.5f * grid, -x, y, 0.0f);
                discardHandle(right);
                discardHandle(left);
            }
        }
        release(staticDef);

        int span = BenchmarkSampleSupport.DEBUG_SIZE ? 5 : 20;
        int count = 5;
        B2ShapeDef dynamicDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        dynamicDef.SetUpdateBodyMass(false);
        for(int row = 0; row < count; row++) {
            float bodyY = (100.0f + row * span) * grid;
            for(int column = 0; column < count; column++) {
                float bodyX = -0.5f * grid * count * span + column * span * grid;
                B2Body body = createBody(B2.DynamicBody(), bodyX, bodyY, 0.0f);
                for(int i = 0; i < span; i++) {
                    for(int j = 0; j < span; j++) {
                        B2Shape shape = BenchmarkSampleSupport.addPolygon(this, body, dynamicDef,
                                0.5f * grid, 0.5f * grid, j * grid, i * grid, 0.0f);
                        discardHandle(shape);
                    }
                }
                body.ApplyMassFromShapes();
            }
        }
        release(dynamicDef);
    }
}
