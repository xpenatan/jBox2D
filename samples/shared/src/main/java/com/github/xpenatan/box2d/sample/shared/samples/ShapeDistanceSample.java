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
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Shape Distance sample. */
public final class ShapeDistanceSample extends AbstractBox2DSample {
    private B2ShapeProxy proxyA = own(CollisionSampleSupport.makeProxy(CollisionSampleSupport.BOX, 0.0f));
    private B2ShapeProxy proxyB = own(CollisionSampleSupport.makeProxy(CollisionSampleSupport.BOX, 0.0f));
    private final B2SimplexCache cache = own(new B2SimplexCache());
    private int typeA = CollisionSampleSupport.BOX;
    private int typeB = CollisionSampleSupport.BOX;
    private float radiusA;
    private float radiusB;
    private float x;
    private float y;
    private float angle;
    private float pointAX;
    private float pointAY;
    private float pointBX;
    private float pointBY;
    private float normalX;
    private float normalY;
    private float distance;
    private int iterations;
    private boolean showIndices;
    private boolean useCache;
    private boolean dragging;
    private boolean rotating;
    private float startX;
    private float startY;
    private float baseX;
    private float baseY;
    private float baseAngle;

    public ShapeDistanceSample() {
        super(1, 0.0f, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        proxyA.SetRadius(radiusA);
        proxyB.SetRadius(radiusB);
        if(!useCache) cache.Clear();
        B2DistanceInput input = new B2DistanceInput();
        B2Transform identity = CollisionSampleSupport.transform(0.0f, 0.0f, 0.0f);
        B2Transform transform = CollisionSampleSupport.transform(x, y, angle);
        input.SetProxyA(proxyA);
        input.SetProxyB(proxyB);
        input.SetTransformA(identity);
        input.SetTransformB(transform);
        input.SetUseRadii(true);
        B2DistanceOutput output = B2Collision.ShapeDistance(input, cache);
        B2Vec2 a = output.GetPointA();
        B2Vec2 b = output.GetPointB();
        B2Vec2 normal = output.GetNormal();
        pointAX = a.GetX(); pointAY = a.GetY();
        pointBX = b.GetX(); pointBY = b.GetY();
        normalX = normal.GetX(); normalY = normal.GetY();
        distance = output.GetDistance();
        iterations = output.GetIterations();
        release(normal, b, a, output, transform, identity, input);
    }

    @Override
    public void mouseDown(float px, float py, int button, int modifiers) {
        if(button != 0) return;
        startX = px; startY = py;
        if((modifiers & 1) != 0) { rotating = true; baseAngle = angle; }
        else { dragging = true; baseX = x; baseY = y; }
    }

    @Override public void mouseMove(float px, float py) {
        if(dragging) { x = baseX + 0.5f * (px - startX); y = baseY + 0.5f * (py - startY); }
        else if(rotating) angle = CollisionSampleSupport.clamp(baseAngle + px - startX, -PI, PI);
    }

    @Override public void mouseUp(float x, float y, int button) { dragging = false; rotating = false; }

    @Override
    public void draw(Box2DSampleDraw draw) {
        CollisionSampleSupport.drawProxy(draw, proxyA, 0.0f, 0.0f, 0.0f, 0x00FFFFFF, showIndices);
        CollisionSampleSupport.drawProxy(draw, proxyB, x, y, angle, 0xFFE4C4FF, showIndices);
        draw.segment(pointAX, pointAY, pointBX, pointBY, 0x696969FF);
        draw.point(pointAX, pointAY, 8.0f, 0xFFFFFFFF);
        draw.point(pointBX, pointBY, 8.0f, 0xFFFFFFFF);
        draw.segment(pointAX, pointAY, pointAX + 0.5f * normalX, pointAY + 0.5f * normalY, 0xFFFF00FF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("shape A", CollisionSampleSupport.SHAPE_NAMES, () -> typeA, this::setTypeA),
                Box2DSampleControl.slider("radius A", 0.0f, 0.5f, 0.01f, () -> radiusA, v -> radiusA = v),
                Box2DSampleControl.combo("shape B", CollisionSampleSupport.SHAPE_NAMES, () -> typeB, this::setTypeB),
                Box2DSampleControl.slider("radius B", 0.0f, 0.5f, 0.01f, () -> radiusB, v -> radiusB = v),
                Box2DSampleControl.slider("x offset", -2.0f, 2.0f, 0.01f, () -> x, v -> x = v),
                Box2DSampleControl.slider("y offset", -2.0f, 2.0f, 0.01f, () -> y, v -> y = v),
                Box2DSampleControl.slider("angle", -PI, PI, 0.01f, () -> angle, v -> angle = v),
                Box2DSampleControl.checkbox("show indices", () -> showIndices ? 1 : 0, v -> showIndices = v != 0),
                Box2DSampleControl.checkbox("use cache", () -> useCache ? 1 : 0, v -> useCache = v != 0),
                Box2DSampleControl.dynamicText(() -> String.format("distance = %.2f, iterations = %d, cache = %d",
                        distance, iterations, cache.GetCount())),
                Box2DSampleControl.text("mouse 1: drag; shift + mouse 1: rotate"));
    }

    private void setTypeA(float value) {
        typeA = (int)value;
        release(proxyA);
        proxyA = own(CollisionSampleSupport.makeProxy(typeA, radiusA));
        cache.Clear();
    }

    private void setTypeB(float value) {
        typeB = (int)value;
        release(proxyB);
        proxyB = own(CollisionSampleSupport.makeProxy(typeB, radiusB));
        cache.Clear();
    }
}
