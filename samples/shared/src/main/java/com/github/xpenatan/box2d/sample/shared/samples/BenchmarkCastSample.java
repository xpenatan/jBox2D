package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2AABB;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2RayResult;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WorldCastHit;
import com.github.xpenatan.box2d.B2WorldCastResult;
import com.github.xpenatan.box2d.B2WorldOverlapResult;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Benchmark / Cast sample. */
public final class BenchmarkCastSample extends AbstractBox2DSample {
    private static final int RAY_CAST = 0;
    private static final int CIRCLE_CAST = 1;
    private static final int OVERLAP = 2;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final float[] originsX;
    private final float[] originsY;
    private final float[] translationsX;
    private final float[] translationsY;
    private final float[] overlapX = new float[32];
    private final float[] overlapY = new float[32];
    private int queryType = CIRCLE_CAST;
    private int rowCount = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 1000;
    private int columnCount = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 1000;
    private int drawIndex;
    private int overlapCount;
    private int hitCount;
    private int nodeVisits;
    private int leafVisits;
    private float radius = 0.1f;
    private float fill = 0.1f;
    private float ratio = 5.0f;
    private float grid = 1.0f;
    private boolean topDown;
    private double buildMilliseconds;
    private double milliseconds;
    private double minimumMilliseconds = Double.MAX_VALUE;
    private boolean drawHit;
    private float drawHitX;
    private float drawHitY;
    private float drawFraction;

    public BenchmarkCastSample() {
        super(1, 0.0f, 0.0f);
        int sampleCount = BenchmarkSampleSupport.DEBUG_SIZE ? 100 : 10000;
        originsX = new float[sampleCount];
        originsY = new float[sampleCount];
        translationsX = new float[sampleCount];
        translationsY = new float[sampleCount];
        setRandomSeed(1234);
        float extent = rowCount * grid;
        for(int i = 0; i < sampleCount; i++) {
            float x1 = randomFloat(0.0f, extent);
            float y1 = randomFloat(0.0f, extent);
            float x2 = randomFloat(0.0f, extent);
            float y2 = randomFloat(0.0f, extent);
            originsX[i] = x1;
            originsY[i] = y1;
            translationsX[i] = x2 - x1;
            translationsY[i] = y2 - y1;
        }
        buildScene();
    }

    private void buildScene() {
        for(B2Body body : bodies) BenchmarkSampleSupport.destroyAndDiscard(this, body);
        bodies.clear();
        setRandomSeed(1234);
        long start = System.nanoTime();
        for(int row = 0; row < rowCount; row++) {
            float y = row * grid;
            for(int column = 0; column < columnCount; column++) {
                if(randomFloat(0.0f, 1.0f) > fill) continue;
                float x = column * grid;
                B2Body body = createStaticBody(x, y, 0.0f);
                bodies.add(body);
                float aspect = randomFloat(1.0f, ratio);
                float halfWidth = randomFloat(0.05f, 0.25f);
                boolean horizontal = randomFloat(-1.0f, 1.0f) > 0.0f;
                B2Polygon box = B2Polygon.CreateBox(horizontal ? aspect * halfWidth : halfWidth,
                        horizontal ? halfWidth : aspect * halfWidth);
                int category = randomInt(0, 2);
                B2Filter filter = new B2Filter();
                filter.SetCategoryBits(1L << category);
                B2SurfaceMaterial material = new B2SurfaceMaterial();
                material.SetFriction(0.6f);
                material.SetCustomColor(category == 0 ? 0x2E86ABFF : category == 1 ? 0xF6C344FF : 0x63B35DFF);
                B2ShapeDef shapeDef = new B2ShapeDef();
                shapeDef.SetFilter(filter);
                shapeDef.SetMaterial(material);
                B2Shape shape = createPolygonShape(body, shapeDef, box);
                discardHandle(shape);
                release(shapeDef, material, filter, box);
            }
        }
        if(topDown) world().RebuildStaticTree();
        buildMilliseconds = (System.nanoTime() - start) * 1.0e-6;
        minimumMilliseconds = Double.MAX_VALUE;
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2QueryFilter filter = new B2QueryFilter();
        filter.SetCategoryBits(-1L);
        filter.SetMaskBits(1L);
        hitCount = 0;
        nodeVisits = 0;
        leafVisits = 0;
        overlapCount = 0;
        drawHit = false;
        long start = System.nanoTime();
        if(queryType == RAY_CAST) runRayQueries(filter);
        else if(queryType == CIRCLE_CAST) runCircleQueries(filter);
        else runOverlapQueries(filter);
        milliseconds = (System.nanoTime() - start) * 1.0e-6;
        minimumMilliseconds = Math.min(minimumMilliseconds, milliseconds);
        release(filter);
    }

    private void runRayQueries(B2QueryFilter filter) {
        for(int i = 0; i < originsX.length; i++) {
            B2Vec2 origin = new B2Vec2(originsX[i], originsY[i]);
            B2Vec2 translation = new B2Vec2(translationsX[i], translationsY[i]);
            B2RayResult result = world().CastRayClosest(origin, translation, filter);
            nodeVisits += result.GetNodeVisits();
            leafVisits += result.GetLeafVisits();
            if(result.GetHit()) {
                hitCount++;
                if(i == drawIndex) {
                    B2Vec2 point = result.GetPoint();
                    drawHit = true;
                    drawHitX = point.GetX();
                    drawHitY = point.GetY();
                }
            }
            release(result, translation, origin);
        }
    }

    private void runCircleQueries(B2QueryFilter filter) {
        for(int i = 0; i < originsX.length; i++) {
            B2ShapeProxy proxy = new B2ShapeProxy();
            B2Vec2 origin = new B2Vec2(originsX[i], originsY[i]);
            B2Vec2 translation = new B2Vec2(translationsX[i], translationsY[i]);
            proxy.AddPoint(origin);
            proxy.SetRadius(radius);
            B2WorldCastResult result = world().CastShape(proxy, translation, filter);
            nodeVisits += result.GetNodeVisits();
            leafVisits += result.GetLeafVisits();
            float closest = 1.0f;
            boolean hit = false;
            float pointX = 0.0f;
            float pointY = 0.0f;
            for(int j = 0; j < result.GetHitCount(); j++) {
                B2WorldCastHit candidate = result.GetHit(j);
                if(!hit || candidate.GetFraction() < closest) {
                    B2Vec2 point = candidate.GetPoint();
                    hit = true;
                    closest = candidate.GetFraction();
                    pointX = point.GetX();
                    pointY = point.GetY();
                }
            }
            if(hit) {
                hitCount++;
                if(i == drawIndex) {
                    drawHit = true;
                    drawFraction = closest;
                    drawHitX = pointX;
                    drawHitY = pointY;
                }
            }
            release(result, translation, origin, proxy);
        }
    }

    private void runOverlapQueries(B2QueryFilter filter) {
        for(int i = 0; i < originsX.length; i++) {
            B2Vec2 lower = new B2Vec2(originsX[i] - radius, originsY[i] - radius);
            B2Vec2 upper = new B2Vec2(originsX[i] + radius, originsY[i] + radius);
            B2AABB aabb = new B2AABB(lower, upper);
            B2WorldOverlapResult result = world().OverlapAABB(aabb, filter);
            nodeVisits += result.GetNodeVisits();
            leafVisits += result.GetLeafVisits();
            hitCount += result.GetShapeCount();
            if(i == drawIndex) {
                overlapCount = Math.min(overlapX.length, result.GetShapeCount());
                for(int j = 0; j < overlapCount; j++) {
                    B2Shape shape = new B2Shape(result.GetShapeId(j));
                    B2AABB shapeAabb = shape.GetAABB();
                    B2Vec2 center = shapeAabb.GetCenter();
                    overlapX[j] = center.GetX();
                    overlapY[j] = center.GetY();
                    release(shapeAabb, shape);
                }
            }
            release(result, aabb, upper, lower);
        }
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        float x1 = originsX[drawIndex];
        float y1 = originsY[drawIndex];
        float x2 = x1 + translationsX[drawIndex];
        float y2 = y1 + translationsY[drawIndex];
        if(queryType == OVERLAP) {
            draw.segment(x1 - radius, y1 - radius, x1 + radius, y1 - radius, 0xFFFFFFFF);
            draw.segment(x1 + radius, y1 - radius, x1 + radius, y1 + radius, 0xFFFFFFFF);
            draw.segment(x1 + radius, y1 + radius, x1 - radius, y1 + radius, 0xFFFFFFFF);
            draw.segment(x1 - radius, y1 + radius, x1 - radius, y1 - radius, 0xFFFFFFFF);
            for(int i = 0; i < overlapCount; i++) draw.point(overlapX[i], overlapY[i], 5.0f, 0xFF69B4FF);
            return;
        }
        draw.segment(x1, y1, x2, y2, 0xFFFFFFFF);
        draw.point(x1, y1, 5.0f, 0x00FF00FF);
        draw.point(x2, y2, 5.0f, 0xFF0000FF);
        if(drawHit) {
            if(queryType == CIRCLE_CAST) {
                draw.circle(x1 + drawFraction * (x2 - x1), y1 + drawFraction * (y2 - y1), radius, 0xFFFFFFFF);
            }
            draw.point(drawHitX, drawHitY, 5.0f, 0xFFFFFFFF);
        }
    }

    private void setQuery(float value) {
        queryType = Math.round(value);
        radius = queryType == OVERLAP ? 5.0f : 0.1f;
        buildScene();
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Query", new String[] {"Ray", "Circle", "Overlap"},
                        () -> queryType, this::setQuery),
                Box2DSampleControl.slider("rows", 0.0f, 1000.0f, 1.0f, () -> rowCount,
                        value -> { rowCount = Math.round(value); buildScene(); }),
                Box2DSampleControl.slider("columns", 0.0f, 1000.0f, 1.0f, () -> columnCount,
                        value -> { columnCount = Math.round(value); buildScene(); }),
                Box2DSampleControl.slider("fill", 0.0f, 1.0f, 0.01f, () -> fill,
                        value -> { fill = value; buildScene(); }),
                Box2DSampleControl.slider("grid", 0.5f, 2.0f, 0.01f, () -> grid,
                        value -> { grid = value; buildScene(); }),
                Box2DSampleControl.slider("ratio", 1.0f, 10.0f, 0.01f, () -> ratio,
                        value -> { ratio = value; buildScene(); }),
                Box2DSampleControl.checkbox("top down", () -> topDown ? 1.0f : 0.0f,
                        value -> { topDown = value != 0.0f; buildScene(); }),
                Box2DSampleControl.button("Draw Next", () -> drawIndex = (drawIndex + 1) % originsX.length),
                Box2DSampleControl.dynamicText(() -> String.format("build time ms = %.3f", buildMilliseconds)),
                Box2DSampleControl.dynamicText(() -> "hit count = " + hitCount + ", node visits = " + nodeVisits
                        + ", leaf visits = " + leafVisits),
                Box2DSampleControl.dynamicText(this::timingText),
                Box2DSampleControl.dynamicText(this::averageTimeText));
    }

    private String timingText() {
        if(minimumMilliseconds == Double.MAX_VALUE) return "total ms = 0.000, min = not measured";
        return String.format("total ms = %.3f, min = %.3f", milliseconds, minimumMilliseconds);
    }

    private String averageTimeText() {
        if(minimumMilliseconds == Double.MAX_VALUE) return "average us = not measured";
        return String.format("average us = %.2f", 1000.0 * minimumMilliseconds / originsX.length);
    }
}
