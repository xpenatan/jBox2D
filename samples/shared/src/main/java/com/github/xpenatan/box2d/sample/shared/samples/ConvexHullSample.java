package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Hull;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Geometry / Convex Hull sample. */
public final class ConvexHullSample extends AbstractBox2DSample {
    private final float[] points = new float[2 * 8];
    private final float[] hullPoints = new float[2 * 8];
    private int hullCount;
    private int generation;
    private boolean auto;
    private boolean bulk;
    private boolean valid;

    public ConvexHullSample() {
        generate();
    }

    private void generate() {
        float angle = PI * randomFloat(0.0f, 1.0f);
        B2Rot rotation = new B2Rot(angle);
        int count = Math.min(8, B2.MaxPolygonVertices());
        for(int i = 0; i < count; i++) {
            float x = Math.max(-4.0f, Math.min(4.0f, randomFloat(0.0f, 10.0f)));
            float y = Math.max(-4.0f, Math.min(4.0f, randomFloat(0.0f, 10.0f)));
            B2Vec2 value = vector(x, y);
            B2Vec2 rotated = rotation.RotateVector(value);
            points[2 * i] = rotated.GetX();
            points[2 * i + 1] = rotated.GetY();
            release(rotated, value);
        }
        release(rotation);
        generation++;
    }

    private void compute() {
        B2Hull hull = new B2Hull();
        for(int i = 0; i < points.length; i += 2) {
            B2Vec2 point = vector(points[i], points[i + 1]);
            hull.AddPoint(point);
            release(point);
        }
        boolean computed = hull.Compute();
        hullCount = computed ? Math.min(hull.GetPointCount(), 8) : 0;
        valid = hullCount > 0 && hull.IsValid();
        for(int i = 0; i < hullCount; i++) {
            B2Vec2 point = hull.GetPoint(i);
            hullPoints[2 * i] = point.GetX();
            hullPoints[2 * i + 1] = point.GetY();
            release(point);
        }
        release(hull);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        if(bulk) {
            for(int i = 0; i < 10000; i++) {
                generate();
                compute();
                if(hullCount == 0) continue;
                if(!valid) { bulk = false; break; }
            }
        }
        else {
            if(auto) generate();
            compute();
            if(hullCount > 0 && !valid) auto = false;
        }
    }

    @Override
    public void keyDown(int key) {
        if(key == 'A') auto = !auto;
        else if(key == 'B') bulk = !bulk;
        else if(key == 'G') generate();
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(int i = 0; i < hullCount; i++) {
            int next = (i + 1) % hullCount;
            draw.segment(hullPoints[2 * i], hullPoints[2 * i + 1],
                    hullPoints[2 * next], hullPoints[2 * next + 1], 0x808080FF);
            draw.point(hullPoints[2 * i], hullPoints[2 * i + 1], 6.0f, 0x00FF00FF);
        }
        for(int i = 0; i < points.length / 2; i++) {
            draw.point(points[2 * i], points[2 * i + 1], 5.0f, 0x0000FFFF);
            draw.worldText(points[2 * i] + 0.1f, points[2 * i + 1] + 0.1f, Integer.toString(i), 0xFFFFFFFF);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("Generate (G)", this::generate),
                Box2DSampleControl.checkbox("Auto (A)", () -> auto ? 1 : 0, value -> auto = value != 0),
                Box2DSampleControl.checkbox("Bulk (B)", () -> bulk ? 1 : 0, value -> bulk = value != 0),
                Box2DSampleControl.dynamicText(() -> valid
                        ? "generation = " + generation + ", count = " + hullCount
                        : "generation = " + generation + ", FAILED"));
    }
}
