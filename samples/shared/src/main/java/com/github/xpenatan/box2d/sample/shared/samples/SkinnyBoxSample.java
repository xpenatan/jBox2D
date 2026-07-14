package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Skinny Box sample. */
public final class SkinnyBoxSample extends AbstractBox2DSample {
    private B2Body body;
    private B2Body bullet;
    private boolean capsule;
    private boolean autoTest;
    private int steps;

    public SkinnyBoxSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addSegmentShape(ground, -10, 0, 10, 0, 0, .9f, 0);
        addOffsetBoxShape(ground, .1f, 1, 0, 1, 0, 0, .9f, 0, 0);
        launch();
    }

    private void launch() {
        if(body != null) destroyBody(body);
        if(bullet != null) { destroyBody(bullet); bullet = null; }
        B2BodyDef def = new B2BodyDef(); B2Vec2 p = vector(0, 8), v = vector(0, -100);
        def.SetType(B2.DynamicBody()); def.SetPosition(p); def.SetLinearVelocity(v); def.SetAngularVelocity(randomFloat(-50, 50));
        body = createBody(def);
        if(capsule) addCapsuleShape(body, 0, -1, 0, 1, .1f, 1, .9f, 0, 0);
        else addBoxShape(body, 2, .05f, 1, .9f, 0, 0);
        release(v, p, def);
    }

    @Override protected void afterStep(float deltaSeconds) { if(autoTest && ++steps % 60 == 0) launch(); }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.checkbox("Capsule", () -> capsule ? 1 : 0, value -> capsule = value != 0),
                Box2DSampleControl.button("Launch", this::launch),
                Box2DSampleControl.checkbox("Auto Test", () -> autoTest ? 1 : 0, value -> autoTest = value != 0));
    }
}
