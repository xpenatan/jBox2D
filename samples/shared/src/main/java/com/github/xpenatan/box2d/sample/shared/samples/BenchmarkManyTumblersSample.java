package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / Many Tumblers sample. */
public final class BenchmarkManyTumblersSample extends AbstractBox2DSample {
    private final List<B2Body> tumblers = new ArrayList<B2Body>();
    private final List<B2Body> items = new ArrayList<B2Body>();
    private float[] positions = new float[0];
    private int rowCount = BenchmarkSampleSupport.DEBUG_SIZE ? 2 : 19;
    private int columnCount = BenchmarkSampleSupport.DEBUG_SIZE ? 2 : 19;
    private int bodyLimit;
    private int stepCount;
    private float angularSpeed = 25.0f;

    public BenchmarkManyTumblersSample() {
        createStaticBody(0.0f, 0.0f, 0.0f);
        createScene();
    }

    private void createScene() {
        for(B2Body body : items) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        for(B2Body body : tumblers) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        items.clear();
        tumblers.clear();
        int count = rowCount * columnCount;
        positions = new float[2 * count];
        int index = 0;
        float x = -4.0f * rowCount;
        for(int row = 0; row < rowCount; row++) {
            float y = -4.0f * columnCount;
            for(int column = 0; column < columnCount; column++) {
                positions[2 * index] = x;
                positions[2 * index + 1] = y;
                createTumbler(x, y);
                index++;
                y += 8.0f;
            }
            x += 8.0f;
        }
        bodyLimit = (BenchmarkSampleSupport.DEBUG_SIZE ? 8 : 50) * count;
        stepCount = 0;
    }

    private void createTumbler(float x, float y) {
        B2Body body = BenchmarkSampleSupport.createBody(this, B2.KinematicBody(), x, y, 0.0f,
                radians(angularSpeed), 0.0f, true, true);
        tumblers.add(body);
        BenchmarkSampleSupport.discardShape(this,
                BenchmarkSampleSupport.addBox(this, body, 0.25f, 2.0f, 2.0f, 0.0f, 0.0f, 50.0f, 0.6f));
        BenchmarkSampleSupport.discardShape(this,
                BenchmarkSampleSupport.addBox(this, body, 0.25f, 2.0f, -2.0f, 0.0f, 0.0f, 50.0f, 0.6f));
        BenchmarkSampleSupport.discardShape(this,
                BenchmarkSampleSupport.addBox(this, body, 2.0f, 0.25f, 0.0f, 2.0f, 0.0f, 50.0f, 0.6f));
        BenchmarkSampleSupport.discardShape(this,
                BenchmarkSampleSupport.addBox(this, body, 2.0f, 0.25f, 0.0f, -2.0f, 0.0f, 50.0f, 0.6f));
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        stepCount++;
        if(items.size() >= bodyLimit || (stepCount & 7) != 0) return;
        for(int i = 0; i < tumblers.size() && items.size() < bodyLimit; i++) {
            B2Body body = createDynamicBody(positions[2 * i], positions[2 * i + 1], 0.0f);
            B2Shape shape = addCapsuleShape(body, -0.1f, 0.0f, 0.1f, 0.0f, 0.075f,
                    1.0f, 0.6f, 0.0f, 0.0f);
            discardHandle(shape);
            items.add(body);
        }
    }

    private void setSpeed(float value) {
        angularSpeed = value;
        for(B2Body body : tumblers) {
            body.SetAngularVelocity(radians(value));
            body.SetAwake(true);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Row Count", 1.0f, 32.0f, 1.0f, () -> rowCount,
                        value -> { rowCount = Math.round(value); createScene(); }),
                Box2DSampleControl.slider("Column Count", 1.0f, 32.0f, 1.0f, () -> columnCount,
                        value -> { columnCount = Math.round(value); createScene(); }),
                Box2DSampleControl.slider("Speed", 0.0f, 100.0f, 1.0f, () -> angularSpeed, this::setSpeed));
    }
}
