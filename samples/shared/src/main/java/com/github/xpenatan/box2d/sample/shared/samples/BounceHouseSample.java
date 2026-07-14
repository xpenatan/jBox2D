package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2ContactEvents;
import com.github.xpenatan.box2d.B2ContactHitEvent;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Bounce House sample. */
public final class BounceHouseSample extends AbstractBox2DSample {
    private final float[][] hits = new float[4][4];
    private B2Body body;
    private B2Shape shape;
    private int shapeType = 2;
    private boolean hitEvents = true;
    private int stepIndex;

    public BounceHouseSample() {
        super(4, 0, 0);
        B2Body ground = createStaticBody(0, 0, 0);
        addSegmentShape(ground, -10, -10, 10, -10, 0, .6f, 0);
        addSegmentShape(ground, 10, -10, 10, 10, 0, .6f, 0);
        addSegmentShape(ground, 10, 10, -10, 10, 0, .6f, 0);
        addSegmentShape(ground, -10, 10, -10, -10, 0, .6f, 0);
        launch();
    }

    private void launch() {
        if(body != null) destroyBody(body);
        B2BodyDef def = new B2BodyDef();
        B2Vec2 velocity = vector(10, 20);
        def.SetType(B2.DynamicBody()); def.SetLinearVelocity(velocity); def.SetGravityScale(0);
        def.SetAllowFastRotation(shapeType == 0);
        body = createBody(def);
        B2ShapeDef shapeDef = shapeDef(1, .3f, 1.2f, 0); shapeDef.SetEnableHitEvents(hitEvents);
        if(shapeType == 0) {
            com.github.xpenatan.box2d.B2Vec2 center = vector(0, 0);
            com.github.xpenatan.box2d.B2Circle circle = new com.github.xpenatan.box2d.B2Circle(center, .5f);
            shape = createCircleShape(body, shapeDef, circle); release(circle, center);
        }
        else if(shapeType == 1) {
            B2Vec2 a = vector(-.5f, 0), b = vector(.5f, 0);
            com.github.xpenatan.box2d.B2Capsule capsule = new com.github.xpenatan.box2d.B2Capsule(a, b, .25f);
            shape = createCapsuleShape(body, shapeDef, capsule); release(capsule, b, a);
        }
        else {
            com.github.xpenatan.box2d.B2Polygon box = com.github.xpenatan.box2d.B2Polygon.CreateBox(2, .1f);
            shape = createPolygonShape(body, shapeDef, box); release(box);
        }
        release(shapeDef, velocity, def);
    }

    @Override protected void afterStep(float deltaSeconds) {
        stepIndex++;
        B2ContactEvents events = world().GetContactEvents();
        for(int i = 0; i < events.GetHitCount(); i++) {
            B2ContactHitEvent event = events.GetHitEvent(i);
            int oldest = 0;
            for(int j = 1; j < hits.length; j++) if(hits[j][3] < hits[oldest][3]) oldest = j;
            B2Vec2 point = event.GetPoint();
            hits[oldest][0] = point.GetX(); hits[oldest][1] = point.GetY();
            hits[oldest][2] = event.GetApproachSpeed(); hits[oldest][3] = stepIndex;
            release(point, event);
        }
        release(events);
    }

    @Override public void draw(Box2DSampleDraw draw) {
        for(float[] hit : hits) if(hit[3] > 0 && stepIndex <= hit[3] + 30) {
            draw.circle(hit[0], hit[1], .1f, 0xFF4500FF);
            draw.worldText(hit[0], hit[1], String.format("%.1f", hit[2]), 0xFFFFFFFF);
        }
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Shape", new String[] {"Circle", "Capsule", "Box"},
                        () -> shapeType, value -> { shapeType = (int)value; launch(); }),
                Box2DSampleControl.checkbox("hit events", () -> hitEvents ? 1 : 0,
                        value -> { hitEvents = value != 0; shape.EnableHitEvents(hitEvents); }));
    }
}
