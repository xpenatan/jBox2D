package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Java port of Box2D 3.1.1's Events / Sensor Funnel sample. */
public final class SensorFunnelSample extends AbstractBox2DSample {
    private static final float[] FUNNEL = {
            -16.8672504f, 31.088623f, 16.8672485f, 31.088623f, 16.8672485f, 17.1978741f,
            8.2682495f, 11.906374f, 16.8672485f, 11.906374f, 16.8672485f, -0.661377f,
            8.2682495f, -5.953125f, 16.8672485f, -5.953125f, 16.8672485f, -13.229126f,
            3.6379986f, -23.151123f, 3.6379986f, -31.088623f, -3.6380005f, -31.088623f,
            -3.6380005f, -23.151123f, -16.8672504f, -13.229126f, -16.8672504f, -5.953125f,
            -8.2682514f, -5.953125f, -16.8672504f, -0.661377f, -16.8672504f, 11.906374f,
            -8.2682514f, 11.906374f, -16.8672504f, 17.1978741f
    };
    private final Map<Long, B2Body> visitors = new HashMap<Long, B2Body>();
    private final List<B2Body> elements = new ArrayList<B2Body>();
    private final long sensorId;
    private int type = 1;
    private float wait = 0.5f;
    private float side = -15.0f;

    public SensorFunnelSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addChain(ground, FUNNEL, true, 0.2f);
        float y = 14.0f;
        float sign = 1.0f;
        for(int i = 0; i < 3; i++) {
            B2Body paddle = addDynamicBox(0, y, 6.0f, 0.5f, 0, 1, .1f, 1, 0);
            B2RevoluteJointDef def = new B2RevoluteJointDef();
            def.SetBodyIdA(ground.GetId());
            def.SetBodyIdB(paddle.GetId());
            B2Vec2 anchorA = vector(0, y);
            B2Vec2 anchorB = vector(0, 0);
            def.SetLocalAnchorA(anchorA);
            def.SetLocalAnchorB(anchorB);
            def.SetMaxMotorTorque(200.0f);
            def.SetMotorSpeed(2.0f * sign);
            def.SetEnableMotor(true);
            createRevoluteJoint(def);
            release(anchorB, anchorA, def);
            y -= 14.0f;
            sign = -sign;
        }
        B2ShapeDef sensorDef = shapeDef(0, .6f, 0, 0);
        sensorDef.SetIsSensor(true);
        sensorDef.SetEnableSensorEvents(true);
        B2Vec2 center = vector(0, -30.5f);
        com.github.xpenatan.box2d.B2Rot rotation = new com.github.xpenatan.box2d.B2Rot(0);
        B2Polygon box = B2Polygon.CreateOffsetBox(4, 1, center, rotation);
        B2Shape sensor = createPolygonShape(ground, sensorDef, box);
        sensorId = sensor.GetId();
        release(box, rotation, center, sensorDef);
        createElement();
    }

    private void createElement() {
        if(elements.size() >= 32) return;
        B2Body body;
        B2Shape shape;
        if(type == 0) {
            body = addDynamicCircle(side, 29.5f, 1.0f, 1, .4f, 0, .1f);
            shape = body.GetShape(0);
        }
        else {
            body = createDynamicBody(side, 29.5f, 0);
            shape = addCapsuleShape(body, 0, -1.2f, 0, 1.2f, .45f, 1, .4f, 0, .1f);
        }
        shape.EnableSensorEvents(true);
        visitors.put(shape.GetId(), body);
        elements.add(body);
        if(shape.native_hasOwnership()) release(shape);
        side = -side;
    }

    private void clear() {
        for(B2Body body : elements) destroyBody(body);
        elements.clear();
        visitors.clear();
        wait = 0.0f;
    }

    @Override protected void afterStep(float deltaSeconds) {
        ArrayList<B2Body> destroy = new ArrayList<B2Body>();
        B2SensorEvents events = world().GetSensorEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent event = events.GetBeginEvent(i);
            if(event.GetSensorShapeId() == sensorId) {
                B2Body body = visitors.remove(event.GetVisitorShapeId());
                if(body != null && !destroy.contains(body)) destroy.add(body);
            }
            release(event);
        }
        release(events);
        for(B2Body body : destroy) { elements.remove(body); destroyBody(body); }
        wait -= deltaSeconds;
        if(wait <= 0.0f) { createElement(); wait += 0.5f; }
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.radio("donut", () -> type == 0 ? 1 : 0,
                        () -> { clear(); type = 0; createElement(); }),
                Box2DSampleControl.radio("human", () -> type == 1 ? 1 : 0,
                        () -> { clear(); type = 1; createElement(); }));
    }
}
