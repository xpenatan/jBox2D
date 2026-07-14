package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Manifold;
import com.github.xpenatan.box2d.B2ManifoldPoint;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;

/** Small drawing and construction helpers shared by the collision testbed ports. */
final class CollisionSampleSupport {
    static final int POINT = 0;
    static final int SEGMENT = 1;
    static final int TRIANGLE = 2;
    static final int BOX = 3;
    static final String[] SHAPE_NAMES = {"point", "segment", "triangle", "box"};

    private CollisionSampleSupport() {
    }

    static B2ShapeProxy makeProxy(int type, float radius) {
        B2ShapeProxy proxy = new B2ShapeProxy();
        if(type == POINT) {
            add(proxy, 0.0f, 0.0f);
        }
        else if(type == SEGMENT) {
            add(proxy, -0.5f, 0.0f);
            add(proxy, 0.5f, 0.0f);
        }
        else if(type == TRIANGLE) {
            add(proxy, -0.5f, 0.0f);
            add(proxy, 0.5f, 0.0f);
            add(proxy, 0.0f, 1.0f);
        }
        else {
            add(proxy, -0.5f, -0.5f);
            add(proxy, 0.5f, -0.5f);
            add(proxy, 0.5f, 0.5f);
            add(proxy, -0.5f, 0.5f);
        }
        proxy.SetRadius(radius);
        return proxy;
    }

    static void add(B2ShapeProxy proxy, float x, float y) {
        B2Vec2 point = new B2Vec2(x, y);
        proxy.AddPoint(point);
        AbstractBox2DSample.release(point);
    }

    static B2Transform transform(float x, float y, float angle) {
        B2Vec2 position = new B2Vec2(x, y);
        B2Rot rotation = new B2Rot(angle);
        B2Transform transform = new B2Transform(position, rotation);
        AbstractBox2DSample.release(rotation, position);
        return transform;
    }

    static void drawProxy(Box2DSampleDraw draw, B2ShapeProxy proxy, float x, float y, float angle, int color,
            boolean indices) {
        int count = proxy.GetPointCount();
        float radius = proxy.GetRadius();
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        float[] px = new float[count];
        float[] py = new float[count];
        for(int i = 0; i < count; i++) {
            B2Vec2 point = proxy.GetPoint(i);
            px[i] = x + c * point.GetX() - s * point.GetY();
            py[i] = y + s * point.GetX() + c * point.GetY();
            AbstractBox2DSample.release(point);
            if(indices) draw.worldText(px[i] + 0.03f, py[i] + 0.03f, Integer.toString(i), 0xFFFFFFFF);
        }
        if(count == 1) {
            if(radius > 0.0f) draw.circle(px[0], py[0], radius, color);
            else draw.point(px[0], py[0], 6.0f, color);
        }
        else {
            for(int i = 0; i < count - 1; i++) draw.segment(px[i], py[i], px[i + 1], py[i + 1], color);
            if(count > 2) draw.segment(px[count - 1], py[count - 1], px[0], py[0], color);
            if(radius > 0.0f) {
                for(int i = 0; i < count; i++) draw.circle(px[i], py[i], radius, color);
            }
        }
    }

    static void drawAABB(Box2DSampleDraw draw, float lowerX, float lowerY, float upperX, float upperY, int color) {
        draw.segment(lowerX, lowerY, upperX, lowerY, color);
        draw.segment(upperX, lowerY, upperX, upperY, color);
        draw.segment(upperX, upperY, lowerX, upperY, color);
        draw.segment(lowerX, upperY, lowerX, lowerY, color);
    }

    static void drawManifold(Box2DSampleDraw draw, B2Manifold manifold, boolean showIds, boolean showSeparation) {
        B2Vec2 normal = manifold.GetNormal();
        float nx = normal.GetX();
        float ny = normal.GetY();
        for(int i = 0; i < manifold.GetPointCount(); i++) {
            B2ManifoldPoint point = manifold.GetPoint(i);
            B2Vec2 p = point.GetPoint();
            float x = p.GetX();
            float y = p.GetY();
            draw.point(x, y, 8.0f, 0x4169E1FF);
            draw.segment(x, y, x + 0.5f * nx, y + 0.5f * ny, 0xEE82EEFF);
            if(showIds) draw.worldText(x + 0.04f, y - 0.05f, "0x" + Integer.toHexString(point.GetId()), 0xFFFFFFFF);
            if(showSeparation) draw.worldText(x + 0.04f, y + 0.05f, String.format("%.3f", point.GetSeparation()), 0xFFFFFFFF);
            AbstractBox2DSample.release(p, point);
        }
        AbstractBox2DSample.release(normal);
    }

    static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
