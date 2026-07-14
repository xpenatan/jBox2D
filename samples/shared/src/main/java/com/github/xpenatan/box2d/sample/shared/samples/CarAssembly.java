package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WheelJointDef;

/** Java counterpart of the reusable Car helper used by the official samples. */
final class CarAssembly {
    private final AbstractBox2DSample sample;
    final B2Body chassis;
    private final B2Body rearWheel;
    private final B2Body frontWheel;
    private final float scale;
    private B2Joint rearAxle;
    private B2Joint frontAxle;
    private float speed;
    private float torque;
    private float hertz;
    private float dampingRatio;

    CarAssembly(AbstractBox2DSample sample, float x, float y, float scale, float hertz, float dampingRatio,
            float torque) {
        this.sample = sample;
        this.scale = scale;
        this.hertz = hertz;
        this.dampingRatio = dampingRatio;
        this.torque = torque;
        B2BodyDef bodyDef = new B2BodyDef();
        B2Vec2 position = new B2Vec2(x, y + scale);
        bodyDef.SetType(B2.DynamicBody()); bodyDef.SetPosition(position);
        chassis = sample.createBody(bodyDef);
        float s = 0.85f * scale;
        sample.addPolygonShape(chassis, new float[] {
                -1.5f * s, -0.5f * s, 1.5f * s, -0.5f * s, 1.5f * s, 0.0f,
                0.0f, 0.9f * s, -1.15f * s, 0.9f * s, -1.5f * s, 0.2f * s
        }, 0.15f * scale, 1.0f / scale, 0.2f, 0.0f, 0.0f);

        position.Set(x - scale, y + 0.35f * scale);
        bodyDef.SetPosition(position); bodyDef.SetAllowFastRotation(true);
        rearWheel = sample.createBody(bodyDef);
        sample.addCircleShape(rearWheel, 0, 0, 0.4f * scale, 2.0f / scale, 1.5f, 0, 0.1f);

        position.Set(x + scale, y + 0.4f * scale);
        bodyDef.SetPosition(position);
        frontWheel = sample.createBody(bodyDef);
        sample.addCircleShape(frontWheel, 0, 0, 0.4f * scale, 2.0f / scale, 1.5f, 0, 0.1f);
        AbstractBox2DSample.release(position, bodyDef);
        rearAxle = axle(rearWheel);
        frontAxle = axle(frontWheel);
    }

    private B2Joint axle(B2Body wheel) {
        B2WheelJointDef def = new B2WheelJointDef();
        B2Vec2 pivot = wheel.GetPosition();
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(chassis, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(wheel, pivot);
        B2Vec2 worldAxis = new B2Vec2(0.0f, 1.0f);
        B2Vec2 localAxis = chassis.GetLocalVector(worldAxis);
        def.SetBodyIdA(chassis.GetId()); def.SetBodyIdB(wheel.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB); def.SetLocalAxisA(localAxis);
        def.SetMotorSpeed(speed); def.SetMaxMotorTorque(torque); def.SetEnableMotor(true);
        def.SetHertz(hertz); def.SetDampingRatio(dampingRatio);
        def.SetLowerTranslation(-0.25f * scale); def.SetUpperTranslation(0.25f * scale); def.SetEnableLimit(true);
        B2Joint joint = sample.createWheelJoint(def);
        AbstractBox2DSample.release(localAxis, worldAxis, localB, localA, pivot, def);
        return joint;
    }

    void setSpeed(float value) {
        if(speed == value) return;
        speed = value;
        rearAxle.WheelSetMotorSpeed(value);
        frontAxle.WheelSetMotorSpeed(value);
        rearAxle.WakeBodies();
    }

    void setTorque(float value) {
        if(torque == value) return;
        torque = value;
        rearAxle.WheelSetMaxMotorTorque(value);
        frontAxle.WheelSetMaxMotorTorque(value);
    }

    void setHertz(float value) {
        if(hertz == value) return;
        hertz = value;
        rearAxle.WheelSetSpringHertz(value);
        frontAxle.WheelSetSpringHertz(value);
    }

    void setDampingRatio(float value) {
        if(dampingRatio == value) return;
        dampingRatio = value;
        rearAxle.WheelSetSpringDampingRatio(value);
        frontAxle.WheelSetSpringDampingRatio(value);
    }

}
