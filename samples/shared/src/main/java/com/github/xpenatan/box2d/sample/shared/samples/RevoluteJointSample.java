package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Revolute sample. */
public final class RevoluteJointSample extends AbstractBox2DSample {
    private final B2Joint joint1;
    private final B2Joint joint2;
    private float motorSpeed = 1.0f;
    private float motorTorque = 1000.0f;
    private float hertz = 2.0f;
    private float dampingRatio = 0.5f;
    private float targetDegrees = 45.0f;
    private boolean enableSpring;
    private boolean enableMotor;
    private boolean enableLimit = true;

    public RevoluteJointSample() {
        B2Body ground = addStaticBox(0.0f, -1.0f, 40.0f, 1.0f, 0.0f);
        B2Body capsule = createDynamicBody(-10.0f, 20.0f, 0.0f);
        addCapsuleShape(capsule, 0.0f, -1.0f, 0.0f, 6.0f, 0.5f, 1.0f, 0.6f, 0.0f, 0.0f);
        B2RevoluteJointDef def1 = revoluteDef(ground, capsule, -10.0f, 20.5f);
        def1.SetTargetAngle(radians(targetDegrees));
        def1.SetEnableSpring(enableSpring);
        def1.SetHertz(hertz);
        def1.SetDampingRatio(dampingRatio);
        def1.SetMotorSpeed(motorSpeed);
        def1.SetMaxMotorTorque(motorTorque);
        def1.SetEnableMotor(enableMotor);
        def1.SetReferenceAngle(0.5f * PI);
        def1.SetLowerAngle(-0.5f * PI);
        def1.SetUpperAngle(0.75f * PI);
        def1.SetEnableLimit(enableLimit);
        joint1 = createRevoluteJoint(def1);
        release(def1);

        addDynamicCircle(5.0f, 30.0f, 2.0f);
        B2Body lever = createDynamicBody(20.0f, 10.0f, 0.0f);
        addOffsetBoxShape(lever, 10.0f, 0.5f, -10.0f, 0.0f, 0.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        B2RevoluteJointDef def2 = revoluteDef(ground, lever, 19.0f, 10.0f);
        def2.SetLowerAngle(-0.25f * PI);
        def2.SetUpperAngle(0.1f * PI);
        def2.SetEnableLimit(true);
        def2.SetEnableMotor(true);
        def2.SetMotorSpeed(0.0f);
        def2.SetMaxMotorTorque(motorTorque);
        joint2 = createRevoluteJoint(def2);
        release(def2);
    }

    private B2RevoluteJointDef revoluteDef(B2Body a, B2Body b, float x, float y) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 pivot = vector(x, y);
        B2Vec2 localA = copyLocalPoint(a, pivot);
        B2Vec2 localB = copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        release(localB, localA, pivot);
        return def;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.checkbox("Limit", () -> enableLimit ? 1 : 0,
                value -> { enableLimit = value != 0; joint1.RevoluteEnableLimit(enableLimit); joint1.WakeBodies(); }));
        c.add(Box2DSampleControl.checkbox("Motor", () -> enableMotor ? 1 : 0,
                value -> { enableMotor = value != 0; joint1.RevoluteEnableMotor(enableMotor); joint1.WakeBodies(); }));
        if(enableMotor) {
            c.add(Box2DSampleControl.slider("Max Torque", 0, 5000, 1, () -> motorTorque,
                    value -> { motorTorque = value; joint1.RevoluteSetMaxMotorTorque(value); joint1.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Speed", -20, 20, .1f, () -> motorSpeed,
                    value -> { motorSpeed = value; joint1.RevoluteSetMotorSpeed(value); joint1.WakeBodies(); }));
        }
        c.add(Box2DSampleControl.checkbox("Spring", () -> enableSpring ? 1 : 0,
                value -> { enableSpring = value != 0; joint1.RevoluteEnableSpring(enableSpring); joint1.WakeBodies(); }));
        if(enableSpring) {
            c.add(Box2DSampleControl.slider("Hertz", 0, 30, .1f, () -> hertz,
                    value -> { hertz = value; joint1.RevoluteSetSpringHertz(value); joint1.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Damping", 0, 2, .1f, () -> dampingRatio,
                    value -> { dampingRatio = value; joint1.RevoluteSetSpringDampingRatio(value); joint1.WakeBodies(); }));
            c.add(Box2DSampleControl.slider("Degrees", -180, 180, 1, () -> targetDegrees,
                    value -> { targetDegrees = value; joint1.RevoluteSetTargetAngle(radians(value)); joint1.WakeBodies(); }));
        }
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Angle (Deg) 1 = %.1f", joint1.RevoluteGetAngle() * 180.0f / PI)));
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Constraint torque 1 = %.1f", joint1.GetConstraintTorque())));
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Constraint torque 2 = %.1f", joint2.GetConstraintTorque())));
        return c;
    }
}
