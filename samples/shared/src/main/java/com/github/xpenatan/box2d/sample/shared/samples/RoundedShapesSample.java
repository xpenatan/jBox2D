package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Shapes / Rounded sample. */
public final class RoundedShapesSample extends AbstractBox2DSample {
    public RoundedShapesSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addOffsetBoxShape(ground, 20, 1, 0, -1, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, 19, 5, 0, 0, .6f, 0, 0);
        addOffsetBoxShape(ground, 1, 5, -19, 5, 0, 0, .6f, 0, 0);
        for(int row = 0; row < 10; row++) for(int column = 0; column < 10; column++) {
            B2Body body = createDynamicBody(-5.0f + column, 2.0f + row, 0.0f);
            int count = randomInt(3, 8);
            float[] vertices = new float[2 * count];
            for(int i = 0; i < count; i++) {
                float angle = 2.0f * PI * i / count;
                float radius = randomFloat(0.3f, 0.5f);
                vertices[2 * i] = radius * (float)Math.cos(angle);
                vertices[2 * i + 1] = radius * (float)Math.sin(angle);
            }
            addPolygonShape(body, vertices, randomFloat(0.05f, 0.25f), 1, .6f, 0, .3f);
        }
    }
}
