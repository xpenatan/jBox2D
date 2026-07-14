package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEndTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Java port of Box2D 3.1.1's Benchmark / Sensor sample. */
public final class BenchmarkSensorSample extends AbstractBox2DSample {
    private static final int COLUMN_COUNT = 40;
    private static final int ROW_COUNT = 40;
    private final Set<Long> activeSensors = new HashSet<Long>();
    private final Map<Long, B2Body> visitorBodies = new HashMap<Long, B2Body>();
    private int maxBeginCount;
    private int maxEndCount;
    private int stepCount;

    public BenchmarkSensorSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2ShapeDef activeDef = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
        activeDef.SetIsSensor(true);
        activeDef.SetEnableSensorEvents(true);
        float gridSize = 3.0f;
        for(int i = 0; i < 81; i++) {
            B2Vec2 center = new B2Vec2(-40.0f * gridSize + i * gridSize, 0.0f);
            B2Rot rotation = new B2Rot(0.0f);
            B2Polygon box = B2Polygon.CreateOffsetBox(0.5f * gridSize, 0.5f * gridSize, center, rotation);
            B2Shape shape = createPolygonShape(ground, activeDef, box);
            activeSensors.add(shape.GetId());
            discardHandle(shape);
            release(box, rotation, center);
        }
        release(activeDef);

        setRandomSeed(42);
        B2ShapeDef passiveDef = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
        passiveDef.SetIsSensor(true);
        passiveDef.SetEnableSensorEvents(true);
        float shift = 5.0f;
        float xCenter = 0.5f * shift * COLUMN_COUNT;
        for(int row = 0; row < ROW_COUNT; row++) {
            float y = row * shift + 10.0f;
            for(int column = 0; column < COLUMN_COUNT; column++) {
                float x = column * shift - xCenter;
                B2Vec2 center = new B2Vec2(x, y + randomFloat(-1.0f, 1.0f));
                B2Rot rotation = new B2Rot(randomFloat(-PI, PI));
                B2Polygon box = B2Polygon.CreateOffsetRoundedBox(0.5f, 0.5f, center, rotation, 0.1f);
                B2Shape shape = createPolygonShape(ground, passiveDef, box);
                discardHandle(shape);
                release(box, rotation, center);
            }
        }
        release(passiveDef);
    }

    private void createRow(float y) {
        float shift = 5.0f;
        float xCenter = 0.5f * shift * COLUMN_COUNT;
        B2ShapeDef shapeDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        shapeDef.SetEnableSensorEvents(true);
        B2Vec2 circleCenter = new B2Vec2(0.0f, 0.0f);
        B2Circle circle = new B2Circle(circleCenter, 0.5f);
        for(int i = 0; i < COLUMN_COUNT; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = new B2Vec2(shift * i - xCenter, y);
            B2Vec2 velocity = new B2Vec2(0.0f, -5.0f);
            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetPosition(position);
            bodyDef.SetGravityScale(0.0f);
            bodyDef.SetLinearVelocity(velocity);
            B2Body body = createBody(bodyDef);
            B2Shape shape = createCircleShape(body, shapeDef, circle);
            visitorBodies.put(shape.GetId(), body);
            discardHandle(shape);
            release(velocity, position, bodyDef);
        }
        release(circle, circleCenter, shapeDef);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2SensorEvents events = world().GetSensorEvents();
        Set<Long> zombies = new HashSet<Long>();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent event = events.GetBeginEvent(i);
            long visitorId = event.GetVisitorShapeId();
            if(activeSensors.contains(event.GetSensorShapeId())) zombies.add(visitorId);
            else setVisitorColor(visitorId, 0x00FF00FF);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2SensorEndTouchEvent event = events.GetEndEvent(i);
            if(!zombies.contains(event.GetVisitorShapeId())) setVisitorColor(event.GetVisitorShapeId(), 0);
        }
        for(Long visitorId : zombies) {
            B2Body body = visitorBodies.remove(visitorId);
            if(body != null) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        }
        maxBeginCount = Math.max(maxBeginCount, events.GetBeginCount());
        maxEndCount = Math.max(maxEndCount, events.GetEndCount());
        release(events);
        stepCount++;
        if((stepCount & 0x1F) == 0) createRow(10.0f + ROW_COUNT * 5.0f);
    }

    private void setVisitorColor(long shapeId, int color) {
        B2Shape shape = new B2Shape(shapeId);
        if(shape.IsValid()) {
            B2SurfaceMaterial material = shape.GetSurfaceMaterial();
            material.SetCustomColor(color);
            shape.SetSurfaceMaterial(material);
        }
        release(shape);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.dynamicText(() -> "max begin touch events = " + maxBeginCount),
                Box2DSampleControl.dynamicText(() -> "max end touch events = " + maxEndCount));
    }
}
