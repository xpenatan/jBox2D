package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WheelJointDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's World / Large World sample, using its official debug-scale cycle count. */
public final class LargeWorldSample extends AbstractBox2DSample {
    public static final int CYCLE_COUNT = 10;
    public static final float PERIOD = 40.0f;
    public static final float X_START = -0.5f * CYCLE_COUNT * PERIOD;

    private final List<B2Body> dynamicBodies = new ArrayList<B2Body>();
    private B2Body carChassis;
    private B2Body rearWheel;
    private float viewX = X_START;
    private float speed;
    private boolean explode = true;
    private boolean followCar;
    private int cycleIndex;
    private float explosionX = X_START + 0.5f * PERIOD;
    private int step;

    public LargeWorldSample() {
        createTerrain();
        for(int cycle = 0; cycle < CYCLE_COUNT; cycle++) {
            float base = (0.5f + cycle) * PERIOD + X_START;
            int kind = cycle % 3;
            if(kind == 0) createBoxField(base);
            else if(kind == 1) for(int i = 0; i < 5; i++) createHuman(base - 2.0f + i, 10.0f, 1.5f);
            else for(int i = 0; i < 5; i++) createDonut(base - 4.0f + 2.0f * i, 12.0f, 0.75f);
        }
        createCar(X_START + 20.0f, 40.0f);
    }

    private void createTerrain() {
        float omega = 2.0f * PI / PERIOD;
        int gridCount = (int)(CYCLE_COUNT * PERIOD);
        B2Body ground = null;
        float bodyOrigin = X_START;
        for(int i = 0; i < gridCount; i++) {
            float worldX = X_START + i;
            if(i % 10 == 0) {
                bodyOrigin = worldX;
                ground = createStaticBody(bodyOrigin, 0.0f, 0.0f);
            }
            int height = Math.round(4.0f * (float)Math.cos(omega * worldX)) + 12;
            for(int y = 0; y < height; y++) {
                addOffsetBoxShape(ground, 0.4f, 0.4f, worldX - bodyOrigin, y,
                        0.0f, 0.0f, 0.6f, 0.0f, 0.0f);
            }
        }
    }

    private void createBoxField(float base) {
        for(int x = 0; x < 10; x++) for(int y = 0; y < 5; y++) {
            dynamicBodies.add(addDynamicBox(base - 3.0f + 0.6f * x, 10.0f + 0.5f * y, 0.3f, 0.2f));
        }
    }

    private void createHuman(float x, float y, float scale) {
        B2Body torso = createDynamicBody(x, y, 0.0f);
        addCapsuleShape(torso, 0.0f, -0.35f * scale, 0.0f, 0.35f * scale, 0.18f * scale,
                1.0f, 0.5f, 0.0f, 0.0f);
        dynamicBodies.add(torso);
        B2Body head = addDynamicCircle(x, y + 0.72f * scale, 0.2f * scale);
        dynamicBodies.add(head);
        addRevoluteJoint(torso, head, x, y + 0.5f * scale, false);
        for(int side = -1; side <= 1; side += 2) {
            B2Body leg = createDynamicBody(x + side * 0.12f * scale, y - 0.85f * scale, 0.0f);
            addCapsuleShape(leg, 0.0f, -0.3f * scale, 0.0f, 0.3f * scale, 0.1f * scale,
                    1.0f, 0.5f, 0.0f, 0.0f);
            dynamicBodies.add(leg);
            addRevoluteJoint(torso, leg, x + side * 0.12f * scale, y - 0.45f * scale, false);
        }
    }

    private void createDonut(float x, float y, float radius) {
        final int count = 8;
        B2Body first = null;
        B2Body previous = null;
        for(int i = 0; i < count; i++) {
            float angle = 2.0f * PI * i / count;
            B2Body body = addDynamicCircle(x + radius * (float)Math.cos(angle),
                    y + radius * (float)Math.sin(angle), 0.18f);
            dynamicBodies.add(body);
            if(first == null) first = body;
            if(previous != null) addDistanceJoint(previous, body,
                    x + radius * (float)Math.cos(angle - 2.0f * PI / count),
                    y + radius * (float)Math.sin(angle - 2.0f * PI / count),
                    x + radius * (float)Math.cos(angle), y + radius * (float)Math.sin(angle),
                    2.0f * radius * (float)Math.sin(PI / count), 4.0f, 0.5f, true);
            previous = body;
        }
        addDistanceJoint(previous, first,
                x + radius * (float)Math.cos(2.0f * PI * (count - 1) / count),
                y + radius * (float)Math.sin(2.0f * PI * (count - 1) / count),
                x + radius, y, 2.0f * radius * (float)Math.sin(PI / count), 4.0f, 0.5f, true);
    }

    private void createCar(float x, float y) {
        carChassis = addDynamicBox(x, y, 2.5f, 0.6f, 0.0f, 2.0f, 0.5f, 0.0f, 0.0f);
        dynamicBodies.add(carChassis);
        B2Body rear = addDynamicCircle(x - 1.6f, y - 0.7f, 0.7f, 1.0f, 0.9f, 0.0f, 0.02f);
        rearWheel = rear;
        B2Body front = addDynamicCircle(x + 1.6f, y - 0.7f, 0.7f, 1.0f, 0.9f, 0.0f, 0.02f);
        dynamicBodies.add(rear);
        dynamicBodies.add(front);
        wheelJoint(carChassis, rear, x - 1.6f, y - 0.7f, true);
        wheelJoint(carChassis, front, x + 1.6f, y - 0.7f, false);
    }

    private com.github.xpenatan.box2d.B2Joint wheelJoint(B2Body chassis, B2Body wheel,
            float x, float y, boolean motor) {
        B2WheelJointDef def = new B2WheelJointDef();
        def.SetBodyIdA(chassis.GetId());
        def.SetBodyIdB(wheel.GetId());
        B2Vec2 anchor = vector(x, y);
        B2Vec2 localA = copyLocalPoint(chassis, anchor);
        B2Vec2 localB = copyLocalPoint(wheel, anchor);
        B2Vec2 axis = vector(0.0f, 1.0f);
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetLocalAxisA(axis);
        def.SetEnableSpring(true);
        def.SetHertz(2.0f);
        def.SetDampingRatio(0.7f);
        def.SetEnableMotor(motor);
        def.SetMaxMotorTorque(2000.0f);
        com.github.xpenatan.box2d.B2Joint joint = createWheelJoint(def);
        release(axis, localB, localA, anchor, def);
        return joint;
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        viewX = Math.max(-0.5f * PERIOD * CYCLE_COUNT,
                Math.min(0.5f * PERIOD * CYCLE_COUNT, viewX + deltaSeconds * speed));
        if(explode && (step & 1) == 1) {
            explosionX = (0.5f + cycleIndex) * PERIOD + X_START;
            for(B2Body body : dynamicBodies) {
                if(!body.IsValid()) continue;
                B2Vec2 p = body.GetPosition();
                float dx = p.GetX() - explosionX;
                float dy = p.GetY() - 7.0f;
                float distance2 = dx * dx + dy * dy;
                if(distance2 < 4.0f && distance2 > 0.001f) {
                    float scale = 1.0f / (float)Math.sqrt(distance2);
                    applyLinearImpulseToCenter(body, dx * scale, dy * scale);
                }
                release(p);
            }
            cycleIndex = (cycleIndex + 1) % CYCLE_COUNT;
        }
        step++;
    }

    @Override
    public void keyDown(int key) {
        if(rearWheel == null) return;
        if(key == 'A') rearWheel.SetAngularVelocity(20.0f);
        else if(key == 'S') rearWheel.SetAngularVelocity(0.0f);
        else if(key == 'D') rearWheel.SetAngularVelocity(-5.0f);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        if(explode) draw.circle(explosionX, 7.0f, 2.0f, 0x00FFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("speed", -400.0f, 400.0f, 1.0f, () -> speed, value -> speed = value),
                Box2DSampleControl.button("stop", () -> speed = 0.0f),
                Box2DSampleControl.checkbox("explode", () -> explode ? 1 : 0, value -> explode = value != 0),
                Box2DSampleControl.checkbox("follow car", () -> followCar ? 1 : 0, value -> followCar = value != 0),
                Box2DSampleControl.text("world size = 0.4 kilometers"),
                Box2DSampleControl.dynamicText(() -> String.format(java.util.Locale.US, "view x = %.1f", viewX)));
    }
}
