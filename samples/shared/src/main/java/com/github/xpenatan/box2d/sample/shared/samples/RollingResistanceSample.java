package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Rolling Resistance sample. */
public final class RollingResistanceSample extends AbstractBox2DSample {
    private final List<B2Body> sceneBodies = new ArrayList<B2Body>();
    private final float resistanceScale = 0.02f;
    private float lift;

    public RollingResistanceSample() {
        createScene();
    }

    private void createScene() {
        for(B2Body body : sceneBodies) destroyBody(body);
        sceneBodies.clear();
        for(int i = 0; i < 20; i++) {
            B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
            addSegmentShape(ground, -40.0f, 2.0f * i, 40.0f, 2.0f * i + lift, 0, .6f, 0);
            sceneBodies.add(ground);

            B2BodyDef def = new B2BodyDef();
            B2Vec2 position = vector(-39.5f, 2.0f * i + 0.75f);
            B2Vec2 velocity = vector(5.0f, 0.0f);
            def.SetType(com.github.xpenatan.box2d.B2.DynamicBody());
            def.SetPosition(position);
            def.SetAngularVelocity(-10.0f);
            def.SetLinearVelocity(velocity);
            B2Body ball = createBody(def);
            addCircleShape(ball, 0, 0, .5f, 1, .6f, 0, resistanceScale * i);
            sceneBodies.add(ball);
            release(velocity, position, def);
        }
    }

    @Override
    public void keyDown(int key) {
        if(key == '1') { lift = 0.0f; createScene(); }
        else if(key == '2') { lift = 5.0f; createScene(); }
        else if(key == '3') { lift = -5.0f; createScene(); }
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(int i = 0; i < 20; i++) draw.worldText(-41.5f, 2.0f * i + 1.0f,
                String.format(java.util.Locale.US, "%.2f", resistanceScale * i), 0xFFFFFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("Level (1)", () -> { lift = 0; createScene(); }),
                Box2DSampleControl.button("Uphill (2)", () -> { lift = 5; createScene(); }),
                Box2DSampleControl.button("Downhill (3)", () -> { lift = -5; createScene(); }));
    }
}
