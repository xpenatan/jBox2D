package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WheelJointDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Wheel sample. */
public final class WheelJointSample extends AbstractBox2DSample {
    private final B2Body ground;
    private final B2Body body;
    private B2Joint joint;
    private float hertz = 1.0f;
    private float dampingRatio = 0.7f;
    private float motorSpeed = 2.0f;
    private float motorTorque = 5.0f;
    private boolean enableSpring = true;
    private boolean enableMotor = true;
    private boolean enableLimit = true;

    public WheelJointSample() {
        ground = createStaticBody(0.0f, 0.0f, 0.0f);
        body = createDynamicBody(0.0f, 10.25f, 0.0f);
        addCapsuleShape(body, 0.0f, -0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.6f, 0.0f, 0.0f);
        recreateJoint();
    }

    private void recreateJoint() {
        if(joint != null) destroyJoint(joint);
        B2WheelJointDef def = new B2WheelJointDef();
        def.SetBodyIdA(ground.GetId()); def.SetBodyIdB(body.GetId());
        B2Vec2 pivot = vector(0.0f, 10.0f);
        B2Vec2 localA = copyLocalPoint(ground, pivot);
        B2Vec2 localB = copyLocalPoint(body, pivot);
        B2Vec2 axis = vector(0.70710677f, 0.70710677f);
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB); def.SetLocalAxisA(axis);
        def.SetMotorSpeed(motorSpeed); def.SetMaxMotorTorque(motorTorque); def.SetEnableMotor(enableMotor);
        def.SetLowerTranslation(-3.0f); def.SetUpperTranslation(3.0f); def.SetEnableLimit(enableLimit);
        def.SetEnableSpring(enableSpring); def.SetHertz(hertz); def.SetDampingRatio(dampingRatio);
        joint = createWheelJoint(def);
        release(axis, localB, localA, pivot, def);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.checkbox("Limit", () -> enableLimit ? 1 : 0,
                value -> { enableLimit = value != 0; recreateJoint(); }));
        c.add(Box2DSampleControl.checkbox("Motor", () -> enableMotor ? 1 : 0,
                value -> { enableMotor = value != 0; recreateJoint(); }));
        if(enableMotor) {
            c.add(Box2DSampleControl.slider("Torque", 0, 20, .1f, () -> motorTorque,
                    value -> { motorTorque = value; recreateJoint(); }));
            c.add(Box2DSampleControl.slider("Speed", -20, 20, .1f, () -> motorSpeed,
                    value -> { motorSpeed = value; recreateJoint(); }));
        }
        c.add(Box2DSampleControl.checkbox("Spring", () -> enableSpring ? 1 : 0,
                value -> { enableSpring = value != 0; recreateJoint(); }));
        if(enableSpring) {
            c.add(Box2DSampleControl.slider("Hertz", 0, 10, .1f, () -> hertz,
                    value -> { hertz = value; recreateJoint(); }));
            c.add(Box2DSampleControl.slider("Damping", 0, 2, .1f, () -> dampingRatio,
                    value -> { dampingRatio = value; recreateJoint(); }));
        }
        c.add(Box2DSampleControl.dynamicText(() -> String.format("Constraint torque = %.1f", joint.GetConstraintTorque())));
        return c;
    }
}
