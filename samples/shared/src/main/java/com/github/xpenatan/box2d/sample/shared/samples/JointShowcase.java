package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2DistanceJointDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2MotorJointDef;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WeldJointDef;
import com.github.xpenatan.box2d.B2WheelJointDef;
import java.util.ArrayList;
import java.util.List;

/** Shared construction for samples that compare the same set of joint types. */
final class JointShowcase {
    final List<B2Body> bodies = new ArrayList<B2Body>();
    final List<B2Joint> joints = new ArrayList<B2Joint>();

    private JointShowcase() {
    }

    static JointShowcase create(AbstractBox2DSample sample, B2Body ground, float startX, float spacing, float y,
            boolean fixedRotation, boolean enableSleep, boolean collideConnected, boolean includeMotor,
            float motorForce, float weldHertz) {
        JointShowcase result = new JointShowcase();
        int total = includeMotor ? 6 : 5;
        for(int index = 0; index < total; index++) {
            float x = startX + spacing * index;
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = new B2Vec2(x, y);
            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetPosition(position);
            bodyDef.SetFixedRotation(fixedRotation);
            bodyDef.SetEnableSleep(enableSleep);
            B2Body body = sample.createBody(bodyDef);
            sample.addBoxShape(body, 1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);
            result.bodies.add(body);

            int type = includeMotor ? index : index == 0 ? 0 : index + 1;
            B2Joint joint;
            if(type == 0) {
                B2DistanceJointDef def = new B2DistanceJointDef();
                def.SetBodyIdA(ground.GetId()); def.SetBodyIdB(body.GetId());
                B2Vec2 pivotA = new B2Vec2(x, y + 3.0f);
                B2Vec2 pivotB = new B2Vec2(x, y + 1.0f);
                B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(ground, pivotA);
                B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(body, pivotB);
                def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
                def.SetLength(2.0f); def.SetCollideConnected(collideConnected);
                joint = sample.createDistanceJoint(def);
                AbstractBox2DSample.release(localB, localA, pivotB, pivotA, def);
            }
            else if(type == 1) {
                B2MotorJointDef def = new B2MotorJointDef();
                B2Vec2 offset = new B2Vec2(x, y);
                def.SetBodyIdA(ground.GetId()); def.SetBodyIdB(body.GetId());
                def.SetLinearOffset(offset); def.SetMaxForce(motorForce); def.SetMaxTorque(20.0f);
                def.SetCollideConnected(collideConnected);
                joint = sample.createMotorJoint(def);
                AbstractBox2DSample.release(offset, def);
            }
            else if(type == 2) {
                B2PrismaticJointDef def = new B2PrismaticJointDef();
                setAnchors(def, ground, body, x - 1.0f, y);
                B2Vec2 axis = new B2Vec2(1.0f, 0.0f);
                def.SetLocalAxisA(axis); def.SetCollideConnected(collideConnected);
                joint = sample.createPrismaticJoint(def);
                AbstractBox2DSample.release(axis, def);
            }
            else if(type == 3) {
                B2RevoluteJointDef def = new B2RevoluteJointDef();
                setAnchors(def, ground, body, x - 1.0f, y);
                def.SetCollideConnected(collideConnected);
                joint = sample.createRevoluteJoint(def);
                AbstractBox2DSample.release(def);
            }
            else if(type == 4) {
                B2WeldJointDef def = new B2WeldJointDef();
                setAnchors(def, ground, body, x - 1.0f, y);
                def.SetAngularHertz(weldHertz); def.SetAngularDampingRatio(0.5f);
                def.SetLinearHertz(weldHertz); def.SetLinearDampingRatio(0.5f);
                def.SetCollideConnected(collideConnected);
                joint = sample.createWeldJoint(def);
                AbstractBox2DSample.release(def);
            }
            else {
                B2WheelJointDef def = new B2WheelJointDef();
                setAnchors(def, ground, body, x - 1.0f, y);
                B2Vec2 axis = new B2Vec2(1.0f, 0.0f);
                def.SetLocalAxisA(axis); def.SetHertz(1.0f); def.SetDampingRatio(0.7f);
                def.SetLowerTranslation(-1.0f); def.SetUpperTranslation(1.0f); def.SetEnableLimit(true);
                def.SetEnableMotor(true); def.SetMaxMotorTorque(10.0f); def.SetMotorSpeed(1.0f);
                def.SetCollideConnected(collideConnected);
                joint = sample.createWheelJoint(def);
                AbstractBox2DSample.release(axis, def);
            }
            result.joints.add(joint);
            AbstractBox2DSample.release(position, bodyDef);
        }
        return result;
    }

    private static void setAnchors(B2PrismaticJointDef def, B2Body a, B2Body b, float x, float y) {
        B2Vec2 pivot = new B2Vec2(x, y);
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(a, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        AbstractBox2DSample.release(localB, localA, pivot);
    }

    private static void setAnchors(B2RevoluteJointDef def, B2Body a, B2Body b, float x, float y) {
        B2Vec2 pivot = new B2Vec2(x, y);
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(a, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        AbstractBox2DSample.release(localB, localA, pivot);
    }

    private static void setAnchors(B2WeldJointDef def, B2Body a, B2Body b, float x, float y) {
        B2Vec2 pivot = new B2Vec2(x, y);
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(a, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        AbstractBox2DSample.release(localB, localA, pivot);
    }

    private static void setAnchors(B2WheelJointDef def, B2Body a, B2Body b, float x, float y) {
        B2Vec2 pivot = new B2Vec2(x, y);
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(a, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        AbstractBox2DSample.release(localB, localA, pivot);
    }
}
