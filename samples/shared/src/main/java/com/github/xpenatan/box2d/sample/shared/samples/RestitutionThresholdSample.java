package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Restitution Threshold sample. */
public final class RestitutionThresholdSample extends AbstractBox2DSample {
    private final B2Body ball; private float px,vy;
    public RestitutionThresholdSample(){
        float ppm=30; world().SetRestitutionThreshold(.1f);
        addStaticBox(205/ppm,120/ppm,50/ppm,5/ppm,radians(70));
        ball=addDynamicCircle(200/ppm,250/ppm,5/ppm,1,0,1,0); ball.SetFixedRotation(true); setLinearVelocity(ball,0,-2.9f);
    }
    @Override protected void afterStep(float dt){B2Vec2 p=ball.GetPosition(),v=ball.GetLinearVelocity();px=p.GetX();vy=v.GetY();release(v,p);}
    @Override public List<Box2DSampleControl> controls(){return Collections.singletonList(Box2DSampleControl.dynamicText(()->String.format("p.x = %.9f, v.y = %.9f",px,vy)));}
}
