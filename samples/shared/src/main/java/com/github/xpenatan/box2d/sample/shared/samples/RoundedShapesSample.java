package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;

/** Java port of Box2D 3.1.1's Shapes / Rounded sample. */
public final class RoundedShapesSample extends AbstractBox2DSample {
    public RoundedShapesSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addOffsetBoxShape(ground, 20, 1, 0, -1, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, 19, 5, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, -19, 5, 0, 0, .6f, 0, 0);
        for(int row = 0; row < 10; row++) for(int column = 0; column < 10; column++) {
            B2Body body = createDynamicBody(-5.0f + column, 2.0f + row, 0.0f);
            B2Polygon polygon = randomPolygon(0.5f);
            polygon.SetRadius(randomFloat(0.05f, 0.25f));
            createPolygonShape(body, polygon, 1, .6f, 0, .3f, null);
            release(polygon);
        }
    }
}
