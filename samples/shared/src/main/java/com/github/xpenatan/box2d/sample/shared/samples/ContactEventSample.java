package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2ContactBeginTouchEvent;
import com.github.xpenatan.box2d.B2ContactEvents;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Hull;
import com.github.xpenatan.box2d.B2Manifold;
import com.github.xpenatan.box2d.B2ManifoldPoint;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** Java port of Box2D 3.1.1's Events / Contact sample. */
public final class ContactEventSample extends AbstractBox2DSample {
    private final B2Body player;
    private final long coreShapeId;
    private final B2Body[] debrisBodies = new B2Body[20];
    private final B2Shape[] debrisShapes = new B2Shape[20];
    private final Map<Long, Integer> debrisByShape = new HashMap<Long, Integer>();
    private final Map<Long, B2Shape> attachedShapes = new HashMap<Long, B2Shape>();
    private final List<float[]> contactPoints = new ArrayList<float[]>();
    private float force = 200.0f;
    private float wait = 0.5f;
    private boolean left, right, up, down;

    public ContactEventSample() {
        B2Body ground = createStaticBody(0, 0, 0);
        addChain(ground, new float[] { 40, -40, -40, -40, -40, 40, 40, 40 }, true, .6f);
        B2BodyDef def = new B2BodyDef();
        def.SetType(B2.DynamicBody());
        def.SetGravityScale(0.0f);
        def.SetLinearDamping(0.5f);
        def.SetAngularDamping(0.5f);
        def.SetIsBullet(true);
        player = createBody(def);
        B2Shape core = addCircleShape(player, 0, 0, 1, 1, .6f, 0, 0);
        core.EnableContactEvents(true);
        coreShapeId = core.GetId();
        release(def);
    }

    private void spawnDebris() {
        int index = -1;
        for(int i = 0; i < debrisBodies.length; i++) {
            if(debrisBodies[i] == null) { index = i; break; }
        }
        if(index < 0) return;
        B2BodyDef def = new B2BodyDef();
        B2Vec2 position = vector(randomFloat(-38, 38), randomFloat(-38, 38));
        float angle = randomFloat(-PI, PI);
        B2Vec2 velocity = vector(randomFloat(-5, 5), randomFloat(-5, 5));
        float angularVelocity = randomFloat(-1, 1);
        def.SetType(B2.DynamicBody());
        def.SetPosition(position);
        def.SetAngle(angle);
        def.SetLinearVelocity(velocity);
        def.SetAngularVelocity(angularVelocity);
        def.SetGravityScale(0);
        B2Body body = createBody(def);
        B2Shape shape;
        if((index + 1) % 3 == 0) shape = addCircleShape(body, 0, 0, .5f, 1, .6f, .8f, 0);
        else if((index + 1) % 2 == 0) shape = addCapsuleShape(body, 0, -.25f, 0, .25f, .25f, 1, .6f, .8f, 0);
        else shape = addBoxShape(body, .4f, .6f, 1, .6f, .8f, 0);
        debrisBodies[index] = body;
        debrisShapes[index] = shape;
        debrisByShape.put(shape.GetId(), index);
        release(velocity, position, def);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        B2Vec2 position = player.GetPosition();
        if(left) applyForceAtOrigin(-force, 0.0f, position);
        if(right) applyForceAtOrigin(force, 0.0f, position);
        if(up) applyForceAtOrigin(0.0f, force, position);
        if(down) applyForceAtOrigin(0.0f, -force, position);
        release(position);
    }

    private void applyForceAtOrigin(float x, float y, B2Vec2 position) {
        B2Vec2 forceVector = vector(x, y);
        player.ApplyForce(forceVector, position, true);
        release(forceVector);
    }

    @Override protected void afterStep(float deltaSeconds) {
        contactPoints.clear();
        boolean[] attach = new boolean[debrisBodies.length];
        HashSet<Long> destroy = new HashSet<Long>();
        B2ContactEvents events = world().GetContactEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2ContactBeginTouchEvent event = events.GetBeginEvent(i);
            B2Manifold manifold = event.GetManifold();
            B2Vec2 normal = manifold.GetNormal();
            for(int p = 0; p < manifold.GetPointCount(); p++) {
                B2ManifoldPoint point = manifold.GetPoint(p);
                B2Vec2 position = point.GetPoint();
                contactPoints.add(new float[] { position.GetX(), position.GetY(),
                        point.GetTotalNormalImpulse() * normal.GetX(),
                        point.GetTotalNormalImpulse() * normal.GetY() });
                release(position, point);
            }
            long shapeA = event.GetShapeIdA();
            long shapeB = event.GetShapeIdB();
            boolean playerA = shapeA == coreShapeId || attachedShapes.containsKey(shapeA);
            boolean playerB = shapeB == coreShapeId || attachedShapes.containsKey(shapeB);
            if(playerA || playerB) {
                long playerShapeId = playerA ? shapeA : shapeB;
                long otherShapeId = playerA ? shapeB : shapeA;
                Integer debrisIndex = debrisByShape.get(otherShapeId);
                if(debrisIndex != null) attach[debrisIndex.intValue()] = true;
                else if(playerShapeId != coreShapeId) destroy.add(playerShapeId);
            }
            release(normal, manifold, event);
        }
        release(events);
        for(int i = 0; i < attach.length; i++) if(attach[i]) attachDebris(i);
        for(long shapeId : destroy) destroyAttachedShape(shapeId);
        if(!destroy.isEmpty()) player.ApplyMassFromShapes();
        wait -= deltaSeconds;
        if(wait < 0) { spawnDebris(); wait += .5f; }
    }

    private void attachDebris(int index) {
        B2Body debris = debrisBodies[index];
        B2Shape originalShape = debrisShapes[index];
        if(debris == null || originalShape == null || !debris.IsValid() || !originalShape.IsValid()) return;

        B2ShapeDef shapeDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        shapeDef.SetEnableContactEvents(true);
        B2Shape attachedShape;
        int shapeType = originalShape.GetType();
        if(shapeType == B2.CircleShape()) {
            B2Circle circle = originalShape.GetCircle();
            B2Vec2 center = circle.GetCenter();
            B2Vec2 worldCenter = debris.GetWorldPoint(center);
            B2Vec2 localCenter = player.GetLocalPoint(worldCenter);
            circle.SetCenter(localCenter);
            attachedShape = createCircleShape(player, shapeDef, circle);
            release(localCenter, worldCenter, center, circle);
        }
        else if(shapeType == B2.CapsuleShape()) {
            B2Capsule capsule = originalShape.GetCapsule();
            B2Vec2 center1 = capsule.GetCenter1();
            B2Vec2 center2 = capsule.GetCenter2();
            B2Vec2 world1 = debris.GetWorldPoint(center1);
            B2Vec2 world2 = debris.GetWorldPoint(center2);
            B2Vec2 local1 = player.GetLocalPoint(world1);
            B2Vec2 local2 = player.GetLocalPoint(world2);
            capsule.SetCenter1(local1);
            capsule.SetCenter2(local2);
            attachedShape = createCapsuleShape(player, shapeDef, capsule);
            release(local2, local1, world2, world1, center2, center1, capsule);
        }
        else {
            B2Polygon originalPolygon = originalShape.GetPolygon();
            B2Hull hull = new B2Hull();
            for(int i = 0; i < originalPolygon.GetVertexCount(); i++) {
                B2Vec2 vertex = originalPolygon.GetVertex(i);
                B2Vec2 worldVertex = debris.GetWorldPoint(vertex);
                B2Vec2 localVertex = player.GetLocalPoint(worldVertex);
                hull.AddPoint(localVertex);
                release(localVertex, worldVertex, vertex);
            }
            hull.Compute();
            B2Polygon polygon = B2Polygon.CreateFromHull(hull, originalPolygon.GetRadius());
            attachedShape = createPolygonShape(player, shapeDef, polygon);
            release(polygon, hull, originalPolygon);
        }

        attachedShapes.put(attachedShape.GetId(), attachedShape);
        debrisByShape.remove(originalShape.GetId());
        destroyBody(debris);
        discardHandle(originalShape);
        discardHandle(debris);
        debrisShapes[index] = null;
        debrisBodies[index] = null;
        release(shapeDef);
    }

    private void destroyAttachedShape(long shapeId) {
        B2Shape shape = attachedShapes.remove(shapeId);
        if(shape == null) return;
        destroyShape(shape, false);
        discardHandle(shape);
    }

    @Override public void keyDown(int key) { setKey(key, true); }
    @Override public void keyUp(int key) { setKey(key, false); }
    private void setKey(int key, boolean value) {
        if(key == 'A') left = value; else if(key == 'D') right = value;
        else if(key == 'W') up = value; else if(key == 'S') down = value;
    }

    @Override public void draw(Box2DSampleDraw draw) {
        for(float[] p : contactPoints) {
            draw.point(p[0], p[1], 10, 0xFFFFFFFF);
            draw.segment(p[0], p[1], p[0] + p[2], p[1] + p[3], 0x8A2BE2FF);
        }
    }

    @Override public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.slider("force", 100, 500, 1,
                () -> force, value -> force = value));
    }
}
