package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WeldJointDef;

/** Java port of Box2D 3.1.1's Joints / Soft Body sample. */
public final class SoftBodySample extends AbstractBox2DSample {
    public SoftBodySample() {
        addGroundSegment(-20, 0, 20, 0);
        int sides = 7; float scale = 2, radius = scale;
        float delta = 2 * PI / sides, length = 2 * PI * radius / sides;
        B2Body[] bodies = new B2Body[sides];
        for(int i = 0; i < sides; i++) {
            float angle = i * delta;
            bodies[i] = createDynamicBody(radius * (float)Math.cos(angle), 10 + radius * (float)Math.sin(angle), angle);
            addCapsuleShape(bodies[i], 0, -.5f * length, 0, .5f * length, .25f * scale, 1, .3f, 0, 0);
        }
        B2Body previous = bodies[sides - 1];
        for(B2Body body : bodies) {
            B2WeldJointDef def = new B2WeldJointDef();
            B2Vec2 a = vector(0, .5f * length), b = vector(0, -.5f * length);
            float angleA = previous.GetRotation().GetAngle();
            float angleB = body.GetRotation().GetAngle();
            def.SetBodyIdA(previous.GetId()); def.SetBodyIdB(body.GetId());
            def.SetLocalAnchorA(a); def.SetLocalAnchorB(b); def.SetReferenceAngle(angleB - angleA);
            def.SetAngularHertz(5); def.SetAngularDampingRatio(0);
            B2Joint joint = createWeldJoint(def);
            previous = body;
            release(b, a, def);
        }
    }
}
