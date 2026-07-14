package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Pixel Imperfect sample. */
public final class PixelImperfectSample extends AbstractBox2DSample {
    private final B2Body ball; private float px,vy;
    public PixelImperfectSample(){
        float ppm=30; addStaticBox(175/ppm,150/ppm,20/ppm,10/ppm,0);
        ball=createDynamicBody(200/ppm,275/ppm,0); ball.SetGravityScale(0); ball.SetFixedRotation(true);
        addRoundedBoxShape(ball,4/ppm,4/ppm,.9f/ppm,1,0,0,0); setLinearVelocity(ball,0,-5);
    }
    @Override protected void afterStep(float dt){B2Vec2 p=ball.GetPosition(),v=ball.GetLinearVelocity();px=p.GetX();vy=v.GetY();release(v,p);}
    @Override public List<Box2DSampleControl> controls(){return Collections.singletonList(Box2DSampleControl.dynamicText(()->String.format("p.x = %.9f, v.y = %.9f",px,vy)));}
}
