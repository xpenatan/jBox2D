package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2DistanceInput;
import com.github.xpenatan.box2d.B2DistanceOutput;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2SimplexCache;
import com.github.xpenatan.box2d.B2Sweep;
import com.github.xpenatan.box2d.B2TOIInput;
import com.github.xpenatan.box2d.B2TOIOutput;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Time of Impact sample. */
public final class TimeOfImpactSample extends AbstractBox2DSample {
    private final B2ShapeProxy proxyA=own(new B2ShapeProxy());
    private final B2ShapeProxy proxyB=own(new B2ShapeProxy());
    private final float[] a=new float[8],b0=new float[4],bhit=new float[4],b1=new float[4];
    private float fraction,distance;
    private int state;

    public TimeOfImpactSample(){
        super(1,0,0);
        add(proxyA,-16.25f,44.75f);add(proxyA,-15.75f,44.75f);add(proxyA,-15.75f,45.25f);add(proxyA,-16.25f,45.25f);
        add(proxyB,0,-0.125f);add(proxyB,0,0.125f);proxyB.SetRadius(0.03f);
    }
    private static void add(B2ShapeProxy p,float x,float y){B2Vec2 v=new B2Vec2(x,y);p.AddPoint(v);release(v);}

    @Override protected void beforeStep(float deltaSeconds){
        B2Sweep sweepA=new B2Sweep();
        setSweep(sweepA,0,0,0,0,0,0);
        B2Sweep sweepB=new B2Sweep();
        float angle1=(float)Math.atan2(0.841092527,-0.540891349);
        float angle2=(float)Math.atan2(0.889056742,-0.457797021);
        setSweep(sweepB,-15.833271f,45.352028f,-15.832434f,45.341305f,angle1,angle2);
        B2TOIInput input=new B2TOIInput();input.SetProxyA(proxyA);input.SetProxyB(proxyB);input.SetSweepA(sweepA);input.SetSweepB(sweepB);input.SetMaxFraction(1);
        B2TOIOutput output=B2Collision.TimeOfImpact(input);fraction=output.GetFraction();state=output.GetState();
        B2Transform transformA=B2Collision.GetSweepTransform(sweepA,0);
        B2Transform transformB0=B2Collision.GetSweepTransform(sweepB,0);
        B2Transform transformBHit=B2Collision.GetSweepTransform(sweepB,fraction);
        B2Transform transformB1=B2Collision.GetSweepTransform(sweepB,1);
        transform(proxyA,transformA,a);transform(proxyB,transformB0,b0);transform(proxyB,transformBHit,bhit);transform(proxyB,transformB1,b1);
        distance=0;
        if(state==B2Collision.TOIHit()){
            B2DistanceInput d=new B2DistanceInput();d.SetProxyA(proxyA);d.SetProxyB(proxyB);
            B2Transform ta=B2Collision.GetSweepTransform(sweepA,fraction);B2Transform tb=B2Collision.GetSweepTransform(sweepB,fraction);
            d.SetTransformA(ta);d.SetTransformB(tb);d.SetUseRadii(false);B2SimplexCache cache=new B2SimplexCache();
            B2DistanceOutput result=B2Collision.ShapeDistance(d,cache);distance=result.GetDistance();release(result,cache,tb,ta,d);
        }
        release(transformB1,transformBHit,transformB0,transformA,output,input,sweepB,sweepA);
    }

    private static void setSweep(B2Sweep sweep,float x1,float y1,float x2,float y2,float a1,float a2){
        B2Vec2 center=new B2Vec2();B2Vec2 c1=new B2Vec2(x1,y1);B2Vec2 c2=new B2Vec2(x2,y2);B2Rot q1=new B2Rot(a1);B2Rot q2=new B2Rot(a2);
        sweep.SetLocalCenter(center);sweep.SetCenter1(c1);sweep.SetCenter2(c2);sweep.SetRotation1(q1);sweep.SetRotation2(q2);
        release(q2,q1,c2,c1,center);
    }
    private static void transform(B2ShapeProxy proxy,B2Transform transform,float[] out){
        for(int i=0;i<proxy.GetPointCount();i++){B2Vec2 p=proxy.GetPoint(i);B2Vec2 w=transform.TransformPoint(p);out[2*i]=w.GetX();out[2*i+1]=w.GetY();release(w,p);}
    }

    @Override public void draw(Box2DSampleDraw draw){
        for(int i=0;i<4;i++){int n=(i+1)%4;draw.segment(a[2*i],a[2*i+1],a[2*n],a[2*n+1],0x808080FF);}
        draw.segment(b0[0],b0[1],b0[2],b0[3],0x00FF00FF);draw.circle(b0[0],b0[1],0.03f,0x00FF00FF);draw.circle(b0[2],b0[3],0.03f,0x00FF00FF);
        draw.segment(bhit[0],bhit[1],bhit[2],bhit[3],0xFFA500FF);
        draw.segment(b1[0],b1[1],b1[2],b1[3],0xFF0000FF);draw.circle(b1[0],b1[1],0.03f,0xFF0000FF);draw.circle(b1[2],b1[3],0.03f,0xFF0000FF);
    }
    @Override public List<Box2DSampleControl> controls(){return Arrays.asList(
            Box2DSampleControl.dynamicText(()->String.format("toi = %.8f, state = %d, distance = %.8f",fraction,state,distance)));}
}
