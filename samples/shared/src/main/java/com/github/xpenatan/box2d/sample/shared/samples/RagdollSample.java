package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Ragdoll sample. */
public final class RagdollSample extends AbstractBox2DSample {
    private HumanRagdoll human;
    private float friction = .03f;
    private float hertz = 5.0f;
    private float damping = .5f;

    public RagdollSample() {
        addGroundSegment(-20, 0, 20, 0);
        human = new HumanRagdoll(this, 0, 25, 1, friction, hertz, damping);
        world().SetContactTuning(240, 0, 2);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Friction", 0, 1, .01f, () -> friction,
                        value -> { friction = value; human.setFrictionTorque(value); }),
                Box2DSampleControl.slider("Hertz", 0, 10, .1f, () -> hertz,
                        value -> { hertz = value; human.setHertz(value); }),
                Box2DSampleControl.slider("Damping", 0, 4, .1f, () -> damping,
                        value -> { damping = value; human.setDampingRatio(value); }),
                Box2DSampleControl.button("Respawn", human::respawn));
    }
}
