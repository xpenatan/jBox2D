package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEndTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Bodies / Sleep sample. */
public final class SleepSample extends AbstractBox2DSample {
    private final long groundShapeId;
    private final long[] sensorShapeIds = new long[2];
    private final boolean[] sensorTouching = new boolean[2];
    private final B2Body pendulum;
    private B2Body invoker;
    private float sleepThreshold = 0.05f;

    public SleepSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Shape groundShape = addSegmentShape(ground, -40.0f, 0.0f, 40.0f, 0.0f, 0.0f, 0.6f, 0.0f);
        groundShape.EnableSensorEvents(true);
        groundShapeId = groundShape.GetId();

        for(int i = 0; i < 2; i++) {
            B2BodyDef def = new B2BodyDef();
            B2Vec2 position = vector(-4.0f, 3.0f + 2.0f * i);
            def.SetType(B2.DynamicBody());
            def.SetPosition(position);
            def.SetIsAwake(false);
            def.SetEnableSleep(true);
            B2Body body = createBody(def);
            addCapsuleShape(body, 0.0f, 1.0f, 1.0f, 1.0f, 0.75f, 1.0f, 0.6f, 0.0f, 0.0f);
            B2ShapeDef sensorDef = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
            sensorDef.SetIsSensor(true);
            sensorDef.SetEnableSensorEvents(true);
            B2Vec2 c1 = vector(0.0f, 1.0f);
            B2Vec2 c2 = vector(1.0f, 1.0f);
            B2Capsule sensor = new B2Capsule(c1, c2, 1.0f);
            B2Shape sensorShape = createCapsuleShape(body, sensorDef, sensor);
            sensorShapeIds[i] = sensorShape.GetId();
            release(sensor, c2, c1, sensorDef, position, def);
        }

        addSleepingCircle(0.0f, 3.0f, false, false);
        B2Body awake = createConfiguredBody(5.0f, 3.0f, true, false, 0.0f, 0.0f);
        addOffsetBoxShape(awake, 1.0f, 1.0f, 0.0f, 1.0f, 0.25f * PI,
                1.0f, 0.6f, 0.0f, 0.0f);
        B2Body sleeper = createConfiguredBody(5.0f, 1.0f, false, true, 0.0f, 0.0f);
        addBoxShape(sleeper, 1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);

        B2BodyDef pendulumDef = new B2BodyDef();
        B2Vec2 pendulumPosition = vector(0.0f, 100.0f);
        pendulumDef.SetType(B2.DynamicBody());
        pendulumDef.SetPosition(pendulumPosition);
        pendulumDef.SetAngularDamping(0.5f);
        pendulumDef.SetSleepThreshold(sleepThreshold);
        pendulum = createBody(pendulumDef);
        addCapsuleShape(pendulum, 0.0f, 0.0f, 90.0f, 0.0f, 0.25f,
                1.0f, 0.6f, 0.0f, 0.0f);
        addRevoluteJoint(ground, pendulum, 0.0f, 100.0f, false);
        release(pendulumPosition, pendulumDef);

        B2Body contactSleeper = createConfiguredBody(-10.0f, 1.0f, false, true, 0.0f, 0.0f);
        addBoxShape(contactSleeper, 1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    private void addSleepingCircle(float x, float y, boolean awake, boolean sleep) {
        B2Body body = createConfiguredBody(x, y, awake, sleep, 0.0f, 0.0f);
        addCircleShape(body, 1.0f, 1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    private B2Body createConfiguredBody(float x, float y, boolean awake, boolean sleep,
            float angularDamping, float threshold) {
        B2BodyDef def = new B2BodyDef();
        B2Vec2 position = vector(x, y);
        def.SetType(B2.DynamicBody());
        def.SetPosition(position);
        def.SetIsAwake(awake);
        def.SetEnableSleep(sleep);
        def.SetAngularDamping(angularDamping);
        if(threshold > 0.0f) def.SetSleepThreshold(threshold);
        B2Body body = createBody(def);
        release(position, def);
        return body;
    }

    private void toggleInvoker() {
        if(invoker == null || !invoker.IsValid()) {
            invoker = createStaticBody(-10.5f, 3.0f, 0.0f);
            B2ShapeDef def = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
            def.SetInvokeContactCreation(true);
            com.github.xpenatan.box2d.B2Vec2 center = vector(0.0f, 0.0f);
            com.github.xpenatan.box2d.B2Rot rotation = new com.github.xpenatan.box2d.B2Rot(0.25f * PI);
            com.github.xpenatan.box2d.B2Polygon polygon = com.github.xpenatan.box2d.B2Polygon.CreateOffsetBox(
                    2.0f, 0.1f, center, rotation);
            createPolygonShape(invoker, def, polygon);
            release(polygon, rotation, center, def);
        }
        else {
            destroyBody(invoker);
            invoker = null;
        }
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2SensorEvents events = world().GetSensorEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent event = events.GetBeginEvent(i);
            updateSensor(event.GetSensorShapeId(), event.GetVisitorShapeId(), true);
            release(event);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2SensorEndTouchEvent event = events.GetEndEvent(i);
            updateSensor(event.GetSensorShapeId(), event.GetVisitorShapeId(), false);
            release(event);
        }
        release(events);
    }

    private void updateSensor(long sensorId, long visitorId, boolean touching) {
        if(visitorId != groundShapeId) return;
        for(int i = 0; i < sensorShapeIds.length; i++) if(sensorId == sensorShapeIds[i]) sensorTouching[i] = touching;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.text("Pendulum Tuning"),
                Box2DSampleControl.slider("sleep velocity", 0.0f, 1.0f, 0.01f,
                        () -> sleepThreshold, value -> { sleepThreshold = value; pendulum.SetAwake(true); }),
                Box2DSampleControl.slider("angular damping", 0.0f, 2.0f, 0.01f,
                        pendulum::GetAngularDamping, pendulum::SetAngularDamping),
                Box2DSampleControl.button(invoker == null ? "Create" : "Destroy", this::toggleInvoker),
                Box2DSampleControl.dynamicText(() -> "sensor touch 0 = " + sensorTouching[0]),
                Box2DSampleControl.dynamicText(() -> "sensor touch 1 = " + sensorTouching[1]));
    }
}
