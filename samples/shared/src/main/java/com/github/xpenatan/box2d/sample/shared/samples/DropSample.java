package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Continuous / Drop sample. */
public final class DropSample extends AbstractBox2DSample {
    private B2Body ground; private final List<B2Body> objects=new ArrayList<B2Body>();
    private HumanRagdoll human; private int scene=1; private boolean continuous=true; private boolean speculative=true;
    private int frameSkip; private int frameCount; private boolean stepThisFrame=true;
    public DropSample(){setInitialSleepEnabled(false);scene1();}
    private void clearDynamic(){if(human!=null){human.destroy();human=null;}for(B2Body b:objects)destroyBody(b);objects.clear();}
    private void clear(){clearDynamic();if(ground!=null)destroyBody(ground);ground=null;}
    private void segmentGround(boolean wall){ground=createStaticBody(0,0,0);addSegmentShape(ground,-5,0,5,0,0,.6f,0);if(wall)addSegmentShape(ground,3,0,3,8,0,.6f,0);}
    private void tiledGround(){ground=createStaticBody(0,0,0);for(int i=0;i<=40;i++)addOffsetBoxShape(ground,.125f,.05f,-5+.25f*i,0,0,0,.6f,0,0);}
    private void scene1(){clear();scene=1;tiledGround();B2Body b=addDynamicCircle(0,4,.125f);setLinearVelocity(b,0,-100);objects.add(b);frameCount=1;}
    private void scene2(){clear();scene=2;segmentGround(false);B2Body b=addDynamicBox(0,4,.75f,.01f,.5f*PI);b.SetAngularVelocity(-.5f);objects.add(b);frameCount=1;}
    private void scene3(){clear();scene=3;tiledGround();human=new HumanRagdoll(this,0,40,1,.03f,1,.5f);frameCount=1;}
    private void scene4(){clear();scene=4;segmentGround(true);for(int i=0;i<5;i++){float shift=i%2==0?-.01f:.01f;objects.add(addDynamicBox(2.5f+shift,.25f+.5f*i,.25f,.25f));}
        B2BodyDef def=new B2BodyDef();B2Vec2 p=vector(-7.7f,1.9f),v=vector(200,0);def.SetType(B2.DynamicBody());def.SetPosition(p);def.SetLinearVelocity(v);def.SetIsBullet(true);B2Body b=createBody(def);addCircleShape(b,0,0,.125f,4,.6f,0,0);objects.add(b);release(v,p,def);frameCount=1;}
    @Override protected void beforeStep(float dt){world().EnableContinuous(continuous);stepThisFrame=frameSkip==0||frameCount%frameSkip==0;frameCount++;}
    @Override protected boolean shouldStepWorld(float dt){return stepThisFrame;}
    private void setContinuous(boolean value){if(continuous!=value){clearDynamic();continuous=value;}}
    private void setSpeculative(boolean value){if(speculative!=value){clearDynamic();speculative=value;world().EnableSpeculative(value);}}
    private void toggleSlowTime(){frameSkip=frameSkip>0?0:60;}
    @Override public void keyDown(int key){if(key=='1')scene1();else if(key=='2')scene2();else if(key=='3')scene3();else if(key=='4')scene4();
        else if(key=='C')setContinuous(!continuous);else if(key=='V')setSpeculative(!speculative);else if(key=='S')toggleSlowTime();}
    @Override public List<Box2DSampleControl> controls(){ArrayList<Box2DSampleControl> c=new ArrayList<Box2DSampleControl>();
        c.add(Box2DSampleControl.button("1 Ball",this::scene1));c.add(Box2DSampleControl.button("2 Ruler",this::scene2));c.add(Box2DSampleControl.button("3 Ragdoll",this::scene3));c.add(Box2DSampleControl.button("4 Bullet",this::scene4));
        c.add(Box2DSampleControl.checkbox("Continuous",()->continuous?1:0,v->setContinuous(v!=0)));
        c.add(Box2DSampleControl.checkbox("Speculative",()->speculative?1:0,v->setSpeculative(v!=0)));
        c.add(Box2DSampleControl.checkbox("Slow Time",()->frameSkip>0?1:0,v->{if((v!=0)!=(frameSkip>0))toggleSlowTime();}));
        c.add(Box2DSampleControl.dynamicText(()->"Scene "+scene));return c;}
}
