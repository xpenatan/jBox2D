package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's World / Large World sample, including native debug/release scales. */
public final class LargeWorldSample extends AbstractBox2DSample {
    public static final int CYCLE_COUNT = BenchmarkSampleSupport.DEBUG_SIZE ? 10 : 600;
    public static final float PERIOD = 40.0f;
    public static final float X_START = -0.5f * CYCLE_COUNT * PERIOD;

    private final CarAssembly car;
    private float viewX = X_START;
    private float speed;
    private boolean explode = true;
    private boolean followCar;
    private boolean driveLeft;
    private boolean brake;
    private boolean driveRight;
    private int cycleIndex;
    private float explosionX = X_START + 0.5f * PERIOD;
    private float carCameraX = X_START + 20.0f;
    private int step;

    public LargeWorldSample() {
        createTerrain();

        int humanIndex = 0;
        for(int cycleIndex = 0; cycleIndex < CYCLE_COUNT; cycleIndex++) {
            float xBase = (0.5f + cycleIndex) * PERIOD + X_START;
            int remainder = cycleIndex % 3;
            if(remainder == 0) {
                for(int i = 0; i < 10; i++) {
                    float x = xBase - 3.0f + 0.6f * i;
                    for(int j = 0; j < 5; j++) {
                        addDynamicBox(x, 10.0f + 0.5f * j, 0.3f, 0.2f);
                    }
                }
            }
            else if(remainder == 1) {
                for(int i = 0; i < 5; i++) {
                    new HumanRagdoll(this, xBase - 2.0f + i, 10.0f, 1.5f,
                            0.05f, 0.0f, 0.0f, ++humanIndex, false);
                }
            }
            else {
                for(int i = 0; i < 5; i++) {
                    new DonutAssembly(this, xBase - 4.0f + 2.0f * i, 12.0f, 0.75f, 0, false);
                }
            }
        }

        car = new CarAssembly(this, X_START + 20.0f, 40.0f, 10.0f, 2.0f, 0.7f, 2000.0f);
    }

    private void createTerrain() {
        float omega = 2.0f * PI / PERIOD;
        int gridCount = (int)(CYCLE_COUNT * PERIOD);
        B2ShapeDef shapeDef = new B2ShapeDef();
        shapeDef.SetInvokeContactCreation(false);
        B2Rot rotation = new B2Rot();
        B2Vec2 center = new B2Vec2();

        float xBody = X_START;
        float xShape = X_START;
        B2Body ground = null;
        for(int i = 0; i < gridCount; i++) {
            if(i % 10 == 0) {
                ground = createStaticBody(xBody, 0.0f, 0.0f);
                xShape = 0.0f;
            }

            int yCount = Math.round(4.0f * B2.Cos(omega * xBody)) + 12;
            float y = 0.0f;
            for(int j = 0; j < yCount; j++) {
                center.Set(xShape, y);
                B2Polygon square = B2Polygon.CreateOffsetRoundedBox(0.4f, 0.4f, center, rotation, 0.1f);
                createPolygonShape(ground, shapeDef, square);
                release(square);
                y += 1.0f;
            }
            xBody += 1.0f;
            xShape += 1.0f;
        }
        release(center, rotation, shapeDef);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        float span = 0.5f * PERIOD * CYCLE_COUNT;
        viewX = Math.max(-span, Math.min(span, viewX + deltaSeconds * speed));

        if((step & 1) == 1 && explode) {
            explosionX = (0.5f + cycleIndex) * PERIOD - span;
            explode(explosionX, 7.0f, 2.0f, 0.1f, 1.0f);
            cycleIndex = (cycleIndex + 1) % CYCLE_COUNT;
        }

        if(driveLeft) car.setSpeed(20.0f);
        if(brake) car.setSpeed(0.0f);
        if(driveRight) car.setSpeed(-5.0f);
        if(followCar) {
            B2Vec2 position = car.chassis.GetPosition();
            carCameraX = position.GetX();
            release(position);
        }
        step++;
    }

    @Override
    public void keyDown(int key) {
        if(key == 'A') driveLeft = true;
        else if(key == 'S') brake = true;
        else if(key == 'D') driveRight = true;
    }

    @Override
    public void keyUp(int key) {
        if(key == 'A') driveLeft = false;
        else if(key == 'S') brake = false;
        else if(key == 'D') driveRight = false;
    }

    @Override
    public boolean tracksCameraX() {
        return speed != 0.0f || followCar;
    }

    @Override
    public float cameraCenterX() {
        return followCar ? carCameraX : viewX;
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        if(explode) draw.circle(explosionX, 7.0f, 2.0f, 0xF0FFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("speed", -400.0f, 400.0f, 1.0f, () -> speed, value -> speed = value),
                Box2DSampleControl.button("stop", () -> speed = 0.0f),
                Box2DSampleControl.checkbox("explode", () -> explode ? 1 : 0, value -> explode = value != 0),
                Box2DSampleControl.checkbox("follow car", () -> followCar ? 1 : 0, value -> followCar = value != 0),
                Box2DSampleControl.text("world size = " + (CYCLE_COUNT * PERIOD / 1000.0f) + " kilometers"));
    }
}
