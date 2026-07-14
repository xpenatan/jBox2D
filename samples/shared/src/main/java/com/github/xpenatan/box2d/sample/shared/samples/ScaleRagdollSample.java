package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Scale Ragdoll sample. */
public final class ScaleRagdollSample extends AbstractBox2DSample {
    private final HumanRagdoll human;
    private float scale = 1.0f;

    public ScaleRagdollSample() {
        addStaticBox(0, -1, 20, 1, 0);
        human = new HumanRagdoll(this, 0, 5, scale, .03f, 1, .5f);
        human.applyAngularImpulse(5.0f);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.slider("Scale", .1f, 10, .01f, () -> scale,
                value -> { scale = value; human.setScale(value); human.applyAngularImpulse(5.0f); }));
    }
}
