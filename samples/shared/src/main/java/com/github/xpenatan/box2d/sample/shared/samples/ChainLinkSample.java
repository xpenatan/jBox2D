package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Chain Link sample. */
public final class ChainLinkSample extends AbstractBox2DSample {
    public ChainLinkSample() {
        float[] points1 = { 40, 1, 0, 0, -40, 0, -40, -1, 0, -1, 40, -1 };
        float[] points2 = { -40, -1, 0, -1, 40, -1, 40, 0, 0, 0, -40, 0 };
        B2Body ground = createStaticBody(0, 0, 0);
        addChain(ground, points1, false, 0.6f);
        addChain(ground, points2, false, 0.6f);
        addDynamicCircle(-5, 2, .5f);
        B2Body capsule = createDynamicBody(0, 2, 0);
        addCapsuleShape(capsule, -.5f, 0, .5f, 0, .25f, 1, .6f, 0, 0);
        addDynamicBox(5, 2, .5f, .5f);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.text("Two open chain shapes are linked smoothly"));
    }
}
