package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Stacking / Tilted Stack sample. */
public final class TiltedStackSample extends AbstractBox2DSample {
    public TiltedStackSample() {
        addStaticBox(0.0f, -1.0f, 1000.0f, 1.0f, 0.0f);
        final int rows = 10;
        final int columns = 10;
        final float dx = 5.0f;
        final float xRoot = -0.5f * dx * (columns - 1.0f);
        for(int column = 0; column < columns; column++) {
            float x = xRoot + column * dx;
            for(int row = 0; row < rows; row++) {
                com.github.xpenatan.box2d.B2Body body = createDynamicBody(x + 0.2f * row, 0.5f + row, 0.0f);
                addRoundedBoxShape(body, 0.45f, 0.45f, 0.05f, 1.0f, 0.3f, 0.0f, 0.0f);
            }
        }
    }
}
