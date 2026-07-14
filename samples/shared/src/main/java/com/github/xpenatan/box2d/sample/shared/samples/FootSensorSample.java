package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2AABB;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Chain;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEndTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Java port of Box2D 3.1.1's Events / Foot Sensor sample. */
public final class FootSensorSample extends AbstractBox2DSample {
    private static final long GROUND = 1, PLAYER = 2, FOOT = 4;
    private final B2Body player;
    private final long sensorId;
    private final Set<Long> overlaps = new HashSet<Long>();
    private boolean left;
    private boolean right;

    public FootSensorSample() {
        float[] points = new float[40];
        for(int i = 0; i < 20; i++) { points[2 * i] = 10.0f - i; points[2 * i + 1] = 0.0f; }
        B2Body ground = createStaticBody(0, 0, 0);
        B2Chain chain = addChain(ground, points, false, .6f);
        for(int i = 0; i < chain.GetSegmentCount(); i++) {
            B2Shape segment = chain.GetSegment(i);
            B2Filter filter = segment.GetFilter();
            filter.SetCategoryBits(GROUND);
            filter.SetMaskBits(FOOT | PLAYER);
            segment.SetFilter(filter);
            segment.EnableSensorEvents(true);
            release(filter, segment);
        }

        B2BodyDef bodyDef = new B2BodyDef();
        B2Vec2 position = vector(0, 1);
        bodyDef.SetType(B2.DynamicBody());
        bodyDef.SetFixedRotation(true);
        bodyDef.SetPosition(position);
        player = createBody(bodyDef);
        B2Shape playerShape = addCapsuleShape(player, 0, -.5f, 0, .5f, .5f, 1, .3f, 0, 0);
        B2Filter playerFilter = playerShape.GetFilter();
        playerFilter.SetCategoryBits(PLAYER);
        playerFilter.SetMaskBits(GROUND);
        playerShape.SetFilter(playerFilter);
        release(playerFilter);

        B2ShapeDef sensorDef = shapeDef(0, .3f, 0, 0);
        B2Filter sensorFilter = new B2Filter();
        sensorFilter.SetCategoryBits(FOOT);
        sensorFilter.SetMaskBits(GROUND);
        sensorDef.SetFilter(sensorFilter);
        sensorDef.SetIsSensor(true);
        sensorDef.SetEnableSensorEvents(true);
        B2Vec2 center = vector(0, -1);
        com.github.xpenatan.box2d.B2Rot rotation = new com.github.xpenatan.box2d.B2Rot(0);
        B2Polygon box = B2Polygon.CreateOffsetBox(.5f, .25f, center, rotation);
        B2Shape sensor = createPolygonShape(player, sensorDef, box);
        sensorId = sensor.GetId();
        release(box, rotation, center, sensorFilter, sensorDef, position, bodyDef);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        if(left) applyForceToCenter(player, -50, 0);
        if(right) applyForceToCenter(player, 50, 0);
    }

    @Override protected void afterStep(float deltaSeconds) {
        B2SensorEvents events = world().GetSensorEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent e = events.GetBeginEvent(i);
            if(e.GetSensorShapeId() == sensorId) overlaps.add(e.GetVisitorShapeId());
            release(e);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2SensorEndTouchEvent e = events.GetEndEvent(i);
            if(e.GetSensorShapeId() == sensorId) overlaps.remove(e.GetVisitorShapeId());
            release(e);
        }
        release(events);
    }

    @Override public void keyDown(int key) { if(key == 'A') left = true; if(key == 'D') right = true; }
    @Override public void keyUp(int key) { if(key == 'A') left = false; if(key == 'D') right = false; }

    @Override public void draw(Box2DSampleDraw draw) {
        for(Long id : overlaps) {
            B2Shape shape = new B2Shape(id);
            if(shape.IsValid()) {
                B2AABB aabb = shape.GetAABB();
                B2Vec2 center = aabb.GetCenter();
                draw.point(center.GetX(), center.GetY(), 10, 0xFFFFFFFF);
                release(center, aabb);
            }
            release(shape);
        }
    }

    @Override public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.dynamicText(() -> "count == " + overlaps.size()));
    }
}
