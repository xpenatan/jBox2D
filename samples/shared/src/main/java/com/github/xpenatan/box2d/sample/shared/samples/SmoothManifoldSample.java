package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2ChainSegment;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2Manifold;
import com.github.xpenatan.box2d.B2ManifoldPoint;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2SimplexCache;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Smooth Manifold sample. */
public final class SmoothManifoldSample extends AbstractBox2DSample {
    private static final float[] POINTS = {
        -20.58325f,14.54175f, -21.90625f,15.8645f, -24.552f,17.1875f, -27.198f,11.89575f,
        -29.84375f,15.8645f, -29.84375f,21.15625f, -25.875f,23.802f, -20.58325f,25.125f,
        -25.875f,29.09375f, -20.58325f,31.7395f, -11.009f,23.229f, -8.677f,21.15625f,
        -6.03125f,21.15625f, -7.35425f,29.09375f, -3.3855f,29.09375f, 1.90625f,30.41675f,
        5.875f,17.1875f, 11.16675f,25.125f, 9.84375f,29.09375f, 13.8125f,31.7395f,
        21.75f,30.41675f, 28.364498f,26.448f, 25.71875f,18.5105f, 24.395748f,13.21875f,
        17.78125f,11.89575f, 15.1355f,7.927f, 5.875f,9.25f, 1.90625f,11.89575f,
        -3.25f,11.89575f, -3.25f,9.9375f, -4.70825f,9.25f, -8.677f,9.25f,
        -11.323f,11.89575f, -13.96875f,11.89575f, -15.29175f,14.54175f, -19.2605f,14.54175f
    };
    private final ArrayList<float[]> contacts = new ArrayList<float[]>();
    private int shapeType = 1;
    private float x;
    private float y = 20.0f;
    private float angle;
    private float round;
    private boolean showIds;
    private boolean showAnchors;
    private boolean showSeparation;
    private boolean dragging;
    private boolean rotating;
    private float startX,startY,baseX,baseY,baseAngle;

    public SmoothManifoldSample() { super(1,0,0); }

    @Override protected void beforeStep(float deltaSeconds) {
        contacts.clear();
        B2Transform identity = CollisionSampleSupport.transform(0,0,0);
        B2Transform transform = CollisionSampleSupport.transform(x,y,angle);
        B2Vec2 zero = new B2Vec2();
        B2Circle circle = new B2Circle(zero,0.5f);
        B2Polygon rounded = B2Polygon.CreateRoundedBox(Math.max(0.1f,0.5f-round),Math.max(0.1f,0.5f-round),round);
        int count=POINTS.length/2;
        for(int i=0;i<count;i++) {
            int i0=(i+count-1)%count,i1=i,i2=(i+1)%count,i3=(i+2)%count;
            B2Vec2 g1=point(i0),p1=point(i1),p2=point(i2),g2=point(i3);
            B2Segment edge=new B2Segment(p1,p2);
            B2ChainSegment chain=new B2ChainSegment(g1,edge,g2);
            B2Manifold manifold;
            if(shapeType==0) manifold=B2Collision.CollideChainSegmentAndCircle(chain,identity,circle,transform);
            else {
                B2SimplexCache cache=new B2SimplexCache();
                manifold=B2Collision.CollideChainSegmentAndPolygon(chain,identity,rounded,transform,cache);
                release(cache);
            }
            capture(manifold);
            release(chain,edge,g2,p2,p1,g1);
        }
        release(rounded,circle,zero,transform,identity);
    }

    private static B2Vec2 point(int index) { return new B2Vec2(POINTS[2*index],POINTS[2*index+1]); }
    private void capture(B2Manifold manifold) {
        B2Vec2 normal=manifold.GetNormal();
        for(int i=0;i<manifold.GetPointCount();i++) {
            B2ManifoldPoint p=manifold.GetPoint(i); B2Vec2 point=p.GetPoint();
            contacts.add(new float[]{point.GetX(),point.GetY(),normal.GetX(),normal.GetY(),p.GetId(),p.GetSeparation()});
            release(point,p);
        }
        release(normal,manifold);
    }

    @Override public void mouseDown(float px,float py,int button,int modifiers) {
        if(button!=0)return; startX=px;startY=py;
        if((modifiers&1)!=0){rotating=true;baseAngle=angle;}else{dragging=true;baseX=x;baseY=y;}
    }
    @Override public void mouseMove(float px,float py){
        if(dragging){x=baseX+px-startX;y=baseY+py-startY;}else if(rotating)angle=CollisionSampleSupport.clamp(baseAngle+px-startX,-PI,PI);
    }
    @Override public void mouseUp(float x,float y,int button){dragging=rotating=false;}

    @Override public void draw(Box2DSampleDraw draw) {
        int count=POINTS.length/2;
        for(int i=0;i<count;i++) {
            int next=(i+1)%count;
            draw.segment(POINTS[2*i],POINTS[2*i+1],POINTS[2*next],POINTS[2*next+1],0xFFFF00FF);
            draw.point(POINTS[2*i],POINTS[2*i+1],4,0xFFFF00FF);
        }
        if(shapeType==0) draw.circle(x,y,0.5f,0xFF00FFFF);
        else {
            B2Polygon polygon=B2Polygon.CreateRoundedBox(Math.max(0.1f,0.5f-round),Math.max(0.1f,0.5f-round),round);
            B2ShapeProxy proxy=new B2ShapeProxy();proxy.SetPolygon(polygon);
            CollisionSampleSupport.drawProxy(draw,proxy,x,y,angle,0xFF00FFFF,false);
            release(proxy,polygon);
        }
        for(float[] p:contacts) {
            draw.point(p[0],p[1],showAnchors?7:5,0x00FF00FF);
            draw.segment(p[0],p[1],p[0]+0.5f*p[2],p[1]+0.5f*p[3],0xFFFFFFFF);
            if(showIds)draw.worldText(p[0]+0.04f,p[1]-0.04f,"0x"+Integer.toHexString((int)p[4]),0xFFFFFFFF);
            if(showSeparation)draw.worldText(p[0]+0.04f,p[1]+0.04f,String.format("%.3f",p[5]),0xFFFFFFFF);
        }
    }

    private void reset(){x=0;y=20;angle=0;}
    @Override public List<Box2DSampleControl> controls(){return Arrays.asList(
            Box2DSampleControl.combo("Shape",new String[]{"Circle","Box"},()->shapeType,v->shapeType=(int)v),
            Box2DSampleControl.slider("x Offset",-30,30,0.01f,()->x,v->x=v),
            Box2DSampleControl.slider("y Offset",0,35,0.01f,()->y,v->y=v),
            Box2DSampleControl.slider("Angle",-PI,PI,0.01f,()->angle,v->angle=v),
            Box2DSampleControl.slider("Round",0,0.4f,0.1f,()->round,v->round=v),
            Box2DSampleControl.checkbox("Show Ids",()->showIds?1:0,v->showIds=v!=0),
            Box2DSampleControl.checkbox("Show Separation",()->showSeparation?1:0,v->showSeparation=v!=0),
            Box2DSampleControl.checkbox("Show Anchors",()->showAnchors?1:0,v->showAnchors=v!=0),
            Box2DSampleControl.button("Reset",this::reset),
            Box2DSampleControl.dynamicText(()->"contacts = "+contacts.size()),
            Box2DSampleControl.text("mouse 1: drag; shift + mouse 1: rotate"));}
}
