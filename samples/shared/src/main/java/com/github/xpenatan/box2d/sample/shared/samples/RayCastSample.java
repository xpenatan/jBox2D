package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2CastResult;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2Hull;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Ray Cast sample. */
public final class RayCastSample extends AbstractBox2DSample {
    private final B2Circle circle;
    private final B2Capsule capsule;
    private final B2Polygon box;
    private final B2Polygon triangle;
    private final B2Segment segment;
    private final B2ShapeProxy circleProxy;
    private final B2ShapeProxy capsuleProxy;
    private final B2ShapeProxy boxProxy;
    private final B2ShapeProxy triangleProxy;
    private final B2ShapeProxy segmentProxy;
    private float offsetX;
    private float offsetY;
    private float angle;
    private float rayStartX = 0.0f;
    private float rayStartY = 30.0f;
    private float rayEndX;
    private float rayEndY;
    private float hitX;
    private float hitY;
    private float normalX;
    private float normalY;
    private float hitFraction;
    private boolean hit;
    private boolean showFraction;
    private boolean rayDrag;
    private boolean translating;
    private boolean rotating;
    private float startX, startY, baseX, baseY, baseAngle;

    public RayCastSample() {
        super(1, 0.0f, 0.0f);
        B2Vec2 zero = new B2Vec2();
        circle = own(new B2Circle(zero, 2.0f));
        B2Vec2 c1 = new B2Vec2(-1.0f, 1.0f);
        B2Vec2 c2 = new B2Vec2(1.0f, -1.0f);
        capsule = own(new B2Capsule(c1, c2, 1.5f));
        box = own(B2Polygon.CreateBox(2.0f, 2.0f));
        B2Hull hull = new B2Hull();
        add(hull, -2.0f, 0.0f); add(hull, 2.0f, 0.0f); add(hull, 2.0f, 3.0f);
        hull.Compute();
        triangle = own(B2Polygon.CreateFromHull(hull, 0.0f));
        B2Vec2 s1 = new B2Vec2(-3.0f, 0.0f);
        B2Vec2 s2 = new B2Vec2(3.0f, 0.0f);
        segment = own(new B2Segment(s1, s2));
        circleProxy = own(new B2ShapeProxy()); circleProxy.SetCircle(circle);
        capsuleProxy = own(new B2ShapeProxy()); capsuleProxy.SetCapsule(capsule);
        boxProxy = own(new B2ShapeProxy()); boxProxy.SetPolygon(box);
        triangleProxy = own(new B2ShapeProxy()); triangleProxy.SetPolygon(triangle);
        segmentProxy = own(new B2ShapeProxy()); segmentProxy.SetSegment(segment);
        release(s2, s1, hull, c2, c1, zero);
    }

    private static void add(B2Hull hull, float x, float y) {
        B2Vec2 point = new B2Vec2(x, y); hull.AddPoint(point); release(point);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        hit = false;
        hitFraction = 1.0f;
        castCircle(-20.0f, 20.0f);
        castCapsule(-10.0f, 20.0f);
        castPolygon(box, 0.0f, 20.0f);
        castPolygon(triangle, 10.0f, 20.0f);
        castSegment(20.0f, 20.0f);
    }

    private float[] localRay(float x, float y) {
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        float sx = rayStartX - x, sy = rayStartY - y;
        float dx = rayEndX - rayStartX, dy = rayEndY - rayStartY;
        return new float[]{c * sx + s * sy, -s * sx + c * sy, c * dx + s * dy, -s * dx + c * dy};
    }

    private void castCircle(float x, float y) {
        float tx = x + offsetX, ty = y + offsetY;
        float[] r = localRay(tx, ty);
        B2Vec2 origin = new B2Vec2(r[0], r[1]); B2Vec2 translation = new B2Vec2(r[2], r[3]);
        B2CastResult result = B2Collision.RayCastCircle(origin, translation, hitFraction, circle);
        accept(result, tx, ty);
        release(result, translation, origin);
    }

    private void castCapsule(float x, float y) {
        float tx = x + offsetX, ty = y + offsetY;
        float[] r = localRay(tx, ty);
        B2Vec2 origin = new B2Vec2(r[0], r[1]); B2Vec2 translation = new B2Vec2(r[2], r[3]);
        B2CastResult result = B2Collision.RayCastCapsule(origin, translation, hitFraction, capsule);
        accept(result, tx, ty);
        release(result, translation, origin);
    }

    private void castPolygon(B2Polygon polygon, float x, float y) {
        float tx = x + offsetX, ty = y + offsetY;
        float[] r = localRay(tx, ty);
        B2Vec2 origin = new B2Vec2(r[0], r[1]); B2Vec2 translation = new B2Vec2(r[2], r[3]);
        B2CastResult result = B2Collision.RayCastPolygon(origin, translation, hitFraction, polygon);
        accept(result, tx, ty);
        release(result, translation, origin);
    }

    private void castSegment(float x, float y) {
        float tx = x + offsetX, ty = y + offsetY;
        float[] r = localRay(tx, ty);
        B2Vec2 origin = new B2Vec2(r[0], r[1]); B2Vec2 translation = new B2Vec2(r[2], r[3]);
        B2CastResult result = B2Collision.RayCastSegment(origin, translation, hitFraction, segment, false);
        accept(result, tx, ty);
        release(result, translation, origin);
    }

    private void accept(B2CastResult result, float x, float y) {
        if(!result.GetHit() || result.GetFraction() > hitFraction) return;
        B2Vec2 point = result.GetPoint(); B2Vec2 normal = result.GetNormal();
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        hitX = x + c * point.GetX() - s * point.GetY();
        hitY = y + s * point.GetX() + c * point.GetY();
        normalX = c * normal.GetX() - s * normal.GetY();
        normalY = s * normal.GetX() + c * normal.GetY();
        hitFraction = result.GetFraction(); hit = true;
        release(normal, point);
    }

    @Override public void mouseDown(float x, float y, int button, int modifiers) {
        if(button != 0) return;
        startX = x; startY = y;
        if((modifiers & 1) != 0) { translating = true; baseX = offsetX; baseY = offsetY; }
        else if((modifiers & 2) != 0) { rotating = true; baseAngle = angle; }
        else { rayStartX = rayEndX = x; rayStartY = rayEndY = y; rayDrag = true; }
    }
    @Override public void mouseMove(float x, float y) {
        if(rayDrag) { rayEndX = x; rayEndY = y; }
        else if(translating) { offsetX = baseX + 0.5f * (x - startX); offsetY = baseY + 0.5f * (y - startY); }
        else if(rotating) angle = CollisionSampleSupport.clamp(baseAngle + 0.5f * (x - startX), -PI, PI);
    }
    @Override public void mouseUp(float x, float y, int button) { rayDrag = translating = rotating = false; }

    @Override
    public void draw(Box2DSampleDraw draw) {
        int color = 0xFFFF00FF;
        CollisionSampleSupport.drawProxy(draw, circleProxy, -20 + offsetX, 20 + offsetY, angle, color, false);
        CollisionSampleSupport.drawProxy(draw, capsuleProxy, -10 + offsetX, 20 + offsetY, angle, color, false);
        CollisionSampleSupport.drawProxy(draw, boxProxy, offsetX, 20 + offsetY, angle, color, false);
        CollisionSampleSupport.drawProxy(draw, triangleProxy, 10 + offsetX, 20 + offsetY, angle, color, false);
        CollisionSampleSupport.drawProxy(draw, segmentProxy, 20 + offsetX, 20 + offsetY, angle, color, false);
        if(hit) {
            float endX = rayStartX + hitFraction * (rayEndX - rayStartX);
            float endY = rayStartY + hitFraction * (rayEndY - rayStartY);
            draw.segment(rayStartX, rayStartY, endX, endY, 0xFFFFFFFF);
            draw.point(rayStartX, rayStartY, 5.0f, 0x00FF00FF);
            draw.point(hitX, hitY, 7.0f, 0xFFFFFFFF);
            draw.segment(hitX, hitY, hitX + normalX, hitY + normalY, 0xEE82EEFF);
            if(showFraction) draw.worldText(hitX + 0.1f, hitY, String.format("%.2f", hitFraction), 0xFFFFFFFF);
        }
        else {
            draw.segment(rayStartX, rayStartY, rayEndX, rayEndY, 0xFFFFFFFF);
            draw.point(rayStartX, rayStartY, 5.0f, 0x00FF00FF);
            draw.point(rayEndX, rayEndY, 5.0f, 0xFF0000FF);
        }
    }

    private void reset() { offsetX = offsetY = angle = 0.0f; }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("x offset", -2, 2, 0.01f, () -> offsetX, v -> offsetX = v),
                Box2DSampleControl.slider("y offset", -2, 2, 0.01f, () -> offsetY, v -> offsetY = v),
                Box2DSampleControl.slider("angle", -PI, PI, 0.01f, () -> angle, v -> angle = v),
                Box2DSampleControl.checkbox("show fraction", () -> showFraction ? 1 : 0, v -> showFraction = v != 0),
                Box2DSampleControl.button("Reset", this::reset),
                Box2DSampleControl.text("mouse 1: ray; shift: translate; control: rotate"));
    }
}
