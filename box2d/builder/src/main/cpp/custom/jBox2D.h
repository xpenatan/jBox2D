#pragma once

#include <box2d/box2d.h>
#include <box2d/collision.h>

#include "RuntimeHelper.h"

#include <cstdint>
#include <vector>

namespace JBox2D {

class B2Vec2 {
public:
    b2Vec2 value;

    B2Vec2();
    B2Vec2(float x, float y);
    explicit B2Vec2(b2Vec2 value);

    float GetX() const;
    float GetY() const;
    void SetX(float x);
    void SetY(float y);
    void Set(float x, float y);
    void SetZero();
    float Length() const;
    float LengthSquared() const;
    float Normalize();
    bool IsValid() const;
};

class B2Rot {
public:
    b2Rot value;

    B2Rot();
    explicit B2Rot(float radians);
    explicit B2Rot(b2Rot value);

    float GetCosine() const;
    float GetSine() const;
    float GetAngle() const;
    void Set(float radians);
    void SetIdentity();
    B2Vec2 RotateVector(const B2Vec2& vector) const;
    B2Vec2 InverseRotateVector(const B2Vec2& vector) const;
};

class B2Transform {
public:
    b2Transform value;

    B2Transform();
    B2Transform(const B2Vec2& position, const B2Rot& rotation);
    explicit B2Transform(b2Transform value);

    B2Vec2 GetPosition() const;
    void SetPosition(const B2Vec2& position);
    B2Rot GetRotation() const;
    void SetRotation(const B2Rot& rotation);
    B2Vec2 TransformPoint(const B2Vec2& point) const;
    B2Vec2 InverseTransformPoint(const B2Vec2& point) const;
};

class B2AABB {
public:
    b2AABB value;

    B2AABB();
    B2AABB(const B2Vec2& lowerBound, const B2Vec2& upperBound);
    explicit B2AABB(b2AABB value);

    B2Vec2 GetLowerBound() const;
    void SetLowerBound(const B2Vec2& lowerBound);
    B2Vec2 GetUpperBound() const;
    void SetUpperBound(const B2Vec2& upperBound);
    B2Vec2 GetCenter() const;
    B2Vec2 GetExtents() const;
    float GetPerimeter() const;
    bool IsValid() const;
    bool Contains(const B2AABB& other) const;
};

class B2Circle {
public:
    b2Circle value;

    B2Circle();
    B2Circle(const B2Vec2& center, float radius);
    explicit B2Circle(b2Circle value);

    B2Vec2 GetCenter() const;
    void SetCenter(const B2Vec2& center);
    float GetRadius() const;
    void SetRadius(float radius);
};

class B2Capsule {
public:
    b2Capsule value;

    B2Capsule();
    B2Capsule(const B2Vec2& center1, const B2Vec2& center2, float radius);
    explicit B2Capsule(b2Capsule value);

    B2Vec2 GetCenter1() const;
    void SetCenter1(const B2Vec2& center);
    B2Vec2 GetCenter2() const;
    void SetCenter2(const B2Vec2& center);
    float GetRadius() const;
    void SetRadius(float radius);
};

class B2Segment {
public:
    b2Segment value;

    B2Segment();
    B2Segment(const B2Vec2& point1, const B2Vec2& point2);
    explicit B2Segment(b2Segment value);

    B2Vec2 GetPoint1() const;
    void SetPoint1(const B2Vec2& point);
    B2Vec2 GetPoint2() const;
    void SetPoint2(const B2Vec2& point);
};

class B2ChainSegment {
public:
    b2ChainSegment value;

    B2ChainSegment();
    B2ChainSegment(const B2Vec2& ghost1, const B2Segment& segment, const B2Vec2& ghost2);
    explicit B2ChainSegment(b2ChainSegment value);

    B2Vec2 GetGhost1() const;
    void SetGhost1(const B2Vec2& point);
    B2Segment GetSegment() const;
    void SetSegment(const B2Segment& segment);
    B2Vec2 GetGhost2() const;
    void SetGhost2(const B2Vec2& point);
};

class B2MassData {
public:
    b2MassData value;

    B2MassData();
    explicit B2MassData(b2MassData value);

    float GetMass() const;
    void SetMass(float mass);
    B2Vec2 GetCenter() const;
    void SetCenter(const B2Vec2& center);
    float GetRotationalInertia() const;
    void SetRotationalInertia(float inertia);
};

class B2Hull {
public:
    B2Hull();

    void ClearPoints();
    void AddPoint(const B2Vec2& point);
    int GetInputPointCount() const;
    bool Compute();
    bool IsValid() const;
    int GetPointCount() const;
    B2Vec2 GetPoint(int index) const;
    const b2Hull* GetHandle() const;

private:
    std::vector<b2Vec2> m_inputPoints;
    b2Hull m_hull;
};

class B2Polygon {
public:
    B2Polygon();
    explicit B2Polygon(b2Polygon value);

    static B2Polygon* CreateSquare(float halfWidth);
    static B2Polygon* CreateBox(float halfWidth, float halfHeight);
    static B2Polygon* CreateRoundedBox(float halfWidth, float halfHeight, float radius);
    static B2Polygon* CreateOffsetBox(float halfWidth, float halfHeight, const B2Vec2& center, const B2Rot& rotation);
    static B2Polygon* CreateOffsetRoundedBox(float halfWidth, float halfHeight, const B2Vec2& center, const B2Rot& rotation, float radius);
    static B2Polygon* CreateFromHull(const B2Hull& hull, float radius);

    int GetVertexCount() const;
    B2Vec2 GetVertex(int index) const;
    B2Vec2 GetNormal(int index) const;
    B2Vec2 GetCentroid() const;
    float GetRadius() const;
    void SetRadius(float radius);
    B2MassData ComputeMass(float density) const;
    b2Polygon GetHandle() const;

private:
    b2Polygon value;
};

/** A generic convex point cloud used by Box2D's GJK, cast, overlap, and TOI APIs. */
class B2ShapeProxy {
public:
    b2ShapeProxy value;

    B2ShapeProxy();
    explicit B2ShapeProxy(b2ShapeProxy value);
    void Clear();
    void AddPoint(const B2Vec2& point);
    int GetPointCount() const;
    B2Vec2 GetPoint(int index) const;
    float GetRadius() const;
    void SetRadius(float radius);
    void SetCircle(const B2Circle& circle);
    void SetCapsule(const B2Capsule& capsule);
    void SetSegment(const B2Segment& segment);
    void SetPolygon(const B2Polygon& polygon);
};

class B2SimplexCache {
public:
    b2SimplexCache value;
    B2SimplexCache();
    void Clear();
    int GetCount() const;
};

class B2DistanceInput {
public:
    b2DistanceInput value;
    B2DistanceInput();
    B2ShapeProxy GetProxyA() const;
    void SetProxyA(const B2ShapeProxy& proxy);
    B2ShapeProxy GetProxyB() const;
    void SetProxyB(const B2ShapeProxy& proxy);
    B2Transform GetTransformA() const;
    void SetTransformA(const B2Transform& transform);
    B2Transform GetTransformB() const;
    void SetTransformB(const B2Transform& transform);
    bool GetUseRadii() const;
    void SetUseRadii(bool useRadii);
};

class B2DistanceOutput {
public:
    B2DistanceOutput();
    explicit B2DistanceOutput(b2DistanceOutput value);
    B2Vec2 GetPointA() const;
    B2Vec2 GetPointB() const;
    B2Vec2 GetNormal() const;
    float GetDistance() const;
    int GetIterations() const;
    int GetSimplexCount() const;
private:
    b2DistanceOutput value;
};

class B2ShapeCastPairInput {
public:
    b2ShapeCastPairInput value;
    B2ShapeCastPairInput();
    B2ShapeProxy GetProxyA() const;
    void SetProxyA(const B2ShapeProxy& proxy);
    B2ShapeProxy GetProxyB() const;
    void SetProxyB(const B2ShapeProxy& proxy);
    B2Transform GetTransformA() const;
    void SetTransformA(const B2Transform& transform);
    B2Transform GetTransformB() const;
    void SetTransformB(const B2Transform& transform);
    B2Vec2 GetTranslationB() const;
    void SetTranslationB(const B2Vec2& translation);
    float GetMaxFraction() const;
    void SetMaxFraction(float fraction);
    bool GetCanEncroach() const;
    void SetCanEncroach(bool canEncroach);
};

class B2Sweep {
public:
    b2Sweep value;
    B2Sweep();
    B2Vec2 GetLocalCenter() const;
    void SetLocalCenter(const B2Vec2& center);
    B2Vec2 GetCenter1() const;
    void SetCenter1(const B2Vec2& center);
    B2Vec2 GetCenter2() const;
    void SetCenter2(const B2Vec2& center);
    B2Rot GetRotation1() const;
    void SetRotation1(const B2Rot& rotation);
    B2Rot GetRotation2() const;
    void SetRotation2(const B2Rot& rotation);
};

class B2TOIInput {
public:
    b2TOIInput value;
    B2TOIInput();
    B2ShapeProxy GetProxyA() const;
    void SetProxyA(const B2ShapeProxy& proxy);
    B2ShapeProxy GetProxyB() const;
    void SetProxyB(const B2ShapeProxy& proxy);
    B2Sweep GetSweepA() const;
    void SetSweepA(const B2Sweep& sweep);
    B2Sweep GetSweepB() const;
    void SetSweepB(const B2Sweep& sweep);
    float GetMaxFraction() const;
    void SetMaxFraction(float fraction);
};

class B2TOIOutput {
public:
    B2TOIOutput();
    explicit B2TOIOutput(b2TOIOutput value);
    int GetState() const;
    float GetFraction() const;
private:
    b2TOIOutput value;
};

/** Results from one dynamic-tree traversal. */
class B2TreeResult {
public:
    B2TreeResult();
    int GetCount() const;
    int GetProxyId(int index) const;
    long long GetUserData(int index) const;
    int GetNodeVisits() const;
    int GetLeafVisits() const;

    std::vector<int> proxyIds;
    std::vector<long long> userData;
    int nodeVisits;
    int leafVisits;
};

/** Direct binding for Box2D's public dynamic AABB tree. */
class B2DynamicTree {
public:
    B2DynamicTree();
    ~B2DynamicTree();
    void Destroy();
    int CreateProxy(const B2AABB& aabb, long long categoryBits, long long userData);
    void DestroyProxy(int proxyId);
    void MoveProxy(int proxyId, const B2AABB& aabb);
    void EnlargeProxy(int proxyId, const B2AABB& aabb);
    void SetCategoryBits(int proxyId, long long categoryBits);
    long long GetCategoryBits(int proxyId) const;
    long long GetUserData(int proxyId) const;
    B2AABB GetAABB(int proxyId) const;
    int GetHeight() const;
    float GetAreaRatio() const;
    B2AABB GetRootBounds() const;
    int GetProxyCount() const;
    int Rebuild(bool fullBuild);
    int GetByteCount() const;
    void Validate() const;
    B2TreeResult* Query(const B2AABB& aabb, long long maskBits) const;
    B2TreeResult* RayCast(const B2Vec2& origin, const B2Vec2& translation, float maxFraction, long long maskBits) const;
    B2TreeResult* ShapeCast(const B2ShapeProxy& proxy, const B2Vec2& translation, float maxFraction,
                            bool canEncroach, long long maskBits) const;
private:
    b2DynamicTree m_tree;
    bool m_destroyed;
};

class B2Filter {
public:
    b2Filter value;

    B2Filter();
    explicit B2Filter(b2Filter value);

    long long GetCategoryBits() const;
    void SetCategoryBits(long long categoryBits);
    long long GetMaskBits() const;
    void SetMaskBits(long long maskBits);
    int GetGroupIndex() const;
    void SetGroupIndex(int groupIndex);
};

class B2QueryFilter {
public:
    b2QueryFilter value;

    B2QueryFilter();
    explicit B2QueryFilter(b2QueryFilter value);

    long long GetCategoryBits() const;
    void SetCategoryBits(long long categoryBits);
    long long GetMaskBits() const;
    void SetMaskBits(long long maskBits);
};

class B2SurfaceMaterial {
public:
    b2SurfaceMaterial value;

    B2SurfaceMaterial();
    explicit B2SurfaceMaterial(b2SurfaceMaterial value);

    float GetFriction() const;
    void SetFriction(float friction);
    float GetRestitution() const;
    void SetRestitution(float restitution);
    float GetRollingResistance() const;
    void SetRollingResistance(float resistance);
    float GetTangentSpeed() const;
    void SetTangentSpeed(float speed);
    int GetUserMaterialId() const;
    void SetUserMaterialId(int materialId);
    long GetCustomColor() const;
    void SetCustomColor(long color);
};

class B2WorldDef {
public:
    b2WorldDef value;

    B2WorldDef();

    B2Vec2 GetGravity() const;
    void SetGravity(const B2Vec2& gravity);
    float GetRestitutionThreshold() const;
    void SetRestitutionThreshold(float threshold);
    float GetHitEventThreshold() const;
    void SetHitEventThreshold(float threshold);
    float GetContactHertz() const;
    void SetContactHertz(float hertz);
    float GetContactDampingRatio() const;
    void SetContactDampingRatio(float ratio);
    float GetMaxContactPushSpeed() const;
    void SetMaxContactPushSpeed(float speed);
    float GetMaximumLinearSpeed() const;
    void SetMaximumLinearSpeed(float speed);
    bool GetEnableSleep() const;
    void SetEnableSleep(bool enabled);
    bool GetEnableContinuous() const;
    void SetEnableContinuous(bool enabled);
};

class B2BodyDef {
public:
    b2BodyDef value;

    B2BodyDef();

    int GetType() const;
    void SetType(int type);
    B2Vec2 GetPosition() const;
    void SetPosition(const B2Vec2& position);
    B2Rot GetRotation() const;
    void SetRotation(const B2Rot& rotation);
    void SetAngle(float radians);
    B2Vec2 GetLinearVelocity() const;
    void SetLinearVelocity(const B2Vec2& velocity);
    float GetAngularVelocity() const;
    void SetAngularVelocity(float velocity);
    float GetLinearDamping() const;
    void SetLinearDamping(float damping);
    float GetAngularDamping() const;
    void SetAngularDamping(float damping);
    float GetGravityScale() const;
    void SetGravityScale(float scale);
    float GetSleepThreshold() const;
    void SetSleepThreshold(float threshold);
    bool GetEnableSleep() const;
    void SetEnableSleep(bool enabled);
    bool GetIsAwake() const;
    void SetIsAwake(bool awake);
    bool GetFixedRotation() const;
    void SetFixedRotation(bool fixedRotation);
    bool GetIsBullet() const;
    void SetIsBullet(bool bullet);
    bool GetIsEnabled() const;
    void SetIsEnabled(bool enabled);
    bool GetAllowFastRotation() const;
    void SetAllowFastRotation(bool allowed);
};

class B2ShapeDef {
public:
    b2ShapeDef value;

    B2ShapeDef();

    B2SurfaceMaterial GetMaterial() const;
    void SetMaterial(const B2SurfaceMaterial& material);
    float GetDensity() const;
    void SetDensity(float density);
    B2Filter GetFilter() const;
    void SetFilter(const B2Filter& filter);
    bool GetIsSensor() const;
    void SetIsSensor(bool sensor);
    bool GetEnableSensorEvents() const;
    void SetEnableSensorEvents(bool enabled);
    bool GetEnableContactEvents() const;
    void SetEnableContactEvents(bool enabled);
    bool GetEnableHitEvents() const;
    void SetEnableHitEvents(bool enabled);
    bool GetEnablePreSolveEvents() const;
    void SetEnablePreSolveEvents(bool enabled);
    bool GetInvokeContactCreation() const;
    void SetInvokeContactCreation(bool enabled);
    bool GetUpdateBodyMass() const;
    void SetUpdateBodyMass(bool enabled);
};

class B2ChainDef {
public:
    b2ChainDef value;

    B2ChainDef();

    void ClearPoints();
    void AddPoint(const B2Vec2& point);
    int GetPointCount() const;
    B2Vec2 GetPoint(int index) const;
    void ClearMaterials();
    void AddMaterial(const B2SurfaceMaterial& material);
    int GetMaterialCount() const;
    B2SurfaceMaterial GetMaterial(int index) const;
    B2Filter GetFilter() const;
    void SetFilter(const B2Filter& filter);
    bool GetIsLoop() const;
    void SetIsLoop(bool loop);
    bool GetEnableSensorEvents() const;
    void SetEnableSensorEvents(bool enabled);
    const b2ChainDef* GetHandle();

private:
    std::vector<b2Vec2> m_points;
    std::vector<b2SurfaceMaterial> m_materials;
};

#define JBOX2D_JOINT_BODY_ACCESSORS \
    long long GetBodyIdA() const; \
    void SetBodyIdA(long long bodyId); \
    long long GetBodyIdB() const; \
    void SetBodyIdB(long long bodyId)

class B2DistanceJointDef {
public:
    b2DistanceJointDef value;
    B2DistanceJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    float GetLength() const;
    void SetLength(float length);
    bool GetEnableSpring() const;
    void SetEnableSpring(bool enabled);
    float GetHertz() const;
    void SetHertz(float hertz);
    float GetDampingRatio() const;
    void SetDampingRatio(float ratio);
    bool GetEnableLimit() const;
    void SetEnableLimit(bool enabled);
    float GetMinLength() const;
    void SetMinLength(float length);
    float GetMaxLength() const;
    void SetMaxLength(float length);
    bool GetEnableMotor() const;
    void SetEnableMotor(bool enabled);
    float GetMaxMotorForce() const;
    void SetMaxMotorForce(float force);
    float GetMotorSpeed() const;
    void SetMotorSpeed(float speed);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2MotorJointDef {
public:
    b2MotorJointDef value;
    B2MotorJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLinearOffset() const;
    void SetLinearOffset(const B2Vec2& offset);
    float GetAngularOffset() const;
    void SetAngularOffset(float offset);
    float GetMaxForce() const;
    void SetMaxForce(float force);
    float GetMaxTorque() const;
    void SetMaxTorque(float torque);
    float GetCorrectionFactor() const;
    void SetCorrectionFactor(float factor);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2MouseJointDef {
public:
    b2MouseJointDef value;
    B2MouseJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetTarget() const;
    void SetTarget(const B2Vec2& target);
    float GetHertz() const;
    void SetHertz(float hertz);
    float GetDampingRatio() const;
    void SetDampingRatio(float ratio);
    float GetMaxForce() const;
    void SetMaxForce(float force);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2FilterJointDef {
public:
    b2FilterJointDef value;
    B2FilterJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
};

class B2PrismaticJointDef {
public:
    b2PrismaticJointDef value;
    B2PrismaticJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    B2Vec2 GetLocalAxisA() const;
    void SetLocalAxisA(const B2Vec2& axis);
    float GetReferenceAngle() const;
    void SetReferenceAngle(float angle);
    float GetTargetTranslation() const;
    void SetTargetTranslation(float translation);
    bool GetEnableSpring() const;
    void SetEnableSpring(bool enabled);
    float GetHertz() const;
    void SetHertz(float hertz);
    float GetDampingRatio() const;
    void SetDampingRatio(float ratio);
    bool GetEnableLimit() const;
    void SetEnableLimit(bool enabled);
    float GetLowerTranslation() const;
    void SetLowerTranslation(float translation);
    float GetUpperTranslation() const;
    void SetUpperTranslation(float translation);
    bool GetEnableMotor() const;
    void SetEnableMotor(bool enabled);
    float GetMaxMotorForce() const;
    void SetMaxMotorForce(float force);
    float GetMotorSpeed() const;
    void SetMotorSpeed(float speed);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2RevoluteJointDef {
public:
    b2RevoluteJointDef value;
    B2RevoluteJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    float GetReferenceAngle() const;
    void SetReferenceAngle(float angle);
    float GetTargetAngle() const;
    void SetTargetAngle(float angle);
    bool GetEnableSpring() const;
    void SetEnableSpring(bool enabled);
    float GetHertz() const;
    void SetHertz(float hertz);
    float GetDampingRatio() const;
    void SetDampingRatio(float ratio);
    bool GetEnableLimit() const;
    void SetEnableLimit(bool enabled);
    float GetLowerAngle() const;
    void SetLowerAngle(float angle);
    float GetUpperAngle() const;
    void SetUpperAngle(float angle);
    bool GetEnableMotor() const;
    void SetEnableMotor(bool enabled);
    float GetMaxMotorTorque() const;
    void SetMaxMotorTorque(float torque);
    float GetMotorSpeed() const;
    void SetMotorSpeed(float speed);
    float GetDrawSize() const;
    void SetDrawSize(float size);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2WeldJointDef {
public:
    b2WeldJointDef value;
    B2WeldJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    float GetReferenceAngle() const;
    void SetReferenceAngle(float angle);
    float GetLinearHertz() const;
    void SetLinearHertz(float hertz);
    float GetAngularHertz() const;
    void SetAngularHertz(float hertz);
    float GetLinearDampingRatio() const;
    void SetLinearDampingRatio(float ratio);
    float GetAngularDampingRatio() const;
    void SetAngularDampingRatio(float ratio);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

class B2WheelJointDef {
public:
    b2WheelJointDef value;
    B2WheelJointDef();
    JBOX2D_JOINT_BODY_ACCESSORS;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    B2Vec2 GetLocalAxisA() const;
    void SetLocalAxisA(const B2Vec2& axis);
    bool GetEnableSpring() const;
    void SetEnableSpring(bool enabled);
    float GetHertz() const;
    void SetHertz(float hertz);
    float GetDampingRatio() const;
    void SetDampingRatio(float ratio);
    bool GetEnableLimit() const;
    void SetEnableLimit(bool enabled);
    float GetLowerTranslation() const;
    void SetLowerTranslation(float translation);
    float GetUpperTranslation() const;
    void SetUpperTranslation(float translation);
    bool GetEnableMotor() const;
    void SetEnableMotor(bool enabled);
    float GetMaxMotorTorque() const;
    void SetMaxMotorTorque(float torque);
    float GetMotorSpeed() const;
    void SetMotorSpeed(float speed);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
};

#undef JBOX2D_JOINT_BODY_ACCESSORS

class B2CastResult {
public:
    B2CastResult();
    explicit B2CastResult(b2CastOutput value);
    B2Vec2 GetPoint() const;
    B2Vec2 GetNormal() const;
    float GetFraction() const;
    int GetIterations() const;
    bool GetHit() const;
private:
    b2CastOutput value;
};

class B2RayResult {
public:
    B2RayResult();
    explicit B2RayResult(b2RayResult value);
    long long GetShapeId() const;
    B2Vec2 GetPoint() const;
    B2Vec2 GetNormal() const;
    float GetFraction() const;
    int GetNodeVisits() const;
    int GetLeafVisits() const;
    bool GetHit() const;
private:
    b2RayResult value;
};

class B2ManifoldPoint {
public:
    B2ManifoldPoint();
    explicit B2ManifoldPoint(b2ManifoldPoint value);
    B2Vec2 GetPoint() const;
    B2Vec2 GetAnchorA() const;
    B2Vec2 GetAnchorB() const;
    float GetSeparation() const;
    float GetNormalImpulse() const;
    float GetTangentImpulse() const;
    float GetTotalNormalImpulse() const;
    float GetNormalVelocity() const;
    int GetId() const;
    bool GetPersisted() const;
private:
    b2ManifoldPoint value;
};

class B2Manifold {
public:
    B2Manifold();
    explicit B2Manifold(b2Manifold value);
    B2Vec2 GetNormal() const;
    float GetRollingImpulse() const;
    int GetPointCount() const;
    B2ManifoldPoint GetPoint(int index) const;
private:
    b2Manifold value;
};

/** Stateless access to the public Box2D 3.1.1 collision algorithms. */
class B2Collision {
public:
    static B2DistanceOutput ShapeDistance(const B2DistanceInput& input, B2SimplexCache& cache);
    static B2CastResult ShapeCast(const B2ShapeCastPairInput& input);
    static B2TOIOutput TimeOfImpact(const B2TOIInput& input);
    static B2Transform GetSweepTransform(const B2Sweep& sweep, float time);

    static B2CastResult RayCastCircle(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                      const B2Circle& circle);
    static B2CastResult RayCastCapsule(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                       const B2Capsule& capsule);
    static B2CastResult RayCastSegment(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                       const B2Segment& segment, bool oneSided);
    static B2CastResult RayCastPolygon(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                       const B2Polygon& polygon);

    static B2Manifold CollideCircles(const B2Circle& shapeA, const B2Transform& transformA,
                                     const B2Circle& shapeB, const B2Transform& transformB);
    static B2Manifold CollideCapsuleAndCircle(const B2Capsule& shapeA, const B2Transform& transformA,
                                              const B2Circle& shapeB, const B2Transform& transformB);
    static B2Manifold CollideSegmentAndCircle(const B2Segment& shapeA, const B2Transform& transformA,
                                              const B2Circle& shapeB, const B2Transform& transformB);
    static B2Manifold CollidePolygonAndCircle(const B2Polygon& shapeA, const B2Transform& transformA,
                                              const B2Circle& shapeB, const B2Transform& transformB);
    static B2Manifold CollideCapsules(const B2Capsule& shapeA, const B2Transform& transformA,
                                      const B2Capsule& shapeB, const B2Transform& transformB);
    static B2Manifold CollideSegmentAndCapsule(const B2Segment& shapeA, const B2Transform& transformA,
                                               const B2Capsule& shapeB, const B2Transform& transformB);
    static B2Manifold CollidePolygonAndCapsule(const B2Polygon& shapeA, const B2Transform& transformA,
                                               const B2Capsule& shapeB, const B2Transform& transformB);
    static B2Manifold CollidePolygons(const B2Polygon& shapeA, const B2Transform& transformA,
                                      const B2Polygon& shapeB, const B2Transform& transformB);
    static B2Manifold CollideSegmentAndPolygon(const B2Segment& shapeA, const B2Transform& transformA,
                                               const B2Polygon& shapeB, const B2Transform& transformB);
    static B2Manifold CollideChainSegmentAndCircle(const B2ChainSegment& shapeA, const B2Transform& transformA,
                                                   const B2Circle& shapeB, const B2Transform& transformB);
    static B2Manifold CollideChainSegmentAndCapsule(const B2ChainSegment& shapeA, const B2Transform& transformA,
                                                    const B2Capsule& shapeB, const B2Transform& transformB,
                                                    B2SimplexCache& cache);
    static B2Manifold CollideChainSegmentAndPolygon(const B2ChainSegment& shapeA, const B2Transform& transformA,
                                                    const B2Polygon& shapeB, const B2Transform& transformB,
                                                    B2SimplexCache& cache);

    static int TOIUnknown();
    static int TOIFailed();
    static int TOIOverlapped();
    static int TOIHit();
    static int TOISeparated();
};

class B2WorldCastHit {
public:
    B2WorldCastHit();
    B2WorldCastHit(long long shapeId, b2Vec2 point, b2Vec2 normal, float fraction);
    long long GetShapeId() const;
    B2Vec2 GetPoint() const;
    B2Vec2 GetNormal() const;
    float GetFraction() const;
private:
    long long shapeId;
    B2Vec2 point;
    B2Vec2 normal;
    float fraction;
};

class B2WorldCastResult {
public:
    B2WorldCastResult();
    int GetHitCount() const;
    B2WorldCastHit GetHit(int index) const;
    int GetNodeVisits() const;
    int GetLeafVisits() const;

    std::vector<B2WorldCastHit> hits;
    int nodeVisits;
    int leafVisits;
};

class B2WorldOverlapResult {
public:
    B2WorldOverlapResult();
    int GetShapeCount() const;
    long long GetShapeId(int index) const;
    int GetNodeVisits() const;
    int GetLeafVisits() const;

    std::vector<long long> shapeIds;
    int nodeVisits;
    int leafVisits;
};

class B2MoverResult {
public:
    B2MoverResult();
    B2Vec2 GetTranslation() const;
    B2Vec2 GetClippedVelocity() const;
    int GetIterationCount() const;
    int GetPlaneCount() const;
    B2Vec2 GetPlaneNormal(int index) const;
    float GetPlaneOffset(int index) const;
    float GetPlanePush(int index) const;

    B2Vec2 translation;
    B2Vec2 clippedVelocity;
    int iterationCount;
    std::vector<b2CollisionPlane> planes;
};

class B2BodyMoveEvent {
public:
    B2BodyMoveEvent();
    explicit B2BodyMoveEvent(const b2BodyMoveEvent& event);
    long long GetBodyId() const;
    B2Transform GetTransform() const;
    bool GetFellAsleep() const;
private:
    long long bodyId;
    B2Transform transform;
    bool fellAsleep;
};

class B2BodyEvents {
public:
    B2BodyEvents();
    explicit B2BodyEvents(const b2BodyEvents& events);
    int GetMoveCount() const;
    B2BodyMoveEvent GetMoveEvent(int index) const;
private:
    std::vector<B2BodyMoveEvent> moveEvents;
};

class B2SensorBeginTouchEvent {
public:
    B2SensorBeginTouchEvent();
    explicit B2SensorBeginTouchEvent(const b2SensorBeginTouchEvent& event);
    long long GetSensorShapeId() const;
    long long GetVisitorShapeId() const;
private:
    long long sensorShapeId;
    long long visitorShapeId;
};

class B2SensorEndTouchEvent {
public:
    B2SensorEndTouchEvent();
    explicit B2SensorEndTouchEvent(const b2SensorEndTouchEvent& event);
    long long GetSensorShapeId() const;
    long long GetVisitorShapeId() const;
private:
    long long sensorShapeId;
    long long visitorShapeId;
};

class B2SensorEvents {
public:
    B2SensorEvents();
    explicit B2SensorEvents(const b2SensorEvents& events);
    int GetBeginCount() const;
    B2SensorBeginTouchEvent GetBeginEvent(int index) const;
    int GetEndCount() const;
    B2SensorEndTouchEvent GetEndEvent(int index) const;
private:
    std::vector<B2SensorBeginTouchEvent> beginEvents;
    std::vector<B2SensorEndTouchEvent> endEvents;
};

class B2ContactBeginTouchEvent {
public:
    B2ContactBeginTouchEvent();
    explicit B2ContactBeginTouchEvent(const b2ContactBeginTouchEvent& event);
    long long GetShapeIdA() const;
    long long GetShapeIdB() const;
    B2Manifold GetManifold() const;
private:
    long long shapeIdA;
    long long shapeIdB;
    B2Manifold manifold;
};

class B2ContactEndTouchEvent {
public:
    B2ContactEndTouchEvent();
    explicit B2ContactEndTouchEvent(const b2ContactEndTouchEvent& event);
    long long GetShapeIdA() const;
    long long GetShapeIdB() const;
private:
    long long shapeIdA;
    long long shapeIdB;
};

class B2ContactHitEvent {
public:
    B2ContactHitEvent();
    explicit B2ContactHitEvent(const b2ContactHitEvent& event);
    long long GetShapeIdA() const;
    long long GetShapeIdB() const;
    B2Vec2 GetPoint() const;
    B2Vec2 GetNormal() const;
    float GetApproachSpeed() const;
private:
    long long shapeIdA;
    long long shapeIdB;
    B2Vec2 point;
    B2Vec2 normal;
    float approachSpeed;
};

class B2ContactEvents {
public:
    B2ContactEvents();
    explicit B2ContactEvents(const b2ContactEvents& events);
    int GetBeginCount() const;
    B2ContactBeginTouchEvent GetBeginEvent(int index) const;
    int GetEndCount() const;
    B2ContactEndTouchEvent GetEndEvent(int index) const;
    int GetHitCount() const;
    B2ContactHitEvent GetHitEvent(int index) const;
private:
    std::vector<B2ContactBeginTouchEvent> beginEvents;
    std::vector<B2ContactEndTouchEvent> endEvents;
    std::vector<B2ContactHitEvent> hitEvents;
};

class B2DebugPolygon {
public:
    B2DebugPolygon();
    B2DebugPolygon(const b2Vec2* vertices, int count);
    int GetVertexCount() const;
    B2Vec2 GetVertex(int index) const;
private:
    std::vector<B2Vec2> vertices;
};

class B2Shape;
class B2Chain;
class B2Joint;
class B2World;

class B2Body {
public:
    B2Body();
    explicit B2Body(long long bodyId);
    explicit B2Body(b2BodyId bodyId);
    long long GetId() const;
    bool IsValid() const;
    void Destroy();
    int GetType() const;
    void SetType(int type);
    B2Vec2 GetPosition() const;
    B2Rot GetRotation() const;
    B2Transform GetTransform() const;
    void SetTransform(const B2Vec2& position, const B2Rot& rotation);
    void SetTargetTransform(const B2Transform& target, float timeStep);
    B2Vec2 GetLocalPoint(const B2Vec2& worldPoint) const;
    B2Vec2 GetWorldPoint(const B2Vec2& localPoint) const;
    B2Vec2 GetLocalVector(const B2Vec2& worldVector) const;
    B2Vec2 GetWorldVector(const B2Vec2& localVector) const;
    B2Vec2 GetLinearVelocity() const;
    void SetLinearVelocity(const B2Vec2& velocity);
    float GetAngularVelocity() const;
    void SetAngularVelocity(float velocity);
    void ApplyForce(const B2Vec2& force, const B2Vec2& point, bool wake);
    void ApplyForceToCenter(const B2Vec2& force, bool wake);
    void ApplyTorque(float torque, bool wake);
    void ApplyLinearImpulse(const B2Vec2& impulse, const B2Vec2& point, bool wake);
    void ApplyLinearImpulseToCenter(const B2Vec2& impulse, bool wake);
    void ApplyAngularImpulse(float impulse, bool wake);
    float GetMass() const;
    float GetRotationalInertia() const;
    B2Vec2 GetLocalCenterOfMass() const;
    B2Vec2 GetWorldCenterOfMass() const;
    B2MassData GetMassData() const;
    void SetMassData(const B2MassData& massData);
    void ApplyMassFromShapes();
    float GetLinearDamping() const;
    void SetLinearDamping(float damping);
    float GetAngularDamping() const;
    void SetAngularDamping(float damping);
    float GetGravityScale() const;
    void SetGravityScale(float scale);
    bool IsAwake() const;
    void SetAwake(bool awake);
    bool IsSleepEnabled() const;
    void EnableSleep(bool enabled);
    bool IsEnabled() const;
    void Disable();
    void Enable();
    bool IsFixedRotation() const;
    void SetFixedRotation(bool fixedRotation);
    bool IsBullet() const;
    void SetBullet(bool bullet);
    int GetShapeCount() const;
    B2Shape* GetShape(int index) const;
    int GetJointCount() const;
    B2Joint* GetJoint(int index) const;
    B2AABB ComputeAABB() const;
    B2Shape* CreateCircleShape(const B2ShapeDef& def, const B2Circle& circle);
    B2Shape* CreateCapsuleShape(const B2ShapeDef& def, const B2Capsule& capsule);
    B2Shape* CreateSegmentShape(const B2ShapeDef& def, const B2Segment& segment);
    B2Shape* CreatePolygonShape(const B2ShapeDef& def, const B2Polygon& polygon);
    B2Chain* CreateChain(B2ChainDef& def);
    b2BodyId GetHandle() const;
private:
    b2BodyId m_bodyId;
};

class B2Shape {
public:
    B2Shape();
    explicit B2Shape(long long shapeId);
    explicit B2Shape(b2ShapeId shapeId);
    long long GetId() const;
    bool IsValid() const;
    void Destroy(bool updateBodyMass);
    int GetType() const;
    long long GetBodyId() const;
    bool IsSensor() const;
    float GetDensity() const;
    void SetDensity(float density, bool updateBodyMass);
    float GetFriction() const;
    void SetFriction(float friction);
    float GetRestitution() const;
    void SetRestitution(float restitution);
    B2SurfaceMaterial GetSurfaceMaterial() const;
    void SetSurfaceMaterial(const B2SurfaceMaterial& material);
    B2Filter GetFilter() const;
    void SetFilter(const B2Filter& filter);
    void EnableSensorEvents(bool enabled);
    bool AreSensorEventsEnabled() const;
    void EnableContactEvents(bool enabled);
    bool AreContactEventsEnabled() const;
    void EnablePreSolveEvents(bool enabled);
    bool ArePreSolveEventsEnabled() const;
    void EnableHitEvents(bool enabled);
    bool AreHitEventsEnabled() const;
    bool TestPoint(const B2Vec2& point) const;
    B2CastResult RayCast(const B2Vec2& origin, const B2Vec2& translation, float maxFraction) const;
    B2Circle GetCircle() const;
    void SetCircle(const B2Circle& circle);
    B2Capsule GetCapsule() const;
    void SetCapsule(const B2Capsule& capsule);
    B2Segment GetSegment() const;
    void SetSegment(const B2Segment& segment);
    B2Polygon* GetPolygon() const;
    void SetPolygon(const B2Polygon& polygon);
    B2AABB GetAABB() const;
    B2MassData GetMassData() const;
    B2Vec2 GetClosestPoint(const B2Vec2& target) const;
private:
    b2ShapeId m_shapeId;
};

class B2Chain {
public:
    B2Chain();
    explicit B2Chain(long long chainId);
    explicit B2Chain(b2ChainId chainId);
    long long GetId() const;
    bool IsValid() const;
    void Destroy();
    int GetSegmentCount() const;
    B2Shape* GetSegment(int index) const;
    float GetFriction() const;
    void SetFriction(float friction);
    float GetRestitution() const;
    void SetRestitution(float restitution);
private:
    b2ChainId m_chainId;
};

class B2Joint {
public:
    B2Joint();
    explicit B2Joint(long long jointId);
    explicit B2Joint(b2JointId jointId);
    long long GetId() const;
    bool IsValid() const;
    void Destroy();
    int GetType() const;
    long long GetBodyIdA() const;
    long long GetBodyIdB() const;
    B2Vec2 GetLocalAnchorA() const;
    void SetLocalAnchorA(const B2Vec2& anchor);
    B2Vec2 GetLocalAnchorB() const;
    void SetLocalAnchorB(const B2Vec2& anchor);
    bool GetCollideConnected() const;
    void SetCollideConnected(bool collide);
    void WakeBodies();
    B2Vec2 GetConstraintForce() const;
    float GetConstraintTorque() const;
    float GetLinearSeparation() const;
    float GetAngularSeparation() const;
    void DistanceSetLength(float length);
    float DistanceGetLength() const;
    void DistanceEnableSpring(bool enabled);
    void DistanceSetSpringHertz(float hertz);
    void DistanceSetSpringDampingRatio(float ratio);
    void DistanceEnableLimit(bool enabled);
    void DistanceSetLengthRange(float minLength, float maxLength);
    void DistanceEnableMotor(bool enabled);
    void DistanceSetMotorSpeed(float speed);
    void DistanceSetMaxMotorForce(float force);
    void MotorSetLinearOffset(const B2Vec2& offset);
    void MotorSetAngularOffset(float offset);
    void MotorSetMaxForce(float force);
    void MotorSetMaxTorque(float torque);
    void MotorSetCorrectionFactor(float factor);
    void MouseSetTarget(const B2Vec2& target);
    void MouseSetSpringHertz(float hertz);
    void MouseSetSpringDampingRatio(float ratio);
    void MouseSetMaxForce(float force);
    void PrismaticEnableSpring(bool enabled);
    void PrismaticSetSpringHertz(float hertz);
    void PrismaticSetSpringDampingRatio(float ratio);
    void PrismaticSetTargetTranslation(float translation);
    void PrismaticEnableLimit(bool enabled);
    void PrismaticSetLimits(float lower, float upper);
    void PrismaticEnableMotor(bool enabled);
    void PrismaticSetMotorSpeed(float speed);
    void PrismaticSetMaxMotorForce(float force);
    float PrismaticGetTranslation() const;
    void RevoluteEnableSpring(bool enabled);
    void RevoluteSetSpringHertz(float hertz);
    void RevoluteSetSpringDampingRatio(float ratio);
    void RevoluteSetTargetAngle(float angle);
    void RevoluteEnableLimit(bool enabled);
    void RevoluteSetLimits(float lower, float upper);
    void RevoluteEnableMotor(bool enabled);
    void RevoluteSetMotorSpeed(float speed);
    void RevoluteSetMaxMotorTorque(float torque);
    float RevoluteGetAngle() const;
    void WheelEnableSpring(bool enabled);
    void WheelSetSpringHertz(float hertz);
    void WheelSetSpringDampingRatio(float ratio);
    void WheelEnableLimit(bool enabled);
    void WheelSetLimits(float lower, float upper);
    void WheelEnableMotor(bool enabled);
    void WheelSetMotorSpeed(float speed);
    void WheelSetMaxMotorTorque(float torque);
    b2JointId GetHandle() const;
private:
    b2JointId m_jointId;
};

class B2DebugDrawEm {
public:
    B2DebugDrawEm();
    virtual ~B2DebugDrawEm();
    void DrawWorld(B2World* world);
    void SetDrawingBounds(const B2AABB& bounds);
    B2AABB GetDrawingBounds() const;
    void SetUseDrawingBounds(bool enabled);
    bool GetUseDrawingBounds() const;
    void SetDrawShapes(bool enabled);
    bool GetDrawShapes() const;
    void SetDrawJoints(bool enabled);
    bool GetDrawJoints() const;
    void SetDrawJointExtras(bool enabled);
    bool GetDrawJointExtras() const;
    void SetDrawBounds(bool enabled);
    bool GetDrawBounds() const;
    void SetDrawMass(bool enabled);
    bool GetDrawMass() const;
    void SetDrawBodyNames(bool enabled);
    bool GetDrawBodyNames() const;
    void SetDrawContacts(bool enabled);
    bool GetDrawContacts() const;
    void SetDrawGraphColors(bool enabled);
    bool GetDrawGraphColors() const;
    void SetDrawContactNormals(bool enabled);
    bool GetDrawContactNormals() const;
    void SetDrawContactImpulses(bool enabled);
    bool GetDrawContactImpulses() const;
    void SetDrawContactFeatures(bool enabled);
    bool GetDrawContactFeatures() const;
    void SetDrawFrictionImpulses(bool enabled);
    bool GetDrawFrictionImpulses() const;
    void SetDrawIslands(bool enabled);
    bool GetDrawIslands() const;
    virtual void DrawPolygon(B2DebugPolygon* polygon, int color);
    virtual void DrawSolidPolygon(const B2Transform& transform, B2DebugPolygon* polygon, float radius, int color);
    virtual void DrawCircle(const B2Vec2& center, float radius, int color);
    virtual void DrawSolidCircle(const B2Transform& transform, float radius, int color);
    virtual void DrawSolidCapsule(const B2Vec2& p1, const B2Vec2& p2, float radius, int color);
    virtual void DrawSegment(const B2Vec2& p1, const B2Vec2& p2, int color);
    virtual void DrawTransform(const B2Transform& transform);
    virtual void DrawPoint(const B2Vec2& point, float size, int color);
private:
    b2DebugDraw m_draw;
};

class B2World {
public:
    B2World();
    explicit B2World(const B2WorldDef& def);
    ~B2World();
    long long GetId() const;
    bool IsValid() const;
    void Destroy();
    void Step(float timeStep, int subStepCount);
    B2Vec2 GetGravity() const;
    void SetGravity(const B2Vec2& gravity);
    bool IsSleepingEnabled() const;
    void EnableSleeping(bool enabled);
    bool IsWarmStartingEnabled() const;
    void EnableWarmStarting(bool enabled);
    bool IsContinuousEnabled() const;
    void EnableContinuous(bool enabled);
    float GetRestitutionThreshold() const;
    void SetRestitutionThreshold(float threshold);
    float GetHitEventThreshold() const;
    void SetHitEventThreshold(float threshold);
    float GetMaximumLinearSpeed() const;
    void SetMaximumLinearSpeed(float speed);
    void SetContactTuning(float hertz, float dampingRatio, float pushSpeed);
    void RebuildStaticTree();
    int GetAwakeBodyCount() const;
    B2Body* CreateBody(const B2BodyDef& def);
    B2Joint* CreateDistanceJoint(const B2DistanceJointDef& def);
    B2Joint* CreateMotorJoint(const B2MotorJointDef& def);
    B2Joint* CreateMouseJoint(const B2MouseJointDef& def);
    B2Joint* CreateFilterJoint(const B2FilterJointDef& def);
    B2Joint* CreatePrismaticJoint(const B2PrismaticJointDef& def);
    B2Joint* CreateRevoluteJoint(const B2RevoluteJointDef& def);
    B2Joint* CreateWeldJoint(const B2WeldJointDef& def);
    B2Joint* CreateWheelJoint(const B2WheelJointDef& def);
    B2BodyEvents* GetBodyEvents() const;
    B2SensorEvents* GetSensorEvents() const;
    B2ContactEvents* GetContactEvents() const;
    B2RayResult CastRayClosest(const B2Vec2& origin, const B2Vec2& translation, const B2QueryFilter& filter) const;
    B2WorldCastResult* CastRay(const B2Vec2& origin, const B2Vec2& translation,
                               const B2QueryFilter& filter) const;
    B2WorldCastResult* CastShape(const B2ShapeProxy& proxy, const B2Vec2& translation,
                                 const B2QueryFilter& filter) const;
    B2WorldOverlapResult* OverlapAABB(const B2AABB& aabb, const B2QueryFilter& filter) const;
    B2WorldOverlapResult* OverlapShape(const B2ShapeProxy& proxy, const B2QueryFilter& filter) const;
    float CastMover(const B2Capsule& mover, const B2Vec2& translation, const B2QueryFilter& filter) const;
    B2MoverResult* SolveMover(const B2Capsule& mover, const B2Vec2& translation, const B2Vec2& velocity,
                             const B2QueryFilter& collideFilter, const B2QueryFilter& castFilter,
                             int maxIterations) const;
    b2WorldId GetHandle() const;
private:
    b2WorldId m_worldId;
    bool m_destroyed;
};

class B2 {
public:
    static int StaticBody();
    static int KinematicBody();
    static int DynamicBody();
    static int CircleShape();
    static int CapsuleShape();
    static int SegmentShape();
    static int PolygonShape();
    static int ChainSegmentShape();
    static int DistanceJoint();
    static int FilterJoint();
    static int MotorJoint();
    static int MouseJoint();
    static int PrismaticJoint();
    static int RevoluteJoint();
    static int WeldJoint();
    static int WheelJoint();
    static int MaxPolygonVertices();
    static long long DefaultCategoryBits();
    static long long DefaultMaskBits();
    static int VersionMajor();
    static int VersionMinor();
    static int VersionRevision();
    static void SetLengthUnitsPerMeter(float lengthUnits);
    static float GetLengthUnitsPerMeter();
};

} // namespace JBox2D

// Upstream Box2D is compiled as C17. Compile this C++ facade with the generated glue.
#include "jBox2D.cpp"
