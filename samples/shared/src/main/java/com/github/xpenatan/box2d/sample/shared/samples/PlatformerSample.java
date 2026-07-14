package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2ContactBeginTouchEvent;
import com.github.xpenatan.box2d.B2ContactEndTouchEvent;
import com.github.xpenatan.box2d.B2ContactEvents;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Java port of Box2D 3.1.1's Events / Platformer sample. */
public final class PlatformerSample extends AbstractBox2DSample {
    private final B2Body player;
    private final B2Body movingPlatform;
    private final B2Shape playerShape;
    private final B2Shape staticPlatformShape;
    private final B2Shape movingPlatformShape;
    private final Set<Long> playerContacts = new HashSet<Long>();
    private float force = 25.0f;
    private float impulse = 25.0f;
    private float jumpDelay = 0.25f;
    private boolean left;
    private boolean right;
    private boolean jump;
    private boolean jumping;
    private boolean canJump;

    public PlatformerSample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);

        B2Body staticPlatform = createStaticBody(-6.0f, 6.0f, 0.0f);
        staticPlatformShape = addBoxShape(staticPlatform, 2.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        staticPlatformShape.EnableContactEvents(true);

        B2BodyDef platformDef = new B2BodyDef();
        B2Vec2 platformPosition = vector(0.0f, 6.0f);
        B2Vec2 platformVelocity = vector(2.0f, 0.0f);
        platformDef.SetType(B2.KinematicBody());
        platformDef.SetPosition(platformPosition);
        platformDef.SetLinearVelocity(platformVelocity);
        movingPlatform = createBody(platformDef);
        movingPlatformShape = addBoxShape(movingPlatform, 3.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        movingPlatformShape.EnableContactEvents(true);

        B2BodyDef playerDef = new B2BodyDef();
        B2Vec2 playerPosition = vector(0.0f, 1.0f);
        playerDef.SetType(B2.DynamicBody());
        playerDef.SetFixedRotation(true);
        playerDef.SetLinearDamping(0.5f);
        playerDef.SetPosition(playerPosition);
        player = createBody(playerDef);
        playerShape = addCapsuleShape(player, 0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f, 0.1f, 0.0f, 0.0f);
        playerShape.EnableContactEvents(true);
        release(playerPosition, playerDef, platformVelocity, platformPosition, platformDef);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        B2Vec2 platformPosition = movingPlatform.GetPosition();
        if(platformPosition.GetX() < -15.0f) setLinearVelocity(movingPlatform, 2.0f, 0.0f);
        else if(platformPosition.GetX() > 15.0f) setLinearVelocity(movingPlatform, -2.0f, 0.0f);
        release(platformPosition);

        if(left) applyForceToCenter(player, -force, 0.0f);
        if(right) applyForceToCenter(player, force, 0.0f);

        B2Vec2 playerPosition = player.GetPosition();
        B2Vec2 velocity = player.GetLinearVelocity();
        boolean belowPlatformTop = playerPosition.GetY() < 6.55f;
        setPlatformCollision(staticPlatformShape, !belowPlatformTop);
        setPlatformCollision(movingPlatformShape, !belowPlatformTop);
        canJump = !playerContacts.isEmpty() && velocity.GetY() < 0.01f && jumpDelay <= 0.0f;
        if(jump && !jumping && canJump) {
            applyLinearImpulseToCenter(player, 0.0f, impulse);
            jumpDelay = 0.5f;
            jumping = true;
        }
        if(!jump) jumping = false;
        jumpDelay = Math.max(0.0f, jumpDelay - deltaSeconds);
        release(velocity, playerPosition);
    }

    private void setPlatformCollision(B2Shape shape, boolean enabled) {
        B2Filter filter = shape.GetFilter();
        long wanted = enabled ? -1L : 0L;
        if(filter.GetMaskBits() != wanted) {
            filter.SetMaskBits(wanted);
            shape.SetFilter(filter);
        }
        release(filter);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2ContactEvents events = world().GetContactEvents();
        for(int i = 0; i < events.GetBeginCount(); i++) {
            B2ContactBeginTouchEvent event = events.GetBeginEvent(i);
            updateContact(event.GetShapeIdA(), event.GetShapeIdB(), true);
            release(event);
        }
        for(int i = 0; i < events.GetEndCount(); i++) {
            B2ContactEndTouchEvent event = events.GetEndEvent(i);
            updateContact(event.GetShapeIdA(), event.GetShapeIdB(), false);
            release(event);
        }
        release(events);
    }

    private void updateContact(long shapeA, long shapeB, boolean begin) {
        long other = shapeA == playerShape.GetId() ? shapeB : shapeB == playerShape.GetId() ? shapeA : 0L;
        if(other == 0L) return;
        if(begin) playerContacts.add(other); else playerContacts.remove(other);
    }

    @Override public void keyDown(int key) { setKey(key, true); }
    @Override public void keyUp(int key) { setKey(key, false); }

    private void setKey(int key, boolean value) {
        if(key == 'A') left = value;
        else if(key == 'D') right = value;
        else if(key == ' ') jump = value;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("force", 0.0f, 50.0f, 0.1f, () -> force, value -> force = value),
                Box2DSampleControl.slider("impulse", 0.0f, 50.0f, 0.1f, () -> impulse, value -> impulse = value),
                Box2DSampleControl.dynamicText(() -> "Movement: A/D/Space"),
                Box2DSampleControl.dynamicText(() -> "Can jump = " + canJump));
    }
}
