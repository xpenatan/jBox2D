package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Benchmark / Spinner sample. */
public final class BenchmarkSpinnerSample extends AbstractBox2DSample {
    public BenchmarkSpinnerSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        float[] points = new float[720];
        float angleStep = -2.0f * PI / 360.0f;
        for(int i = 0; i < 360; i++) {
            float angle = i * angleStep;
            points[2 * i] = 40.0f * (float)Math.cos(angle);
            points[2 * i + 1] = 32.0f + 40.0f * (float)Math.sin(angle);
        }
        addChain(ground, points, true, 0.1f);

        B2Body spinner = BenchmarkSampleSupport.createBody(this, B2.DynamicBody(), 0.0f, 12.0f, 0.0f,
                0.0f, 0.0f, true, false);
        B2Polygon spinnerBox = B2Polygon.CreateRoundedBox(0.4f, 20.0f, 0.2f);
        B2ShapeDef spinnerDef = shapeDef(1.0f, 0.0f, 0.0f, 0.0f);
        B2Shape spinnerShape = createPolygonShape(spinner, spinnerDef, spinnerBox);
        discardHandle(spinnerShape);
        release(spinnerDef, spinnerBox);

        B2RevoluteJointDef jointDef = new B2RevoluteJointDef();
        B2Vec2 anchorA = new B2Vec2(0.0f, 12.0f);
        B2Vec2 anchorB = new B2Vec2(0.0f, 0.0f);
        jointDef.SetBodyIdA(ground.GetId());
        jointDef.SetBodyIdB(spinner.GetId());
        jointDef.SetLocalAnchorA(anchorA);
        jointDef.SetLocalAnchorB(anchorB);
        jointDef.SetEnableMotor(true);
        jointDef.SetMotorSpeed(5.0f);
        jointDef.SetMaxMotorTorque(40000.0f);
        createRevoluteJoint(jointDef);
        release(anchorB, anchorA, jointDef);

        B2ShapeDef itemDef = shapeDef(0.25f, 0.1f, 0.1f, 0.0f);
        B2Vec2 capsuleOne = new B2Vec2(-0.25f, 0.0f);
        B2Vec2 capsuleTwo = new B2Vec2(0.25f, 0.0f);
        B2Capsule capsule = new B2Capsule(capsuleOne, capsuleTwo, 0.25f);
        B2Vec2 circleCenter = new B2Vec2(0.0f, 0.0f);
        B2Circle circle = new B2Circle(circleCenter, 0.35f);
        B2Polygon square = B2Polygon.CreateBox(0.35f, 0.35f);
        int bodyCount = BenchmarkSampleSupport.DEBUG_SIZE ? 499 : 3038;
        float x = -24.0f;
        float y = 2.0f;
        for(int i = 0; i < bodyCount; i++) {
            B2Body body = createDynamicBody(x, y, 0.0f);
            B2Shape shape;
            if(i % 3 == 0) shape = createCapsuleShape(body, itemDef, capsule);
            else if(i % 3 == 1) shape = createCircleShape(body, itemDef, circle);
            else shape = createPolygonShape(body, itemDef, square);
            discardHandle(shape);
            x += 1.0f;
            if(x > 24.0f) {
                x = -24.0f;
                y += 1.0f;
            }
        }
        release(square, circle, circleCenter, capsule, capsuleTwo, capsuleOne, itemDef);
    }
}
