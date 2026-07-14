package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2MoverResult;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2ShapeProxy;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WorldCastHit;
import com.github.xpenatan.box2d.B2WorldCastResult;
import com.github.xpenatan.box2d.B2WorldOverlapResult;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Character / Mover sample. */
public final class MoverSample extends AbstractBox2DSample {
    private static final long STATIC_BIT = 0x0001L;
    private static final long MOVER_BIT = 0x0002L;
    private static final long DYNAMIC_BIT = 0x0004L;
    private static final long DEBRIS_BIT = 0x0008L;
    private static final long ALL_BITS = -1L;

    private static final int POGO_POINT = 0;
    private static final int POGO_CIRCLE = 1;
    private static final int POGO_SEGMENT = 2;
    private static final float CAPSULE_RADIUS = 0.3f;
    private static final float CAPSULE_HALF_LENGTH = 0.5f;
    private static final float ELEVATOR_X = 112.0f;
    private static final float ELEVATOR_Y = 10.0f;
    private static final float ELEVATOR_AMPLITUDE = 4.0f;

    private static final String TERRAIN_ONE =
            "M 2.6458333,201.08333 H 293.68751 v -47.625 h -2.64584 l -10.58333,7.9375 -13.22916,7.9375 " +
            "-13.24648,5.29167 -31.73269,7.9375 -21.16667,2.64583 -23.8125,10.58333 H 142.875 v -5.29167 " +
            "h -5.29166 v 5.29167 H 119.0625 v -2.64583 h -2.64583 v -2.64584 h -2.64584 v -2.64583 H 111.125 " +
            "v -2.64583 H 84.666668 v -2.64583 h -5.291666 v -2.64584 h -5.291667 v -2.64583 H 68.791668 V 174.625 " +
            "h -5.291666 v -2.64584 H 52.916669 L 39.6875,177.27083 H 34.395833 L 23.8125,185.20833 H 15.875 " +
            "L 5.2916669,187.85416 V 153.45833 H 2.6458333 v 47.625";

    private static final String TERRAIN_TWO =
            "M 2.6458333,201.08333 H 293.68751 l 0,-23.8125 h -23.8125 l 21.16667,21.16667 h -23.8125 " +
            "l -39.68751,-13.22917 -26.45833,7.9375 -23.8125,2.64583 h -13.22917 l -0.0575,2.64584 h -5.29166 " +
            "v -2.64583 l -7.86855,-1e-5 -0.0114,-2.64583 h -2.64583 l -2.64583,2.64584 h -7.9375 " +
            "l -2.64584,2.64583 -2.58891,-2.64584 h -13.28609 v -2.64583 h -2.64583 v -2.64584 l -5.29167,1e-5 " +
            "v -2.64583 h -2.64583 v -2.64583 l -5.29167,-1e-5 v -2.64583 h -2.64583 v -2.64584 h -5.291667 " +
            "v -2.64583 H 92.60417 V 174.625 h -5.291667 v -2.64584 l -34.395835,1e-5 -7.9375,-2.64584 " +
            "-7.9375,-2.64583 -5.291667,-5.29167 H 21.166667 L 13.229167,158.75 5.2916668,153.45833 " +
            "H 2.6458334 l -10e-8,47.625";

    private final B2Body elevator;
    private float moverX = 2.0f;
    private float moverY = 8.0f;
    private float velocityX;
    private float velocityY;
    private float jumpSpeed = 10.0f;
    private float maxSpeed = 6.0f;
    private float minSpeed = 0.1f;
    private float stopSpeed = 3.0f;
    private float accelerate = 20.0f;
    private float airSteer = 0.2f;
    private float friction = 8.0f;
    private float gravity = 30.0f;
    private float pogoHertz = 5.0f;
    private float pogoDampingRatio = 0.8f;
    private int pogoShape = POGO_SEGMENT;
    private float pogoVelocity;
    private float time;
    private boolean onGround;
    private boolean jumpReleased = true;
    private boolean lockCamera = true;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean jumpPressed;
    private int totalIterations;

    private final float[] planeNormalX = new float[8];
    private final float[] planeNormalY = new float[8];
    private final float[] planeOffset = new float[8];
    private int planeCount;
    private float pogoOriginX;
    private float pogoOriginY;
    private float pogoPoint1X;
    private float pogoPoint1Y;
    private float pogoPoint2X;
    private float pogoPoint2Y;
    private float pogoDeltaX;
    private float pogoDeltaY;
    private boolean pogoHit;
    private float kickX;
    private float kickY;
    private float kickTimer;

    public MoverSample() {
        B2Body groundOne = createStaticBody(0.0f, 0.0f, 0.0f);
        addChain(groundOne, parsePath(TERRAIN_ONE, -50.0f, -200.0f, 0.2f), true, 0.6f);

        B2Body groundTwo = createStaticBody(98.0f, 0.0f, 0.0f);
        addChain(groundTwo, parsePath(TERRAIN_TWO, 0.0f, -200.0f, 0.2f), true, 0.6f);

        createBridge(groundOne, groundTwo);
        createFriendlyMover();
        createDebrisBall();
        elevator = createElevator();
    }

    private void createBridge(B2Body groundOne, B2Body groundTwo) {
        B2Polygon box = B2Polygon.CreateBox(0.5f, 0.125f);
        B2ShapeDef shapeDef = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        float xBase = 48.7f;
        float yBase = 9.2f;
        B2Body previous = groundOne;
        for(int i = 0; i < 50; i++) {
            B2BodyDef bodyDef = new B2BodyDef();
            B2Vec2 position = new B2Vec2(xBase + 0.5f + i, yBase);
            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetPosition(position);
            bodyDef.SetAngularDamping(0.2f);
            B2Body body = createBody(bodyDef);
            createPolygonShape(body, shapeDef, box);
            createBridgeJoint(previous, body, xBase + i, yBase);
            previous = body;
            release(position, bodyDef);
        }
        createBridgeJoint(previous, groundTwo, xBase + 50.0f, yBase);
        release(shapeDef, box);
    }

    private void createBridgeJoint(B2Body bodyA, B2Body bodyB, float x, float y) {
        B2RevoluteJointDef jointDef = new B2RevoluteJointDef();
        B2Vec2 pivot = new B2Vec2(x, y);
        B2Vec2 localA = copyLocalPoint(bodyA, pivot);
        B2Vec2 localB = copyLocalPoint(bodyB, pivot);
        jointDef.SetBodyIdA(bodyA.GetId());
        jointDef.SetBodyIdB(bodyB.GetId());
        jointDef.SetLocalAnchorA(localA);
        jointDef.SetLocalAnchorB(localB);
        jointDef.SetMaxMotorTorque(10.0f);
        jointDef.SetEnableMotor(true);
        jointDef.SetHertz(3.0f);
        jointDef.SetDampingRatio(0.8f);
        jointDef.SetEnableSpring(true);
        createRevoluteJoint(jointDef);
        release(localB, localA, pivot, jointDef);
    }

    private void createFriendlyMover() {
        B2Body body = createStaticBody(32.0f, 4.5f, 0.0f);
        B2ShapeDef shapeDef = filteredShapeDef(0.0f, 0.6f, 0.0f, 0.0f, MOVER_BIT, ALL_BITS);
        B2Vec2 centerOne = new B2Vec2(0.0f, -CAPSULE_HALF_LENGTH);
        B2Vec2 centerTwo = new B2Vec2(0.0f, CAPSULE_HALF_LENGTH);
        B2Capsule capsule = new B2Capsule(centerOne, centerTwo, CAPSULE_RADIUS);
        createCapsuleShape(body, shapeDef, capsule);
        release(capsule, centerTwo, centerOne, shapeDef);
    }

    private void createDebrisBall() {
        B2Body body = createDynamicBody(7.0f, 7.0f, 0.0f);
        B2ShapeDef shapeDef = filteredShapeDef(1.0f, 0.6f, 0.7f, 0.2f, DEBRIS_BIT, ALL_BITS);
        B2Vec2 center = new B2Vec2(0.0f, 0.0f);
        B2Circle circle = new B2Circle(center, 0.3f);
        createCircleShape(body, shapeDef, circle);
        release(circle, center, shapeDef);
    }

    private B2Body createElevator() {
        B2Body body = createKinematicBody(ELEVATOR_X, ELEVATOR_Y - ELEVATOR_AMPLITUDE, 0.0f);
        B2ShapeDef shapeDef = filteredShapeDef(1.0f, 0.6f, 0.0f, 0.0f, DYNAMIC_BIT, ALL_BITS);
        B2Polygon box = B2Polygon.CreateBox(2.0f, 0.1f);
        createPolygonShape(body, shapeDef, box);
        release(box, shapeDef);
        return body;
    }

    private B2ShapeDef filteredShapeDef(float density, float shapeFriction, float restitution,
            float rollingResistance, long categoryBits, long maskBits) {
        B2ShapeDef shapeDef = shapeDef(density, shapeFriction, restitution, rollingResistance);
        B2Filter filter = new B2Filter();
        filter.SetCategoryBits(categoryBits);
        filter.SetMaskBits(maskBits);
        shapeDef.SetFilter(filter);
        release(filter);
        return shapeDef;
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        float y = ELEVATOR_AMPLITUDE * (float)Math.cos(time + PI) + ELEVATOR_Y;
        B2Vec2 position = new B2Vec2(ELEVATOR_X, y);
        B2Rot rotation = new B2Rot(0.0f);
        B2Transform target = new B2Transform(position, rotation);
        elevator.SetTargetTransform(target, deltaSeconds);
        time += deltaSeconds;
        kickTimer = Math.max(0.0f, kickTimer - deltaSeconds);
        release(target, rotation, position);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        if(jumpPressed) {
            if(onGround && jumpReleased) {
                velocityY = jumpSpeed;
                onGround = false;
                jumpReleased = false;
            }
        }
        else {
            jumpReleased = true;
        }

        float throttle = (leftPressed ? -1.0f : 0.0f) + (rightPressed ? 1.0f : 0.0f);
        solveMove(deltaSeconds, throttle);
    }

    private void solveMove(float deltaSeconds, float throttle) {
        float speed = length(velocityX, velocityY);
        if(speed < minSpeed) {
            velocityX = 0.0f;
            velocityY = 0.0f;
        }
        else if(onGround) {
            float control = speed < stopSpeed ? stopSpeed : speed;
            float drop = control * friction * deltaSeconds;
            float newSpeed = Math.max(0.0f, speed - drop);
            float ratio = newSpeed / speed;
            velocityX *= ratio;
            velocityY *= ratio;
        }

        float desiredVelocity = maxSpeed * throttle;
        float desiredSpeed = Math.abs(desiredVelocity);
        float desiredDirection = desiredSpeed > 0.0f ? desiredVelocity / desiredSpeed : 0.0f;
        desiredSpeed = Math.min(desiredSpeed, maxSpeed);
        if(onGround) velocityY = 0.0f;

        float currentSpeed = velocityX * desiredDirection;
        float addSpeed = desiredSpeed - currentSpeed;
        if(addSpeed > 0.0f) {
            float steer = onGround ? 1.0f : airSteer;
            float accelerationSpeed = steer * accelerate * maxSpeed * deltaSeconds;
            velocityX += Math.min(accelerationSpeed, addSpeed) * desiredDirection;
        }
        velocityY -= gravity * deltaSeconds;

        castPogo(deltaSeconds);

        B2Vec2 centerOne = new B2Vec2(moverX, moverY - CAPSULE_HALF_LENGTH);
        B2Vec2 centerTwo = new B2Vec2(moverX, moverY + CAPSULE_HALF_LENGTH);
        B2Capsule mover = new B2Capsule(centerOne, centerTwo, CAPSULE_RADIUS);
        B2Vec2 translation = new B2Vec2(deltaSeconds * velocityX,
                deltaSeconds * (velocityY + pogoVelocity));
        B2Vec2 velocity = new B2Vec2(velocityX, velocityY);
        B2QueryFilter collideFilter = queryFilter(MOVER_BIT, STATIC_BIT | DYNAMIC_BIT | MOVER_BIT);
        B2QueryFilter castFilter = queryFilter(MOVER_BIT, STATIC_BIT | DYNAMIC_BIT);
        B2MoverResult result = world().SolveMover(mover, translation, velocity, collideFilter, castFilter, 5);

        B2Vec2 actualTranslation = result.GetTranslation();
        B2Vec2 clippedVelocity = result.GetClippedVelocity();
        moverX += actualTranslation.GetX();
        moverY += actualTranslation.GetY();
        velocityX = clippedVelocity.GetX();
        velocityY = clippedVelocity.GetY();
        totalIterations = result.GetIterationCount();
        planeCount = Math.min(planeNormalX.length, result.GetPlaneCount());
        for(int i = 0; i < planeCount; i++) {
            B2Vec2 normal = result.GetPlaneNormal(i);
            planeNormalX[i] = normal.GetX();
            planeNormalY[i] = normal.GetY();
            planeOffset[i] = result.GetPlaneOffset(i);
        }

        release(result, castFilter, collideFilter, velocity, translation, mover, centerTwo, centerOne);
    }

    private void castPogo(float deltaSeconds) {
        float pogoRestLength = 3.0f * CAPSULE_RADIUS;
        float rayLength = pogoRestLength + CAPSULE_RADIUS;
        pogoOriginX = moverX;
        pogoOriginY = moverY - CAPSULE_HALF_LENGTH;
        float segmentOffset = 0.75f * CAPSULE_RADIUS;
        pogoPoint1X = pogoOriginX - segmentOffset;
        pogoPoint1Y = pogoOriginY;
        pogoPoint2X = pogoOriginX + segmentOffset;
        pogoPoint2Y = pogoOriginY;

        B2ShapeProxy proxy = new B2ShapeProxy();
        if(pogoShape == POGO_SEGMENT) {
            B2Vec2 pointOne = new B2Vec2(pogoPoint1X, pogoPoint1Y);
            B2Vec2 pointTwo = new B2Vec2(pogoPoint2X, pogoPoint2Y);
            proxy.AddPoint(pointOne);
            proxy.AddPoint(pointTwo);
            release(pointTwo, pointOne);
        }
        else {
            B2Vec2 origin = new B2Vec2(pogoOriginX, pogoOriginY);
            proxy.AddPoint(origin);
            proxy.SetRadius(pogoShape == POGO_CIRCLE ? 0.5f * CAPSULE_RADIUS : 0.0f);
            release(origin);
        }

        pogoDeltaX = 0.0f;
        pogoDeltaY = pogoShape == POGO_CIRCLE ? -rayLength + 0.5f * CAPSULE_RADIUS : -rayLength;
        B2Vec2 castTranslation = new B2Vec2(pogoDeltaX, pogoDeltaY);
        B2QueryFilter filter = queryFilter(MOVER_BIT, STATIC_BIT | DYNAMIC_BIT);
        B2WorldCastResult result = world().CastShape(proxy, castTranslation, filter);
        float fraction = 1.0f;
        long hitShapeId = 0L;
        float hitPointX = 0.0f;
        float hitPointY = 0.0f;
        pogoHit = false;
        for(int i = 0; i < result.GetHitCount(); i++) {
            B2WorldCastHit hit = result.GetHit(i);
            if(!pogoHit || hit.GetFraction() < fraction) {
                B2Vec2 point = hit.GetPoint();
                fraction = hit.GetFraction();
                hitShapeId = hit.GetShapeId();
                hitPointX = point.GetX();
                hitPointY = point.GetY();
                pogoHit = true;
            }
        }

        if(!onGround) onGround = pogoHit && velocityY <= 0.01f;
        else onGround = pogoHit;

        if(!pogoHit) {
            pogoVelocity = 0.0f;
        }
        else {
            float currentLength = fraction * rayLength;
            float offset = currentLength - pogoRestLength;
            pogoVelocity = springDamper(pogoHertz, pogoDampingRatio, offset, pogoVelocity, deltaSeconds);
            pogoDeltaX *= fraction;
            pogoDeltaY *= fraction;
            applyPogoForce(hitShapeId, hitPointX, hitPointY);
        }

        release(result, filter, castTranslation, proxy);
    }

    private void applyPogoForce(long shapeId, float pointX, float pointY) {
        B2Shape shape = new B2Shape(shapeId);
        if(shape.IsValid()) {
            B2Body body = new B2Body(shape.GetBodyId());
            if(body.IsValid()) {
                B2Vec2 force = new B2Vec2(0.0f, -50.0f);
                B2Vec2 point = new B2Vec2(pointX, pointY);
                body.ApplyForce(force, point, true);
                release(point, force);
            }
            release(body);
        }
        release(shape);
    }

    private static float springDamper(float hertz, float dampingRatio, float position, float velocity,
            float deltaSeconds) {
        float omega = 2.0f * PI * hertz;
        float omegaStep = omega * deltaSeconds;
        return (velocity - omega * omegaStep * position)
                / (1.0f + 2.0f * dampingRatio * omegaStep + omegaStep * omegaStep);
    }

    private static B2QueryFilter queryFilter(long categoryBits, long maskBits) {
        B2QueryFilter filter = new B2QueryFilter();
        filter.SetCategoryBits(categoryBits);
        filter.SetMaskBits(maskBits);
        return filter;
    }

    @Override
    public void keyDown(int key) {
        if(key == 'A') leftPressed = true;
        else if(key == 'D') rightPressed = true;
        else if(key == ' ') jumpPressed = true;
        else if(key == 'K') kick();
    }

    @Override
    public void keyUp(int key) {
        if(key == 'A') leftPressed = false;
        else if(key == 'D') rightPressed = false;
        else if(key == ' ') jumpPressed = false;
    }

    private void kick() {
        kickX = moverX;
        kickY = moverY - CAPSULE_HALF_LENGTH - 3.0f * CAPSULE_RADIUS;
        kickTimer = 0.12f;
        B2ShapeProxy proxy = new B2ShapeProxy();
        B2Vec2 center = new B2Vec2(kickX, kickY);
        proxy.AddPoint(center);
        proxy.SetRadius(0.5f);
        B2QueryFilter filter = queryFilter(MOVER_BIT, DEBRIS_BIT);
        B2WorldOverlapResult result = world().OverlapShape(proxy, filter);
        long previousBodyId = 0L;
        for(int i = 0; i < result.GetShapeCount(); i++) {
            B2Shape shape = new B2Shape(result.GetShapeId(i));
            if(shape.IsValid()) {
                long bodyId = shape.GetBodyId();
                if(bodyId != previousBodyId) {
                    B2Body body = new B2Body(bodyId);
                    if(body.IsValid() && body.GetType() == B2.DynamicBody()) {
                        B2Vec2 bodyCenter = body.GetWorldCenterOfMass();
                        float directionX = bodyCenter.GetX() - moverX;
                        float directionY = bodyCenter.GetY() - moverY;
                        float length = length(directionX, directionY);
                        if(length > 0.0001f) directionX /= length;
                        else directionX = 0.0f;
                        B2Vec2 impulse = new B2Vec2(2.0f * directionX, 2.0f);
                        body.ApplyLinearImpulseToCenter(impulse, true);
                        release(impulse);
                    }
                    release(body);
                    previousBodyId = bodyId;
                }
            }
            release(shape);
        }
        release(result, filter, center, proxy);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        int moverColor = onGround ? 0xFFA500FF : 0x7FFFD4FF;
        float lowerY = moverY - CAPSULE_HALF_LENGTH;
        float upperY = moverY + CAPSULE_HALF_LENGTH;
        draw.segment(moverX, lowerY, moverX, upperY, moverColor);
        draw.circle(moverX, lowerY, CAPSULE_RADIUS, moverColor);
        draw.circle(moverX, upperY, CAPSULE_RADIUS, moverColor);
        draw.segment(moverX, moverY, moverX + velocityX, moverY + velocityY, 0x800080FF);

        int pogoColor = pogoHit ? 0xDDA0DDFF : 0x808080FF;
        draw.segment(pogoOriginX, pogoOriginY, pogoOriginX + pogoDeltaX, pogoOriginY + pogoDeltaY,
                0x808080FF);
        if(pogoShape == POGO_POINT) {
            draw.point(pogoOriginX + pogoDeltaX, pogoOriginY + pogoDeltaY, 10.0f, pogoColor);
        }
        else if(pogoShape == POGO_CIRCLE) {
            draw.circle(pogoOriginX + pogoDeltaX, pogoOriginY + pogoDeltaY, 0.5f * CAPSULE_RADIUS, pogoColor);
        }
        else {
            draw.segment(pogoPoint1X + pogoDeltaX, pogoPoint1Y + pogoDeltaY,
                    pogoPoint2X + pogoDeltaX, pogoPoint2Y + pogoDeltaY, pogoColor);
        }

        for(int i = 0; i < planeCount; i++) {
            float x = moverX + (planeOffset[i] - CAPSULE_RADIUS) * planeNormalX[i];
            float y = moverY + (planeOffset[i] - CAPSULE_RADIUS) * planeNormalY[i];
            draw.point(x, y, 5.0f, 0xFFFF00FF);
            draw.segment(x, y, x + 0.1f * planeNormalX[i], y + 0.1f * planeNormalY[i], 0xFFFF00FF);
        }
        if(kickTimer > 0.0f) draw.circle(kickX, kickY, 0.5f, 0xDAA520FF);

        draw.screenText(8.0f, 20.0f, String.format("position %.2f %.2f", moverX, moverY), 0xFFFFFFFF);
        draw.screenText(8.0f, 38.0f, String.format("velocity %.2f %.2f", velocityX, velocityY), 0xFFFFFFFF);
        draw.screenText(8.0f, 56.0f, "iterations " + totalIterations, 0xFFFFFFFF);
    }

    @Override
    public boolean tracksCameraX() {
        return lockCamera;
    }

    @Override
    public float cameraCenterX() {
        return moverX;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("Jump Speed", 0.0f, 40.0f, 1.0f, () -> jumpSpeed, value -> jumpSpeed = value),
                Box2DSampleControl.slider("Min Speed", 0.0f, 1.0f, 0.01f, () -> minSpeed, value -> minSpeed = value),
                Box2DSampleControl.slider("Max Speed", 0.0f, 20.0f, 1.0f, () -> maxSpeed, value -> maxSpeed = value),
                Box2DSampleControl.slider("Stop Speed", 0.0f, 10.0f, 0.1f, () -> stopSpeed, value -> stopSpeed = value),
                Box2DSampleControl.slider("Accelerate", 0.0f, 100.0f, 1.0f, () -> accelerate, value -> accelerate = value),
                Box2DSampleControl.slider("Friction", 0.0f, 10.0f, 0.1f, () -> friction, value -> friction = value),
                Box2DSampleControl.slider("Gravity", 0.0f, 100.0f, 0.1f, () -> gravity, value -> gravity = value),
                Box2DSampleControl.slider("Air Steer", 0.0f, 1.0f, 0.01f, () -> airSteer, value -> airSteer = value),
                Box2DSampleControl.slider("Pogo Hertz", 0.0f, 30.0f, 1.0f, () -> pogoHertz, value -> pogoHertz = value),
                Box2DSampleControl.slider("Pogo Damping", 0.0f, 4.0f, 0.1f,
                        () -> pogoDampingRatio, value -> pogoDampingRatio = value),
                Box2DSampleControl.text("Pogo Shape"),
                Box2DSampleControl.radio("Point", () -> pogoShape == POGO_POINT ? 1.0f : 0.0f,
                        () -> pogoShape = POGO_POINT),
                Box2DSampleControl.radio("Circle", () -> pogoShape == POGO_CIRCLE ? 1.0f : 0.0f,
                        () -> pogoShape = POGO_CIRCLE),
                Box2DSampleControl.radio("Segment", () -> pogoShape == POGO_SEGMENT ? 1.0f : 0.0f,
                        () -> pogoShape = POGO_SEGMENT),
                Box2DSampleControl.checkbox("Lock Camera", () -> lockCamera ? 1.0f : 0.0f,
                        value -> lockCamera = value != 0.0f),
                Box2DSampleControl.text("A/D move, Space jumps, K kicks debris"));
    }

    private static float length(float x, float y) {
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Matches the official testbed's straight-line SVG path parser. */
    private static float[] parsePath(String path, float offsetX, float offsetY, float scale) {
        ArrayList<Float> points = new ArrayList<Float>(128);
        int[] cursor = {0};
        char command = 0;
        float currentX = 0.0f;
        float currentY = 0.0f;
        while(true) {
            skipSeparators(path, cursor);
            if(cursor[0] >= path.length()) break;
            char c = path.charAt(cursor[0]);
            if(isCommand(c)) {
                command = c;
                cursor[0]++;
                if(command == 'z' || command == 'Z') break;
                continue;
            }

            float first = readNumber(path, cursor);
            if(command == 'M' || command == 'L' || command == 'm' || command == 'l') {
                float second = readNumber(path, cursor);
                if(command == 'M' || command == 'L') {
                    currentX = first;
                    currentY = second;
                }
                else {
                    currentX += first;
                    currentY += second;
                }
            }
            else if(command == 'H') currentX = first;
            else if(command == 'h') currentX += first;
            else if(command == 'V') currentY = first;
            else if(command == 'v') currentY += first;
            else throw new IllegalArgumentException("Unsupported SVG path command: " + command);

            points.add(scale * (currentX + offsetX));
            points.add(-scale * (currentY + offsetY));
        }

        float[] result = new float[points.size()];
        for(int i = 0; i < result.length; i++) result[i] = points.get(i);
        return result;
    }

    private static boolean isCommand(char c) {
        return c == 'M' || c == 'L' || c == 'H' || c == 'V' || c == 'm' || c == 'l'
                || c == 'h' || c == 'v' || c == 'z' || c == 'Z';
    }

    private static void skipSeparators(String value, int[] cursor) {
        while(cursor[0] < value.length()) {
            char c = value.charAt(cursor[0]);
            if(Character.isWhitespace(c) || c == ',') cursor[0]++;
            else break;
        }
    }

    private static float readNumber(String value, int[] cursor) {
        skipSeparators(value, cursor);
        int start = cursor[0];
        while(cursor[0] < value.length()) {
            char c = value.charAt(cursor[0]);
            if((c >= '0' && c <= '9') || c == '.' || c == 'e' || c == 'E'
                    || ((c == '-' || c == '+') && (cursor[0] == start
                    || value.charAt(cursor[0] - 1) == 'e' || value.charAt(cursor[0] - 1) == 'E'))) {
                cursor[0]++;
            }
            else break;
        }
        if(start == cursor[0]) throw new IllegalArgumentException("Expected SVG number at " + start);
        return Float.parseFloat(value.substring(start, cursor[0]));
    }
}
