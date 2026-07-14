package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Door sample. */
public final class DoorSample extends AbstractBox2DSample {
    private final B2Body door;
    private final B2Joint joint;
    private float impulse = 50000.0f;
    private float maximumTranslationError;
    private boolean enableLimit = true;
    private float tipX;
    private float tipY;

    public DoorSample() {
        super(4, 0, 0);
        B2Body ground = createStaticBody(0, 0, 0);
        door = createDynamicBody(0, 1.5f, 0); door.SetGravityScale(0);
        addBoxShape(door, .1f, 1.5f, 1000, .6f, 0, 0);
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 a = vector(0, 0), b = vector(0, -1.5f);
        def.SetBodyIdA(ground.GetId()); def.SetBodyIdB(door.GetId()); def.SetLocalAnchorA(a); def.SetLocalAnchorB(b);
        def.SetTargetAngle(0); def.SetEnableSpring(true); def.SetHertz(1); def.SetDampingRatio(.5f);
        def.SetLowerAngle(-.5f * PI); def.SetUpperAngle(.5f * PI); def.SetEnableLimit(enableLimit);
        joint = createRevoluteJoint(def); release(b, a, def);
    }

    private void applyImpulse() {
        B2Vec2 local = vector(0, 1.5f), point = door.GetWorldPoint(local);
        applyLinearImpulse(door, impulse, 0, point.GetX(), point.GetY());
        maximumTranslationError = 0; release(point, local);
    }

    @Override protected void afterStep(float deltaSeconds) {
        B2Vec2 local = vector(0, 1.5f), point = door.GetWorldPoint(local);
        tipX = point.GetX(); tipY = point.GetY(); release(point, local);
        maximumTranslationError = Math.max(maximumTranslationError, joint.GetLinearSeparation());
    }

    @Override public void draw(Box2DSampleDraw draw) {
        draw.point(tipX, tipY, 5, 0xBDB76BFF);
        draw.segment(0, 0, 1, 0, 0xFF0000FF); draw.segment(0, 0, 0, 1, 0x00FF00FF);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("impulse", this::applyImpulse),
                Box2DSampleControl.slider("magnitude", 1000, 100000, 1, () -> impulse, value -> impulse = value),
                Box2DSampleControl.checkbox("limit", () -> enableLimit ? 1 : 0,
                        value -> { enableLimit = value != 0; joint.RevoluteEnableLimit(enableLimit); }),
                Box2DSampleControl.dynamicText(() -> "translation error = " + maximumTranslationError));
    }
}
