package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Modify Geometry sample. */
public final class ModifyGeometrySample extends AbstractBox2DSample {
    private final B2Body body;
    private final B2Shape shape;
    private int shapeType = B2.CircleShape();
    private float scale = 1.0f;

    public ModifyGeometrySample() {
        addStaticBox(0.0f, -1.0f, 10.0f, 1.0f, 0.0f);
        addDynamicBox(0.0f, 4.0f, 1.0f, 1.0f);
        body = createKinematicBody(0.0f, 1.0f, 0.0f);
        shape = addCircleShape(body, 0.0f, 0.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
    }

    private void updateShape() {
        if(shapeType == B2.CircleShape()) {
            B2Vec2 center = vector(0.0f, 0.0f);
            B2Circle circle = new B2Circle(center, 0.5f * scale);
            shape.SetCircle(circle);
            release(circle, center);
        }
        else if(shapeType == B2.CapsuleShape()) {
            B2Vec2 a = vector(-0.5f * scale, 0.0f);
            B2Vec2 b = vector(0.0f, 0.5f * scale);
            B2Capsule capsule = new B2Capsule(a, b, 0.5f * scale);
            shape.SetCapsule(capsule);
            release(capsule, b, a);
        }
        else if(shapeType == B2.SegmentShape()) {
            B2Vec2 a = vector(-0.5f * scale, 0.0f);
            B2Vec2 b = vector(0.75f * scale, 0.0f);
            B2Segment segment = new B2Segment(a, b);
            shape.SetSegment(segment);
            release(segment, b, a);
        }
        else {
            B2Polygon polygon = B2Polygon.CreateBox(0.5f * scale, 0.75f * scale);
            shape.SetPolygon(polygon);
            release(polygon);
        }
        body.ApplyMassFromShapes();
    }

    private void setShapeType(int type) { shapeType = type; updateShape(); }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.radio("Circle", () -> shapeType == B2.CircleShape() ? 1 : 0,
                () -> setShapeType(B2.CircleShape())));
        controls.add(Box2DSampleControl.radio("Capsule", () -> shapeType == B2.CapsuleShape() ? 1 : 0,
                () -> setShapeType(B2.CapsuleShape())));
        controls.add(Box2DSampleControl.radio("Segment", () -> shapeType == B2.SegmentShape() ? 1 : 0,
                () -> setShapeType(B2.SegmentShape())));
        controls.add(Box2DSampleControl.radio("Polygon", () -> shapeType == B2.PolygonShape() ? 1 : 0,
                () -> setShapeType(B2.PolygonShape())));
        controls.add(Box2DSampleControl.slider("Scale", 0.1f, 10.0f, 0.01f,
                () -> scale, value -> { scale = value; updateShape(); }));
        controls.add(typeControl("Static", B2.StaticBody()));
        controls.add(typeControl("Kinematic", B2.KinematicBody()));
        controls.add(typeControl("Dynamic", B2.DynamicBody()));
        return controls;
    }

    private Box2DSampleControl typeControl(String label, int type) {
        return Box2DSampleControl.radio(label, () -> body.GetType() == type ? 1 : 0, () -> body.SetType(type));
    }
}
