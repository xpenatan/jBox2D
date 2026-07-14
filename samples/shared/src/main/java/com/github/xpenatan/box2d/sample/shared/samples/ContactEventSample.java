package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2ContactBeginTouchEvent;
import com.github.xpenatan.box2d.B2ContactEvents;
import com.github.xpenatan.box2d.B2Manifold;
import com.github.xpenatan.box2d.B2ManifoldPoint;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Java port of Box2D 3.1.1's Events / Contact sample. */
public final class ContactEventSample extends AbstractBox2DSample {
    private final B2Body player;
    private final long coreShapeId;
    private final List<B2Body> debris = new ArrayList<B2Body>();
    private final Map<Long, B2Body> debrisByShape = new HashMap<Long, B2Body>();
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
        if(debris.size() >= 20) return;
        int index = debris.size();
        B2BodyDef def = new B2BodyDef();
        B2Vec2 position = vector(randomFloat(-38, 38), randomFloat(-38, 38));
        B2Vec2 velocity = vector(randomFloat(-5, 5), randomFloat(-5, 5));
        def.SetType(B2.DynamicBody());
        def.SetPosition(position);
        def.SetAngle(randomFloat(-PI, PI));
        def.SetLinearVelocity(velocity);
        def.SetAngularVelocity(randomFloat(-1, 1));
        def.SetGravityScale(0);
        B2Body body = createBody(def);
        B2Shape shape;
        if((index + 1) % 3 == 0) shape = addCircleShape(body, 0, 0, .5f, 1, .6f, .8f, 0);
        else if((index + 1) % 2 == 0) shape = addCapsuleShape(body, 0, -.25f, 0, .25f, .25f, 1, .6f, .8f, 0);
        else shape = addBoxShape(body, .4f, .6f, 1, .6f, .8f, 0);
        debris.add(body);
        debrisByShape.put(shape.GetId(), body);
        release(velocity, position, def);
    }

    @Override protected void beforeStep(float deltaSeconds) {
        if(left) applyForceToCenter(player, -force, 0);
        if(right) applyForceToCenter(player, force, 0);
        if(up) applyForceToCenter(player, 0, force);
        if(down) applyForceToCenter(player, 0, -force);
    }

    @Override protected void afterStep(float deltaSeconds) {
        contactPoints.clear();
        ArrayList<B2Body> attach = new ArrayList<B2Body>();
        B2ContactEvents events = world().GetContactEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2ContactBeginTouchEvent event = events.GetBeginEvent(i);
            B2Manifold manifold = event.GetManifold();
            B2Vec2 normal = manifold.GetNormal();
            for(int p = 0; p < manifold.GetPointCount(); p++) {
                B2ManifoldPoint point = manifold.GetPoint(p);
                B2Vec2 position = point.GetPoint();
                contactPoints.add(new float[] { position.GetX(), position.GetY(), normal.GetX(), normal.GetY() });
                release(position, point);
            }
            B2Body body = event.GetShapeIdA() == coreShapeId ? debrisByShape.get(event.GetShapeIdB())
                    : event.GetShapeIdB() == coreShapeId ? debrisByShape.get(event.GetShapeIdA()) : null;
            if(body != null && !attach.contains(body)) attach.add(body);
            release(normal, manifold, event);
        }
        release(events);
        for(B2Body body : attach) {
            B2Vec2 bodyPosition = body.GetPosition();
            B2Vec2 local = player.GetLocalPoint(bodyPosition);
            addCircleShape(player, local.GetX(), local.GetY(), .35f, 1, .6f, 0, 0).EnableContactEvents(true);
            debris.remove(body);
            debrisByShape.values().remove(body);
            destroyBody(body);
            release(local, bodyPosition);
        }
        wait -= deltaSeconds;
        if(wait <= 0) { spawnDebris(); wait += .5f; }
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
