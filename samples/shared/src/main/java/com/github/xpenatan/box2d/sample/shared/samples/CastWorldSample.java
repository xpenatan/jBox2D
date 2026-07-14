package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2RayResult;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WorldCastHit;
import com.github.xpenatan.box2d.B2WorldCastResult;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Cast World sample. */
public final class CastWorldSample extends AbstractBox2DSample {
    private static final int MAX_BODIES = 64;
    private static final int IGNORE_INDEX = 7;
    private final B2Body[] bodies = new B2Body[MAX_BODIES];
    private final B2Shape[] shapes = new B2Shape[MAX_BODIES];
    private int bodyIndex;
    private int mode = 1;
    private int castType;
    private float castRadius = 0.5f;
    private boolean simple;
    private float rayStartX = -20.0f;
    private float rayStartY = 10.0f;
    private float rayEndX = 20.0f;
    private float rayEndY = 10.0f;
    private float angle;
    private boolean dragging;
    private boolean rotating;
    private float angleAnchor;
    private float baseAngle;
    private int hitCount;
    private final float[] hitX = new float[3];
    private final float[] hitY = new float[3];
    private final float[] normalX = new float[3];
    private final float[] normalY = new float[3];
    private final float[] fractions = new float[3];

    public CastWorldSample() {
        addGroundSegment(-40.0f, 0.0f, 40.0f, 0.0f);
    }

    private void create(int shapeType) {
        if(bodies[bodyIndex] != null) {
            if(bodies[bodyIndex].IsValid()) destroyBody(bodies[bodyIndex]);
            discardHandle(shapes[bodyIndex]);
            discardHandle(bodies[bodyIndex]);
            shapes[bodyIndex] = null;
            bodies[bodyIndex] = null;
        }
        float x = randomFloat(-20.0f, 20.0f);
        float y = randomFloat(0.0f, 20.0f);
        B2BodyDef def = new B2BodyDef();
        B2Vec2 position = new B2Vec2(x, y);
        def.SetPosition(position);
        def.SetAngle(randomFloat(-PI, PI));
        int mod = bodyIndex % 3;
        def.SetType(mod == 0 ? B2.StaticBody() : mod == 1 ? B2.KinematicBody() : B2.DynamicBody());
        if(mod == 2) def.SetGravityScale(0.0f);
        B2Body body = createBody(def);
        B2Shape shape;
        if(shapeType == 0) shape = addPolygonShape(body, new float[]{-0.5f, 0, 0.5f, 0, 0, 1.5f}, 0, 1, 0.6f, 0, 0);
        else if(shapeType == 1) shape = addPolygonShape(body, new float[]{-0.1f, 0, 0.1f, 0, 0, 1.5f}, 0.5f, 1, 0.6f, 0, 0);
        else if(shapeType == 2) {
            float w = 1.0f, b = w / (2.0f + (float)Math.sqrt(2.0)), s = (float)Math.sqrt(2.0) * b;
            shape = addPolygonShape(body, new float[]{0.5f*s,0, 0.5f*w,b, 0.5f*w,b+s, 0.5f*s,w,
                    -0.5f*s,w, -0.5f*w,b+s, -0.5f*w,b, -0.5f*s,0}, 0, 1, 0.6f, 0, 0);
        }
        else if(shapeType == 3) shape = addBoxShape(body, 0.5f, 0.5f, 1, 0.6f, 0, 0);
        else if(shapeType == 4) shape = addCircleShape(body, 0, 0, 0.5f, 1, 0.6f, 0, 0);
        else if(shapeType == 5) shape = addCapsuleShape(body, -0.5f, 0, 0.5f, 0, 0.25f, 1, 0.6f, 0, 0);
        else shape = addSegmentShape(body, -1, 0, 1, 0, 0, 0.6f, 0);
        bodies[bodyIndex] = body;
        shapes[bodyIndex] = shape;
        bodyIndex = (bodyIndex + 1) % MAX_BODIES;
        release(position, def);
    }

    private void createN(int type) { for(int i = 0; i < 10; i++) create(type); }

    private void destroyOne() {
        for(int i = 0; i < bodies.length; i++) {
            if(bodies[i] != null && bodies[i].IsValid()) {
                destroyBody(bodies[i]);
                discardHandle(shapes[i]);
                discardHandle(bodies[i]);
                shapes[i] = null;
                bodies[i] = null;
                return;
            }
        }
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        B2Vec2 origin = new B2Vec2(rayStartX, rayStartY);
        B2Vec2 translation = new B2Vec2(rayEndX - rayStartX, rayEndY - rayStartY);
        B2QueryFilter filter = new B2QueryFilter();
        if(simple) {
            B2RayResult result = world().CastRayClosest(origin, translation, filter);
            hitCount = result.GetHit() && result.GetFraction() > 0.0f ? 1 : 0;
            if(hitCount == 1) copy(result.GetPoint(), result.GetNormal(), result.GetFraction(), 0);
            release(result);
        }
        else {
            B2WorldCastResult result;
            if(castType == 0) result = world().CastRay(origin, translation, filter);
            else {
                B2ShapeProxy proxy = castProxy();
                result = world().CastShape(proxy, translation, filter);
                release(proxy);
            }
            selectHits(result);
            release(result);
        }
        release(filter, translation, origin);
    }

    private B2ShapeProxy castProxy() {
        B2ShapeProxy proxy = new B2ShapeProxy();
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        if(castType == 1) {
            B2Vec2 center = new B2Vec2(rayStartX, rayStartY);
            B2Circle circle = new B2Circle(center, castRadius);
            proxy.SetCircle(circle);
            release(circle, center);
        }
        else if(castType == 2) {
            B2Vec2 p1 = new B2Vec2(rayStartX - 0.25f*c, rayStartY - 0.25f*s);
            B2Vec2 p2 = new B2Vec2(rayStartX + 0.25f*c, rayStartY + 0.25f*s);
            B2Capsule capsule = new B2Capsule(p1, p2, castRadius);
            proxy.SetCapsule(capsule);
            release(capsule, p2, p1);
        }
        else {
            float[][] local = {{-0.25f,-0.5f},{0.25f,-0.5f},{0.25f,0.5f},{-0.25f,0.5f}};
            for(float[] p : local) {
                B2Vec2 point = new B2Vec2(rayStartX + c*p[0] - s*p[1], rayStartY + s*p[0] + c*p[1]);
                proxy.AddPoint(point); release(point);
            }
            proxy.SetRadius(castRadius);
        }
        return proxy;
    }

    private void selectHits(B2WorldCastResult result) {
        ArrayList<B2WorldCastHit> candidates = new ArrayList<B2WorldCastHit>();
        for(int i = 0; i < result.GetHitCount(); i++) {
            B2WorldCastHit hit = result.GetHit(i);
            if(hit.GetFraction() > 0.0f && !isIgnored(hit.GetShapeId())) candidates.add(hit);
            else release(hit);
        }
        if(mode == 1 || mode == 3) Collections.sort(candidates, Comparator.comparingDouble(B2WorldCastHit::GetFraction));
        hitCount = Math.min(mode <= 1 ? 1 : 3, candidates.size());
        for(int i = 0; i < hitCount; i++) {
            B2WorldCastHit value = candidates.get(i);
            copy(value.GetPoint(), value.GetNormal(), value.GetFraction(), i);
        }
        for(B2WorldCastHit value : candidates) release(value);
    }

    private boolean isIgnored(long shapeId) {
        B2Shape ignored = shapes[IGNORE_INDEX];
        return ignored != null && ignored.IsValid() && ignored.GetId() == shapeId;
    }

    private void copy(B2Vec2 point, B2Vec2 normal, float fraction, int index) {
        hitX[index] = point.GetX(); hitY[index] = point.GetY();
        normalX[index] = normal.GetX(); normalY[index] = normal.GetY(); fractions[index] = fraction;
        release(normal, point);
    }

    @Override public void mouseDown(float x, float y, int button, int modifiers) {
        if(button != 0) return;
        if((modifiers & 1) != 0) { rotating = true; angleAnchor = x; baseAngle = angle; }
        else { dragging = true; rayStartX = rayEndX = x; rayStartY = rayEndY = y; }
    }
    @Override public void mouseMove(float x, float y) {
        if(dragging) { rayEndX = x; rayEndY = y; }
        else if(rotating) angle = baseAngle + x - angleAnchor;
    }
    @Override public void mouseUp(float x, float y, int button) { dragging = rotating = false; }

    @Override
    public void draw(Box2DSampleDraw draw) {
        if(hitCount == 0) draw.segment(rayStartX, rayStartY, rayEndX, rayEndY, 0xD3D3D3FF);
        int[] colors = {0xFF0000FF, 0x00FF00FF, 0x0000FFFF};
        for(int i = 0; i < hitCount; i++) {
            float cx = rayStartX + fractions[i] * (rayEndX - rayStartX);
            float cy = rayStartY + fractions[i] * (rayEndY - rayStartY);
            draw.segment(rayStartX, rayStartY, cx, cy, 0xD3D3D3FF);
            draw.point(hitX[i], hitY[i], 7.0f, colors[i]);
            draw.segment(hitX[i], hitY[i], hitX[i] + normalX[i], hitY[i] + normalY[i], 0xFF00FFFF);
        }
        draw.point(rayStartX, rayStartY, 5.0f, 0x00FF00FF);
        if(castType == 1) draw.circle(rayStartX, rayStartY, castRadius, 0xFFFF00FF);
        else if(castType == 2) {
            float c = 0.25f*(float)Math.cos(angle), s = 0.25f*(float)Math.sin(angle);
            draw.segment(rayStartX-c, rayStartY-s, rayStartX+c, rayStartY+s, 0xFFFF00FF);
            draw.circle(rayStartX-c, rayStartY-s, castRadius, 0xFFFF00FF);
            draw.circle(rayStartX+c, rayStartY+s, castRadius, 0xFFFF00FF);
        }
    }

    @Override public List<Box2DSampleControl> controls() {
        List<Box2DSampleControl> result = new ArrayList<Box2DSampleControl>();
        result.add(Box2DSampleControl.checkbox("Simple", () -> simple ? 1 : 0, v -> simple = v != 0));
        result.add(Box2DSampleControl.combo("Type", new String[]{"Ray", "Circle", "Capsule", "Polygon"},
                () -> castType, v -> castType = (int)v));
        result.add(Box2DSampleControl.slider("Radius", 0, 2, 0.1f, () -> castRadius, v -> castRadius = v));
        result.add(Box2DSampleControl.combo("Mode", new String[]{"Any", "Closest", "Multiple", "Sorted"},
                () -> mode, v -> mode = (int)v));
        String[] names = {"Polygon 1", "Polygon 2", "Polygon 3", "Box", "Circle", "Capsule", "Segment"};
        for(int i = 0; i < names.length; i++) {
            final int type = i;
            result.add(Box2DSampleControl.button(names[i], () -> create(type)));
            result.add(Box2DSampleControl.button("10x " + names[i], () -> createN(type)));
        }
        result.add(Box2DSampleControl.button("Destroy Shape", this::destroyOne));
        result.add(Box2DSampleControl.text("Shape 7 is intentionally ignored by callback casts"));
        result.add(Box2DSampleControl.text("mouse 1: cast; shift + mouse 1: rotate cast shape"));
        return result;
    }
}
