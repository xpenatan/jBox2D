package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2RayResult;
import com.github.xpenatan.box2d.B2SensorBeginTouchEvent;
import com.github.xpenatan.box2d.B2SensorEndTouchEvent;
import com.github.xpenatan.box2d.B2SensorEvents;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Java port of Box2D 3.1.1's Events / Sensor Types sample. */
public final class SensorTypesSample extends AbstractBox2DSample {
    private static final long GROUND = 1L;
    private static final long SENSOR = 2L;
    private static final long DEFAULT = 4L;
    private final long staticSensorId;
    private final long kinematicSensorId;
    private final long dynamicSensorId;
    private final B2Body kinematicBody;
    private final Set<Long> staticOverlaps = new HashSet<Long>();
    private final Set<Long> kinematicOverlaps = new HashSet<Long>();
    private final Set<Long> dynamicOverlaps = new HashSet<Long>();
    private boolean rayHit;
    private float rayX;
    private float rayY;

    public SensorTypesSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addFilteredSegment(ground, -6.0f, 0.0f, 6.0f, 0.0f);
        addFilteredSegment(ground, -6.0f, 0.0f, -6.0f, 4.0f);
        addFilteredSegment(ground, 6.0f, 0.0f, 6.0f, 4.0f);

        B2Body staticBody = createStaticBody(-3.0f, 0.8f, 0.0f);
        staticSensorId = createSensor(staticBody, 1.0f).GetId();

        B2BodyDef kinematicDef = new B2BodyDef();
        B2Vec2 velocity = vector(0.0f, 1.0f);
        kinematicDef.SetType(B2.KinematicBody());
        kinematicDef.SetLinearVelocity(velocity);
        kinematicBody = createBody(kinematicDef);
        kinematicSensorId = createSensor(kinematicBody, 1.0f).GetId();

        B2Body dynamicBody = createDynamicBody(3.0f, 1.0f, 0.0f);
        dynamicSensorId = createSensor(dynamicBody, 1.0f).GetId();
        B2ShapeDef solidDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        B2Filter solidFilter = new B2Filter();
        solidFilter.SetCategoryBits(DEFAULT);
        solidDef.SetFilter(solidFilter);
        B2Polygon innerBox = B2Polygon.CreateBox(0.8f, 0.8f);
        createPolygonShape(dynamicBody, solidDef, innerBox);

        B2Body visitor = createDynamicBody(-5.0f, 1.0f, 0.0f);
        B2ShapeDef visitorDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        B2Filter visitorFilter = new B2Filter();
        visitorFilter.SetCategoryBits(DEFAULT);
        visitorFilter.SetMaskBits(GROUND | DEFAULT | SENSOR);
        visitorDef.SetFilter(visitorFilter);
        visitorDef.SetEnableSensorEvents(true);
        B2Vec2 center = vector(0.0f, 0.0f);
        B2Circle circle = new B2Circle(center, 0.5f);
        createCircleShape(visitor, visitorDef, circle);
        release(circle, center, visitorFilter, visitorDef, innerBox, solidFilter, solidDef, velocity, kinematicDef);
    }

    private void addFilteredSegment(B2Body ground, float x1, float y1, float x2, float y2) {
        B2Shape shape = addSegmentShape(ground, x1, y1, x2, y2, 0.0f, 0.6f, 0.0f);
        B2Filter filter = shape.GetFilter();
        filter.SetCategoryBits(GROUND);
        filter.SetMaskBits(DEFAULT);
        shape.SetFilter(filter);
        shape.EnableSensorEvents(true);
        release(filter);
    }

    private B2Shape createSensor(B2Body body, float halfExtent) {
        B2ShapeDef def = shapeDef(0.0f, 0.6f, 0.0f, 0.0f);
        B2Filter filter = new B2Filter();
        filter.SetCategoryBits(SENSOR);
        def.SetFilter(filter);
        def.SetIsSensor(true);
        def.SetEnableSensorEvents(true);
        B2Polygon box = B2Polygon.CreateBox(halfExtent, halfExtent);
        B2Shape shape = createPolygonShape(body, def, box);
        release(box, filter, def);
        return shape;
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        B2Vec2 position = kinematicBody.GetPosition();
        if(position.GetY() < 0.0f) setLinearVelocity(kinematicBody, 0.0f, 1.0f);
        else if(position.GetY() > 3.0f) setLinearVelocity(kinematicBody, 0.0f, -1.0f);
        release(position);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2SensorEvents events = world().GetSensorEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2SensorBeginTouchEvent event = events.GetBeginEvent(i);
            updateOverlap(event.GetSensorShapeId(), event.GetVisitorShapeId(), true);
            release(event);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2SensorEndTouchEvent event = events.GetEndEvent(i);
            updateOverlap(event.GetSensorShapeId(), event.GetVisitorShapeId(), false);
            release(event);
        }
        release(events);

        B2Vec2 origin = vector(5.0f, 1.0f);
        B2Vec2 translation = vector(-10.0f, 0.0f);
        B2QueryFilter filter = new B2QueryFilter();
        B2RayResult result = world().CastRayClosest(origin, translation, filter);
        rayHit = result.GetHit();
        if(rayHit) {
            B2Vec2 point = result.GetPoint();
            rayX = point.GetX();
            rayY = point.GetY();
            release(point);
        }
        release(result, filter, translation, origin);
    }

    private void updateOverlap(long sensor, long visitor, boolean begin) {
        Set<Long> overlaps = sensor == staticSensorId ? staticOverlaps
                : sensor == kinematicSensorId ? kinematicOverlaps
                : sensor == dynamicSensorId ? dynamicOverlaps : null;
        if(overlaps == null) return;
        if(begin) overlaps.add(visitor); else overlaps.remove(visitor);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.segment(5.0f, 1.0f, -5.0f, 1.0f, 0x696969FF);
        if(rayHit) draw.point(rayX, rayY, 10.0f, 0x00FFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.dynamicText(() -> "static: " + staticOverlaps.size() + " overlap(s)"),
                Box2DSampleControl.dynamicText(() -> "kinematic: " + kinematicOverlaps.size() + " overlap(s)"),
                Box2DSampleControl.dynamicText(() -> "dynamic: " + dynamicOverlaps.size() + " overlap(s)"));
    }
}
