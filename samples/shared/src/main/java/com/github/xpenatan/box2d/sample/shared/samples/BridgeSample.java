package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Bridge sample. */
public final class BridgeSample extends AbstractBox2DSample {
    private static final int COUNT = 160;
    private final List<B2Joint> bridgeJoints = new ArrayList<B2Joint>();
    private float frictionTorque = 200.0f;
    private float springHertz = 2.0f;
    private float springDampingRatio = 0.7f;

    public BridgeSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Body previous = ground;
        float xBase = -80.0f;
        for(int i = 0; i < COUNT; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = vector(xBase + 0.5f + i, 20.0f);
            bodyDef.SetType(com.github.xpenatan.box2d.B2.DynamicBody());
            bodyDef.SetPosition(position);
            bodyDef.SetLinearDamping(0.1f);
            bodyDef.SetAngularDamping(0.1f);
            B2Body plank = createBody(bodyDef);
            addBoxShape(plank, 0.5f, 0.125f, 20.0f, 0.6f, 0.0f, 0.0f);
            bridgeJoints.add(createBridgeJoint(previous, plank, xBase + i, 20.0f));
            previous = plank;
            release(position, bodyDef);
        }
        bridgeJoints.add(createBridgeJoint(previous, ground, xBase + COUNT, 20.0f));
        for(int i = 0; i < 2; i++) {
            B2Body body = createDynamicBody(-8.0f + 8.0f * i, 22.0f, 0.0f);
            addPolygonShape(body, new float[] {-0.5f, 0.0f, 0.5f, 0.0f, 0.0f, 1.5f}, 0.0f,
                    20.0f, 0.6f, 0.0f, 0.0f);
        }
        for(int i = 0; i < 3; i++) addDynamicCircle(-6.0f + 6.0f * i, 25.0f, 0.5f, 20.0f, 0.6f, 0.0f, 0.0f);
    }

    private B2Joint createBridgeJoint(B2Body a, B2Body b, float x, float y) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 pivot = vector(x, y);
        B2Vec2 localA = copyLocalPoint(a, pivot), localB = copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        def.SetEnableMotor(true); def.SetMaxMotorTorque(frictionTorque);
        def.SetEnableSpring(true); def.SetHertz(springHertz); def.SetDampingRatio(springDampingRatio);
        B2Joint joint = createRevoluteJoint(def);
        release(localB, localA, pivot, def);
        return joint;
    }

    private void forEachJoint(java.util.function.Consumer<B2Joint> action) {
        for(B2Joint joint : bridgeJoints) action.accept(joint);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Joint Friction", 0, 10000, 1, () -> frictionTorque,
                        value -> { frictionTorque = value; forEachJoint(j -> j.RevoluteSetMaxMotorTorque(value)); }),
                Box2DSampleControl.slider("Spring hertz", 0, 30, .1f, () -> springHertz,
                        value -> { springHertz = value; forEachJoint(j -> j.RevoluteSetSpringHertz(value)); }),
                Box2DSampleControl.slider("Spring damping", 0, 2, .1f, () -> springDampingRatio,
                        value -> { springDampingRatio = value; forEachJoint(j -> j.RevoluteSetSpringDampingRatio(value)); }));
    }
}
