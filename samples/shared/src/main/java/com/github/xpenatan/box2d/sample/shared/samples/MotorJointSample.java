package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Motor Joint sample. */
public final class MotorJointSample extends AbstractBox2DSample {
    private final B2Body body;
    private final B2Joint joint;
    private float time;
    private float maxForce = 500.0f;
    private float maxTorque = 500.0f;
    private float correctionFactor = 0.3f;
    private boolean go = true;
    private float targetX;
    private float targetY = 8.0f;
    private float targetAngle;
    private float forceX;
    private float forceY;
    private float torque;

    public MotorJointSample() {
        B2Body ground = addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        body = addDynamicBox(0.0f, 8.0f, 2.0f, 0.5f);
        joint = addMotorJoint(ground, body, maxForce, maxTorque, correctionFactor);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        if(go) time += deltaSeconds;
        targetX = 6.0f * (float)Math.sin(2.0f * time);
        targetY = 8.0f + 4.0f * (float)Math.sin(time);
        targetAngle = 2.0f * time;
        B2Vec2 offset = vector(targetX, targetY);
        joint.MotorSetLinearOffset(offset);
        joint.MotorSetAngularOffset(targetAngle);
        release(offset);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2Vec2 force = joint.GetConstraintForce();
        forceX = force.GetX();
        forceY = force.GetY();
        torque = joint.GetConstraintTorque();
        release(force);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        float c = (float)Math.cos(targetAngle), s = (float)Math.sin(targetAngle);
        draw.segment(targetX, targetY, targetX + c, targetY + s, 0xFF0000FF);
        draw.segment(targetX, targetY, targetX - s, targetY + c, 0x00FF00FF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.checkbox("Go", () -> go ? 1 : 0, value -> go = value != 0),
                Box2DSampleControl.slider("Max Force", 0, 10000, 1, () -> maxForce,
                        value -> { maxForce = value; joint.MotorSetMaxForce(value); }),
                Box2DSampleControl.slider("Max Torque", 0, 10000, 1, () -> maxTorque,
                        value -> { maxTorque = value; joint.MotorSetMaxTorque(value); }),
                Box2DSampleControl.slider("Correction", 0, 1, .1f, () -> correctionFactor,
                        value -> { correctionFactor = value; joint.MotorSetCorrectionFactor(value); }),
                Box2DSampleControl.button("Apply Impulse", () -> applyLinearImpulseToCenter(body, 100, 0)),
                Box2DSampleControl.dynamicText(() -> String.format("force = {%.0f, %.0f}, torque = %.0f", forceX, forceY, torque)));
    }
}
