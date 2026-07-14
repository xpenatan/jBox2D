package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Benchmark / Smash sample. */
public final class BenchmarkSmashSample extends AbstractBox2DSample {
    public BenchmarkSmashSample() {
        super(4, 0.0f, 0.0f);
        B2BodyDef projectileDef = new B2BodyDef();
        B2Vec2 projectilePosition = new B2Vec2(-20.0f, 0.0f);
        B2Vec2 projectileVelocity = new B2Vec2(40.0f, 0.0f);
        projectileDef.SetType(B2.DynamicBody());
        projectileDef.SetPosition(projectilePosition);
        projectileDef.SetLinearVelocity(projectileVelocity);
        B2Body projectile = createBody(projectileDef);
        B2Shape projectileShape = addBoxShape(projectile, 4.0f, 4.0f, 8.0f, 0.6f, 0.0f, 0.0f);
        discardHandle(projectileShape);
        release(projectileVelocity, projectilePosition, projectileDef);

        int columns = BenchmarkSampleSupport.DEBUG_SIZE ? 20 : 120;
        int rows = BenchmarkSampleSupport.DEBUG_SIZE ? 10 : 80;
        B2Polygon box = B2Polygon.CreateBox(0.2f, 0.2f);
        B2ShapeDef shapeDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        for(int column = 0; column < columns; column++) {
            for(int row = 0; row < rows; row++) {
                float x = column * 0.4f + 30.0f;
                float y = (row - rows / 2.0f) * 0.4f;
                B2Body body = BenchmarkSampleSupport.createBody(this, B2.DynamicBody(), x, y, 0.0f,
                        0.0f, 0.0f, false, true);
                B2Shape shape = createPolygonShape(body, shapeDef, box);
                discardHandle(shape);
            }
        }
        release(shapeDef, box);
    }
}
