package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WorldOverlapResult;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Overlap World sample. */
public final class OverlapWorldSample extends AbstractBox2DSample {
    private static final int MAX_BODIES = 64;
    private final B2Body[] bodies = new B2Body[MAX_BODIES];
    private final B2Shape[] shapes = new B2Shape[MAX_BODIES];
    private int bodyIndex;
    private int queryType;
    private float queryX;
    private float queryY = 10.0f;
    private float angle;
    private boolean dragging;
    private boolean rotating;
    private float startX;
    private float baseAngle;
    private int overlapCount;

    public OverlapWorldSample() {
        createN(0);
    }

    private void create(int shapeType) {
        if(bodies[bodyIndex] != null && bodies[bodyIndex].IsValid()) destroyBody(bodies[bodyIndex]);
        B2BodyDef def = new B2BodyDef();
        B2Vec2 position = new B2Vec2(randomFloat(-20.0f, 20.0f), randomFloat(0.0f, 20.0f));
        def.SetPosition(position); def.SetAngle(randomFloat(-PI, PI));
        B2Body body = createBody(def);
        B2Shape shape;
        if(shapeType == 0) shape = addPolygonShape(body, new float[]{-0.5f, 0, 0.5f, 0, 0, 1.5f}, 0, 0, 0.6f, 0, 0);
        else if(shapeType == 1) shape = addPolygonShape(body, new float[]{-0.1f, 0, 0.1f, 0, 0, 1.5f}, 0, 0, 0.6f, 0, 0);
        else if(shapeType == 2) {
            float w = 1.0f, b = w / (2.0f + (float)Math.sqrt(2.0)), s = (float)Math.sqrt(2.0) * b;
            shape = addPolygonShape(body, new float[]{0.5f*s,0, 0.5f*w,b, 0.5f*w,b+s, 0.5f*s,w,
                    -0.5f*s,w, -0.5f*w,b+s, -0.5f*w,b, -0.5f*s,0}, 0, 0, 0.6f, 0, 0);
        }
        else if(shapeType == 3) shape = addBoxShape(body, 0.5f, 0.5f, 0, 0.6f, 0, 0);
        else if(shapeType == 4) shape = addCircleShape(body, 0, 0, 0.5f, 0, 0.6f, 0, 0);
        else if(shapeType == 5) shape = addCapsuleShape(body, -0.5f, 0, 0.5f, 0, 0.25f, 0, 0.6f, 0, 0);
        else shape = addSegmentShape(body, -1, 0, 1, 0, 0, 0.6f, 0);
        bodies[bodyIndex] = body; shapes[bodyIndex] = shape;
        bodyIndex = (bodyIndex + 1) % MAX_BODIES;
        release(position, def);
    }

    private void createN(int type) { for(int i = 0; i < 10; i++) create(type); }
    private void destroyOne() {
        for(int i = 0; i < bodies.length; i++) if(bodies[i] != null && bodies[i].IsValid()) {
            destroyBody(bodies[i]); return;
        }
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        B2ShapeProxy proxy = queryProxy();
        B2QueryFilter filter = new B2QueryFilter();
        B2WorldOverlapResult result = world().OverlapShape(proxy, filter);
        overlapCount = result.GetShapeCount();
        int destroyed = 0;
        for(int r = 0; r < result.GetShapeCount() && destroyed < 16; r++) {
            long id = result.GetShapeId(r);
            for(int i = 0; i < shapes.length; i++) {
                if(i == 7 || shapes[i] == null || !shapes[i].IsValid() || shapes[i].GetId() != id) continue;
                destroyBody(bodies[i]); destroyed++; break;
            }
        }
        release(result, filter, proxy);
    }

    private B2ShapeProxy queryProxy() {
        B2ShapeProxy proxy = new B2ShapeProxy();
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        if(queryType == 0) {
            B2Vec2 center = new B2Vec2(queryX, queryY);
            B2Circle circle = new B2Circle(center, 1.0f);
            proxy.SetCircle(circle); release(circle, center);
        }
        else if(queryType == 1) {
            B2Vec2 p1 = new B2Vec2(queryX - c, queryY - s);
            B2Vec2 p2 = new B2Vec2(queryX + c, queryY + s);
            B2Capsule capsule = new B2Capsule(p1, p2, 0.5f);
            proxy.SetCapsule(capsule); release(capsule, p2, p1);
        }
        else {
            float[][] local = {{-2,-0.5f},{2,-0.5f},{2,0.5f},{-2,0.5f}};
            for(float[] p : local) {
                B2Vec2 point = new B2Vec2(queryX + c*p[0] - s*p[1], queryY + s*p[0] + c*p[1]);
                proxy.AddPoint(point); release(point);
            }
        }
        return proxy;
    }

    @Override public void mouseDown(float x, float y, int button, int modifiers) {
        if(button != 0) return;
        if((modifiers & 1) != 0) { rotating = true; startX = x; baseAngle = angle; }
        else { dragging = true; queryX = x; queryY = y; }
    }
    @Override public void mouseMove(float x, float y) {
        if(dragging) { queryX = x; queryY = y; }
        else if(rotating) angle = baseAngle + x - startX;
    }
    @Override public void mouseUp(float x, float y, int button) { dragging = rotating = false; }

    @Override public void draw(Box2DSampleDraw draw) {
        if(queryType == 0) draw.circle(queryX, queryY, 1.0f, 0xFFFFFFFF);
        else if(queryType == 1) {
            float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
            draw.segment(queryX-c, queryY-s, queryX+c, queryY+s, 0xFFFFFFFF);
            draw.circle(queryX-c, queryY-s, 0.5f, 0xFFFFFFFF);
            draw.circle(queryX+c, queryY+s, 0.5f, 0xFFFFFFFF);
        }
        else {
            B2ShapeProxy proxy = queryProxy();
            CollisionSampleSupport.drawProxy(draw, proxy, 0, 0, 0, 0xFFFFFFFF, false);
            release(proxy);
        }
        if(shapes[7] != null && shapes[7].IsValid()) {
            B2Vec2 p = bodies[7].GetPosition(); draw.worldText(p.GetX(), p.GetY(), "skip", 0xFFFFFFFF); release(p);
        }
    }

    @Override public List<Box2DSampleControl> controls() {
        List<Box2DSampleControl> result = new ArrayList<Box2DSampleControl>();
        String[] names = {"Polygon 1", "Polygon 2", "Polygon 3", "Box", "Circle", "Capsule", "Segment"};
        for(int i = 0; i < names.length; i++) {
            final int type = i;
            result.add(Box2DSampleControl.button(names[i], () -> create(type)));
            result.add(Box2DSampleControl.button("10x " + names[i], () -> createN(type)));
        }
        result.add(Box2DSampleControl.button("Destroy Shape", this::destroyOne));
        result.add(Box2DSampleControl.combo("Overlap Shape", new String[]{"Circle", "Capsule", "Box"},
                () -> queryType, v -> queryType = (int)v));
        result.add(Box2DSampleControl.dynamicText(() -> "overlaps = " + overlapCount));
        result.add(Box2DSampleControl.text("mouse 1: drag query; shift + mouse 1: rotate"));
        return result;
    }
}
