package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WeldJointDef;

/** Java counterpart of Box2D's reusable seven-segment Donut helper. */
final class DonutAssembly {
    private static final int SIDE_COUNT = 7;

    private final AbstractBox2DSample sample;
    private final B2Body[] bodies = new B2Body[SIDE_COUNT];
    private final B2Shape[] shapes = new B2Shape[SIDE_COUNT];
    private final B2Joint[] joints = new B2Joint[SIDE_COUNT];

    DonutAssembly(AbstractBox2DSample sample, float x, float y, float scale, int groupIndex,
            boolean enableSensorEvents) {
        this.sample = sample;
        create(x, y, scale, groupIndex, enableSensorEvents);
    }

    private void create(float x, float y, float scale, int groupIndex, boolean enableSensorEvents) {
        float radius = scale;
        float deltaAngle = 2.0f * AbstractBox2DSample.PI / SIDE_COUNT;
        float length = 2.0f * AbstractBox2DSample.PI * radius / SIDE_COUNT;

        B2Vec2 center1 = new B2Vec2(0.0f, -0.5f * length);
        B2Vec2 center2 = new B2Vec2(0.0f, 0.5f * length);
        B2Capsule capsule = new B2Capsule(center1, center2, 0.25f * scale);
        B2ShapeDef shapeDef = sample.shapeDef(1.0f, 0.3f, 0.0f, 0.0f);
        shapeDef.SetEnableSensorEvents(enableSensorEvents);
        B2Filter filter = new B2Filter();
        filter.SetGroupIndex(-groupIndex);
        shapeDef.SetFilter(filter);

        float angle = 0.0f;
        for(int i = 0; i < SIDE_COUNT; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = new B2Vec2(
                    radius * B2.Cos(angle) + x,
                    radius * B2.Sin(angle) + y);
            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetPosition(position);
            bodyDef.SetAngle(angle);
            bodies[i] = sample.createBody(bodyDef);
            shapes[i] = sample.createCapsuleShape(bodies[i], shapeDef, capsule);
            AbstractBox2DSample.release(position, bodyDef);
            angle += deltaAngle;
        }

        B2WeldJointDef weldDef = new B2WeldJointDef();
        B2Vec2 anchorA = new B2Vec2(0.0f, 0.5f * length);
        B2Vec2 anchorB = new B2Vec2(0.0f, -0.5f * length);
        weldDef.SetAngularHertz(5.0f);
        weldDef.SetAngularDampingRatio(0.0f);
        weldDef.SetLocalAnchorA(anchorA);
        weldDef.SetLocalAnchorB(anchorB);
        B2Body previous = bodies[SIDE_COUNT - 1];
        for(int i = 0; i < SIDE_COUNT; i++) {
            B2Body current = bodies[i];
            B2Rot rotationA = previous.GetRotation();
            B2Rot rotationB = current.GetRotation();
            weldDef.SetBodyIdA(previous.GetId());
            weldDef.SetBodyIdB(current.GetId());
            weldDef.SetReferenceAngle(rotationB.RelativeAngle(rotationA));
            joints[i] = sample.createWeldJoint(weldDef);
            previous = current;
            AbstractBox2DSample.release(rotationB, rotationA);
        }

        AbstractBox2DSample.release(anchorB, anchorA, weldDef, filter, shapeDef, capsule, center2, center1);
    }

    long[] shapeIds() {
        long[] ids = new long[SIDE_COUNT];
        for(int i = 0; i < SIDE_COUNT; i++) ids[i] = shapes[i].GetId();
        return ids;
    }

    void destroy() {
        for(B2Body body : bodies) sample.destroyBody(body);
        for(B2Joint joint : joints) sample.discardHandle(joint);
        for(B2Shape shape : shapes) sample.discardHandle(shape);
        for(B2Body body : bodies) sample.discardHandle(body);
    }
}
