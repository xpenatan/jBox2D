package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2DistanceInput;
import com.github.xpenatan.box2d.B2DistanceOutput;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2SimplexCache;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / Shape Distance sample. */
public final class BenchmarkShapeDistanceSample extends AbstractBox2DSample {
    private final int count = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 10000;
    private final B2ShapeProxy proxyA = own(new B2ShapeProxy());
    private final B2ShapeProxy proxyB = own(new B2ShapeProxy());
    private final float[] ax;
    private final float[] ay;
    private final float[] aa;
    private final float[] bx;
    private final float[] by;
    private final float[] ba;
    private int drawIndex;
    private int totalIterations;
    private double minimumMilliseconds = Double.MAX_VALUE;
    private float drawPointAX;
    private float drawPointAY;
    private float drawPointBX;
    private float drawPointBY;
    private float drawNormalX;
    private float drawNormalY;
    private float drawDistance;

    public BenchmarkShapeDistanceSample() {
        super(1, 0.0f, 0.0f);
        for(int i = 0; i < 8; i++) {
            float angle = 2.0f * PI * i / 8.0f;
            B2Vec2 point = new B2Vec2(0.5f * (float)Math.cos(angle), 0.5f * (float)Math.sin(angle));
            proxyA.AddPoint(point);
            proxyB.AddPoint(point);
            release(point);
        }
        proxyB.SetRadius(0.1f);
        ax = new float[count];
        ay = new float[count];
        aa = new float[count];
        bx = new float[count];
        by = new float[count];
        ba = new float[count];
        setRandomSeed(42);
        for(int i = 0; i < count; i++) {
            ax[i] = randomFloat(-0.1f, 0.1f);
            ay[i] = randomFloat(-0.1f, 0.1f);
            aa[i] = randomFloat(-PI, PI);
            bx[i] = randomFloat(0.25f, 2.0f);
            by[i] = randomFloat(0.25f, 2.0f);
            ba[i] = randomFloat(-PI, PI);
        }
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        long start = System.nanoTime();
        totalIterations = 0;
        for(int i = 0; i < count; i++) {
            B2Transform transformA = CollisionSampleSupport.transform(ax[i], ay[i], aa[i]);
            B2Transform transformB = CollisionSampleSupport.transform(bx[i], by[i], ba[i]);
            B2DistanceInput input = new B2DistanceInput();
            input.SetProxyA(proxyA);
            input.SetProxyB(proxyB);
            input.SetTransformA(transformA);
            input.SetTransformB(transformB);
            input.SetUseRadii(true);
            B2SimplexCache cache = new B2SimplexCache();
            B2DistanceOutput output = B2Collision.ShapeDistance(input, cache);
            totalIterations += output.GetIterations();
            if(i == drawIndex) {
                B2Vec2 pointA = output.GetPointA();
                B2Vec2 pointB = output.GetPointB();
                B2Vec2 normal = output.GetNormal();
                drawPointAX = pointA.GetX();
                drawPointAY = pointA.GetY();
                drawPointBX = pointB.GetX();
                drawPointBY = pointB.GetY();
                drawNormalX = normal.GetX();
                drawNormalY = normal.GetY();
                drawDistance = output.GetDistance();
            }
            release(output, cache, input, transformB, transformA);
        }
        minimumMilliseconds = Math.min(minimumMilliseconds, (System.nanoTime() - start) * 1.0e-6);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        CollisionSampleSupport.drawProxy(draw, proxyA, ax[drawIndex], ay[drawIndex], aa[drawIndex],
                0x63B35DFF, false);
        CollisionSampleSupport.drawProxy(draw, proxyB, bx[drawIndex], by[drawIndex], ba[drawIndex],
                0x2E86ABFF, false);
        draw.segment(drawPointAX, drawPointAY, drawPointBX, drawPointBY, 0x696969FF);
        draw.point(drawPointAX, drawPointAY, 10.0f, 0xFFFFFFFF);
        draw.point(drawPointBX, drawPointBY, 10.0f, 0xFFFFFFFF);
        draw.segment(drawPointAX, drawPointAY, drawPointAX + 0.5f * drawNormalX,
                drawPointAY + 0.5f * drawNormalY, 0xFFFF00FF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.slider("draw index", 0.0f, count - 1.0f, 1.0f,
                () -> drawIndex, value -> drawIndex = Math.round(value)));
        controls.add(Box2DSampleControl.dynamicText(() -> "count = " + count));
        controls.add(Box2DSampleControl.dynamicText(this::timingText));
        controls.add(Box2DSampleControl.dynamicText(() -> String.format("average iterations = %.3f",
                totalIterations / (float)count)));
        controls.add(Box2DSampleControl.dynamicText(() -> String.format("distance = %.3f", drawDistance)));
        return controls;
    }

    private String timingText() {
        if(minimumMilliseconds == Double.MAX_VALUE) return "min ms = not measured, ave us = not measured";
        return String.format("min ms = %.3f, ave us = %.3f",
                minimumMilliseconds, 1000.0 * minimumMilliseconds / count);
    }
}
