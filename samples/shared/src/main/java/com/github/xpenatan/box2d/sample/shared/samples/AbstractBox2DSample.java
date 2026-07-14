package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2AABB;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Capsule;
import com.github.xpenatan.box2d.B2Chain;
import com.github.xpenatan.box2d.B2ChainDef;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2DistanceJointDef;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2FilterJointDef;
import com.github.xpenatan.box2d.B2Hull;
import com.github.xpenatan.box2d.B2Joint;
import com.github.xpenatan.box2d.B2MotorJointDef;
import com.github.xpenatan.box2d.B2MouseJointDef;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2PrismaticJointDef;
import com.github.xpenatan.box2d.B2QueryFilter;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Segment;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.B2WeldJointDef;
import com.github.xpenatan.box2d.B2WheelJointDef;
import com.github.xpenatan.box2d.B2World;
import com.github.xpenatan.box2d.B2WorldDef;
import com.github.xpenatan.box2d.B2WorldOverlapResult;
import com.github.xpenatan.box2d.sample.shared.Box2DSample;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleSettings;
import com.github.xpenatan.jParser.api.NativeObject;
import java.util.ArrayList;
import java.util.List;

/** Common Java-side testbed behavior and allocation-safe Box2D construction helpers. */
public abstract class AbstractBox2DSample implements Box2DSample {
    protected static final float PI = (float)Math.PI;

    private final B2World world;
    private final int fallbackSubStepCount;
    private final List<NativeObject> ownedHandles = new ArrayList<NativeObject>();
    private B2Body mouseBody;
    private B2Body mouseGroundBody;
    private B2Joint mouseJoint;
    private int randomState = 0x1234ABCD;
    private int bodyCount;
    private int shapeCount;
    private int jointCount;
    private boolean disposed;

    protected AbstractBox2DSample() {
        this(4, 0.0f, -10.0f);
    }

    protected AbstractBox2DSample(int subStepCount) {
        this(subStepCount, 0.0f, -10.0f);
    }

    protected AbstractBox2DSample(int subStepCount, float gravityX, float gravityY) {
        B2WorldDef worldDef = new B2WorldDef();
        B2Vec2 gravity = new B2Vec2(gravityX, gravityY);
        worldDef.SetGravity(gravity);
        world = new B2World(worldDef);
        fallbackSubStepCount = subStepCount;
        release(gravity, worldDef);
    }

    @Override
    public final void step(float deltaSeconds, Box2DSampleSettings settings) {
        if(disposed || !world.IsValid()) {
            return;
        }
        int subSteps = fallbackSubStepCount;
        if(settings != null) {
            world.EnableSleeping(settings.sleepEnabled());
            world.EnableWarmStarting(settings.warmStartingEnabled());
            world.EnableContinuous(settings.continuousEnabled());
            subSteps = settings.subStepCount();
        }
        float step = Math.max(1.0f / Box2DSampleSettings.MAX_HERTZ,
                Math.min(deltaSeconds, 1.0f / Box2DSampleSettings.MIN_HERTZ));
        beforeStep(step);
        world.Step(step, subSteps);
        afterStep(step);
    }

    protected void beforeStep(float deltaSeconds) {
    }

    protected void afterStep(float deltaSeconds) {
    }

    @Override
    public final B2World world() {
        return world;
    }

    @Override
    public int bodyCount() {
        return bodyCount;
    }

    @Override
    public int shapeCount() {
        return shapeCount;
    }

    @Override
    public int jointCount() {
        return jointCount;
    }

    /** Matches the official sample framework's left-button dynamic-body dragging. */
    @Override
    public void mouseDown(float x, float y, int button, int modifiers) {
        if(disposed || button != 0 || mouseJoint != null) {
            return;
        }

        B2Vec2 point = new B2Vec2(x, y);
        B2Vec2 lower = new B2Vec2(x - 0.001f, y - 0.001f);
        B2Vec2 upper = new B2Vec2(x + 0.001f, y + 0.001f);
        B2AABB bounds = new B2AABB(lower, upper);
        B2QueryFilter filter = new B2QueryFilter();
        B2WorldOverlapResult overlaps = world.OverlapAABB(bounds, filter);

        for(int i = 0; i < overlaps.GetShapeCount(); i++) {
            B2Shape shape = new B2Shape(overlaps.GetShapeId(i));
            B2Body body = null;
            if(shape.IsValid()) {
                body = new B2Body(shape.GetBodyId());
                if(body.IsValid() && body.GetType() == B2.DynamicBody() && shape.TestPoint(point)) {
                    mouseBody = body;
                    body = null;
                }
            }
            release(body, shape);
            if(mouseBody != null) {
                break;
            }
        }

        if(mouseBody != null) {
            mouseGroundBody = createStaticBody(0.0f, 0.0f, 0.0f);
            B2Vec2 gravity = world.GetGravity();
            float gravityMagnitude = (float)Math.sqrt(gravity.GetX() * gravity.GetX()
                    + gravity.GetY() * gravity.GetY());
            B2MouseJointDef jointDef = new B2MouseJointDef();
            jointDef.SetBodyIdA(mouseGroundBody.GetId());
            jointDef.SetBodyIdB(mouseBody.GetId());
            jointDef.SetTarget(point);
            jointDef.SetHertz(10.0f);
            jointDef.SetDampingRatio(0.7f);
            jointDef.SetMaxForce(1000.0f * mouseBody.GetMass() * gravityMagnitude);
            mouseJoint = createMouseJoint(jointDef);
            mouseBody.SetAwake(true);
            release(jointDef, gravity);
        }

        release(overlaps, filter, bounds, upper, lower, point);
    }

    @Override
    public void mouseMove(float x, float y) {
        if(mouseJoint == null) {
            return;
        }
        if(!mouseJoint.IsValid()) {
            endMouseDrag();
            return;
        }
        B2Vec2 target = new B2Vec2(x, y);
        mouseJoint.MouseSetTarget(target);
        if(mouseBody != null && mouseBody.IsValid()) {
            mouseBody.SetAwake(true);
        }
        release(target);
    }

    @Override
    public void mouseUp(float x, float y, int button) {
        if(button == 0) {
            endMouseDrag();
        }
    }

    @Override
    public void dispose() {
        if(disposed) {
            return;
        }
        endMouseDrag();
        disposed = true;
        if(world.IsValid()) {
            world.Destroy();
        }
        for(int i = ownedHandles.size() - 1; i >= 0; i--) {
            release(ownedHandles.get(i));
        }
        ownedHandles.clear();
        release(world);
    }

    protected B2Body createBody(int type, float x, float y, float angle) {
        B2BodyDef bodyDef = new B2BodyDef();
        B2Vec2 position = new B2Vec2(x, y);
        bodyDef.SetType(type);
        bodyDef.SetPosition(position);
        bodyDef.SetAngle(angle);
        B2Body body = own(world.CreateBody(bodyDef));
        bodyCount++;
        release(position, bodyDef);
        return body;
    }

    /** Creates and tracks a body from a fully configured definition. */
    protected B2Body createBody(B2BodyDef bodyDef) {
        B2Body body = own(world.CreateBody(bodyDef));
        bodyCount++;
        return body;
    }

    protected B2Body createDynamicBody(float x, float y, float angle) {
        return createBody(B2.DynamicBody(), x, y, angle);
    }

    protected B2Body createStaticBody(float x, float y, float angle) {
        return createBody(B2.StaticBody(), x, y, angle);
    }

    protected B2Body createKinematicBody(float x, float y, float angle) {
        return createBody(B2.KinematicBody(), x, y, angle);
    }

    protected B2Body addGroundSegment(float x1, float y1, float x2, float y2) {
        B2Body body = createStaticBody(0.0f, 0.0f, 0.0f);
        addSegmentShape(body, x1, y1, x2, y2, 0.0f, 0.6f, 0.0f);
        return body;
    }

    protected B2Body addGroundBox(float halfWidth) {
        return addBox(B2.StaticBody(), 0.0f, -0.5f, halfWidth, 0.5f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f);
    }

    protected B2Body addStaticBox(float x, float y, float halfWidth, float halfHeight, float angle) {
        return addBox(B2.StaticBody(), x, y, halfWidth, halfHeight, angle, 0.0f, 0.6f, 0.0f, 0.0f);
    }

    protected B2Body addDynamicBox(float x, float y, float halfWidth, float halfHeight) {
        return addDynamicBox(x, y, halfWidth, halfHeight, 0.0f);
    }

    protected B2Body addDynamicBox(float x, float y, float halfWidth, float halfHeight, float angle) {
        return addBox(B2.DynamicBody(), x, y, halfWidth, halfHeight, angle, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    protected B2Body addDynamicBox(float x, float y, float halfWidth, float halfHeight, float angle, float density,
            float friction, float restitution, float rollingResistance) {
        return addBox(B2.DynamicBody(), x, y, halfWidth, halfHeight, angle, density, friction, restitution,
                rollingResistance);
    }

    protected B2Body addDynamicCircle(float x, float y, float radius) {
        return addCircle(B2.DynamicBody(), x, y, 0.0f, 0.0f, radius, 1.0f, 0.6f, 0.0f, 0.0f);
    }

    protected B2Body addDynamicCircle(float x, float y, float radius, float density, float friction,
            float restitution, float rollingResistance) {
        return addCircle(B2.DynamicBody(), x, y, 0.0f, 0.0f, radius, density, friction, restitution,
                rollingResistance);
    }

    protected B2Body addDynamicCapsule(float x, float y, float halfLength, float radius, float angle) {
        B2Body body = createDynamicBody(x, y, angle);
        addCapsuleShape(body, 0.0f, -halfLength, 0.0f, halfLength, radius, 1.0f, 0.6f, 0.0f, 0.0f);
        return body;
    }

    protected B2Shape addBoxShape(B2Body body, float halfWidth, float halfHeight, float density, float friction,
            float restitution, float rollingResistance) {
        B2Polygon polygon = B2Polygon.CreateBox(halfWidth, halfHeight);
        B2Shape shape = createPolygonShape(body, polygon, density, friction, restitution, rollingResistance, null);
        release(polygon);
        return shape;
    }

    protected B2Shape addOffsetBoxShape(B2Body body, float halfWidth, float halfHeight, float offsetX, float offsetY,
            float angle, float density, float friction, float restitution, float rollingResistance) {
        B2Vec2 center = new B2Vec2(offsetX, offsetY);
        B2Rot rotation = new B2Rot(angle);
        B2Polygon polygon = B2Polygon.CreateOffsetBox(halfWidth, halfHeight, center, rotation);
        B2Shape shape = createPolygonShape(body, polygon, density, friction, restitution, rollingResistance, null);
        release(polygon, rotation, center);
        return shape;
    }

    protected B2Shape addRoundedBoxShape(B2Body body, float halfWidth, float halfHeight, float radius, float density,
            float friction, float restitution, float rollingResistance) {
        B2Polygon polygon = B2Polygon.CreateRoundedBox(halfWidth, halfHeight, radius);
        B2Shape shape = createPolygonShape(body, polygon, density, friction, restitution, rollingResistance, null);
        release(polygon);
        return shape;
    }

    protected B2Shape addPolygonShape(B2Body body, float[] vertices, float radius, float density, float friction,
            float restitution, float rollingResistance) {
        if(vertices == null || vertices.length < 6 || (vertices.length & 1) != 0) {
            throw new IllegalArgumentException("A polygon requires at least three x/y vertices");
        }
        B2Hull hull = new B2Hull();
        for(int i = 0; i < vertices.length; i += 2) {
            B2Vec2 point = new B2Vec2(vertices[i], vertices[i + 1]);
            hull.AddPoint(point);
            release(point);
        }
        if(!hull.Compute()) {
            release(hull);
            throw new IllegalArgumentException("The polygon vertices do not form a valid convex hull");
        }
        B2Polygon polygon = B2Polygon.CreateFromHull(hull, radius);
        B2Shape shape = createPolygonShape(body, polygon, density, friction, restitution, rollingResistance, null);
        release(polygon, hull);
        return shape;
    }

    protected B2Shape addCircleShape(B2Body body, float centerX, float centerY, float radius, float density,
            float friction, float restitution, float rollingResistance) {
        B2Vec2 center = new B2Vec2(centerX, centerY);
        B2Circle circle = new B2Circle(center, radius);
        B2ShapeDef shapeDef = shapeDef(density, friction, restitution, rollingResistance);
        B2Shape shape = own(body.CreateCircleShape(shapeDef, circle));
        shapeCount++;
        release(circle, center, shapeDef);
        return shape;
    }

    protected B2Shape addCapsuleShape(B2Body body, float x1, float y1, float x2, float y2, float radius,
            float density, float friction, float restitution, float rollingResistance) {
        B2Vec2 center1 = new B2Vec2(x1, y1);
        B2Vec2 center2 = new B2Vec2(x2, y2);
        B2Capsule capsule = new B2Capsule(center1, center2, radius);
        B2ShapeDef shapeDef = shapeDef(density, friction, restitution, rollingResistance);
        B2Shape shape = own(body.CreateCapsuleShape(shapeDef, capsule));
        shapeCount++;
        release(capsule, center2, center1, shapeDef);
        return shape;
    }

    protected B2Shape addSegmentShape(B2Body body, float x1, float y1, float x2, float y2, float density,
            float friction, float restitution) {
        B2Vec2 point1 = new B2Vec2(x1, y1);
        B2Vec2 point2 = new B2Vec2(x2, y2);
        B2Segment segment = new B2Segment(point1, point2);
        B2ShapeDef shapeDef = shapeDef(density, friction, restitution, 0.0f);
        B2Shape shape = own(body.CreateSegmentShape(shapeDef, segment));
        shapeCount++;
        release(segment, point2, point1, shapeDef);
        return shape;
    }

    protected B2Chain addChain(B2Body body, float[] points, boolean loop, float friction) {
        if(points == null || points.length < 8 || (points.length & 1) != 0) {
            throw new IllegalArgumentException("A Box2D chain requires at least four x/y points");
        }
        B2ChainDef chainDef = new B2ChainDef();
        chainDef.SetIsLoop(loop);
        B2SurfaceMaterial material = new B2SurfaceMaterial();
        material.SetFriction(friction);
        chainDef.ClearMaterials();
        chainDef.AddMaterial(material);
        for(int i = 0; i < points.length; i += 2) {
            B2Vec2 point = new B2Vec2(points[i], points[i + 1]);
            chainDef.AddPoint(point);
            release(point);
        }
        B2Chain chain = own(body.CreateChain(chainDef));
        shapeCount += Math.max(0, points.length / 2 - (loop ? 0 : 3));
        release(material, chainDef);
        return chain;
    }

    protected B2Joint addDistanceJoint(B2Body bodyA, B2Body bodyB, float worldAnchorAX, float worldAnchorAY,
            float worldAnchorBX, float worldAnchorBY, float length, float hertz, float dampingRatio,
            boolean collideConnected) {
        B2DistanceJointDef def = new B2DistanceJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        setLocalAnchors(def, bodyA, bodyB, worldAnchorAX, worldAnchorAY, worldAnchorBX, worldAnchorBY);
        def.SetLength(length);
        def.SetEnableSpring(hertz > 0.0f);
        def.SetHertz(hertz);
        def.SetDampingRatio(dampingRatio);
        def.SetCollideConnected(collideConnected);
        B2Joint joint = own(world.CreateDistanceJoint(def));
        jointCount++;
        release(def);
        return joint;
    }

    protected B2Joint addRevoluteJoint(B2Body bodyA, B2Body bodyB, float worldX, float worldY,
            boolean collideConnected) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 anchor = new B2Vec2(worldX, worldY);
        B2Vec2 localA = copyLocalPoint(bodyA, anchor);
        B2Vec2 localB = copyLocalPoint(bodyB, anchor);
        float angleA = bodyA.GetRotation().GetAngle();
        float angleB = bodyB.GetRotation().GetAngle();
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetReferenceAngle(angleB - angleA);
        def.SetCollideConnected(collideConnected);
        B2Joint joint = own(world.CreateRevoluteJoint(def));
        jointCount++;
        release(localB, localA, anchor, def);
        return joint;
    }

    protected B2Joint addWeldJoint(B2Body bodyA, B2Body bodyB, float worldX, float worldY, float hertz,
            float dampingRatio) {
        B2WeldJointDef def = new B2WeldJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 anchor = new B2Vec2(worldX, worldY);
        B2Vec2 localA = copyLocalPoint(bodyA, anchor);
        B2Vec2 localB = copyLocalPoint(bodyB, anchor);
        float angleA = bodyA.GetRotation().GetAngle();
        float angleB = bodyB.GetRotation().GetAngle();
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetReferenceAngle(angleB - angleA);
        def.SetLinearHertz(hertz);
        def.SetAngularHertz(hertz);
        def.SetLinearDampingRatio(dampingRatio);
        def.SetAngularDampingRatio(dampingRatio);
        B2Joint joint = own(world.CreateWeldJoint(def));
        jointCount++;
        release(localB, localA, anchor, def);
        return joint;
    }

    protected B2Joint addPrismaticJoint(B2Body bodyA, B2Body bodyB, float worldX, float worldY, float axisX,
            float axisY, float lower, float upper, boolean enableLimit) {
        B2PrismaticJointDef def = new B2PrismaticJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 anchor = new B2Vec2(worldX, worldY);
        B2Vec2 localA = copyLocalPoint(bodyA, anchor);
        B2Vec2 localB = copyLocalPoint(bodyB, anchor);
        B2Vec2 axis = new B2Vec2(axisX, axisY);
        B2Rot rotationA = bodyA.GetRotation();
        B2Vec2 localAxis = rotationA.InverseRotateVector(axis);
        float angleA = rotationA.GetAngle();
        float angleB = bodyB.GetRotation().GetAngle();
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetLocalAxisA(localAxis);
        def.SetReferenceAngle(angleB - angleA);
        def.SetEnableLimit(enableLimit);
        def.SetLowerTranslation(lower);
        def.SetUpperTranslation(upper);
        B2Joint joint = own(world.CreatePrismaticJoint(def));
        jointCount++;
        release(localAxis, rotationA, axis, localB, localA, anchor, def);
        return joint;
    }

    protected B2Joint addWheelJoint(B2Body bodyA, B2Body bodyB, float worldX, float worldY, float axisX,
            float axisY, float hertz, float dampingRatio) {
        B2WheelJointDef def = new B2WheelJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 anchor = new B2Vec2(worldX, worldY);
        B2Vec2 localA = copyLocalPoint(bodyA, anchor);
        B2Vec2 localB = copyLocalPoint(bodyB, anchor);
        B2Vec2 axis = new B2Vec2(axisX, axisY);
        B2Rot rotationA = bodyA.GetRotation();
        B2Vec2 localAxis = rotationA.InverseRotateVector(axis);
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        def.SetLocalAxisA(localAxis);
        def.SetEnableSpring(true);
        def.SetHertz(hertz);
        def.SetDampingRatio(dampingRatio);
        B2Joint joint = own(world.CreateWheelJoint(def));
        jointCount++;
        release(localAxis, rotationA, axis, localB, localA, anchor, def);
        return joint;
    }

    protected B2Joint addMotorJoint(B2Body bodyA, B2Body bodyB, float maxForce, float maxTorque,
            float correctionFactor) {
        B2MotorJointDef def = new B2MotorJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Vec2 positionB = bodyB.GetPosition();
        B2Vec2 localOffset = copyLocalPoint(bodyA, positionB);
        float angleA = bodyA.GetRotation().GetAngle();
        float angleB = bodyB.GetRotation().GetAngle();
        def.SetLinearOffset(localOffset);
        def.SetAngularOffset(angleB - angleA);
        def.SetMaxForce(maxForce);
        def.SetMaxTorque(maxTorque);
        def.SetCorrectionFactor(correctionFactor);
        B2Joint joint = own(world.CreateMotorJoint(def));
        jointCount++;
        release(localOffset, positionB, def);
        return joint;
    }

    protected B2Joint addFilterJoint(B2Body bodyA, B2Body bodyB) {
        B2FilterJointDef def = new B2FilterJointDef();
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        B2Joint joint = own(world.CreateFilterJoint(def));
        jointCount++;
        release(def);
        return joint;
    }

    protected B2Joint createDistanceJoint(B2DistanceJointDef def) {
        B2Joint joint = own(world.CreateDistanceJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createMotorJoint(B2MotorJointDef def) {
        B2Joint joint = own(world.CreateMotorJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createMouseJoint(B2MouseJointDef def) {
        B2Joint joint = own(world.CreateMouseJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createFilterJoint(B2FilterJointDef def) {
        B2Joint joint = own(world.CreateFilterJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createPrismaticJoint(B2PrismaticJointDef def) {
        B2Joint joint = own(world.CreatePrismaticJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createRevoluteJoint(B2RevoluteJointDef def) {
        B2Joint joint = own(world.CreateRevoluteJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createWeldJoint(B2WeldJointDef def) {
        B2Joint joint = own(world.CreateWeldJoint(def));
        jointCount++;
        return joint;
    }

    protected B2Joint createWheelJoint(B2WheelJointDef def) {
        B2Joint joint = own(world.CreateWheelJoint(def));
        jointCount++;
        return joint;
    }

    protected void destroyBody(B2Body body) {
        if(body != null && body.IsValid()) {
            shapeCount = Math.max(0, shapeCount - body.GetShapeCount());
            jointCount = Math.max(0, jointCount - body.GetJointCount());
            body.Destroy();
            bodyCount = Math.max(0, bodyCount - 1);
        }
    }

    protected void destroyJoint(B2Joint joint) {
        if(joint != null && joint.IsValid()) {
            joint.Destroy();
            jointCount = Math.max(0, jointCount - 1);
        }
    }

    private void endMouseDrag() {
        if(mouseJoint != null) {
            if(mouseJoint.IsValid()) {
                destroyJoint(mouseJoint);
            }
            else {
                jointCount = Math.max(0, jointCount - 1);
            }
            discardHandle(mouseJoint);
            mouseJoint = null;
        }
        if(mouseGroundBody != null) {
            if(mouseGroundBody.IsValid()) {
                destroyBody(mouseGroundBody);
            }
            else {
                bodyCount = Math.max(0, bodyCount - 1);
            }
            discardHandle(mouseGroundBody);
            mouseGroundBody = null;
        }
        release(mouseBody);
        mouseBody = null;
    }

    protected void setGravity(float x, float y) {
        B2Vec2 gravity = new B2Vec2(x, y);
        world.SetGravity(gravity);
        release(gravity);
    }

    protected void setLinearVelocity(B2Body body, float x, float y) {
        B2Vec2 velocity = new B2Vec2(x, y);
        body.SetLinearVelocity(velocity);
        release(velocity);
    }

    protected void setTransform(B2Body body, float x, float y, float angle) {
        B2Vec2 position = new B2Vec2(x, y);
        B2Rot rotation = new B2Rot(angle);
        body.SetTransform(position, rotation);
        release(rotation, position);
    }

    protected void applyLinearImpulse(B2Body body, float impulseX, float impulseY, float pointX, float pointY) {
        B2Vec2 impulse = new B2Vec2(impulseX, impulseY);
        B2Vec2 point = new B2Vec2(pointX, pointY);
        body.ApplyLinearImpulse(impulse, point, true);
        release(point, impulse);
    }

    protected void applyLinearImpulseToCenter(B2Body body, float impulseX, float impulseY) {
        B2Vec2 impulse = new B2Vec2(impulseX, impulseY);
        body.ApplyLinearImpulseToCenter(impulse, true);
        release(impulse);
    }

    protected void applyForceToCenter(B2Body body, float forceX, float forceY) {
        B2Vec2 force = new B2Vec2(forceX, forceY);
        body.ApplyForceToCenter(force, true);
        release(force);
    }

    protected float randomFloat(float minimum, float maximum) {
        randomState ^= randomState << 13;
        randomState ^= randomState >>> 17;
        randomState ^= randomState << 5;
        float unit = (randomState & 0x7FFFFFFF) / (float)0x7FFFFFFF;
        return minimum + (maximum - minimum) * unit;
    }

    protected void setRandomSeed(int seed) {
        randomState = seed;
    }

    protected int randomInt(int minimum, int maximumInclusive) {
        return minimum + (int)(randomFloat(0.0f, 0.999999f) * (maximumInclusive - minimum + 1));
    }

    protected static float radians(float degrees) {
        return degrees * PI / 180.0f;
    }

    protected static B2Vec2 vector(float x, float y) {
        return new B2Vec2(x, y);
    }

    /**
     * Copies a value-returning binding before another call can reuse its native
     * scratch storage. This is required when two local points must coexist in a
     * joint definition.
     */
    protected static B2Vec2 copyLocalPoint(B2Body body, B2Vec2 worldPoint) {
        B2Vec2 value = body.GetLocalPoint(worldPoint);
        return new B2Vec2(value.GetX(), value.GetY());
    }

    protected static void release(NativeObject... objects) {
        for(NativeObject object : objects) {
            if(object != null && object.native_hasOwnership() && !object.isDisposed()) {
                object.dispose();
            }
        }
    }

    private B2Body addBox(int type, float x, float y, float halfWidth, float halfHeight, float angle, float density,
            float friction, float restitution, float rollingResistance) {
        B2Body body = createBody(type, x, y, angle);
        addBoxShape(body, halfWidth, halfHeight, density, friction, restitution, rollingResistance);
        return body;
    }

    private B2Body addCircle(int type, float x, float y, float centerX, float centerY, float radius, float density,
            float friction, float restitution, float rollingResistance) {
        B2Body body = createBody(type, x, y, 0.0f);
        addCircleShape(body, centerX, centerY, radius, density, friction, restitution, rollingResistance);
        return body;
    }

    protected B2Shape createPolygonShape(B2Body body, B2Polygon polygon, float density, float friction,
            float restitution, float rollingResistance, B2Filter filter) {
        B2ShapeDef shapeDef = shapeDef(density, friction, restitution, rollingResistance);
        if(filter != null) {
            shapeDef.SetFilter(filter);
        }
        B2Shape shape = own(body.CreatePolygonShape(shapeDef, polygon));
        shapeCount++;
        release(shapeDef);
        return shape;
    }

    protected B2Shape createPolygonShape(B2Body body, B2ShapeDef shapeDef, B2Polygon polygon) {
        B2Shape shape = own(body.CreatePolygonShape(shapeDef, polygon));
        shapeCount++;
        return shape;
    }

    protected B2Shape createCircleShape(B2Body body, B2ShapeDef shapeDef, B2Circle circle) {
        B2Shape shape = own(body.CreateCircleShape(shapeDef, circle));
        shapeCount++;
        return shape;
    }

    protected B2Shape createCapsuleShape(B2Body body, B2ShapeDef shapeDef, B2Capsule capsule) {
        B2Shape shape = own(body.CreateCapsuleShape(shapeDef, capsule));
        shapeCount++;
        return shape;
    }

    protected B2Shape createSegmentShape(B2Body body, B2ShapeDef shapeDef, B2Segment segment) {
        B2Shape shape = own(body.CreateSegmentShape(shapeDef, segment));
        shapeCount++;
        return shape;
    }

    protected B2ShapeDef shapeDef(float density, float friction, float restitution, float rollingResistance) {
        B2ShapeDef shapeDef = new B2ShapeDef();
        B2SurfaceMaterial material = new B2SurfaceMaterial();
        material.SetFriction(friction);
        material.SetRestitution(restitution);
        material.SetRollingResistance(rollingResistance);
        shapeDef.SetDensity(density);
        shapeDef.SetMaterial(material);
        release(material);
        return shapeDef;
    }

    private void setLocalAnchors(B2DistanceJointDef def, B2Body bodyA, B2Body bodyB, float worldAX, float worldAY,
            float worldBX, float worldBY) {
        B2Vec2 anchorA = new B2Vec2(worldAX, worldAY);
        B2Vec2 anchorB = new B2Vec2(worldBX, worldBY);
        B2Vec2 localA = copyLocalPoint(bodyA, anchorA);
        B2Vec2 localB = copyLocalPoint(bodyB, anchorB);
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        release(localB, localA, anchorB, anchorA);
    }

    protected <T extends NativeObject> T own(T object) {
        if(object != null) {
            ownedHandles.add(object);
        }
        return object;
    }

    /** Releases a Java/native handle while leaving the underlying Box2D object in the world. */
    protected void discardHandle(NativeObject object) {
        if(object != null) {
            for(int i = ownedHandles.size() - 1; i >= 0; i--) {
                if(ownedHandles.get(i) == object) {
                    ownedHandles.remove(i);
                    break;
                }
            }
            release(object);
        }
    }
}
