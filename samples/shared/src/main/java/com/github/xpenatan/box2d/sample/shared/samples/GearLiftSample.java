package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Gear Lift sample. */
public final class GearLiftSample extends AbstractBox2DSample {
    private final B2Joint driver;
    private float motorTorque = 80.0f;
    private float motorSpeed;
    private boolean enableMotor = true;
    private boolean left;
    private boolean right;

    public GearLiftSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addChain(ground, new float[] {
                -11.2999996f, -0.216666f, 9.3375f, -0.216666f, 9.337502f, 7.191666f,
                8.808334f, 7.191666f, 8.808334f, 0.3125f, 0.341668f, 0.3125f,
                0.341668f, 0.841666f, -0.1875f, 0.841668f, -0.1875f, 1.370834f,
                -0.716666f, 1.370834f, -0.716666f, 1.900002f, -1.245834f, 1.900002f,
                -1.245834f, 2.429168f, -1.775f, 2.429168f, -1.775f, 2.958334f,
                -2.304166f, 2.958334f, -2.304166f, 3.4875f, -2.833332f, 3.4875f,
                -2.833332f, 4.016668f, -3.3625f, 4.016666f, -3.3625f, 4.545832f,
                -3.891666f, 4.545834f, -3.891666f, 5.075f, -4.4208328f, 5.075f,
                -4.4208328f, 5.604168f, -4.9499994f, 5.604166f, -4.9499994f, 6.133334f,
                -5.479166f, 6.133334f, -5.479166f, 6.6625f, -6.0083328f, 6.6625f,
                -6.0083328f, 7.191666f, -11.2999996f, 7.191666f, -11.2999996f, -0.216666f
        }, true, .6f);
        B2Body gear1 = createGear(-4.25f, 9.75f, 1.0f, 1.06f, .09f, .06f, .03f);
        B2RevoluteJointDef driverDef = revoluteAt(ground, gear1, -4.25f, 9.75f);
        driverDef.SetEnableMotor(enableMotor); driverDef.SetMaxMotorTorque(motorTorque); driverDef.SetMotorSpeed(motorSpeed);
        driver = createRevoluteJoint(driverDef); release(driverDef);

        B2Body gear2 = createGear(-2.25f, 10.75f, 1.0f, 1.09f, .09f, .06f, .03f);
        B2RevoluteJointDef followerDef = revoluteAt(ground, gear2, -2.25f, 10.75f);
        followerDef.SetEnableMotor(true); followerDef.SetMaxMotorTorque(.5f);
        followerDef.SetReferenceAngle(.25f * PI); followerDef.SetLowerAngle(-.3f * PI);
        followerDef.SetUpperAngle(.8f * PI); followerDef.SetEnableLimit(true);
        createRevoluteJoint(followerDef); release(followerDef);

        float linkX = -1.04f, linkY = 10.68f, halfLength = .07f;
        B2Body previous = gear2;
        for(int i = 0; i < 40; i++) {
            B2Body link = createDynamicBody(linkX, linkY, 0);
            addCapsuleShape(link, 0, -halfLength, 0, halfLength, .05f, 2, .6f, 0, 0);
            B2RevoluteJointDef def = revoluteAt(previous, link, linkX, linkY + halfLength);
            def.SetEnableMotor(true); def.SetMaxMotorTorque(.05f); createRevoluteJoint(def); release(def);
            previous = link; linkY -= 2 * halfLength;
        }
        float doorY = 3.65f;
        B2Body door = addDynamicBox(linkX, doorY, .15f, 1.5f, 0, 1, .1f, 0, 0);
        B2RevoluteJointDef doorLink = revoluteAt(previous, door, linkX, doorY + 1.5f);
        doorLink.SetEnableMotor(true); doorLink.SetMaxMotorTorque(.05f); createRevoluteJoint(doorLink); release(doorLink);
        B2PrismaticJointDef slider = new B2PrismaticJointDef();
        B2Vec2 world = vector(linkX, doorY), la = ground.GetLocalPoint(world), lb = vector(0, 0), axis = vector(0, 1);
        slider.SetBodyIdA(ground.GetId()); slider.SetBodyIdB(door.GetId()); slider.SetLocalAnchorA(la); slider.SetLocalAnchorB(lb);
        slider.SetLocalAxisA(axis); slider.SetMaxMotorForce(.2f); slider.SetEnableMotor(true); slider.SetCollideConnected(true);
        createPrismaticJoint(slider); release(axis, lb, la, world, slider);

        for(int i = 0; i < 20; i++) {
            float py = 4.25f + .2f * i;
            for(int j = 0; j < 10; j++) {
                float px = -3.15f + .2f * j;
                B2Body particle = createDynamicBody(px, py, randomFloat(-PI, PI));
                int type = (i + j) % 3;
                if(type == 0) addPolygonShape(particle, new float[] {-.08f, -.07f, .09f, -.06f, .02f, .1f},
                        .015f, 1, .4f, 0, .3f);
                else if(type == 1) addRoundedBoxShape(particle, .07f, .09f, .015f, 1, .4f, 0, .3f);
                else addCircleShape(particle, 0, 0, .08f, 1, .4f, 0, .3f);
            }
        }
    }

    private B2Body createGear(float x, float y, float radius, float toothCenterRadius, float toothHalfWidth,
            float toothHalfHeight, float toothRadius) {
        B2Body body = createDynamicBody(x, y, 0); addCircleShape(body, 0, 0, radius, 1, .1f, 0, 0);
        B2ShapeDef def = shapeDef(1, .1f, 0, 0);
        for(int i = 0; i < 16; i++) {
            float angle = 2 * PI * i / 16;
            B2Vec2 center = vector(toothCenterRadius * (float)Math.cos(angle),
                    toothCenterRadius * (float)Math.sin(angle));
            B2Rot rotation = new B2Rot(angle);
            B2Polygon tooth = B2Polygon.CreateOffsetRoundedBox(toothHalfWidth, toothHalfHeight, center, rotation, toothRadius);
            createPolygonShape(body, def, tooth); release(tooth, rotation, center);
        }
        release(def); return body;
    }

    private B2RevoluteJointDef revoluteAt(B2Body a, B2Body b, float x, float y) {
        B2RevoluteJointDef def = new B2RevoluteJointDef(); B2Vec2 p = vector(x, y);
        B2Vec2 la = copyLocalPoint(a, p), lb = copyLocalPoint(b, p);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId()); def.SetLocalAnchorA(la); def.SetLocalAnchorB(lb);
        release(lb, la, p); return def;
    }

    @Override protected void beforeStep(float deltaSeconds) {
        if(left) motorSpeed = Math.max(-.3f, motorSpeed - .01f);
        if(right) motorSpeed = Math.min(.3f, motorSpeed + .01f);
        if(left || right) { driver.RevoluteSetMotorSpeed(motorSpeed); driver.WakeBodies(); }
    }
    @Override public void keyDown(int key) { if(key == 'A') left = true; if(key == 'D') right = true; }
    @Override public void keyUp(int key) { if(key == 'A') left = false; if(key == 'D') right = false; }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.checkbox("Motor", () -> enableMotor ? 1 : 0,
                        value -> { enableMotor = value != 0; driver.RevoluteEnableMotor(enableMotor); driver.WakeBodies(); }),
                Box2DSampleControl.slider("Max Torque", 0, 100, 1, () -> motorTorque,
                        value -> { motorTorque = value; driver.RevoluteSetMaxMotorTorque(value); driver.WakeBodies(); }),
                Box2DSampleControl.slider("Speed", -.3f, .3f, .01f, () -> motorSpeed,
                        value -> { motorSpeed = value; driver.RevoluteSetMotorSpeed(value); driver.WakeBodies(); }));
    }
}
