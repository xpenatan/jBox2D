package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Ball & Chain sample. */
public final class BallAndChainSample extends AbstractBox2DSample {
    private static final int COUNT = 30;
    private final List<B2Joint> joints = new ArrayList<B2Joint>();
    private float frictionTorque = 100.0f;

    public BallAndChainSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        B2Body previous = ground;
        float hx = 0.5f;
        B2ShapeDef linkDef = filteredDef(20.0f, 1L, 2L);
        B2Vec2 c1 = vector(-hx, 0.0f), c2 = vector(hx, 0.0f);
        B2Capsule capsule = new B2Capsule(c1, c2, 0.125f);
        for(int i = 0; i < COUNT; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = vector((1.0f + 2.0f * i) * hx, COUNT * hx);
            bodyDef.SetType(B2.DynamicBody()); bodyDef.SetPosition(position);
            B2Body link = createBody(bodyDef);
            createCapsuleShape(link, linkDef, capsule);
            joints.add(link(previous, link, 2.0f * i * hx, COUNT * hx, i > 0));
            previous = link;
            release(position, bodyDef);
        }
        B2Body ball = createDynamicBody((1.0f + 2.0f * COUNT) * hx + 4.0f - hx, COUNT * hx, 0.0f);
        B2ShapeDef ballDef = filteredDef(20.0f, 2L, 1L);
        B2Vec2 center = vector(0.0f, 0.0f);
        B2Circle circle = new B2Circle(center, 4.0f);
        createCircleShape(ball, ballDef, circle);
        joints.add(link(previous, ball, 2.0f * COUNT * hx, COUNT * hx, true));
        release(circle, center, ballDef, capsule, c2, c1, linkDef);
    }

    private B2ShapeDef filteredDef(float density, long category, long mask) {
        B2ShapeDef def = shapeDef(density, 0.6f, 0.0f, 0.0f);
        B2Filter filter = new B2Filter();
        filter.SetCategoryBits(category); filter.SetMaskBits(mask); def.SetFilter(filter);
        release(filter);
        return def;
    }

    private B2Joint link(B2Body a, B2Body b, float x, float y, boolean spring) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 pivot = vector(x, y), localA = copyLocalPoint(a, pivot), localB = copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        def.SetEnableMotor(true); def.SetMaxMotorTorque(frictionTorque);
        def.SetEnableSpring(spring); def.SetHertz(4.0f);
        B2Joint joint = createRevoluteJoint(def);
        release(localB, localA, pivot, def);
        return joint;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.slider("Joint Friction", 0, 1000, 1,
                () -> frictionTorque, value -> {
                    frictionTorque = value;
                    for(B2Joint joint : joints) joint.RevoluteSetMaxMotorTorque(value);
                }));
    }
}
