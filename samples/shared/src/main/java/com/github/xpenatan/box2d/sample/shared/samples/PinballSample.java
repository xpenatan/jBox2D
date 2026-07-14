package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Continuous / Pinball sample. */
public final class PinballSample extends AbstractBox2DSample {
    private final B2Joint leftJoint,rightJoint; private boolean flip;
    public PinballSample(){
        B2Body ground=createStaticBody(0,0,0);addChain(ground,new float[]{-8,6,-8,20,8,20,8,6,0,-2},true,.6f);
        B2Body left=createDynamicBody(-2,0,0),right=createDynamicBody(2,0,0);left.EnableSleep(false);right.EnableSleep(false);
        addBoxShape(left,1.75f,.2f,1,.6f,0,0);addBoxShape(right,1.75f,.2f,1,.6f,0,0);
        leftJoint=flipper(ground,left,-2,0,radians(-30),radians(5));rightJoint=flipper(ground,right,2,0,radians(-5),radians(30));
        spinner(ground,-4,17);spinner(ground,4,8);
        B2Body bumper1=createStaticBody(-4,8,0);addCircleShape(bumper1,0,0,1,0,.6f,1.5f,0);
        B2Body bumper2=createStaticBody(4,17,0);addCircleShape(bumper2,0,0,1,0,.6f,1.5f,0);
        B2BodyDef def=new B2BodyDef();B2Vec2 p=vector(1,15);def.SetType(B2.DynamicBody());def.SetPosition(p);def.SetIsBullet(true);B2Body ball=createBody(def);addCircleShape(ball,0,0,.2f,1,.6f,0,0);release(p,def);
    }
    private B2Joint flipper(B2Body g,B2Body b,float x,float y,float lo,float hi){B2RevoluteJointDef d=new B2RevoluteJointDef();B2Vec2 a=vector(x,y),z=vector(0,0);d.SetBodyIdA(g.GetId());d.SetBodyIdB(b.GetId());d.SetLocalAnchorA(a);d.SetLocalAnchorB(z);d.SetEnableMotor(true);d.SetMaxMotorTorque(1000);d.SetEnableLimit(true);d.SetLowerAngle(lo);d.SetUpperAngle(hi);B2Joint j=createRevoluteJoint(d);release(z,a,d);return j;}
    private void spinner(B2Body g,float x,float y){B2Body b=createDynamicBody(x,y,0);addBoxShape(b,1.5f,.125f,1,.6f,0,0);addBoxShape(b,.125f,1.5f,1,.6f,0,0);B2RevoluteJointDef d=new B2RevoluteJointDef();B2Vec2 a=vector(x,y),z=vector(0,0);d.SetBodyIdA(g.GetId());d.SetBodyIdB(b.GetId());d.SetLocalAnchorA(a);d.SetLocalAnchorB(z);d.SetEnableMotor(true);d.SetMaxMotorTorque(.1f);createRevoluteJoint(d);release(z,a,d);}
    @Override protected void beforeStep(float dt){leftJoint.RevoluteSetMotorSpeed(flip?20:-10);rightJoint.RevoluteSetMotorSpeed(flip?-20:10);}
    @Override public void keyDown(int key){if(key==' ')flip=true;}@Override public void keyUp(int key){if(key==' ')flip=false;}
}
