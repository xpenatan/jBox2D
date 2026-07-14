package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2DistanceJointDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Distance Joint sample. */
public final class DistanceJointSample extends AbstractBox2DSample {
    private final B2Body ground;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final List<B2Joint> joints = new ArrayList<B2Joint>();
    private float hertz = 2.0f;
    private float dampingRatio = 0.5f;
    private float length = 1.0f;
    private float minLength = 1.0f;
    private float maxLength = 1.0f;
    private boolean enableSpring;
    private boolean enableLimit;

    public DistanceJointSample() {
        ground = createStaticBody(0.0f, 0.0f, 0.0f);
        createScene(1);
    }

    private void createScene(int count) {
        for(B2Joint joint : joints) destroyJoint(joint);
        for(B2Body body : bodies) destroyBody(body);
        joints.clear();
        bodies.clear();
        B2Body previous = ground;
        for(int i = 0; i < count; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = vector(length * (i + 1.0f), 20.0f);
            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetAngularDamping(0.1f);
            bodyDef.SetPosition(position);
            B2Body body = createBody(bodyDef);
            addCircleShape(body, 0.0f, 0.0f, 0.25f, 20.0f, 0.6f, 0.0f, 0.0f);

            B2DistanceJointDef jointDef = new B2DistanceJointDef();
            jointDef.SetBodyIdA(previous.GetId());
            jointDef.SetBodyIdB(body.GetId());
            B2Vec2 worldA = vector(length * i, 20.0f);
            B2Vec2 worldB = vector(length * (i + 1.0f), 20.0f);
            B2Vec2 localA = copyLocalPoint(previous, worldA);
            B2Vec2 localB = copyLocalPoint(body, worldB);
            jointDef.SetLocalAnchorA(localA);
            jointDef.SetLocalAnchorB(localB);
            jointDef.SetHertz(hertz);
            jointDef.SetDampingRatio(dampingRatio);
            jointDef.SetLength(length);
            jointDef.SetMinLength(minLength);
            jointDef.SetMaxLength(maxLength);
            jointDef.SetEnableSpring(enableSpring);
            jointDef.SetEnableLimit(enableLimit);
            joints.add(createDistanceJoint(jointDef));
            bodies.add(body);
            previous = body;
            release(localB, localA, worldB, worldA, jointDef, position, bodyDef);
        }
    }

    private void updateLength(float value) {
        length = value;
        for(B2Joint joint : joints) { joint.DistanceSetLength(value); joint.WakeBodies(); }
    }

    private void updateSpring(float value) {
        enableSpring = value != 0.0f;
        for(B2Joint joint : joints) { joint.DistanceEnableSpring(enableSpring); joint.WakeBodies(); }
    }

    private void updateHertz(float value) {
        hertz = value;
        for(B2Joint joint : joints) { joint.DistanceSetSpringHertz(value); joint.WakeBodies(); }
    }

    private void updateDamping(float value) {
        dampingRatio = value;
        for(B2Joint joint : joints) { joint.DistanceSetSpringDampingRatio(value); joint.WakeBodies(); }
    }

    private void updateLimit(float value) {
        enableLimit = value != 0.0f;
        for(B2Joint joint : joints) { joint.DistanceEnableLimit(enableLimit); joint.WakeBodies(); }
    }

    private void updateRange() {
        for(B2Joint joint : joints) { joint.DistanceSetLengthRange(minLength, maxLength); joint.WakeBodies(); }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.slider("Length", 0.1f, 4.0f, 0.1f, () -> length, this::updateLength));
        controls.add(Box2DSampleControl.checkbox("Spring", () -> enableSpring ? 1 : 0, this::updateSpring));
        if(enableSpring) {
            controls.add(Box2DSampleControl.slider("Hertz", 0.0f, 15.0f, 0.1f, () -> hertz, this::updateHertz));
            controls.add(Box2DSampleControl.slider("Damping", 0.0f, 4.0f, 0.1f, () -> dampingRatio, this::updateDamping));
        }
        controls.add(Box2DSampleControl.checkbox("Limit", () -> enableLimit ? 1 : 0, this::updateLimit));
        if(enableLimit) {
            controls.add(Box2DSampleControl.slider("Min Length", 0.1f, 4.0f, 0.1f,
                    () -> minLength, value -> { minLength = value; updateRange(); }));
            controls.add(Box2DSampleControl.slider("Max Length", 0.1f, 4.0f, 0.1f,
                    () -> maxLength, value -> { maxLength = value; updateRange(); }));
        }
        controls.add(Box2DSampleControl.slider("Count", 1.0f, 10.0f, 1.0f,
                () -> bodies.size(), value -> createScene((int)value)));
        return controls;
    }
}
