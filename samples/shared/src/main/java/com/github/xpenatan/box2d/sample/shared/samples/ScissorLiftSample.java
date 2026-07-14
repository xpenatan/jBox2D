package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2DistanceJointDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WheelJointDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Scissor Lift sample. */
public final class ScissorLiftSample extends AbstractBox2DSample {
    private final B2Joint liftJoint;
    private float motorForce = 2000.0f;
    private float motorSpeed = 0.25f;
    private boolean enableMotor;

    public ScissorLiftSample() {
        super(8);
        B2Body ground = addGroundSegment(-20, 0, 20, 0);
        B2Body base1 = ground, base2 = ground, link1 = null;
        float baseAnchor1X = -2.5f, baseAnchor1Y = .2f;
        float baseAnchor2X = 2.5f, baseAnchor2Y = .2f;
        float y = .5f;
        for(int i = 0; i < 3; i++) {
            B2BodyDef def1 = new B2BodyDef(); B2Vec2 position = vector(0, y);
            def1.SetType(B2.DynamicBody()); def1.SetPosition(position); def1.SetAngle(.15f); def1.SetSleepThreshold(.01f);
            B2Body body1 = createBody(def1);
            addCapsuleShape(body1, -2.5f, 0, 2.5f, 0, .15f, 1, .6f, 0, 0);
            def1.SetAngle(-.15f);
            B2Body body2 = createBody(def1);
            addCapsuleShape(body2, -2.5f, 0, 2.5f, 0, .15f, 1, .6f, 0, 0);
            if(i == 1) link1 = body2;

            createPin(base1, body1, baseAnchor1X, baseAnchor1Y, -2.5f, 0, i == 0);
            if(i == 0) createWheelPin(base2, body2, baseAnchor2X, baseAnchor2Y, 2.5f, 0, true);
            else createPin(base2, body2, baseAnchor2X, baseAnchor2Y, 2.5f, 0, false);
            createPin(body1, body2, 0, 0, 0, 0, false);
            base1 = body2; base2 = body1;
            baseAnchor1X = -2.5f; baseAnchor1Y = 0;
            baseAnchor2X = 2.5f; baseAnchor2Y = 0;
            y += 1;
            release(position, def1);
        }
        B2Body platform = addDynamicBox(0, y, 3, .2f);
        createPin(platform, base1, -2.5f, -.4f, baseAnchor1X, baseAnchor1Y, true);
        createWheelPin(platform, base2, 2.5f, -.4f, baseAnchor2X, baseAnchor2Y, true);

        B2DistanceJointDef distance = new B2DistanceJointDef();
        B2Vec2 localA = vector(-2.5f, .2f), localB = vector(.5f, 0);
        distance.SetBodyIdA(ground.GetId()); distance.SetBodyIdB(link1.GetId());
        distance.SetLocalAnchorA(localA); distance.SetLocalAnchorB(localB);
        distance.SetEnableSpring(true); distance.SetMinLength(.2f); distance.SetMaxLength(5.5f); distance.SetEnableLimit(true);
        distance.SetEnableMotor(enableMotor); distance.SetMotorSpeed(motorSpeed); distance.SetMaxMotorForce(motorForce);
        liftJoint = createDistanceJoint(distance);
        release(localB, localA, distance);
        new CarAssembly(this, 0, y + 2, 1, 3, .7f, 0);
    }

    private void createPin(B2Body a, B2Body b, float ax, float ay, float bx, float by, boolean collide) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 la = vector(ax, ay), lb = vector(bx, by);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId()); def.SetLocalAnchorA(la); def.SetLocalAnchorB(lb);
        def.SetCollideConnected(collide); createRevoluteJoint(def); release(lb, la, def);
    }

    private void createWheelPin(B2Body a, B2Body b, float ax, float ay, float bx, float by, boolean collide) {
        B2WheelJointDef def = new B2WheelJointDef();
        B2Vec2 la = vector(ax, ay), lb = vector(bx, by), axis = vector(1, 0);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId()); def.SetLocalAnchorA(la); def.SetLocalAnchorB(lb);
        def.SetLocalAxisA(axis); def.SetEnableSpring(false); def.SetCollideConnected(collide);
        createWheelJoint(def); release(axis, lb, la, def);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.checkbox("Motor", () -> enableMotor ? 1 : 0,
                        value -> { enableMotor = value != 0; liftJoint.DistanceEnableMotor(enableMotor); liftJoint.WakeBodies(); }),
                Box2DSampleControl.slider("Max Force", 0, 3000, 1, () -> motorForce,
                        value -> { motorForce = value; liftJoint.DistanceSetMaxMotorForce(value); liftJoint.WakeBodies(); }),
                Box2DSampleControl.slider("Speed", -.3f, .3f, .01f, () -> motorSpeed,
                        value -> { motorSpeed = value; liftJoint.DistanceSetMotorSpeed(value); liftJoint.WakeBodies(); }));
    }
}
