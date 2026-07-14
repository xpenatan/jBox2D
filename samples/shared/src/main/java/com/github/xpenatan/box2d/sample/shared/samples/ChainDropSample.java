package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Chain Drop sample. */
public final class ChainDropSample extends AbstractBox2DSample {
    private B2Body body;
    private float yOffset = -.1f;
    private float speed = -42;

    public ChainDropSample() {
        B2Body ground = createStaticBody(0, -6, 0);
        addChain(ground, new float[] {-10, -2, 10, -2, 10, 1, -10, 1}, true, .6f);
        launch();
    }

    private void launch() {
        if(body != null) destroyBody(body);
        B2BodyDef def = new B2BodyDef(); B2Vec2 position = vector(0, 10 + yOffset), velocity = vector(0, speed);
        def.SetType(B2.DynamicBody()); def.SetPosition(position); def.SetLinearVelocity(velocity);
        def.SetAngle(.5f * PI); def.SetFixedRotation(true);
        body = createBody(def); addCircleShape(body, 0, 0, .5f, 1, .6f, 0, 0);
        release(velocity, position, def);
    }

    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Speed", -100, 0, 1, () -> speed, value -> speed = value),
                Box2DSampleControl.slider("Y Offset", -1, 1, .1f, () -> yOffset, value -> yOffset = value),
                Box2DSampleControl.button("Launch", this::launch));
    }
}
