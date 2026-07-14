package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Joints / Fixed Rotation sample. */
public final class FixedRotationSample extends AbstractBox2DSample {
    private final JointShowcase showcase;
    private boolean fixedRotation = true;

    public FixedRotationSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        showcase = JointShowcase.create(this, ground, -12.5f, 5.0f, 10.0f,
                fixedRotation, true, false, true, 200.0f, 1.0f);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.checkbox("Fixed Rotation", () -> fixedRotation ? 1 : 0,
                value -> {
                    fixedRotation = value != 0;
                    for(B2Body body : showcase.bodies) body.SetFixedRotation(fixedRotation);
                }));
    }
}
