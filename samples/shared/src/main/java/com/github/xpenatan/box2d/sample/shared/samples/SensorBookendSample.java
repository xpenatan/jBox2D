package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEndTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Events / Sensor Bookend lifecycle sample. */
public final class SensorBookendSample extends AbstractBox2DSample {
    private B2Body sensorBody1;
    private B2Body sensorBody2;
    private B2Body visitorBody;
    private B2Shape sensorShape1;
    private B2Shape sensorShape2;
    private B2Shape visitorShape;
    private long sensorId1;
    private long sensorId2;
    private long visitorId;
    private boolean visiting1;
    private boolean visiting2;
    private int sensorOverlapCount;

    public SensorBookendSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addSegmentShape(ground, -10, 0, 10, 0, 0, .6f, 0);
        addSegmentShape(ground, -10, 0, -10, 10, 0, .6f, 0);
        addSegmentShape(ground, 10, 0, 10, 10, 0, .6f, 0);
        createSensor1();
        createSensor2();
        createVisitor();
    }

    private B2Shape sensorBox(B2Body body, float hx, float hy, float radius) {
        B2ShapeDef def = shapeDef(0, .6f, 0, 0);
        def.SetIsSensor(true);
        def.SetEnableSensorEvents(true);
        B2Polygon box = radius > 0 ? B2Polygon.CreateRoundedBox(hx, hy, radius) : B2Polygon.CreateBox(hx, hy);
        B2Shape shape = createPolygonShape(body, def, box);
        release(box, def);
        return shape;
    }

    private void createSensor1() {
        sensorBody1 = createStaticBody(-2, 1, 0);
        sensorShape1 = sensorBox(sensorBody1, 1, 1, 0);
        sensorId1 = sensorShape1.GetId();
    }

    private void createSensor2() {
        sensorBody2 = createDynamicBody(2, 1, 0);
        sensorShape2 = sensorBox(sensorBody2, .5f, .5f, .5f);
        sensorId2 = sensorShape2.GetId();
        addBoxShape(sensorBody2, .5f, .5f, 1, .6f, 0, 0);
    }

    private void createVisitor() {
        visitorBody = createDynamicBody(-4, 1, 0);
        visitorShape = addCircleShape(visitorBody, 0, 0, .5f, 1, .6f, 0, 0);
        visitorShape.EnableSensorEvents(true);
        visitorId = visitorShape.GetId();
    }

    private void toggleBody(B2Body body) { if(body.IsEnabled()) body.Disable(); else body.Enable(); }

    @Override protected void afterStep(float deltaSeconds) {
        B2SensorEvents events = world().GetSensorEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent e = events.GetBeginEvent(i);
            update(e.GetSensorShapeId(), e.GetVisitorShapeId(), 1);
            release(e);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2SensorEndTouchEvent e = events.GetEndEvent(i);
            update(e.GetSensorShapeId(), e.GetVisitorShapeId(), -1);
            release(e);
        }
        release(events);
    }

    private void update(long sensor, long visitor, int delta) {
        if(sensor == sensorId1 && visitor == visitorId) visiting1 = delta > 0;
        else if(sensor == sensorId2 && visitor == visitorId) visiting2 = delta > 0;
        else if((sensor == sensorId1 && visitor == sensorId2) || (sensor == sensorId2 && visitor == sensorId1))
            sensorOverlapCount += delta;
    }

    @Override public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        addControls(c, "visitor", visitorBody, visitorShape, () -> { createVisitor(); }, () -> { destroyBody(visitorBody); visitorBody = null; });
        addControls(c, "sensor1", sensorBody1, sensorShape1, () -> { createSensor1(); }, () -> { destroyBody(sensorBody1); sensorBody1 = null; });
        addControls(c, "sensor2", sensorBody2, sensorShape2, () -> { createSensor2(); }, () -> { destroyBody(sensorBody2); sensorBody2 = null; });
        c.add(Box2DSampleControl.dynamicText(() -> "visiting 1 == " + visiting1));
        c.add(Box2DSampleControl.dynamicText(() -> "visiting 2 == " + visiting2));
        c.add(Box2DSampleControl.dynamicText(() -> "sensors overlap count == " + sensorOverlapCount));
        return c;
    }

    private void addControls(List<Box2DSampleControl> out, String name, B2Body body, B2Shape shape,
            Box2DSampleControl.Action create, Box2DSampleControl.Action destroy) {
        if(body == null || !body.IsValid()) out.add(Box2DSampleControl.button("create " + name, create));
        else {
            out.add(Box2DSampleControl.button("destroy " + name, destroy));
            out.add(Box2DSampleControl.checkbox(name + " events", () -> shape.AreSensorEventsEnabled() ? 1 : 0,
                    value -> shape.EnableSensorEvents(value != 0)));
            out.add(Box2DSampleControl.checkbox("enable " + name + " body", () -> body.IsEnabled() ? 1 : 0,
                    value -> { if(value != 0) body.Enable(); else body.Disable(); }));
        }
    }
}
