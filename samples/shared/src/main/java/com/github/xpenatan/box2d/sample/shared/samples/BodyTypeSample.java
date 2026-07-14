package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Bodies / Body Type sample. */
public final class BodyTypeSample extends AbstractBox2DSample {
    private final B2Body attachment;
    private final B2Body secondAttachment;
    private final B2Body platform;
    private final B2Body secondPayload;
    private final B2Body touchingBody;
    private final B2Body floatingBody;
    private final B2Body[] switchable;
    private int type = B2.DynamicBody();
    private boolean enabled = true;
    private final float speed = 3.0f;

    public BodyTypeSample() {
        B2Body ground = addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        attachment = addDynamicBox(-2.0f, 3.0f, 0.5f, 2.0f);
        secondAttachment = addDynamicBox(3.0f, 3.0f, 0.5f, 2.0f);
        platform = createDynamicBody(-4.0f, 5.0f, 0.0f);
        addOffsetBoxShape(platform, 0.5f, 4.0f, 4.0f, 0.0f, 0.5f * PI,
                2.0f, 0.6f, 0.0f, 0.0f);
        addMotorRevolute(attachment, platform, -2.0f, 5.0f);
        addMotorRevolute(secondAttachment, platform, 3.0f, 5.0f);

        B2PrismaticJointDef prism = new B2PrismaticJointDef();
        prism.SetBodyIdA(ground.GetId());
        prism.SetBodyIdB(platform.GetId());
        B2Vec2 anchor = vector(0.0f, 5.0f);
        B2Vec2 localA = copyLocalPoint(ground, anchor);
        B2Vec2 localB = copyLocalPoint(platform, anchor);
        B2Vec2 axis = vector(1.0f, 0.0f);
        prism.SetLocalAnchorA(localA);
        prism.SetLocalAnchorB(localB);
        prism.SetLocalAxisA(axis);
        prism.SetMaxMotorForce(1000.0f);
        prism.SetMotorSpeed(0.0f);
        prism.SetEnableMotor(true);
        prism.SetLowerTranslation(-10.0f);
        prism.SetUpperTranslation(10.0f);
        prism.SetEnableLimit(true);
        createPrismaticJoint(prism);
        release(axis, localB, localA, anchor, prism);

        addDynamicBox(-3.0f, 8.0f, 0.75f, 0.75f, 0.0f, 2.0f, 0.6f, 0.0f, 0.0f);
        secondPayload = addDynamicBox(2.0f, 8.0f, 0.75f, 0.75f, 0.0f, 2.0f, 0.6f, 0.0f, 0.0f);
        touchingBody = createDynamicBody(8.0f, 0.2f, 0.0f);
        addCapsuleShape(touchingBody, 0.0f, 0.0f, 1.0f, 0.0f, 0.25f,
                2.0f, 0.6f, 0.0f, 0.0f);

        B2BodyDef floatingDef = new B2BodyDef();
        floatingDef.SetType(B2.DynamicBody());
        B2Vec2 floatingPosition = vector(-8.0f, 12.0f);
        floatingDef.SetPosition(floatingPosition);
        floatingDef.SetGravityScale(0.0f);
        floatingBody = createBody(floatingDef);
        addCircleShape(floatingBody, 0.0f, 0.5f, 0.25f, 2.0f, 0.6f, 0.0f, 0.0f);
        release(floatingPosition, floatingDef);
        switchable = new B2Body[] { platform, secondAttachment, secondPayload, touchingBody, floatingBody };
    }

    private void addMotorRevolute(B2Body bodyA, B2Body bodyB, float x, float y) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 pivot = vector(x, y);
        B2Vec2 localA = copyLocalPoint(bodyA, pivot);
        B2Vec2 localB = copyLocalPoint(bodyB, pivot);
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetMaxMotorTorque(50.0f);
        def.SetEnableMotor(true);
        createRevoluteJoint(def);
        release(localB, localA, pivot, def);
    }

    private void setType(int nextType) {
        type = nextType;
        for(B2Body body : switchable) body.SetType(type);
        if(type == B2.KinematicBody()) {
            setLinearVelocity(platform, -speed, 0.0f);
            platform.SetAngularVelocity(0.0f);
        }
    }

    private void setEnabled(boolean value) {
        enabled = value;
        for(B2Body body : switchable) {
            if(value) body.Enable(); else body.Disable();
        }
        if(value && type == B2.KinematicBody()) setLinearVelocity(platform, -speed, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        if(type != B2.KinematicBody() || !enabled) return;
        B2Vec2 position = platform.GetPosition();
        B2Vec2 velocity = platform.GetLinearVelocity();
        if((position.GetX() < -14.0f && velocity.GetX() < 0.0f)
                || (position.GetX() > 6.0f && velocity.GetX() > 0.0f)) {
            setLinearVelocity(platform, -velocity.GetX(), velocity.GetY());
        }
        release(velocity, position);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.radio("Static", () -> type == B2.StaticBody() ? 1 : 0,
                        () -> setType(B2.StaticBody())),
                Box2DSampleControl.radio("Kinematic", () -> type == B2.KinematicBody() ? 1 : 0,
                        () -> setType(B2.KinematicBody())),
                Box2DSampleControl.radio("Dynamic", () -> type == B2.DynamicBody() ? 1 : 0,
                        () -> setType(B2.DynamicBody())),
                Box2DSampleControl.checkbox("Enable", () -> enabled ? 1 : 0, value -> setEnabled(value != 0)));
    }
}
