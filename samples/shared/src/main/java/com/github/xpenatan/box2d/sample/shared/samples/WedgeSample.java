package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;

/** Java port of Box2D 3.1.1's Continuous / Wedge sample. */
public final class WedgeSample extends AbstractBox2DSample {
    public WedgeSample(){B2Body g=createStaticBody(0,0,0);addSegmentShape(g,-4,8,0,0,0,.6f,0);addSegmentShape(g,0,0,0,8,0,.6f,0);B2Body b=addDynamicCircle(-.45f,10.75f,.3f,1,.2f,0,0);setLinearVelocity(b,0,-200);}
}
