package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Breakable sample. */
public final class BreakableJointSample extends AbstractBox2DSample {
    private final JointShowcase showcase;
    private final List<float[]> forceLabels = new ArrayList<float[]>();
    private float breakForce = 1000.0f;
    private float gravity = -10.0f;

    public BreakableJointSample() {
        B2Body ground = addGroundSegment(-40.0f, 0.0f, 40.0f, 0.0f);
        showcase = JointShowcase.create(this, ground, -12.5f, 5.0f, 10.0f,
                false, false, true, true, 1000.0f, 2.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        forceLabels.clear();
        for(int i = 0; i < showcase.joints.size(); i++) {
            B2Joint joint = showcase.joints.get(i);
            if(!joint.IsValid()) continue;
            B2Vec2 force = joint.GetConstraintForce();
            float fx = force.GetX(), fy = force.GetY();
            if(fx * fx + fy * fy > breakForce * breakForce) destroyJoint(joint);
            else forceLabels.add(new float[] {-12.5f + 5.0f * i, 13.0f, fx, fy});
            release(force);
        }
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(float[] f : forceLabels) draw.worldText(f[0], f[1], String.format("(%.1f, %.1f)", f[2], f[3]), 0xFFFFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("break force", 0, 10000, .1f, () -> breakForce, value -> breakForce = value),
                Box2DSampleControl.slider("gravity", -50, 50, .1f, () -> gravity,
                        value -> { gravity = value; setGravity(0.0f, value); }));
    }
}
