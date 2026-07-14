package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import java.util.ArrayList;
import java.util.List;

/** Java counterpart of Box2D's reusable eleven-bone Human helper. */
final class HumanRagdoll {
    private static final int HIP = 0, TORSO = 1, HEAD = 2, UPPER_LEFT_LEG = 3, LOWER_LEFT_LEG = 4,
            UPPER_RIGHT_LEG = 5, LOWER_RIGHT_LEG = 6, UPPER_LEFT_ARM = 7, LOWER_LEFT_ARM = 8,
            UPPER_RIGHT_ARM = 9, LOWER_RIGHT_ARM = 10;
    private static final int[] PARENT = {-1, HIP, TORSO, HIP, UPPER_LEFT_LEG, HIP, UPPER_RIGHT_LEG,
            TORSO, UPPER_LEFT_ARM, TORSO, UPPER_RIGHT_ARM};
    private static final float[] BODY_Y = {.95f, 1.2f, 1.475f, .775f, .475f, .775f, .475f,
            1.225f, .975f, 1.225f, .975f};
    private static final float[] PIVOT_Y = {0, 1.0f, 1.4f, .9f, .625f, .9f, .625f,
            1.35f, 1.1f, 1.35f, 1.1f};
    private static final float[] LOWER = {0, -.25f, -.3f, -.05f, -.5f, -.05f, -.5f,
            -.1f, -.2f, -.1f, -.2f};
    private static final float[] UPPER = {0, 0, .1f, .4f, -.02f, .4f, -.02f,
            .8f, .3f, .8f, .3f};
    private static final float[] FRICTION_SCALE = {0, .5f, .25f, 1, .5f, 1, .5f, .5f, .1f, .5f, .1f};

    private final AbstractBox2DSample sample;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final List<B2Shape> shapes = new ArrayList<B2Shape>();
    private final List<B2Joint> joints = new ArrayList<B2Joint>();
    private float originX;
    private float originY;
    private float scale;
    private float frictionTorque;
    private float hertz;
    private float dampingRatio;

    HumanRagdoll(AbstractBox2DSample sample, float x, float y, float scale, float frictionTorque, float hertz,
            float dampingRatio) {
        this.sample = sample;
        spawn(x, y, scale, frictionTorque, hertz, dampingRatio);
    }

    void spawn(float x, float y, float scale, float frictionTorque, float hertz, float dampingRatio) {
        this.originX = x; this.originY = y; this.scale = scale;
        this.frictionTorque = frictionTorque; this.hertz = hertz; this.dampingRatio = dampingRatio;
        B2ShapeDef shapeDef = sample.shapeDef(1.0f, 0.2f, 0.0f, 0.0f);
        B2Filter filter = new B2Filter();
        filter.SetGroupIndex(-1); filter.SetCategoryBits(2); filter.SetMaskBits(3); shapeDef.SetFilter(filter);
        for(int i = 0; i < 11; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = new B2Vec2(x, y + BODY_Y[i] * scale);
            bodyDef.SetType(B2.DynamicBody()); bodyDef.SetPosition(position); bodyDef.SetSleepThreshold(0.1f);
            bodyDef.SetLinearDamping(i == HEAD || i == LOWER_LEFT_ARM || i == LOWER_RIGHT_ARM ? 0.1f : 0.0f);
            B2Body body = sample.createBody(bodyDef);
            createBoneShape(body, i, scale, shapeDef);
            bodies.add(body);
            AbstractBox2DSample.release(position, bodyDef);
        }
        for(int i = 1; i < 11; i++) joints.add(createJoint(i));
        AbstractBox2DSample.release(filter, shapeDef);
    }

    private void createBoneShape(B2Body body, int index, float s, B2ShapeDef def) {
        float y1, y2, radius;
        switch(index) {
            case HIP: y1 = -.02f; y2 = .02f; radius = .095f; break;
            case TORSO: y1 = -.135f; y2 = .135f; radius = .09f; break;
            case HEAD: y1 = -.038f; y2 = .039f; radius = .075f; break;
            case UPPER_LEFT_LEG: case UPPER_RIGHT_LEG: y1 = -.125f; y2 = .125f; radius = .06f; break;
            case LOWER_LEFT_LEG: case LOWER_RIGHT_LEG: y1 = -.155f; y2 = .125f; radius = .045f; break;
            case UPPER_LEFT_ARM: case UPPER_RIGHT_ARM: y1 = -.125f; y2 = .125f; radius = .035f; break;
            default: y1 = -.125f; y2 = .125f; radius = .03f; break;
        }
        B2Vec2 center1 = new B2Vec2(0, y1 * s), center2 = new B2Vec2(0, y2 * s);
        com.github.xpenatan.box2d.B2Capsule capsule = new com.github.xpenatan.box2d.B2Capsule(center1, center2, radius * s);
        shapes.add(sample.createCapsuleShape(body, def, capsule));
        AbstractBox2DSample.release(capsule, center2, center1);
        if(index == LOWER_LEFT_LEG || index == LOWER_RIGHT_LEG) {
            shapes.add(sample.addPolygonShape(body, new float[] {-.03f * s, -.185f * s, .11f * s, -.185f * s,
                    .11f * s, -.16f * s, -.03f * s, -.14f * s}, .015f * s, 1.0f, .05f, 0, 0));
        }
    }

    private B2Joint createJoint(int index) {
        B2Body parent = bodies.get(PARENT[index]), child = bodies.get(index);
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 pivot = new B2Vec2(originX, originY + PIVOT_Y[index] * scale);
        B2Vec2 localA = AbstractBox2DSample.copyLocalPoint(parent, pivot);
        B2Vec2 localB = AbstractBox2DSample.copyLocalPoint(child, pivot);
        def.SetBodyIdA(parent.GetId()); def.SetBodyIdB(child.GetId());
        def.SetLocalAnchorA(localA); def.SetLocalAnchorB(localB);
        def.SetEnableLimit(true); def.SetLowerAngle(LOWER[index] * AbstractBox2DSample.PI);
        def.SetUpperAngle(UPPER[index] * AbstractBox2DSample.PI);
        if(index == LOWER_LEFT_ARM || index == LOWER_RIGHT_ARM) def.SetReferenceAngle(0.25f * AbstractBox2DSample.PI);
        def.SetEnableMotor(true); def.SetMaxMotorTorque(FRICTION_SCALE[index] * frictionTorque * scale);
        def.SetEnableSpring(hertz > 0); def.SetHertz(hertz); def.SetDampingRatio(dampingRatio);
        def.SetDrawSize(0.05f);
        B2Joint joint = sample.createRevoluteJoint(def);
        AbstractBox2DSample.release(localB, localA, pivot, def);
        return joint;
    }

    void destroy() {
        for(B2Joint joint : joints) sample.destroyJoint(joint);
        for(B2Body body : bodies) sample.destroyBody(body);
        for(B2Joint joint : joints) sample.discardHandle(joint);
        for(B2Shape shape : shapes) sample.discardHandle(shape);
        for(B2Body body : bodies) sample.discardHandle(body);
        joints.clear(); shapes.clear(); bodies.clear();
    }

    void respawn() { destroy(); spawn(originX, originY, scale, frictionTorque, hertz, dampingRatio); }
    void setScale(float value) { destroy(); spawn(originX, originY, value, frictionTorque, hertz, dampingRatio); }
    void setFrictionTorque(float value) {
        frictionTorque = value;
        for(int i = 0; i < joints.size(); i++) {
            B2Joint joint = joints.get(i);
            joint.RevoluteEnableMotor(value != 0);
            joint.RevoluteSetMaxMotorTorque(FRICTION_SCALE[i + 1] * value * scale);
        }
    }
    void setHertz(float value) {
        hertz = value;
        for(B2Joint joint : joints) { joint.RevoluteEnableSpring(value != 0); joint.RevoluteSetSpringHertz(value); }
    }
    void setDampingRatio(float value) { dampingRatio = value; for(B2Joint joint : joints) joint.RevoluteSetSpringDampingRatio(value); }
    void applyAngularImpulse(float magnitude) { bodies.get(TORSO).ApplyAngularImpulse(magnitude, true); }
}
