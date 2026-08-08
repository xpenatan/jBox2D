package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Bounce Humans sample. */
public final class BounceHumansSample extends AbstractBox2DSample {
    private final List<HumanRagdoll> humans = new ArrayList<HumanRagdoll>();
    private float countdown;
    private float time;
    private float gravityX;
    private float gravityY;

    public BounceHumansSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        B2Shape s;
        s = addSegmentShape(ground, -10, -10, 10, -10, 0, .1f, 1.3f);
        s = addSegmentShape(ground, 10, -10, 10, 10, 0, .1f, 1.3f);
        s = addSegmentShape(ground, 10, 10, -10, 10, 0, .1f, 1.3f);
        s = addSegmentShape(ground, -10, 10, -10, -10, 0, .1f, 1.3f);
        addCircleShape(ground, 0, 0, 2, 0, .1f, 2, 0);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        if(humans.size() < 5 && countdown <= 0) {
            humans.add(new HumanRagdoll(this, 0, 5, 1, 0, 1, .1f));
            countdown = 2;
        }
        B2Rot halfTimeRotation = new B2Rot(0.5f * time);
        B2Rot timeRotation = new B2Rot(time);
        gravityX = 10.0f * halfTimeRotation.GetSine();
        gravityY = 10.0f * timeRotation.GetCosine();
        release(timeRotation, halfTimeRotation);
        setGravity(gravityX, gravityY);
        // The upstream stress test intentionally advances its animation at a
        // fixed 60 Hz, independently of the testbed's selected simulation rate.
        time += 1.0f / 60.0f;
        countdown -= 1.0f / 60.0f;
    }

    @Override public void draw(Box2DSampleDraw draw) {
        draw.segment(0, 0, .3f * gravityX, .3f * gravityY, 0xFFFFFFFF);
    }
}
