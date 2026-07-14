package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2ContactEvents;
import com.github.xpenatan.box2d.B2ContactHitEvent;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Java port of Box2D 3.1.1's Stacking / Circle Stack hit-event sample. */
public final class CircleStackSample extends AbstractBox2DSample {
    private final Map<Long, Integer> shapeIndices = new HashMap<Long, Integer>();
    private final List<String> eventLabels = new ArrayList<String>();
    private final List<float[]> hitPoints = new ArrayList<float[]>();

    public CircleStackSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Shape groundShape = addSegmentShape(ground, -10.0f, 0.0f, 10.0f, 0.0f, 0.0f, 0.6f, 0.0f);
        shapeIndices.put(groundShape.GetId(), 0);
        setGravity(0.0f, -20.0f);
        world().SetContactTuning(90.0f, 10.0f, 3.0f);

        float y = 0.75f;
        for(int i = 0; i < 10; i++) {
            B2Body body = createDynamicBody(0.0f, y, 0.0f);
            B2ShapeDef shapeDef = shapeDef(1.0f + 4.0f * i, 0.0f, 0.0f, 0.0f);
            shapeDef.SetEnableHitEvents(true);
            B2Vec2 center = vector(0.0f, 0.0f);
            B2Circle circle = new B2Circle(center, 0.5f);
            B2Shape shape = createCircleShape(body, shapeDef, circle);
            shapeIndices.put(shape.GetId(), i + 1);
            release(circle, center, shapeDef);
            y += 1.25f;
        }
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2ContactEvents events = world().GetContactEvents();
        for(int i = 0; i < events.GetHitCount(); i++) {
            B2ContactHitEvent event = events.GetHitEvent(i);
            Integer a = shapeIndices.get(event.GetShapeIdA());
            Integer b = shapeIndices.get(event.GetShapeIdB());
            eventLabels.add((a == null ? -1 : a) + ", " + (b == null ? -1 : b));
            B2Vec2 point = event.GetPoint();
            hitPoints.add(new float[] { point.GetX(), point.GetY() });
            release(point, event);
        }
        release(events);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(float[] point : hitPoints) draw.point(point[0], point[1], 10.0f, 0xFFFFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        List<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.text("Hit events"));
        for(final String label : eventLabels) controls.add(Box2DSampleControl.text(label));
        return controls;
    }
}
