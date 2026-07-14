package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2ChainSegment;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2Hull;
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

/** Java port of Box2D 3.1.1's Collision / Manifold sample. */
public final class ManifoldSample extends AbstractBox2DSample {
    private interface ManifoldCall { B2Manifold call(B2Transform transformA, B2Transform transformB); }
    private static final class Snapshot {
        float labelX, labelY;
        int count;
        final float[] x = new float[2], y = new float[2], nx = new float[2], ny = new float[2];
        final float[] separation = new float[2];
        final int[] id = new int[2];
    }

    private final ArrayList<Snapshot> contacts = new ArrayList<Snapshot>();
    private final B2SimplexCache chainCache1 = own(new B2SimplexCache());
    private final B2SimplexCache chainCache2 = own(new B2SimplexCache());
    private float x = 0.17f;
    private float y = 1.12f;
    private float angle;
    private float round = 0.1f;
    private boolean showCount;
    private boolean showIds;
    private boolean showSeparation;
    private boolean showAnchors;
    private boolean enableCaching = true;
    private boolean dragging;
    private boolean rotating;
    private float startX, startY, baseX, baseY, baseAngle;

    public ManifoldSample() {
        super(1, 0.0f, 0.0f);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        contacts.clear();
        if(!enableCaching) { chainCache1.Clear(); chainCache2.Clear(); }
        collideRowOne();
        collideRowTwo();
        collideChains();
    }

    private void collideRowOne() {
        B2Circle c05 = circle(0.5f), c10 = circle(1.0f);
        at(-10,-5,(a,b)->B2Collision.CollideCircles(c05,a,c10,b));

        B2Capsule cap1 = capsule(-0.5f, 0, 0.5f, 0, 0.25f);
        at(-6,-5,(a,b)->B2Collision.CollideCapsuleAndCircle(cap1,a,c05,b));

        B2Segment segment = segment(-1,0,1,0);
        at(-2,-5,(a,b)->B2Collision.CollideSegmentAndCircle(segment,a,c05,b));

        B2Polygon square = B2Polygon.CreateSquare(0.5f); square.SetRadius(round);
        at(2,-5,(a,b)->B2Collision.CollidePolygonAndCircle(square,a,c05,b));

        B2Capsule cap2 = capsule(0.25f,0,1,0,0.1f);
        at(6,-5,(a,b)->B2Collision.CollideCapsules(cap1,a,cap2,b));
        at(10,-5,(a,b)->B2Collision.CollideSegmentAndCapsule(segment,a,cap1,b));
        release(cap2, square, segment, cap1, c10, c05);
    }

    private void collideRowTwo() {
        B2Polygon square = B2Polygon.CreateSquare(0.5f);
        at(-10,0,(a,b)->B2Collision.CollidePolygons(square,a,square,b));
        B2Polygon bar = B2Polygon.CreateBox(2.0f, 0.1f);
        B2Polygon small = B2Polygon.CreateSquare(0.25f);
        at(-6,0,(a,b)->B2Collision.CollidePolygons(bar,a,small,b));
        float h = Math.max(0.1f, 0.5f - round);
        B2Polygon rounded = B2Polygon.CreateRoundedBox(h, h, round);
        at(-2,0,(a,b)->B2Collision.CollidePolygons(square,a,rounded,b));
        B2Segment segment = segment(-1,0,1,0);
        at(2,0,(a,b)->B2Collision.CollideSegmentAndPolygon(segment,a,rounded,b));
        B2Polygon wedge = polygon(new float[]{-0.1f,-0.5f, 0.1f,-0.5f, 0,0.5f}, round);
        at(6,0,(a,b)->B2Collision.CollidePolygons(wedge,a,wedge,b));
        release(wedge, segment, rounded, small, bar, square);
    }

    private void collideChains() {
        B2Vec2 g1 = new B2Vec2(2,1), p1 = new B2Vec2(1,1), p2 = new B2Vec2(-1,0), g2 = new B2Vec2(-2,0);
        B2Segment edge = new B2Segment(p1,p2);
        B2ChainSegment chain = new B2ChainSegment(g1, edge, g2);
        B2Circle circle = circle(0.5f);
        at(-10,5,(a,b)->B2Collision.CollideChainSegmentAndCircle(chain,a,circle,b));
        B2Polygon rounded = B2Polygon.CreateRoundedBox(Math.max(0.1f,0.5f-round), Math.max(0.1f,0.5f-round), round);
        at(-2,5,(a,b)->B2Collision.CollideChainSegmentAndPolygon(chain,a,rounded,b,chainCache1));
        B2Capsule capsule = capsule(-0.5f,0,0.5f,0,0.25f);
        at(6,5,(a,b)->B2Collision.CollideChainSegmentAndCapsule(chain,a,capsule,b,chainCache2));
        release(capsule, rounded, circle, chain, edge, g2, p2, p1, g1);
    }

    private void capture(B2Manifold manifold, float labelX, float labelY) {
        Snapshot snapshot = new Snapshot(); snapshot.labelX = labelX; snapshot.labelY = labelY;
        snapshot.count = manifold.GetPointCount();
        B2Vec2 normal = manifold.GetNormal();
        for(int i = 0; i < snapshot.count; i++) {
            B2ManifoldPoint point = manifold.GetPoint(i); B2Vec2 p = point.GetPoint();
            snapshot.x[i] = p.GetX(); snapshot.y[i] = p.GetY();
            snapshot.nx[i] = normal.GetX(); snapshot.ny[i] = normal.GetY();
            snapshot.separation[i] = point.GetSeparation(); snapshot.id[i] = point.GetId();
            release(p, point);
        }
        contacts.add(snapshot);
        release(normal, manifold);
    }

    private void at(float labelX, float labelY, ManifoldCall call) {
        B2Transform transformA = transform(labelX, labelY, 0.0f);
        B2Transform transformB = transform(labelX + x, labelY + y, angle);
        capture(call.call(transformA, transformB), labelX, labelY);
        release(transformB, transformA);
    }

    private static B2Circle circle(float radius) { B2Vec2 p = new B2Vec2(); B2Circle c = new B2Circle(p,radius); release(p); return c; }
    private static B2Capsule capsule(float x1,float y1,float x2,float y2,float radius) {
        B2Vec2 p1 = new B2Vec2(x1,y1), p2 = new B2Vec2(x2,y2); B2Capsule c = new B2Capsule(p1,p2,radius); release(p2,p1); return c;
    }
    private static B2Segment segment(float x1,float y1,float x2,float y2) {
        B2Vec2 p1 = new B2Vec2(x1,y1), p2 = new B2Vec2(x2,y2); B2Segment s = new B2Segment(p1,p2); release(p2,p1); return s;
    }
    private static B2Polygon polygon(float[] vertices, float radius) {
        B2Hull hull = new B2Hull();
        for(int i=0;i<vertices.length;i+=2) { B2Vec2 p = new B2Vec2(vertices[i],vertices[i+1]); hull.AddPoint(p); release(p); }
        hull.Compute(); B2Polygon polygon = B2Polygon.CreateFromHull(hull,radius); release(hull); return polygon;
    }
    private static B2Transform transform(float x,float y,float angle) { return CollisionSampleSupport.transform(x,y,angle); }

    @Override public void mouseDown(float px,float py,int button,int modifiers) {
        if(button != 0) return; startX=px; startY=py;
        if((modifiers&1)!=0) { rotating=true; baseAngle=angle; }
        else { dragging=true; baseX=x; baseY=y; }
    }
    @Override public void mouseMove(float px,float py) {
        if(dragging) { x=baseX+0.5f*(px-startX); y=baseY+0.5f*(py-startY); }
        else if(rotating) angle=CollisionSampleSupport.clamp(baseAngle+px-startX,-PI,PI);
    }
    @Override public void mouseUp(float x,float y,int button) { dragging=rotating=false; }

    @Override public void draw(Box2DSampleDraw draw) {
        float[][] positions={{-10,-5},{-6,-5},{-2,-5},{2,-5},{6,-5},{10,-5},{-10,0},{-6,0},{-2,0},{2,0},{6,0},{-10,5},{-2,5},{6,5}};
        for(float[] p:positions) {
            draw.circle(p[0],p[1],0.5f,0x7FFFD4FF);
            draw.circle(p[0]+x,p[1]+y,0.5f,0xEEE8AAFF);
        }
        for(Snapshot snapshot:contacts) {
            if(showCount) draw.worldText(snapshot.labelX,snapshot.labelY,Integer.toString(snapshot.count),0xFFFFFFFF);
            for(int i=0;i<snapshot.count;i++) {
                int color=showAnchors?0x00FF00FF:0x4169E1FF;
                draw.point(snapshot.x[i],snapshot.y[i],8,color);
                draw.segment(snapshot.x[i],snapshot.y[i],snapshot.x[i]+0.5f*snapshot.nx[i],snapshot.y[i]+0.5f*snapshot.ny[i],0xEE82EEFF);
                if(showIds) draw.worldText(snapshot.x[i]+0.04f,snapshot.y[i]-0.04f,"0x"+Integer.toHexString(snapshot.id[i]),0xFFFFFFFF);
                if(showSeparation) draw.worldText(snapshot.x[i]+0.04f,snapshot.y[i]+0.04f,String.format("%.3f",snapshot.separation[i]),0xFFFFFFFF);
            }
        }
    }

    private void reset() { x=y=angle=0; }
    @Override public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("x offset",-2,2,0.01f,()->x,v->x=v),
                Box2DSampleControl.slider("y offset",-2,2,0.01f,()->y,v->y=v),
                Box2DSampleControl.slider("angle",-PI,PI,0.01f,()->angle,v->angle=v),
                Box2DSampleControl.slider("round",0,0.4f,0.1f,()->round,v->round=v),
                Box2DSampleControl.checkbox("show count",()->showCount?1:0,v->showCount=v!=0),
                Box2DSampleControl.checkbox("show ids",()->showIds?1:0,v->showIds=v!=0),
                Box2DSampleControl.checkbox("show separation",()->showSeparation?1:0,v->showSeparation=v!=0),
                Box2DSampleControl.checkbox("show anchors",()->showAnchors?1:0,v->showAnchors=v!=0),
                Box2DSampleControl.checkbox("enable caching",()->enableCaching?1:0,v->enableCaching=v!=0),
                Box2DSampleControl.button("Reset",this::reset),
                Box2DSampleControl.dynamicText(()->"contact points = "+contactPointCount()),
                Box2DSampleControl.text("mouse 1: drag; shift + mouse 1: rotate"));
    }
    private int contactPointCount() { int count=0; for(Snapshot s:contacts) count+=s.count; return count; }
}
