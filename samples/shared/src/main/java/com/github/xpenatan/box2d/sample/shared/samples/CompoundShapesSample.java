package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2AABB;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Compound Shapes sample. */
public final class CompoundShapesSample extends AbstractBox2DSample {
    private final B2Body table1;
    private final B2Body table2;
    private final B2Body ship1;
    private final B2Body ship2;
    private boolean drawAabbs;

    public CompoundShapesSample() {
        addGroundSegment(50.0f, 0.0f, -50.0f, 0.0f);
        table1 = table(-15.0f, 1.0f, 1.5f);
        table2 = table(-5.0f, 1.0f, 2.0f);
        ship1 = ship(5.0f, false);
        ship2 = ship(15.0f, true);
    }

    private B2Body table(float x, float y, float legHalfHeight) {
        B2Body body = createDynamicBody(x, y, 0.0f);
        addOffsetBoxShape(body, 3.0f, 0.5f, 0.0f, 3.5f, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        addOffsetBoxShape(body, 0.5f, legHalfHeight, -2.5f, legHalfHeight, 0.0f,
                1.0f, 0.6f, 0.0f, 0.0f);
        addOffsetBoxShape(body, 0.5f, legHalfHeight, 2.5f, legHalfHeight, 0.0f,
                1.0f, 0.6f, 0.0f, 0.0f);
        return body;
    }

    private B2Body ship(float x, boolean crossed) {
        B2Body body = createDynamicBody(x, 1.0f, 0.0f);
        if(crossed) {
            addPolygonShape(body, new float[] { -2, 0, 1, 2, 0, 4 }, 0, 1, .6f, 0, 0);
            addPolygonShape(body, new float[] { 2, 0, -1, 2, 0, 4 }, 0, 1, .6f, 0, 0);
        }
        else {
            addPolygonShape(body, new float[] { -2, 0, 0, 4f / 3f, 0, 4 }, 0, 1, .6f, 0, 0);
            addPolygonShape(body, new float[] { 2, 0, 0, 4f / 3f, 0, 4 }, 0, 1, .6f, 0, 0);
        }
        return body;
    }

    private B2Body clonePose(B2Body source) {
        B2Vec2 p = source.GetPosition();
        B2Rot r = source.GetRotation();
        B2Body body = createDynamicBody(p.GetX(), p.GetY(), r.GetAngle());
        release(r, p);
        return body;
    }

    private void intrude() {
        B2Body obstruction1 = clonePose(table1);
        addOffsetBoxShape(obstruction1, 4.0f, 0.1f, 0.0f, 3.0f, 0.0f, 1, .6f, 0, 0);
        B2Body obstruction2 = clonePose(table2);
        addOffsetBoxShape(obstruction2, 4.0f, 0.1f, 0.0f, 3.0f, 0.0f, 1, .6f, 0, 0);
        B2Body circle1 = clonePose(ship1);
        addCircleShape(circle1, 0.0f, 2.0f, 0.5f, 1, .6f, 0, 0);
        B2Body circle2 = clonePose(ship2);
        addCircleShape(circle2, 0.0f, 2.0f, 0.5f, 1, .6f, 0, 0);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        if(!drawAabbs) return;
        drawAabb(draw, table1);
        drawAabb(draw, table2);
        drawAabb(draw, ship1);
        drawAabb(draw, ship2);
    }

    private void drawAabb(Box2DSampleDraw draw, B2Body body) {
        B2AABB aabb = body.ComputeAABB();
        B2Vec2 lo = aabb.GetLowerBound();
        B2Vec2 hi = aabb.GetUpperBound();
        int color = 0xFFFF00FF;
        draw.segment(lo.GetX(), lo.GetY(), hi.GetX(), lo.GetY(), color);
        draw.segment(hi.GetX(), lo.GetY(), hi.GetX(), hi.GetY(), color);
        draw.segment(hi.GetX(), hi.GetY(), lo.GetX(), hi.GetY(), color);
        draw.segment(lo.GetX(), hi.GetY(), lo.GetX(), lo.GetY(), color);
        release(hi, lo, aabb);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(Box2DSampleControl.button("Intrude", this::intrude),
                Box2DSampleControl.checkbox("Body AABBs", () -> drawAabbs ? 1 : 0,
                        value -> drawAabbs = value != 0));
    }
}
