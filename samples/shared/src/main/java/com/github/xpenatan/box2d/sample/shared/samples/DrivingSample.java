package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Driving sample. */
public final class DrivingSample extends AbstractBox2DSample {
    private final CarAssembly car;
    private float throttle;
    private float hertz = 5.0f;
    private float dampingRatio = 0.7f;
    private float torque = 5.0f;
    private float speed = 35.0f;
    private float kph;

    public DrivingSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        float[] points = new float[50];
        int p = 0;
        points[p++] = -20; points[p++] = -20; points[p++] = -20; points[p++] = 0; points[p++] = 20; points[p++] = 0;
        float[] heights = {.25f, 1, 4, 0, 0, -1, -2, -2, -1.25f, 0};
        float x = 20;
        for(int pass = 0; pass < 2; pass++) for(float height : heights) { x += 5; points[p++] = x; points[p++] = height; }
        points[p++] = x + 40; points[p++] = 0; points[p++] = x + 40; points[p++] = -20;
        // Box2D chains are one-sided. The official sample fills this loop in
        // reverse so its collision normals face the car.
        for(int left = 0, right = points.length - 2; left < right; left += 2, right -= 2) {
            float swapX = points[left], swapY = points[left + 1];
            points[left] = points[right]; points[left + 1] = points[right + 1];
            points[right] = swapX; points[right + 1] = swapY;
        }
        addChain(ground, points, true, .6f);
        x += 80;
        addSegmentShape(ground, x, 0, x + 40, 0, 0, .6f, 0);
        x += 40; addSegmentShape(ground, x, 0, x + 10, 5, 0, .6f, 0);
        x += 20; addSegmentShape(ground, x, 0, x + 40, 0, 0, .6f, 0);
        x += 40; addSegmentShape(ground, x, 0, x, 20, 0, .6f, 0);

        B2Body teeter = addDynamicBox(140, 1, 10, .25f);
        teeter.SetAngularVelocity(1.0f);
        B2RevoluteJointDef teeterDef = revoluteDef(ground, teeter, 140, 1);
        teeterDef.SetLowerAngle(radians(-8)); teeterDef.SetUpperAngle(radians(8)); teeterDef.SetEnableLimit(true);
        createRevoluteJoint(teeterDef); release(teeterDef);

        B2Body previous = ground;
        for(int i = 0; i < 20; i++) {
            B2Body plank = createDynamicBody(161 + 2 * i, -.125f, 0);
            addCapsuleShape(plank, -1, 0, 1, 0, .125f, 1, .6f, 0, 0);
            B2RevoluteJointDef def = revoluteDef(previous, plank, 160 + 2 * i, -.125f);
            createRevoluteJoint(def); release(def); previous = plank;
        }
        B2RevoluteJointDef end = revoluteDef(previous, ground, 200, -.125f);
        end.SetEnableMotor(true); end.SetMaxMotorTorque(50); createRevoluteJoint(end); release(end);
        for(int i = 0; i < 5; i++) addDynamicBox(230, .5f + i, .5f, .5f, 0, .25f, .25f, .25f, 0);
        car = new CarAssembly(this, 0, 0, 1, hertz, dampingRatio, torque);
    }

    private B2RevoluteJointDef revoluteDef(B2Body a, B2Body b, float x, float y) {
        B2RevoluteJointDef def = new B2RevoluteJointDef(); B2Vec2 pivot = vector(x, y);
        B2Vec2 la = copyLocalPoint(a, pivot), lb = copyLocalPoint(b, pivot);
        def.SetBodyIdA(a.GetId()); def.SetBodyIdB(b.GetId()); def.SetLocalAnchorA(la); def.SetLocalAnchorB(lb);
        release(lb, la, pivot); return def;
    }

    @Override protected void afterStep(float deltaSeconds) {
        B2Vec2 velocity = car.chassis.GetLinearVelocity(); kph = velocity.GetX() * 3.6f; release(velocity);
    }

    @Override public boolean tracksCameraX() { return true; }

    @Override public float cameraCenterX() {
        B2Vec2 position = car.chassis.GetPosition();
        float x = position.GetX();
        release(position);
        return x;
    }

    @Override public void keyDown(int key) {
        if(key == 'A') { throttle = 1; car.setSpeed(speed); }
        else if(key == 'S') { throttle = 0; car.setSpeed(0); }
        else if(key == 'D') { throttle = -1; car.setSpeed(-speed); }
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Spring Hertz", 0, 20, .1f, () -> hertz,
                        value -> { hertz = value; car.setHertz(value); }),
                Box2DSampleControl.slider("Damping Ratio", 0, 10, .1f, () -> dampingRatio,
                        value -> { dampingRatio = value; car.setDampingRatio(value); }),
                Box2DSampleControl.slider("Speed", 0, 50, .1f, () -> speed,
                        value -> { speed = value; car.setSpeed(throttle * value); }),
                Box2DSampleControl.slider("Torque", 0, 10, .1f, () -> torque,
                        value -> { torque = value; car.setTorque(value); }),
                Box2DSampleControl.dynamicText(() -> "Keys: left = A, brake = S, right = D"),
                Box2DSampleControl.dynamicText(() -> String.format("speed in kph: %.2f", kph)));
    }
}
