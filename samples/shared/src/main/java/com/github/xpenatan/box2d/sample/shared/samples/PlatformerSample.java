package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Events / Platformer sample. */
public final class PlatformerSample extends AbstractBox2DSample {
    private final B2Body player;
    private final B2Body movingPlatform;
    private final B2Shape playerShape;
    private final B2Shape staticPlatformShape;
    private final B2Shape movingPlatformShape;
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
        staticPlatformShape.EnablePreSolveEvents(true);

        B2BodyDef platformDef = new B2BodyDef();
        B2Vec2 platformPosition = vector(0.0f, 6.0f);
        B2Vec2 platformVelocity = vector(2.0f, 0.0f);
        platformDef.SetType(B2.KinematicBody());
        platformDef.SetPosition(platformPosition);
        platformDef.SetLinearVelocity(platformVelocity);
        movingPlatform = createBody(platformDef);
        movingPlatformShape = addBoxShape(movingPlatform, 3.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        movingPlatformShape.EnablePreSolveEvents(true);

        B2BodyDef playerDef = new B2BodyDef();
        B2Vec2 playerPosition = vector(0.0f, 1.0f);
        playerDef.SetType(B2.DynamicBody());
        playerDef.SetFixedRotation(true);
        playerDef.SetLinearDamping(0.5f);
        playerDef.SetPosition(playerPosition);
        player = createBody(playerDef);
        playerShape = addCapsuleShape(player, 0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f, 0.1f, 0.0f, 0.0f);
        world().EnableOneSidedPlatform(playerShape.GetId(), 0.5f);
        release(playerPosition, playerDef, platformVelocity, platformPosition, platformDef);
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        B2Vec2 velocity = player.GetLinearVelocity();
        canJump = jumpDelay == 0.0f && !jumping && velocity.GetY() < 0.01f
                && player.HasSupportingContact(0.9f, 4);
        release(velocity);

        B2Vec2 platformPosition = movingPlatform.GetPosition();
        if(platformPosition.GetX() < -15.0f) setLinearVelocity(movingPlatform, 2.0f, 0.0f);
        else if(platformPosition.GetX() > 15.0f) setLinearVelocity(movingPlatform, -2.0f, 0.0f);
        release(platformPosition);

        if(left) applyForceToCenter(player, -force, 0.0f);
        if(right) applyForceToCenter(player, force, 0.0f);

        if(jump && !jumping && canJump) {
            applyLinearImpulseToCenter(player, 0.0f, impulse);
            jumpDelay = 0.5f;
            jumping = true;
        }
        if(!jump) jumping = false;
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        jumpDelay = Math.max(0.0f, jumpDelay - deltaSeconds);
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
