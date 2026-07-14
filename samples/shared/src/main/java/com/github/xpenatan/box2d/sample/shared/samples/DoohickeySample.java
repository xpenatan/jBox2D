package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Joints / Doohickey sample. */
public final class DoohickeySample extends AbstractBox2DSample {
    public DoohickeySample() {
        B2Body ground = addGroundSegment(-20, 0, 20, 0);
        addOffsetBoxShape(ground, 1, 1, 0, 1, 0, 0, .6f, 0, 0);
        for(int i = 0; i < 4; i++) spawn(0, 4 + 2 * i, .5f);
    }

    private void spawn(float x, float y, float scale) {
        B2Body wheel1 = addDynamicCircle(x - 5 * scale, y + 3 * scale, scale, 1, .6f, 0, .1f);
        B2Body wheel2 = addDynamicCircle(x + 5 * scale, y + 3 * scale, scale, 1, .6f, 0, .1f);
        B2Body bar1 = createDynamicBody(x - 1.5f * scale, y + 3 * scale, 0);
        B2Body bar2 = createDynamicBody(x + 1.5f * scale, y + 3 * scale, 0);
        addCapsuleShape(bar1, -3.5f * scale, 0, 3.5f * scale, 0, .15f * scale, 1, .6f, 0, .1f);
        addCapsuleShape(bar2, -3.5f * scale, 0, 3.5f * scale, 0, .15f * scale, 1, .6f, 0, .1f);
        createAxle(wheel1, bar1, -3.5f * scale, scale);
        createAxle(wheel2, bar2, 3.5f * scale, scale);
        B2PrismaticJointDef def = new B2PrismaticJointDef();
        B2Vec2 axis = vector(1, 0), a = vector(2 * scale, 0), b = vector(-2 * scale, 0);
        def.SetBodyIdA(bar1.GetId()); def.SetBodyIdB(bar2.GetId()); def.SetLocalAxisA(axis);
        def.SetLocalAnchorA(a); def.SetLocalAnchorB(b); def.SetLowerTranslation(-2 * scale); def.SetUpperTranslation(2 * scale);
        def.SetEnableLimit(true); def.SetEnableMotor(true); def.SetMaxMotorForce(2 * scale);
        def.SetEnableSpring(true); def.SetHertz(1); def.SetDampingRatio(.5f);
        createPrismaticJoint(def); release(b, a, axis, def);
    }

    private void createAxle(B2Body wheel, B2Body bar, float barAnchor, float scale) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 a = vector(0, 0), b = vector(barAnchor, 0);
        def.SetBodyIdA(wheel.GetId()); def.SetBodyIdB(bar.GetId()); def.SetLocalAnchorA(a); def.SetLocalAnchorB(b);
        def.SetEnableMotor(true); def.SetMaxMotorTorque(2 * scale);
        createRevoluteJoint(def); release(b, a, def);
    }
}
