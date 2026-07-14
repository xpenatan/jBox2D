package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / Barrel sample. */
public final class BenchmarkBarrelSample extends AbstractBox2DSample {
    private static final int CIRCLE = 0;
    private static final int CAPSULE = 1;
    private static final int MIX = 2;
    private static final int COMPOUND = 3;
    private static final int HUMAN = 4;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final List<HumanRagdoll> humans = new ArrayList<HumanRagdoll>();
    private int shapeType = COMPOUND;

    public BenchmarkBarrelSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        for(int i = 0; i < 81; i++) {
            B2Shape shape = addOffsetBoxShape(ground, 0.5f, 0.5f, -40.0f + i, 0.0f, 0.0f,
                    0.0f, 0.6f, 0.0f, 0.0f);
            discardHandle(shape);
        }
        for(int i = 0; i < 100; i++) {
            B2Shape left = addOffsetBoxShape(ground, 0.5f, 0.5f, -40.0f, 1.0f + i, 0.0f,
                    0.0f, 0.6f, 0.0f, 0.0f);
            B2Shape right = addOffsetBoxShape(ground, 0.5f, 0.5f, 40.0f, 1.0f + i, 0.0f,
                    0.0f, 0.6f, 0.0f, 0.0f);
            discardHandle(left);
            discardHandle(right);
        }
        B2Shape deepFloor = addSegmentShape(ground, -800.0f, -80.0f, 800.0f, -80.0f,
                0.0f, 0.6f, 0.0f);
        discardHandle(deepFloor);
        createScene();
    }

    private void createScene() {
        for(HumanRagdoll human : humans) human.destroy();
        humans.clear();
        for(B2Body body : bodies) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        bodies.clear();
        setRandomSeed(42);

        int columns = BenchmarkSampleSupport.DEBUG_SIZE ? 10 : 26;
        int rows = BenchmarkSampleSupport.DEBUG_SIZE ? 40 : 150;
        if(shapeType == COMPOUND && !BenchmarkSampleSupport.DEBUG_SIZE) columns = 20;
        if(shapeType == HUMAN) {
            rows = BenchmarkSampleSupport.DEBUG_SIZE ? 5 : 30;
            if(BenchmarkSampleSupport.DEBUG_SIZE) columns = 10;
        }

        float shift = 1.15f;
        float centerX = shift * columns / 2.0f;
        float centerY = shift / 2.0f;
        float side = -0.1f;
        float extraY = 0.5f;
        if(shapeType == COMPOUND) {
            extraY = 0.25f;
            side = 0.25f;
            shift = 2.0f;
            centerX = shift * columns / 2.0f - 1.0f;
        }
        else if(shapeType == HUMAN) {
            extraY = 0.5f;
            side = 0.55f;
            shift = 2.5f;
            centerX = shift * columns / 2.0f;
        }

        int index = 0;
        float yStart = shapeType == HUMAN ? 2.0f : 100.0f;
        for(int column = 0; column < columns; column++) {
            float x = column * shift - centerX;
            for(int row = 0; row < rows; row++) {
                float y = row * (shift + extraY) + centerY + yStart;
                float bodyX = x + side;
                side = -side;
                if(shapeType == HUMAN) {
                    humans.add(new HumanRagdoll(this, bodyX, y, 3.5f, 0.05f, 5.0f, 0.5f));
                }
                else {
                    createBody(bodyX, y, index);
                }
                index++;
            }
        }
    }

    private void createBody(float x, float y, int index) {
        B2Body body = BenchmarkSampleSupport.createBody(this, com.github.xpenatan.box2d.B2.DynamicBody(),
                x, y, 0.0f, 0.0f, shapeType == MIX ? 0.3f : 0.0f, true, true);
        bodies.add(body);
        if(shapeType == CIRCLE || (shapeType == MIX && index % 3 == 0)) {
            float radius = randomFloat(0.25f, 0.75f);
            B2ShapeDef def = shapeDef(1.0f, 0.5f, 0.0f, 0.2f);
            B2Vec2 center = new B2Vec2(0.0f, 0.0f);
            B2Circle circle = new B2Circle(center, radius);
            B2Shape shape = createCircleShape(body, def, circle);
            discardHandle(shape);
            release(circle, center, def);
        }
        else if(shapeType == CAPSULE || (shapeType == MIX && index % 3 == 1)) {
            float radius = randomFloat(0.25f, 0.5f);
            float length = randomFloat(0.25f, 1.0f);
            B2ShapeDef def = shapeDef(1.0f, 0.5f, 0.0f, 0.2f);
            B2Vec2 centerOne = new B2Vec2(0.0f, -0.5f * length);
            B2Vec2 centerTwo = new B2Vec2(0.0f, 0.5f * length);
            B2Capsule capsule = new B2Capsule(centerOne, centerTwo, radius);
            B2Shape shape = createCapsuleShape(body, def, capsule);
            discardHandle(shape);
            release(capsule, centerTwo, centerOne, def);
        }
        else if(shapeType == MIX) {
            float width = randomFloat(0.1f, 0.5f);
            float height = randomFloat(0.5f, 0.75f);
            float radius = 0.25f * Math.max(0.0f, randomFloat(-1.0f, 1.0f));
            B2Polygon box = B2Polygon.CreateRoundedBox(width, height, radius);
            B2ShapeDef def = shapeDef(1.0f, 0.5f, 0.0f, 0.0f);
            B2Shape shape = createPolygonShape(body, def, box);
            discardHandle(shape);
            release(box, def);
        }
        else {
            B2Shape left = addPolygonShape(body,
                    new float[] {-1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 2.0f},
                    0.0f, 1.0f, 0.5f, 0.0f, 0.0f);
            B2Shape right = addPolygonShape(body,
                    new float[] {1.0f, 0.0f, -0.5f, 1.0f, 0.0f, 2.0f},
                    0.0f, 1.0f, 0.5f, 0.0f, 0.0f);
            discardHandle(left);
            discardHandle(right);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Shape", new String[] {"Circle", "Capsule", "Mix", "Compound", "Human"},
                        () -> shapeType, value -> { shapeType = Math.round(value); createScene(); }),
                Box2DSampleControl.button("Reset Scene", this::createScene));
    }
}
