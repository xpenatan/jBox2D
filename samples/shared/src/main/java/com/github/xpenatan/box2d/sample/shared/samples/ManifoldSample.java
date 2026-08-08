package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2ChainSegment;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Collision;
import com.github.xpenatan.box2d.B2Hull;
import com.github.xpenatan.box2d.B2Manifold;
import com.github.xpenatan.box2d.B2ManifoldPoint;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2SimplexCache;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's complete Collision / Manifold matrix. */
public final class ManifoldSample extends AbstractBox2DSample {
    private interface ManifoldCall { B2Manifold call(B2Transform transformA, B2Transform transformB); }
    private interface SceneCall { void capture(B2Transform transformA, B2Transform transformB); }

    private static final int CIRCLE = 0, CAPSULE = 1, SEGMENT = 2, POLYGON = 3;
    private static final int COLOR_A = 0x7FFFD4FF;
    private static final int COLOR_B = 0xEEE8AAFF;
    private static final int LIGHT_GRAY = 0xD3D3D3FF;

    private static final class ContactSnapshot {
        float originAX, originAY, originBX, originBY;
        int count;
        final float[] x = new float[2], y = new float[2], nx = new float[2], ny = new float[2];
        final float[] anchorAX = new float[2], anchorAY = new float[2];
        final float[] anchorBX = new float[2], anchorBY = new float[2];
        final float[] separation = new float[2];
        final int[] id = new int[2];
    }

    private static final class ShapeSnapshot {
        int type, color;
        float radius;
        float[] points;
    }

    private static final class LineSnapshot {
        float x1, y1, x2, y2;
        int color;
    }

    private static final class PointSnapshot {
        float x, y, size;
        int color;
    }

    private final ArrayList<ContactSnapshot> contacts = new ArrayList<ContactSnapshot>();
    private final ArrayList<ShapeSnapshot> shapes = new ArrayList<ShapeSnapshot>();
    private final ArrayList<LineSnapshot> lines = new ArrayList<LineSnapshot>();
    private final ArrayList<PointSnapshot> points = new ArrayList<PointSnapshot>();
    private final B2SimplexCache roundedCache1 = own(new B2SimplexCache());
    private final B2SimplexCache roundedCache2 = own(new B2SimplexCache());
    private final B2SimplexCache capsuleCache1 = own(new B2SimplexCache());
    private final B2SimplexCache capsuleCache2 = own(new B2SimplexCache());
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
        shapes.clear();
        lines.clear();
        points.clear();
        if(!enableCaching) {
            roundedCache1.Clear(); roundedCache2.Clear();
            capsuleCache1.Clear(); capsuleCache2.Clear();
        }
        rowOne();
        rowTwo();
        rowThree();
    }

    private void rowOne() {
        final B2Circle circle05 = circle(0.5f);
        final B2Circle circle10 = circle(1.0f);
        at(-10, -5, (a,b) -> B2Collision.CollideCircles(circle05,a,circle10,b),
                (a,b) -> { addCircle(circle05,a,COLOR_A); addCircle(circle10,b,COLOR_B); });

        final B2Capsule capsule1 = capsule(-0.5f,0,0.5f,0,0.25f);
        at(-6, -5, (a,b) -> B2Collision.CollideCapsuleAndCircle(capsule1,a,circle05,b),
                (a,b) -> { addCapsule(capsule1,a,COLOR_A); addCircle(circle05,b,COLOR_B); });

        final B2Segment segment = segment(-1,0,1,0);
        at(-2, -5, (a,b) -> B2Collision.CollideSegmentAndCircle(segment,a,circle05,b),
                (a,b) -> { addSegment(segment,a,COLOR_A); addCircle(circle05,b,COLOR_B); });

        final B2Polygon roundedSquare = B2Polygon.CreateSquare(0.5f);
        roundedSquare.SetRadius(round);
        at(2, -5, (a,b) -> B2Collision.CollidePolygonAndCircle(roundedSquare,a,circle05,b),
                (a,b) -> { addPolygon(roundedSquare,a,COLOR_A); addCircle(circle05,b,COLOR_B); });

        final B2Capsule capsule2 = capsule(0.25f,0,1.0f,0,0.1f);
        at(6, -5, (a,b) -> B2Collision.CollideCapsules(capsule1,a,capsule2,b),
                (a,b) -> { addCapsule(capsule1,a,COLOR_A); addCapsule(capsule2,b,COLOR_B); });

        B2Vec2 boxCenter = vector(1.0f, -1.0f);
        B2Rot boxRotation = new B2Rot(0.25f * PI);
        final B2Polygon offsetBox = B2Polygon.CreateOffsetBox(0.25f, 1.0f, boxCenter, boxRotation);
        final B2Capsule shortCapsule = capsule(-0.4f,0,-0.1f,0,0.1f);
        at(10, -5, (a,b) -> B2Collision.CollidePolygonAndCapsule(offsetBox,a,shortCapsule,b),
                (a,b) -> { addPolygon(offsetBox,a,COLOR_A); addCapsule(shortCapsule,b,COLOR_B); });

        at(14, -5, (a,b) -> B2Collision.CollideSegmentAndCapsule(segment,a,capsule1,b),
                (a,b) -> { addSegment(segment,a,COLOR_A); addCapsule(capsule1,b,COLOR_B); });

        release(shortCapsule, offsetBox, boxRotation, boxCenter, capsule2, roundedSquare,
                segment, capsule1, circle10, circle05);
    }

    private void rowTwo() {
        final B2Polygon square = B2Polygon.CreateSquare(0.5f);
        at(-10, 0, (a,b) -> B2Collision.CollidePolygons(square,a,square,b),
                (a,b) -> { addPolygon(square,a,COLOR_A); addPolygon(square,b,COLOR_B); });

        final B2Polygon bar = B2Polygon.CreateBox(2.0f, 0.1f);
        final B2Polygon small = B2Polygon.CreateSquare(0.25f);
        at(-6, 0, (a,b) -> B2Collision.CollidePolygons(bar,a,small,b),
                (a,b) -> { addPolygon(bar,a,COLOR_A); addPolygon(small,b,COLOR_B); });

        final B2Polygon rounded = B2Polygon.CreateRoundedBox(0.5f - round, 0.5f - round, round);
        at(-2, 0, (a,b) -> B2Collision.CollidePolygons(square,a,rounded,b),
                (a,b) -> { addPolygon(square,a,COLOR_A); addPolygon(rounded,b,COLOR_B); });
        at(2, 0, (a,b) -> B2Collision.CollidePolygons(rounded,a,rounded,b),
                (a,b) -> { addPolygon(rounded,a,COLOR_A); addPolygon(rounded,b,COLOR_B); });

        final B2Segment segment = segment(-1,0,1,0);
        at(6, 0, (a,b) -> B2Collision.CollideSegmentAndPolygon(segment,a,rounded,b),
                (a,b) -> { addSegment(segment,a,COLOR_A); addPolygon(rounded,b,COLOR_B); });

        final B2Polygon wedge = polygon(new float[] {-0.1f,-0.5f, 0.1f,-0.5f, 0,0.5f}, round);
        at(10, 0, (a,b) -> B2Collision.CollidePolygons(wedge,a,wedge,b),
                (a,b) -> { addPolygon(wedge,a,COLOR_A); addPolygon(wedge,b,COLOR_B); });

        final B2Polygon wedge1 = polygon(new float[] {
                0.175740838f,0.224936664f, -0.301293969f,0.194021404f, -0.105151534f,-0.432157338f
        }, 0.158798501f);
        final B2Polygon wedge2 = polygon(new float[] {
                -0.427884758f,-0.225028217f, 0.0566576123f,-0.128772855f, 0.176625848f,0.338923335f
        }, 0.205900759f);
        at(14, 0, (a,b) -> B2Collision.CollidePolygons(wedge1,a,wedge2,b),
                (a,b) -> { addPolygon(wedge1,a,COLOR_A); addPolygon(wedge2,b,COLOR_B); });

        release(wedge2, wedge1, wedge, segment, rounded, small, bar, square);
    }

    private void rowThree() {
        final B2Polygon box = B2Polygon.CreateBox(1.0f, 1.0f);
        final B2Polygon triangle = polygon(new float[] {-0.05f,0, 0.05f,0, 0,0.1f}, 0.0f);
        at(-10, 5, (a,b) -> B2Collision.CollidePolygons(box,a,triangle,b),
                (a,b) -> { addPolygon(box,a,COLOR_A); addPolygon(triangle,b,COLOR_B); });

        final B2ChainSegment chain1 = chain(2,1, 1,1, -1,0, -2,0);
        final B2Circle circle = circle(0.5f);
        at(-6, 5, (a,b) -> B2Collision.CollideChainSegmentAndCircle(chain1,a,circle,b),
                (a,b) -> { addFullChain(chain1,a); addCircle(circle,b,COLOR_B); });

        final B2ChainSegment chain2 = chain(3,1, 2,1, 1,1, -1,0);
        final B2Polygon rounded = B2Polygon.CreateRoundedBox(0.5f - round, 0.5f - round, round);
        at(2, 5, (a,b) -> B2Collision.CollideChainSegmentAndPolygon(chain1,a,rounded,b,roundedCache1),
                (a,b) -> { addChainRightGhost(chain1,a); addPolygon(rounded,b,COLOR_B); });
        at(2, 5, (a,b) -> B2Collision.CollideChainSegmentAndPolygon(chain2,a,rounded,b,roundedCache2),
                (a,b) -> addChainLeftGhost(chain2,a));

        final B2Capsule capsule = capsule(-0.5f,0,0.5f,0,0.25f);
        at(10, 5, (a,b) -> B2Collision.CollideChainSegmentAndCapsule(chain1,a,capsule,b,capsuleCache1),
                (a,b) -> { addChainRightGhost(chain1,a); addCapsule(capsule,b,COLOR_B); });
        at(10, 5, (a,b) -> B2Collision.CollideChainSegmentAndCapsule(chain2,a,capsule,b,capsuleCache2),
                (a,b) -> addChainLeftGhost(chain2,a));

        release(capsule, rounded, chain2, circle, chain1, triangle, box);
    }

    private void at(float originX, float originY, ManifoldCall collision, SceneCall scene) {
        B2Transform transformA = transform(originX, originY, 0.0f);
        B2Transform transformB = transform(originX + x, originY + y, angle);
        capture(collision.call(transformA, transformB), originX, originY, originX + x, originY + y);
        scene.capture(transformA, transformB);
        release(transformB, transformA);
    }

    private void capture(B2Manifold manifold, float originAX, float originAY, float originBX, float originBY) {
        ContactSnapshot snapshot = new ContactSnapshot();
        snapshot.originAX = originAX; snapshot.originAY = originAY;
        snapshot.originBX = originBX; snapshot.originBY = originBY;
        snapshot.count = manifold.GetPointCount();
        B2Vec2 normal = manifold.GetNormal();
        for(int i = 0; i < snapshot.count; i++) {
            B2ManifoldPoint point = manifold.GetPoint(i);
            B2Vec2 p = point.GetPoint(), anchorA = point.GetAnchorA(), anchorB = point.GetAnchorB();
            snapshot.x[i] = p.GetX(); snapshot.y[i] = p.GetY();
            snapshot.nx[i] = normal.GetX(); snapshot.ny[i] = normal.GetY();
            snapshot.anchorAX[i] = anchorA.GetX(); snapshot.anchorAY[i] = anchorA.GetY();
            snapshot.anchorBX[i] = anchorB.GetX(); snapshot.anchorBY[i] = anchorB.GetY();
            snapshot.separation[i] = point.GetSeparation(); snapshot.id[i] = point.GetId();
            release(anchorB, anchorA, p, point);
        }
        contacts.add(snapshot);
        release(normal, manifold);
    }

    private void addCircle(B2Circle circle, B2Transform transform, int color) {
        B2Vec2 local = circle.GetCenter();
        B2Vec2 center = transform.TransformPoint(local);
        addShape(CIRCLE, color, circle.GetRadius(), new float[] {center.GetX(), center.GetY()});
        release(center, local);
    }

    private void addCapsule(B2Capsule capsule, B2Transform transform, int color) {
        B2Vec2 local1 = capsule.GetCenter1(), local2 = capsule.GetCenter2();
        B2Vec2 p1 = transform.TransformPoint(local1);
        B2Vec2 p2 = transform.TransformPoint(local2);
        addShape(CAPSULE, color, capsule.GetRadius(), new float[] {p1.GetX(),p1.GetY(),p2.GetX(),p2.GetY()});
        release(p2, p1, local2, local1);
    }

    private void addSegment(B2Segment segment, B2Transform transform, int color) {
        B2Vec2 local1 = segment.GetPoint1(), local2 = segment.GetPoint2();
        B2Vec2 p1 = transform.TransformPoint(local1);
        B2Vec2 p2 = transform.TransformPoint(local2);
        addShape(SEGMENT, color, 0.0f, new float[] {p1.GetX(),p1.GetY(),p2.GetX(),p2.GetY()});
        release(p2, p1, local2, local1);
    }

    private void addPolygon(B2Polygon polygon, B2Transform transform, int color) {
        float[] vertices = new float[2 * polygon.GetVertexCount()];
        for(int i = 0; i < polygon.GetVertexCount(); i++) {
            B2Vec2 local = polygon.GetVertex(i);
            B2Vec2 world = transform.TransformPoint(local);
            vertices[2*i] = world.GetX(); vertices[2*i+1] = world.GetY();
            release(world, local);
        }
        addShape(POLYGON, color, polygon.GetRadius(), vertices);
    }

    private void addShape(int type, int color, float radius, float[] vertices) {
        ShapeSnapshot shape = new ShapeSnapshot();
        shape.type = type; shape.color = color; shape.radius = radius; shape.points = vertices;
        shapes.add(shape);
    }

    private void addFullChain(B2ChainSegment chain, B2Transform transform) {
        B2Vec2 ghost1 = chain.GetGhost1(), ghost2 = chain.GetGhost2();
        B2Segment segment = chain.GetSegment();
        B2Vec2 p1 = segment.GetPoint1(), p2 = segment.GetPoint2();
        addLocalLine(transform, ghost1, p1, LIGHT_GRAY);
        addLocalLine(transform, p1, p2, COLOR_A);
        addLocalLine(transform, p2, ghost2, LIGHT_GRAY);
        release(p2, p1, segment, ghost2, ghost1);
    }

    private void addChainRightGhost(B2ChainSegment chain, B2Transform transform) {
        B2Segment segment = chain.GetSegment();
        B2Vec2 p1 = segment.GetPoint1(), p2 = segment.GetPoint2(), ghost2 = chain.GetGhost2();
        addLocalLine(transform, p1, p2, COLOR_A);
        addLocalPoint(transform, p1, 4.0f, COLOR_A); addLocalPoint(transform, p2, 4.0f, COLOR_A);
        addLocalLine(transform, p2, ghost2, LIGHT_GRAY);
        release(ghost2, p2, p1, segment);
    }

    private void addChainLeftGhost(B2ChainSegment chain, B2Transform transform) {
        B2Segment segment = chain.GetSegment();
        B2Vec2 ghost1 = chain.GetGhost1(), p1 = segment.GetPoint1(), p2 = segment.GetPoint2();
        addLocalLine(transform, ghost1, p1, LIGHT_GRAY);
        addLocalLine(transform, p1, p2, COLOR_A);
        addLocalPoint(transform, p1, 4.0f, COLOR_A); addLocalPoint(transform, p2, 4.0f, COLOR_A);
        release(p2, p1, ghost1, segment);
    }

    private void addLocalLine(B2Transform transform, B2Vec2 local1, B2Vec2 local2, int color) {
        B2Vec2 p1 = transform.TransformPoint(local1), p2 = transform.TransformPoint(local2);
        LineSnapshot line = new LineSnapshot();
        line.x1=p1.GetX(); line.y1=p1.GetY(); line.x2=p2.GetX(); line.y2=p2.GetY(); line.color=color;
        lines.add(line); release(p2,p1);
    }

    private void addLocalPoint(B2Transform transform, B2Vec2 local, float size, int color) {
        B2Vec2 p = transform.TransformPoint(local);
        PointSnapshot point = new PointSnapshot();
        point.x=p.GetX(); point.y=p.GetY(); point.size=size; point.color=color;
        points.add(point); release(p);
    }

    private static B2Circle circle(float radius) {
        B2Vec2 p = new B2Vec2(); B2Circle circle = new B2Circle(p,radius); release(p); return circle;
    }

    private static B2Capsule capsule(float x1,float y1,float x2,float y2,float radius) {
        B2Vec2 p1 = new B2Vec2(x1,y1), p2 = new B2Vec2(x2,y2);
        B2Capsule capsule = new B2Capsule(p1,p2,radius); release(p2,p1); return capsule;
    }

    private static B2Segment segment(float x1,float y1,float x2,float y2) {
        B2Vec2 p1 = new B2Vec2(x1,y1), p2 = new B2Vec2(x2,y2);
        B2Segment segment = new B2Segment(p1,p2); release(p2,p1); return segment;
    }

    private static B2ChainSegment chain(float gx1,float gy1,float x1,float y1,float x2,float y2,float gx2,float gy2) {
        B2Vec2 ghost1 = vector(gx1,gy1), p1 = vector(x1,y1), p2 = vector(x2,y2), ghost2 = vector(gx2,gy2);
        B2Segment segment = new B2Segment(p1,p2);
        B2ChainSegment chain = new B2ChainSegment(ghost1,segment,ghost2);
        release(segment,ghost2,p2,p1,ghost1); return chain;
    }

    private static B2Polygon polygon(float[] vertices, float radius) {
        B2Hull hull = new B2Hull();
        for(int i=0;i<vertices.length;i+=2) {
            B2Vec2 p = new B2Vec2(vertices[i],vertices[i+1]); hull.AddPoint(p); release(p);
        }
        hull.Compute(); B2Polygon polygon = B2Polygon.CreateFromHull(hull,radius); release(hull); return polygon;
    }

    private static B2Transform transform(float x,float y,float angle) {
        return CollisionSampleSupport.transform(x,y,angle);
    }

    @Override
    public void mouseDown(float px,float py,int button,int modifiers) {
        if(button != 0) return;
        startX=px; startY=py;
        if(modifiers == 0 && !rotating) { dragging=true; baseX=x; baseY=y; }
        else if((modifiers & 1) != 0 && !dragging) { rotating=true; baseAngle=angle; }
    }

    @Override
    public void mouseMove(float px,float py) {
        if(dragging) { x=baseX+0.5f*(px-startX); y=baseY+0.5f*(py-startY); }
        else if(rotating) angle=CollisionSampleSupport.clamp(baseAngle+px-startX,-PI,PI);
    }

    @Override
    public void mouseUp(float px,float py,int button) {
        if(button == 0) dragging=rotating=false;
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(ShapeSnapshot shape : shapes) {
            if(shape.type == CIRCLE) {
                draw.circle(shape.points[0],shape.points[1],shape.radius,shape.color);
            }
            else if(shape.type == CAPSULE) {
                draw.segment(shape.points[0],shape.points[1],shape.points[2],shape.points[3],shape.color);
                draw.circle(shape.points[0],shape.points[1],shape.radius,shape.color);
                draw.circle(shape.points[2],shape.points[3],shape.radius,shape.color);
            }
            else if(shape.type == SEGMENT) {
                draw.segment(shape.points[0],shape.points[1],shape.points[2],shape.points[3],shape.color);
            }
            else {
                int count = shape.points.length / 2;
                for(int i=0;i<count;i++) {
                    int j=(i+1)%count;
                    draw.segment(shape.points[2*i],shape.points[2*i+1],shape.points[2*j],shape.points[2*j+1],shape.color);
                    if(shape.radius>0) draw.circle(shape.points[2*i],shape.points[2*i+1],shape.radius,shape.color);
                }
            }
        }
        for(LineSnapshot line : lines) draw.segment(line.x1,line.y1,line.x2,line.y2,line.color);
        for(PointSnapshot point : points) draw.point(point.x,point.y,point.size,point.color);

        for(ContactSnapshot contact : contacts) {
            if(showCount) draw.worldText(0.5f*(contact.originAX+contact.originBX),
                    0.5f*(contact.originAY+contact.originBY),Integer.toString(contact.count),0xFFFFFFFF);
            for(int i=0;i<contact.count;i++) {
                float px=contact.x[i], py=contact.y[i];
                draw.segment(px,py,px+0.5f*contact.nx[i],py+0.5f*contact.ny[i],0xEE82EEFF);
                if(showAnchors) {
                    draw.point(contact.originAX+contact.anchorAX[i],contact.originAY+contact.anchorAY[i],5,0xFF0000FF);
                    draw.point(contact.originBX+contact.anchorBX[i],contact.originBY+contact.anchorBY[i],5,0x00FF00FF);
                }
                else draw.point(px,py,10,0x0000FFFF);
                if(showIds) draw.worldText(px+0.05f,py-0.02f,String.format("0x%04x",contact.id[i]),0xFFFFFFFF);
                if(showSeparation) draw.worldText(px+0.05f,py+0.03f,String.format("%.3f",contact.separation[i]),0xFFFFFFFF);
            }
        }
    }

    private void reset() { x=y=angle=0; }

    @Override
    public List<Box2DSampleControl> controls() {
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
                Box2DSampleControl.text("mouse button 1: drag"),
                Box2DSampleControl.text("mouse button 1 + shift: rotate"));
    }
}
