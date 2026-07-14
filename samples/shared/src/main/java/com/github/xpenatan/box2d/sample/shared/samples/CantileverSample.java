package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WeldJointDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Cantilever sample. */
public final class CantileverSample extends AbstractBox2DSample {
    private final B2Body ground;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final List<B2Joint> joints = new ArrayList<B2Joint>();
    private float linearHertz = 15.0f;
    private float linearDamping = 0.5f;
    private float angularHertz = 5.0f;
    private float angularDamping = 0.5f;
    private float gravityScale = 1.0f;
    private boolean collideConnected;

    public CantileverSample() {
        ground = createStaticBody(0.0f, 0.0f, 0.0f);
        for(int i = 0; i < 8; i++) {
            B2Body body = createDynamicBody(0.5f + i, 0.0f, 0.0f);
            body.SetAwake(false);
            addCapsuleShape(body, -0.5f, 0.0f, 0.5f, 0.0f, 0.125f, 20.0f, 0.6f, 0.0f, 0.0f);
            bodies.add(body);
        }
        recreateJoints();
    }

    private void recreateJoints() {
        for(B2Joint joint : joints) destroyJoint(joint);
        joints.clear();
        B2Body previous = ground;
        for(int i = 0; i < bodies.size(); i++) {
            B2Body body = bodies.get(i);
            B2WeldJointDef def = new B2WeldJointDef();
            B2Vec2 pivot = vector(i, 0.0f), localA = copyLocalPoint(previous, pivot);
            B2Vec2 localB = copyLocalPoint(body, pivot);
            def.SetBodyIdA(previous.GetId()); def.SetBodyIdB(body.GetId());
            def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
            def.SetLinearHertz(linearHertz); def.SetLinearDampingRatio(linearDamping);
            def.SetAngularHertz(angularHertz); def.SetAngularDampingRatio(angularDamping);
            def.SetCollideConnected(collideConnected);
            joints.add(createWeldJoint(def));
            previous = body;
            release(localB, localA, pivot, def);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> c = new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.slider("Linear Hertz", 0, 20, .1f, () -> linearHertz,
                value -> { linearHertz = value; recreateJoints(); }));
        c.add(Box2DSampleControl.slider("Linear Damping Ratio", 0, 10, .1f, () -> linearDamping,
                value -> { linearDamping = value; recreateJoints(); }));
        c.add(Box2DSampleControl.slider("Angular Hertz", 0, 20, .1f, () -> angularHertz,
                value -> { angularHertz = value; recreateJoints(); }));
        c.add(Box2DSampleControl.slider("Angular Damping Ratio", 0, 10, .1f, () -> angularDamping,
                value -> { angularDamping = value; recreateJoints(); }));
        c.add(Box2DSampleControl.checkbox("Collide Connected", () -> collideConnected ? 1 : 0,
                value -> { collideConnected = value != 0; for(B2Joint joint : joints) joint.SetCollideConnected(collideConnected); }));
        c.add(Box2DSampleControl.slider("Gravity Scale", -1, 1, .1f, () -> gravityScale,
                value -> { gravityScale = value; for(B2Body body : bodies) body.SetGravityScale(value); }));
        c.add(Box2DSampleControl.dynamicText(() -> {
            B2Vec2 p = bodies.get(bodies.size() - 1).GetPosition();
            String text = String.format("tip-y = %.2f", p.GetY());
            release(p);
            return text;
        }));
        return c;
    }
}
