package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2CastResult;
import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2DistanceInput;
import com.github.xpenatan.box2d.B2DistanceOutput;
import com.github.xpenatan.box2d.B2ShapeCastPairInput;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2SimplexCache;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Shape Cast sample. */
public final class ShapeCastSample extends AbstractBox2DSample {
    private B2ShapeProxy proxyA=own(CollisionSampleSupport.makeProxy(CollisionSampleSupport.BOX,0));
    private B2ShapeProxy proxyB=own(CollisionSampleSupport.makeProxy(CollisionSampleSupport.POINT,0.2f));
    private int typeA=CollisionSampleSupport.BOX,typeB=CollisionSampleSupport.POINT;
    private float radiusA,radiusB=0.2f;
    private float x=-0.6f,y,angle,translationX=2,translationY;
    private boolean showIndices,encroach,dragging,sweeping,rotating;
    private float startX,startY,baseX,baseY,baseAngle;
    private boolean hit;
    private float fraction,hitX,hitY,normalX,normalY,distance;
    private int iterations;

    public ShapeCastSample(){super(1,0,0);}

    @Override protected void beforeStep(float deltaSeconds){
        proxyA.SetRadius(radiusA);proxyB.SetRadius(radiusB);
        B2Transform identity=CollisionSampleSupport.transform(0,0,0);
        B2Transform transformB=CollisionSampleSupport.transform(x,y,angle);
        B2Vec2 translation=new B2Vec2(translationX,translationY);
        B2ShapeCastPairInput input=new B2ShapeCastPairInput();
        input.SetProxyA(proxyA);input.SetProxyB(proxyB);input.SetTransformA(identity);input.SetTransformB(transformB);
        input.SetTranslationB(translation);input.SetMaxFraction(1);input.SetCanEncroach(encroach);
        B2CastResult output=B2Collision.ShapeCast(input);
        hit=output.GetHit();fraction=output.GetFraction();iterations=output.GetIterations();
        B2Vec2 point=output.GetPoint(),normal=output.GetNormal();
        hitX=point.GetX();hitY=point.GetY();normalX=normal.GetX();normalY=normal.GetY();
        B2Transform impact=CollisionSampleSupport.transform(x+fraction*translationX,y+fraction*translationY,angle);
        B2DistanceInput distanceInput=new B2DistanceInput();
        distanceInput.SetProxyA(proxyA);distanceInput.SetProxyB(proxyB);distanceInput.SetTransformA(identity);
        distanceInput.SetTransformB(impact);distanceInput.SetUseRadii(false);
        B2SimplexCache cache=new B2SimplexCache();
        B2DistanceOutput distanceOutput=B2Collision.ShapeDistance(distanceInput,cache);
        distance=distanceOutput.GetDistance();
        release(distanceOutput,cache,distanceInput,impact,normal,point,output,input,translation,transformB,identity);
    }

    @Override public void mouseDown(float px,float py,int button,int modifiers){
        if(button!=0)return;startX=px;startY=py;
        if((modifiers&1)!=0){rotating=true;baseAngle=angle;}
        else if((modifiers&2)!=0){sweeping=true;}
        else{dragging=true;baseX=x;baseY=y;}
    }
    @Override public void mouseMove(float px,float py){
        if(dragging){x=baseX+0.5f*(px-startX);y=baseY+0.5f*(py-startY);}
        else if(rotating)angle=CollisionSampleSupport.clamp(baseAngle+px-startX,-PI,PI);
        else if(sweeping){translationX=px-startX;translationY=py-startY;}
    }
    @Override public void mouseUp(float x,float y,int button){dragging=sweeping=rotating=false;}

    @Override public void draw(Box2DSampleDraw draw){
        CollisionSampleSupport.drawProxy(draw,proxyA,0,0,0,0x00FFFFFF,showIndices);
        CollisionSampleSupport.drawProxy(draw,proxyB,x,y,angle,0x90EE90FF,showIndices);
        CollisionSampleSupport.drawProxy(draw,proxyB,x+translationX,y+translationY,angle,0xCD5C5CFF,false);
        if(hit){
            CollisionSampleSupport.drawProxy(draw,proxyB,x+fraction*translationX,y+fraction*translationY,angle,0xDDA0DDFF,false);
            draw.point(hitX,hitY,6,fraction>0?0xFFFFFFFF:0xCD853FFF);
            if(fraction>0)draw.segment(hitX,hitY,hitX+0.5f*normalX,hitY+0.5f*normalY,0xFFFF00FF);
        }
    }

    private void setTypeA(float v){typeA=(int)v;release(proxyA);proxyA=own(CollisionSampleSupport.makeProxy(typeA,radiusA));}
    private void setTypeB(float v){typeB=(int)v;release(proxyB);proxyB=own(CollisionSampleSupport.makeProxy(typeB,radiusB));}
    @Override public List<Box2DSampleControl> controls(){return Arrays.asList(
            Box2DSampleControl.combo("shape A",CollisionSampleSupport.SHAPE_NAMES,()->typeA,this::setTypeA),
            Box2DSampleControl.slider("radius A",0,0.5f,0.01f,()->radiusA,v->radiusA=v),
            Box2DSampleControl.combo("shape B",CollisionSampleSupport.SHAPE_NAMES,()->typeB,this::setTypeB),
            Box2DSampleControl.slider("radius B",0,0.5f,0.01f,()->radiusB,v->radiusB=v),
            Box2DSampleControl.slider("x offset",-2,2,0.01f,()->x,v->x=v),
            Box2DSampleControl.slider("y offset",-2,2,0.01f,()->y,v->y=v),
            Box2DSampleControl.slider("angle",-PI,PI,0.01f,()->angle,v->angle=v),
            Box2DSampleControl.checkbox("show indices",()->showIndices?1:0,v->showIndices=v!=0),
            Box2DSampleControl.checkbox("encroach",()->encroach?1:0,v->encroach=v!=0),
            Box2DSampleControl.dynamicText(()->String.format("hit = %s, iterations = %d, fraction = %.3f, distance = %.3f",hit,iterations,fraction,distance)),
            Box2DSampleControl.text("mouse 1: drag; shift: rotate; control: sweep"));}
}
