package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Prismatic sample. */
public final class PrismaticJointSample extends AbstractBox2DSample {
    private final B2Joint joint;
    private float motorSpeed = 2.0f;
    private float motorForce = 25.0f;
    private float hertz = 1.0f;
    private float dampingRatio = 0.5f;
    private float translation;
    private boolean enableSpring;
    private boolean enableMotor;
    private boolean enableLimit = true;
    private float constraintForce;

    public PrismaticJointSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Body body = addDynamicBox(0.0f, 10.0f, 0.5f, 2.0f);
        B2PrismaticJointDef def = new B2PrismaticJointDef();
        def.SetBodyIdA(ground.GetId()); def.SetBodyIdB(body.GetId());
        B2Vec2 pivot = vector(0.0f, 9.0f);
        B2Vec2 localA = copyLocalPoint(ground, pivot);
        B2Vec2 localB = copyLocalPoint(body, pivot);
        B2Vec2 axis = vector(0.70710677f, 0.70710677f);
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB); def.SetLocalAxisA(axis);
        def.SetMotorSpeed(motorSpeed); def.SetMaxMotorForce(motorForce); def.SetEnableMotor(enableMotor);
        def.SetLowerTranslation(-10.0f); def.SetUpperTranslation(10.0f); def.SetEnableLimit(enableLimit);
        def.SetEnableSpring(enableSpring); def.SetHertz(hertz); def.SetDampingRatio(dampingRatio);
        joint = createPrismaticJoint(def);
        release(axis, localB, localA, pivot, def);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2Vec2 force = joint.GetConstraintForce();
        constraintForce = force.Length();
        release(force);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.checkbox("Limit", () -> enableLimit ? 1 : 0,
                value -> { enableLimit = value != 0; joint.PrismaticEnableLimit(enableLimit); joint.WakeBodies(); }));
        c.add(Box2DSampleControl.checkbox("Motor", () -> enableMotor ? 1 : 0,
                value -> { enableMotor = value != 0; joint.PrismaticEnableMotor(enableMotor); joint.WakeBodies(); }));
        if(enableMotor) {
            c.add(Box2DSampleControl.slider("Max Force", 0, 200, 1, () -> motorForce,
                    value -> { motorForce = value; joint.PrismaticSetMaxMotorForce(value); joint.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Speed", -40, 40, .1f, () -> motorSpeed,
                    value -> { motorSpeed = value; joint.PrismaticSetMotorSpeed(value); joint.WakeBodies(); }));
        }
        c.add(Box2DSampleControl.checkbox("Spring", () -> enableSpring ? 1 : 0,
                value -> { enableSpring = value != 0; joint.PrismaticEnableSpring(enableSpring); joint.WakeBodies(); }));
        if(enableSpring) {
            c.add(Box2DSampleControl.slider("Hertz", 0, 10, .1f, () -> hertz,
                    value -> { hertz = value; joint.PrismaticSetSpringHertz(value); joint.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Damping", 0, 2, .1f, () -> dampingRatio,
                    value -> { dampingRatio = value; joint.PrismaticSetSpringDampingRatio(value); joint.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Translation", -5, 5, .1f, () -> translation,
                    value -> { translation = value; joint.PrismaticSetTargetTranslation(value); joint.WakeBodies(); }));
        }
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Constraint force = %.1f", constraintForce)));
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Translation = %.1f", joint.PrismaticGetTranslation())));
        return c;
    }
}
