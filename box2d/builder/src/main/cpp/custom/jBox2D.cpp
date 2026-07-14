#include <algorithm>
#include <cmath>
#include <cfloat>

namespace JBox2D {

static b2BodyId loadBodyId(long long value) { return b2LoadBodyId(static_cast<uint64_t>(value)); }
static b2ShapeId loadShapeId(long long value) { return b2LoadShapeId(static_cast<uint64_t>(value)); }
static b2ChainId loadChainId(long long value) { return b2LoadChainId(static_cast<uint64_t>(value)); }
static b2JointId loadJointId(long long value) { return b2LoadJointId(static_cast<uint64_t>(value)); }
static int checkedIndex(int index, int count) { return index >= 0 && index < count ? index : -1; }

B2Vec2::B2Vec2() : value{0.0f, 0.0f} {}
B2Vec2::B2Vec2(float x, float y) : value{x, y} {}
B2Vec2::B2Vec2(b2Vec2 value) : value(value) {}
float B2Vec2::GetX() const { return value.x; }
float B2Vec2::GetY() const { return value.y; }
void B2Vec2::SetX(float x) { value.x = x; }
void B2Vec2::SetY(float y) { value.y = y; }
void B2Vec2::Set(float x, float y) { value = {x, y}; }
void B2Vec2::SetZero() { value = {0.0f, 0.0f}; }
float B2Vec2::Length() const { return b2Length(value); }
float B2Vec2::LengthSquared() const { return b2LengthSquared(value); }
float B2Vec2::Normalize() {
    float length = b2Length(value);
    value = b2Normalize(value);
    return length;
}
bool B2Vec2::IsValid() const { return b2IsValidVec2(value); }

B2Rot::B2Rot() : value(b2Rot_identity) {}
B2Rot::B2Rot(float radians) : value(b2MakeRot(radians)) {}
B2Rot::B2Rot(b2Rot value) : value(value) {}
float B2Rot::GetCosine() const { return value.c; }
float B2Rot::GetSine() const { return value.s; }
float B2Rot::GetAngle() const { return b2Rot_GetAngle(value); }
void B2Rot::Set(float radians) { value = b2MakeRot(radians); }
void B2Rot::SetIdentity() { value = b2Rot_identity; }
B2Vec2 B2Rot::RotateVector(const B2Vec2& vector) const { return B2Vec2(b2RotateVector(value, vector.value)); }
B2Vec2 B2Rot::InverseRotateVector(const B2Vec2& vector) const { return B2Vec2(b2InvRotateVector(value, vector.value)); }

B2Transform::B2Transform() : value(b2Transform_identity) {}
B2Transform::B2Transform(const B2Vec2& position, const B2Rot& rotation) : value{position.value, rotation.value} {}
B2Transform::B2Transform(b2Transform value) : value(value) {}
B2Vec2 B2Transform::GetPosition() const { return B2Vec2(value.p); }
void B2Transform::SetPosition(const B2Vec2& position) { value.p = position.value; }
B2Rot B2Transform::GetRotation() const { return B2Rot(value.q); }
void B2Transform::SetRotation(const B2Rot& rotation) { value.q = rotation.value; }
B2Vec2 B2Transform::TransformPoint(const B2Vec2& point) const { return B2Vec2(b2TransformPoint(value, point.value)); }
B2Vec2 B2Transform::InverseTransformPoint(const B2Vec2& point) const { return B2Vec2(b2InvTransformPoint(value, point.value)); }

B2AABB::B2AABB() : value{{0.0f, 0.0f}, {0.0f, 0.0f}} {}
B2AABB::B2AABB(const B2Vec2& lowerBound, const B2Vec2& upperBound) : value{lowerBound.value, upperBound.value} {}
B2AABB::B2AABB(b2AABB value) : value(value) {}
B2Vec2 B2AABB::GetLowerBound() const { return B2Vec2(value.lowerBound); }
void B2AABB::SetLowerBound(const B2Vec2& lowerBound) { value.lowerBound = lowerBound.value; }
B2Vec2 B2AABB::GetUpperBound() const { return B2Vec2(value.upperBound); }
void B2AABB::SetUpperBound(const B2Vec2& upperBound) { value.upperBound = upperBound.value; }
B2Vec2 B2AABB::GetCenter() const { return B2Vec2(b2AABB_Center(value)); }
B2Vec2 B2AABB::GetExtents() const { return B2Vec2(b2AABB_Extents(value)); }
float B2AABB::GetPerimeter() const {
    float wx = value.upperBound.x - value.lowerBound.x;
    float wy = value.upperBound.y - value.lowerBound.y;
    return 2.0f * (wx + wy);
}
bool B2AABB::IsValid() const { return b2IsValidAABB(value); }
bool B2AABB::Contains(const B2AABB& other) const { return b2AABB_Contains(value, other.value); }

B2Circle::B2Circle() : value{{0.0f, 0.0f}, 0.0f} {}
B2Circle::B2Circle(const B2Vec2& center, float radius) : value{center.value, radius} {}
B2Circle::B2Circle(b2Circle value) : value(value) {}
B2Vec2 B2Circle::GetCenter() const { return B2Vec2(value.center); }
void B2Circle::SetCenter(const B2Vec2& center) { value.center = center.value; }
float B2Circle::GetRadius() const { return value.radius; }
void B2Circle::SetRadius(float radius) { value.radius = radius; }

B2Capsule::B2Capsule() : value{{0.0f, 0.0f}, {0.0f, 0.0f}, 0.0f} {}
B2Capsule::B2Capsule(const B2Vec2& center1, const B2Vec2& center2, float radius) : value{center1.value, center2.value, radius} {}
B2Capsule::B2Capsule(b2Capsule value) : value(value) {}
B2Vec2 B2Capsule::GetCenter1() const { return B2Vec2(value.center1); }
void B2Capsule::SetCenter1(const B2Vec2& center) { value.center1 = center.value; }
B2Vec2 B2Capsule::GetCenter2() const { return B2Vec2(value.center2); }
void B2Capsule::SetCenter2(const B2Vec2& center) { value.center2 = center.value; }
float B2Capsule::GetRadius() const { return value.radius; }
void B2Capsule::SetRadius(float radius) { value.radius = radius; }

B2Segment::B2Segment() : value{{0.0f, 0.0f}, {0.0f, 0.0f}} {}
B2Segment::B2Segment(const B2Vec2& point1, const B2Vec2& point2) : value{point1.value, point2.value} {}
B2Segment::B2Segment(b2Segment value) : value(value) {}
B2Vec2 B2Segment::GetPoint1() const { return B2Vec2(value.point1); }
void B2Segment::SetPoint1(const B2Vec2& point) { value.point1 = point.value; }
B2Vec2 B2Segment::GetPoint2() const { return B2Vec2(value.point2); }
void B2Segment::SetPoint2(const B2Vec2& point) { value.point2 = point.value; }

B2ChainSegment::B2ChainSegment() : value{} {}
B2ChainSegment::B2ChainSegment(const B2Vec2& ghost1, const B2Segment& segment, const B2Vec2& ghost2)
    : value{ghost1.value, segment.value, ghost2.value, 0} {}
B2ChainSegment::B2ChainSegment(b2ChainSegment value) : value(value) {}
B2Vec2 B2ChainSegment::GetGhost1() const { return B2Vec2(value.ghost1); }
void B2ChainSegment::SetGhost1(const B2Vec2& point) { value.ghost1 = point.value; }
B2Segment B2ChainSegment::GetSegment() const { return B2Segment(value.segment); }
void B2ChainSegment::SetSegment(const B2Segment& segment) { value.segment = segment.value; }
B2Vec2 B2ChainSegment::GetGhost2() const { return B2Vec2(value.ghost2); }
void B2ChainSegment::SetGhost2(const B2Vec2& point) { value.ghost2 = point.value; }

B2MassData::B2MassData() : value{} {}
B2MassData::B2MassData(b2MassData value) : value(value) {}
float B2MassData::GetMass() const { return value.mass; }
void B2MassData::SetMass(float mass) { value.mass = mass; }
B2Vec2 B2MassData::GetCenter() const { return B2Vec2(value.center); }
void B2MassData::SetCenter(const B2Vec2& center) { value.center = center.value; }
float B2MassData::GetRotationalInertia() const { return value.rotationalInertia; }
void B2MassData::SetRotationalInertia(float inertia) { value.rotationalInertia = inertia; }

B2Hull::B2Hull() : m_hull{} {}
void B2Hull::ClearPoints() { m_inputPoints.clear(); m_hull = {}; }
void B2Hull::AddPoint(const B2Vec2& point) { m_inputPoints.push_back(point.value); }
int B2Hull::GetInputPointCount() const { return static_cast<int>(m_inputPoints.size()); }
bool B2Hull::Compute() {
    if(m_inputPoints.size() < 3 || m_inputPoints.size() > B2_MAX_POLYGON_VERTICES) {
        m_hull = {};
        return false;
    }
    m_hull = b2ComputeHull(m_inputPoints.data(), static_cast<int>(m_inputPoints.size()));
    return m_hull.count >= 3;
}
bool B2Hull::IsValid() const { return m_hull.count >= 3 && b2ValidateHull(&m_hull); }
int B2Hull::GetPointCount() const { return m_hull.count; }
B2Vec2 B2Hull::GetPoint(int index) const {
    int checked = checkedIndex(index, m_hull.count);
    return checked >= 0 ? B2Vec2(m_hull.points[checked]) : B2Vec2();
}
const b2Hull* B2Hull::GetHandle() const { return &m_hull; }

B2Polygon::B2Polygon() : value{} {}
B2Polygon::B2Polygon(b2Polygon value) : value(value) {}
B2Polygon* B2Polygon::CreateSquare(float halfWidth) { return new B2Polygon(b2MakeSquare(halfWidth)); }
B2Polygon* B2Polygon::CreateBox(float halfWidth, float halfHeight) { return new B2Polygon(b2MakeBox(halfWidth, halfHeight)); }
B2Polygon* B2Polygon::CreateRoundedBox(float halfWidth, float halfHeight, float radius) { return new B2Polygon(b2MakeRoundedBox(halfWidth, halfHeight, radius)); }
B2Polygon* B2Polygon::CreateOffsetBox(float halfWidth, float halfHeight, const B2Vec2& center, const B2Rot& rotation) {
    return new B2Polygon(b2MakeOffsetBox(halfWidth, halfHeight, center.value, rotation.value));
}
B2Polygon* B2Polygon::CreateOffsetRoundedBox(float halfWidth, float halfHeight, const B2Vec2& center, const B2Rot& rotation, float radius) {
    return new B2Polygon(b2MakeOffsetRoundedBox(halfWidth, halfHeight, center.value, rotation.value, radius));
}
B2Polygon* B2Polygon::CreateFromHull(const B2Hull& hull, float radius) { return new B2Polygon(b2MakePolygon(hull.GetHandle(), radius)); }
int B2Polygon::GetVertexCount() const { return value.count; }
B2Vec2 B2Polygon::GetVertex(int index) const {
    int checked = checkedIndex(index, value.count);
    return checked >= 0 ? B2Vec2(value.vertices[checked]) : B2Vec2();
}
B2Vec2 B2Polygon::GetNormal(int index) const {
    int checked = checkedIndex(index, value.count);
    return checked >= 0 ? B2Vec2(value.normals[checked]) : B2Vec2();
}
B2Vec2 B2Polygon::GetCentroid() const { return B2Vec2(value.centroid); }
float B2Polygon::GetRadius() const { return value.radius; }
void B2Polygon::SetRadius(float radius) { value.radius = radius; }
B2MassData B2Polygon::ComputeMass(float density) const { return B2MassData(b2ComputePolygonMass(&value, density)); }
b2Polygon B2Polygon::GetHandle() const { return value; }

B2ShapeProxy::B2ShapeProxy() : value{} {}
B2ShapeProxy::B2ShapeProxy(b2ShapeProxy value) : value(value) {}
void B2ShapeProxy::Clear() { value = {}; }
void B2ShapeProxy::AddPoint(const B2Vec2& point) {
    if(value.count < B2_MAX_POLYGON_VERTICES) {
        value.points[value.count++] = point.value;
    }
}
int B2ShapeProxy::GetPointCount() const { return value.count; }
B2Vec2 B2ShapeProxy::GetPoint(int index) const {
    int checked = checkedIndex(index, value.count);
    return checked >= 0 ? B2Vec2(value.points[checked]) : B2Vec2();
}
float B2ShapeProxy::GetRadius() const { return value.radius; }
void B2ShapeProxy::SetRadius(float radius) { value.radius = radius; }
void B2ShapeProxy::SetCircle(const B2Circle& circle) { value = b2MakeProxy(&circle.value.center, 1, circle.value.radius); }
void B2ShapeProxy::SetCapsule(const B2Capsule& capsule) { value = b2MakeProxy(&capsule.value.center1, 2, capsule.value.radius); }
void B2ShapeProxy::SetSegment(const B2Segment& segment) { value = b2MakeProxy(&segment.value.point1, 2, 0.0f); }
void B2ShapeProxy::SetPolygon(const B2Polygon& polygon) {
    b2Polygon p = polygon.GetHandle();
    value = b2MakeProxy(p.vertices, p.count, p.radius);
}

B2SimplexCache::B2SimplexCache() : value{} {}
void B2SimplexCache::Clear() { value = {}; }
int B2SimplexCache::GetCount() const { return value.count; }

B2DistanceInput::B2DistanceInput() : value{} {
    value.transformA = b2Transform_identity;
    value.transformB = b2Transform_identity;
}
B2ShapeProxy B2DistanceInput::GetProxyA() const { return B2ShapeProxy(value.proxyA); }
void B2DistanceInput::SetProxyA(const B2ShapeProxy& proxy) { value.proxyA = proxy.value; }
B2ShapeProxy B2DistanceInput::GetProxyB() const { return B2ShapeProxy(value.proxyB); }
void B2DistanceInput::SetProxyB(const B2ShapeProxy& proxy) { value.proxyB = proxy.value; }
B2Transform B2DistanceInput::GetTransformA() const { return B2Transform(value.transformA); }
void B2DistanceInput::SetTransformA(const B2Transform& transform) { value.transformA = transform.value; }
B2Transform B2DistanceInput::GetTransformB() const { return B2Transform(value.transformB); }
void B2DistanceInput::SetTransformB(const B2Transform& transform) { value.transformB = transform.value; }
bool B2DistanceInput::GetUseRadii() const { return value.useRadii; }
void B2DistanceInput::SetUseRadii(bool useRadii) { value.useRadii = useRadii; }

B2DistanceOutput::B2DistanceOutput() : value{} {}
B2DistanceOutput::B2DistanceOutput(b2DistanceOutput value) : value(value) {}
B2Vec2 B2DistanceOutput::GetPointA() const { return B2Vec2(value.pointA); }
B2Vec2 B2DistanceOutput::GetPointB() const { return B2Vec2(value.pointB); }
B2Vec2 B2DistanceOutput::GetNormal() const { return B2Vec2(value.normal); }
float B2DistanceOutput::GetDistance() const { return value.distance; }
int B2DistanceOutput::GetIterations() const { return value.iterations; }
int B2DistanceOutput::GetSimplexCount() const { return value.simplexCount; }

B2ShapeCastPairInput::B2ShapeCastPairInput() : value{} {
    value.transformA = b2Transform_identity;
    value.transformB = b2Transform_identity;
    value.maxFraction = 1.0f;
}
B2ShapeProxy B2ShapeCastPairInput::GetProxyA() const { return B2ShapeProxy(value.proxyA); }
void B2ShapeCastPairInput::SetProxyA(const B2ShapeProxy& proxy) { value.proxyA = proxy.value; }
B2ShapeProxy B2ShapeCastPairInput::GetProxyB() const { return B2ShapeProxy(value.proxyB); }
void B2ShapeCastPairInput::SetProxyB(const B2ShapeProxy& proxy) { value.proxyB = proxy.value; }
B2Transform B2ShapeCastPairInput::GetTransformA() const { return B2Transform(value.transformA); }
void B2ShapeCastPairInput::SetTransformA(const B2Transform& transform) { value.transformA = transform.value; }
B2Transform B2ShapeCastPairInput::GetTransformB() const { return B2Transform(value.transformB); }
void B2ShapeCastPairInput::SetTransformB(const B2Transform& transform) { value.transformB = transform.value; }
B2Vec2 B2ShapeCastPairInput::GetTranslationB() const { return B2Vec2(value.translationB); }
void B2ShapeCastPairInput::SetTranslationB(const B2Vec2& translation) { value.translationB = translation.value; }
float B2ShapeCastPairInput::GetMaxFraction() const { return value.maxFraction; }
void B2ShapeCastPairInput::SetMaxFraction(float fraction) { value.maxFraction = fraction; }
bool B2ShapeCastPairInput::GetCanEncroach() const { return value.canEncroach; }
void B2ShapeCastPairInput::SetCanEncroach(bool canEncroach) { value.canEncroach = canEncroach; }

B2Sweep::B2Sweep() : value{} {
    value.q1 = b2Rot_identity;
    value.q2 = b2Rot_identity;
}
B2Vec2 B2Sweep::GetLocalCenter() const { return B2Vec2(value.localCenter); }
void B2Sweep::SetLocalCenter(const B2Vec2& center) { value.localCenter = center.value; }
B2Vec2 B2Sweep::GetCenter1() const { return B2Vec2(value.c1); }
void B2Sweep::SetCenter1(const B2Vec2& center) { value.c1 = center.value; }
B2Vec2 B2Sweep::GetCenter2() const { return B2Vec2(value.c2); }
void B2Sweep::SetCenter2(const B2Vec2& center) { value.c2 = center.value; }
B2Rot B2Sweep::GetRotation1() const { return B2Rot(value.q1); }
void B2Sweep::SetRotation1(const B2Rot& rotation) { value.q1 = rotation.value; }
B2Rot B2Sweep::GetRotation2() const { return B2Rot(value.q2); }
void B2Sweep::SetRotation2(const B2Rot& rotation) { value.q2 = rotation.value; }

B2TOIInput::B2TOIInput() : value{} { value.maxFraction = 1.0f; }
B2ShapeProxy B2TOIInput::GetProxyA() const { return B2ShapeProxy(value.proxyA); }
void B2TOIInput::SetProxyA(const B2ShapeProxy& proxy) { value.proxyA = proxy.value; }
B2ShapeProxy B2TOIInput::GetProxyB() const { return B2ShapeProxy(value.proxyB); }
void B2TOIInput::SetProxyB(const B2ShapeProxy& proxy) { value.proxyB = proxy.value; }
B2Sweep B2TOIInput::GetSweepA() const { B2Sweep sweep; sweep.value = value.sweepA; return sweep; }
void B2TOIInput::SetSweepA(const B2Sweep& sweep) { value.sweepA = sweep.value; }
B2Sweep B2TOIInput::GetSweepB() const { B2Sweep sweep; sweep.value = value.sweepB; return sweep; }
void B2TOIInput::SetSweepB(const B2Sweep& sweep) { value.sweepB = sweep.value; }
float B2TOIInput::GetMaxFraction() const { return value.maxFraction; }
void B2TOIInput::SetMaxFraction(float fraction) { value.maxFraction = fraction; }

B2TOIOutput::B2TOIOutput() : value{} {}
B2TOIOutput::B2TOIOutput(b2TOIOutput value) : value(value) {}
int B2TOIOutput::GetState() const { return value.state; }
float B2TOIOutput::GetFraction() const { return value.fraction; }

B2TreeResult::B2TreeResult() : nodeVisits(0), leafVisits(0) {}
int B2TreeResult::GetCount() const { return static_cast<int>(proxyIds.size()); }
int B2TreeResult::GetProxyId(int index) const {
    int checked = checkedIndex(index, static_cast<int>(proxyIds.size()));
    return checked >= 0 ? proxyIds[checked] : -1;
}
long long B2TreeResult::GetUserData(int index) const {
    int checked = checkedIndex(index, static_cast<int>(userData.size()));
    return checked >= 0 ? userData[checked] : 0;
}
int B2TreeResult::GetNodeVisits() const { return nodeVisits; }
int B2TreeResult::GetLeafVisits() const { return leafVisits; }

static bool treeQueryCallback(int proxyId, uint64_t userData, void* context) {
    B2TreeResult* result = static_cast<B2TreeResult*>(context);
    result->proxyIds.push_back(proxyId);
    result->userData.push_back(static_cast<long long>(userData));
    return true;
}

static float treeRayCastCallback(const b2RayCastInput* input, int proxyId, uint64_t userData, void* context) {
    treeQueryCallback(proxyId, userData, context);
    return input->maxFraction;
}

static float treeShapeCastCallback(const b2ShapeCastInput* input, int proxyId, uint64_t userData, void* context) {
    treeQueryCallback(proxyId, userData, context);
    return input->maxFraction;
}

B2DynamicTree::B2DynamicTree() : m_tree(b2DynamicTree_Create()), m_destroyed(false) {}
B2DynamicTree::~B2DynamicTree() { Destroy(); }
void B2DynamicTree::Destroy() {
    if(!m_destroyed) {
        b2DynamicTree_Destroy(&m_tree);
        m_tree = {};
        m_destroyed = true;
    }
}
int B2DynamicTree::CreateProxy(const B2AABB& aabb, long long categoryBits, long long userData) {
    return b2DynamicTree_CreateProxy(&m_tree, aabb.value, static_cast<uint64_t>(categoryBits),
                                     static_cast<uint64_t>(userData));
}
void B2DynamicTree::DestroyProxy(int proxyId) { b2DynamicTree_DestroyProxy(&m_tree, proxyId); }
void B2DynamicTree::MoveProxy(int proxyId, const B2AABB& aabb) { b2DynamicTree_MoveProxy(&m_tree, proxyId, aabb.value); }
void B2DynamicTree::EnlargeProxy(int proxyId, const B2AABB& aabb) { b2DynamicTree_EnlargeProxy(&m_tree, proxyId, aabb.value); }
void B2DynamicTree::SetCategoryBits(int proxyId, long long bits) {
    b2DynamicTree_SetCategoryBits(&m_tree, proxyId, static_cast<uint64_t>(bits));
}
long long B2DynamicTree::GetCategoryBits(int proxyId) const {
    return static_cast<long long>(b2DynamicTree_GetCategoryBits(const_cast<b2DynamicTree*>(&m_tree), proxyId));
}
long long B2DynamicTree::GetUserData(int proxyId) const {
    return static_cast<long long>(b2DynamicTree_GetUserData(&m_tree, proxyId));
}
B2AABB B2DynamicTree::GetAABB(int proxyId) const { return B2AABB(b2DynamicTree_GetAABB(&m_tree, proxyId)); }
int B2DynamicTree::GetHeight() const { return b2DynamicTree_GetHeight(&m_tree); }
float B2DynamicTree::GetAreaRatio() const { return b2DynamicTree_GetAreaRatio(&m_tree); }
B2AABB B2DynamicTree::GetRootBounds() const { return B2AABB(b2DynamicTree_GetRootBounds(&m_tree)); }
int B2DynamicTree::GetProxyCount() const { return b2DynamicTree_GetProxyCount(&m_tree); }
int B2DynamicTree::Rebuild(bool fullBuild) { return b2DynamicTree_Rebuild(&m_tree, fullBuild); }
int B2DynamicTree::GetByteCount() const { return b2DynamicTree_GetByteCount(&m_tree); }
void B2DynamicTree::Validate() const { b2DynamicTree_Validate(&m_tree); }
B2TreeResult* B2DynamicTree::Query(const B2AABB& aabb, long long maskBits) const {
    B2TreeResult* result = new B2TreeResult();
    b2TreeStats stats = b2DynamicTree_Query(&m_tree, aabb.value, static_cast<uint64_t>(maskBits),
                                            treeQueryCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}
B2TreeResult* B2DynamicTree::RayCast(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                     long long maskBits) const {
    B2TreeResult* result = new B2TreeResult();
    b2RayCastInput input{origin.value, translation.value, maxFraction};
    b2TreeStats stats = b2DynamicTree_RayCast(&m_tree, &input, static_cast<uint64_t>(maskBits),
                                              treeRayCastCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}
B2TreeResult* B2DynamicTree::ShapeCast(const B2ShapeProxy& proxy, const B2Vec2& translation, float maxFraction,
                                       bool canEncroach, long long maskBits) const {
    B2TreeResult* result = new B2TreeResult();
    b2ShapeCastInput input{proxy.value, translation.value, maxFraction, canEncroach};
    b2TreeStats stats = b2DynamicTree_ShapeCast(&m_tree, &input, static_cast<uint64_t>(maskBits),
                                                treeShapeCastCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}

B2Filter::B2Filter() : value(b2DefaultFilter()) {}
B2Filter::B2Filter(b2Filter value) : value(value) {}
long long B2Filter::GetCategoryBits() const { return static_cast<long long>(value.categoryBits); }
void B2Filter::SetCategoryBits(long long bits) { value.categoryBits = static_cast<uint64_t>(bits); }
long long B2Filter::GetMaskBits() const { return static_cast<long long>(value.maskBits); }
void B2Filter::SetMaskBits(long long bits) { value.maskBits = static_cast<uint64_t>(bits); }
int B2Filter::GetGroupIndex() const { return value.groupIndex; }
void B2Filter::SetGroupIndex(int index) { value.groupIndex = index; }

B2QueryFilter::B2QueryFilter() : value(b2DefaultQueryFilter()) {}
B2QueryFilter::B2QueryFilter(b2QueryFilter value) : value(value) {}
long long B2QueryFilter::GetCategoryBits() const { return static_cast<long long>(value.categoryBits); }
void B2QueryFilter::SetCategoryBits(long long bits) { value.categoryBits = static_cast<uint64_t>(bits); }
long long B2QueryFilter::GetMaskBits() const { return static_cast<long long>(value.maskBits); }
void B2QueryFilter::SetMaskBits(long long bits) { value.maskBits = static_cast<uint64_t>(bits); }

B2SurfaceMaterial::B2SurfaceMaterial() : value(b2DefaultSurfaceMaterial()) {}
B2SurfaceMaterial::B2SurfaceMaterial(b2SurfaceMaterial value) : value(value) {}
float B2SurfaceMaterial::GetFriction() const { return value.friction; }
void B2SurfaceMaterial::SetFriction(float friction) { value.friction = friction; }
float B2SurfaceMaterial::GetRestitution() const { return value.restitution; }
void B2SurfaceMaterial::SetRestitution(float restitution) { value.restitution = restitution; }
float B2SurfaceMaterial::GetRollingResistance() const { return value.rollingResistance; }
void B2SurfaceMaterial::SetRollingResistance(float resistance) { value.rollingResistance = resistance; }
float B2SurfaceMaterial::GetTangentSpeed() const { return value.tangentSpeed; }
void B2SurfaceMaterial::SetTangentSpeed(float speed) { value.tangentSpeed = speed; }
int B2SurfaceMaterial::GetUserMaterialId() const { return value.userMaterialId; }
void B2SurfaceMaterial::SetUserMaterialId(int id) { value.userMaterialId = id; }
long B2SurfaceMaterial::GetCustomColor() const { return static_cast<long>(value.customColor); }
void B2SurfaceMaterial::SetCustomColor(long color) { value.customColor = static_cast<uint32_t>(color); }

B2WorldDef::B2WorldDef() : value(b2DefaultWorldDef()) {}
B2Vec2 B2WorldDef::GetGravity() const { return B2Vec2(value.gravity); }
void B2WorldDef::SetGravity(const B2Vec2& gravity) { value.gravity = gravity.value; }
float B2WorldDef::GetRestitutionThreshold() const { return value.restitutionThreshold; }
void B2WorldDef::SetRestitutionThreshold(float v) { value.restitutionThreshold = v; }
float B2WorldDef::GetHitEventThreshold() const { return value.hitEventThreshold; }
void B2WorldDef::SetHitEventThreshold(float v) { value.hitEventThreshold = v; }
float B2WorldDef::GetContactHertz() const { return value.contactHertz; }
void B2WorldDef::SetContactHertz(float v) { value.contactHertz = v; }
float B2WorldDef::GetContactDampingRatio() const { return value.contactDampingRatio; }
void B2WorldDef::SetContactDampingRatio(float v) { value.contactDampingRatio = v; }
float B2WorldDef::GetMaxContactPushSpeed() const { return value.maxContactPushSpeed; }
void B2WorldDef::SetMaxContactPushSpeed(float v) { value.maxContactPushSpeed = v; }
float B2WorldDef::GetMaximumLinearSpeed() const { return value.maximumLinearSpeed; }
void B2WorldDef::SetMaximumLinearSpeed(float v) { value.maximumLinearSpeed = v; }
bool B2WorldDef::GetEnableSleep() const { return value.enableSleep; }
void B2WorldDef::SetEnableSleep(bool v) { value.enableSleep = v; }
bool B2WorldDef::GetEnableContinuous() const { return value.enableContinuous; }
void B2WorldDef::SetEnableContinuous(bool v) { value.enableContinuous = v; }

B2BodyDef::B2BodyDef() : value(b2DefaultBodyDef()) {}
int B2BodyDef::GetType() const { return static_cast<int>(value.type); }
void B2BodyDef::SetType(int type) { value.type = static_cast<b2BodyType>(type); }
B2Vec2 B2BodyDef::GetPosition() const { return B2Vec2(value.position); }
void B2BodyDef::SetPosition(const B2Vec2& v) { value.position = v.value; }
B2Rot B2BodyDef::GetRotation() const { return B2Rot(value.rotation); }
void B2BodyDef::SetRotation(const B2Rot& v) { value.rotation = v.value; }
void B2BodyDef::SetAngle(float radians) { value.rotation = b2MakeRot(radians); }
B2Vec2 B2BodyDef::GetLinearVelocity() const { return B2Vec2(value.linearVelocity); }
void B2BodyDef::SetLinearVelocity(const B2Vec2& v) { value.linearVelocity = v.value; }
float B2BodyDef::GetAngularVelocity() const { return value.angularVelocity; }
void B2BodyDef::SetAngularVelocity(float v) { value.angularVelocity = v; }
float B2BodyDef::GetLinearDamping() const { return value.linearDamping; }
void B2BodyDef::SetLinearDamping(float v) { value.linearDamping = v; }
float B2BodyDef::GetAngularDamping() const { return value.angularDamping; }
void B2BodyDef::SetAngularDamping(float v) { value.angularDamping = v; }
float B2BodyDef::GetGravityScale() const { return value.gravityScale; }
void B2BodyDef::SetGravityScale(float v) { value.gravityScale = v; }
float B2BodyDef::GetSleepThreshold() const { return value.sleepThreshold; }
void B2BodyDef::SetSleepThreshold(float v) { value.sleepThreshold = v; }
bool B2BodyDef::GetEnableSleep() const { return value.enableSleep; }
void B2BodyDef::SetEnableSleep(bool v) { value.enableSleep = v; }
bool B2BodyDef::GetIsAwake() const { return value.isAwake; }
void B2BodyDef::SetIsAwake(bool v) { value.isAwake = v; }
bool B2BodyDef::GetFixedRotation() const { return value.fixedRotation; }
void B2BodyDef::SetFixedRotation(bool v) { value.fixedRotation = v; }
bool B2BodyDef::GetIsBullet() const { return value.isBullet; }
void B2BodyDef::SetIsBullet(bool v) { value.isBullet = v; }
bool B2BodyDef::GetIsEnabled() const { return value.isEnabled; }
void B2BodyDef::SetIsEnabled(bool v) { value.isEnabled = v; }
bool B2BodyDef::GetAllowFastRotation() const { return value.allowFastRotation; }
void B2BodyDef::SetAllowFastRotation(bool v) { value.allowFastRotation = v; }

B2ShapeDef::B2ShapeDef() : value(b2DefaultShapeDef()) {}
B2SurfaceMaterial B2ShapeDef::GetMaterial() const { return B2SurfaceMaterial(value.material); }
void B2ShapeDef::SetMaterial(const B2SurfaceMaterial& v) { value.material = v.value; }
float B2ShapeDef::GetDensity() const { return value.density; }
void B2ShapeDef::SetDensity(float v) { value.density = v; }
B2Filter B2ShapeDef::GetFilter() const { return B2Filter(value.filter); }
void B2ShapeDef::SetFilter(const B2Filter& v) { value.filter = v.value; }
bool B2ShapeDef::GetIsSensor() const { return value.isSensor; }
void B2ShapeDef::SetIsSensor(bool v) { value.isSensor = v; }
bool B2ShapeDef::GetEnableSensorEvents() const { return value.enableSensorEvents; }
void B2ShapeDef::SetEnableSensorEvents(bool v) { value.enableSensorEvents = v; }
bool B2ShapeDef::GetEnableContactEvents() const { return value.enableContactEvents; }
void B2ShapeDef::SetEnableContactEvents(bool v) { value.enableContactEvents = v; }
bool B2ShapeDef::GetEnableHitEvents() const { return value.enableHitEvents; }
void B2ShapeDef::SetEnableHitEvents(bool v) { value.enableHitEvents = v; }
bool B2ShapeDef::GetEnablePreSolveEvents() const { return value.enablePreSolveEvents; }
void B2ShapeDef::SetEnablePreSolveEvents(bool v) { value.enablePreSolveEvents = v; }
bool B2ShapeDef::GetInvokeContactCreation() const { return value.invokeContactCreation; }
void B2ShapeDef::SetInvokeContactCreation(bool v) { value.invokeContactCreation = v; }
bool B2ShapeDef::GetUpdateBodyMass() const { return value.updateBodyMass; }
void B2ShapeDef::SetUpdateBodyMass(bool v) { value.updateBodyMass = v; }

B2ChainDef::B2ChainDef() : value(b2DefaultChainDef()) {
    if(value.materials != nullptr && value.materialCount > 0) {
        m_materials.assign(value.materials, value.materials + value.materialCount);
    }
}
void B2ChainDef::ClearPoints() { m_points.clear(); }
void B2ChainDef::AddPoint(const B2Vec2& point) { m_points.push_back(point.value); }
int B2ChainDef::GetPointCount() const { return static_cast<int>(m_points.size()); }
B2Vec2 B2ChainDef::GetPoint(int index) const {
    int checked = checkedIndex(index, static_cast<int>(m_points.size()));
    return checked >= 0 ? B2Vec2(m_points[checked]) : B2Vec2();
}
void B2ChainDef::ClearMaterials() { m_materials.clear(); }
void B2ChainDef::AddMaterial(const B2SurfaceMaterial& material) { m_materials.push_back(material.value); }
int B2ChainDef::GetMaterialCount() const { return static_cast<int>(m_materials.size()); }
B2SurfaceMaterial B2ChainDef::GetMaterial(int index) const {
    int checked = checkedIndex(index, static_cast<int>(m_materials.size()));
    return checked >= 0 ? B2SurfaceMaterial(m_materials[checked]) : B2SurfaceMaterial();
}
B2Filter B2ChainDef::GetFilter() const { return B2Filter(value.filter); }
void B2ChainDef::SetFilter(const B2Filter& filter) { value.filter = filter.value; }
bool B2ChainDef::GetIsLoop() const { return value.isLoop; }
void B2ChainDef::SetIsLoop(bool loop) { value.isLoop = loop; }
bool B2ChainDef::GetEnableSensorEvents() const { return value.enableSensorEvents; }
void B2ChainDef::SetEnableSensorEvents(bool enabled) { value.enableSensorEvents = enabled; }
const b2ChainDef* B2ChainDef::GetHandle() {
    value.points = m_points.empty() ? nullptr : m_points.data();
    value.count = static_cast<int>(m_points.size());
    value.materials = m_materials.empty() ? nullptr : m_materials.data();
    value.materialCount = static_cast<int>(m_materials.size());
    return &value;
}

#define DEFINE_JOINT_IDS(Type, NativeType) \
long long Type::GetBodyIdA() const { return static_cast<long long>(b2StoreBodyId(value.bodyIdA)); } \
void Type::SetBodyIdA(long long id) { value.bodyIdA = loadBodyId(id); } \
long long Type::GetBodyIdB() const { return static_cast<long long>(b2StoreBodyId(value.bodyIdB)); } \
void Type::SetBodyIdB(long long id) { value.bodyIdB = loadBodyId(id); }

#define DEFINE_ANCHORS(Type) \
B2Vec2 Type::GetLocalAnchorA() const { return B2Vec2(value.localAnchorA); } \
void Type::SetLocalAnchorA(const B2Vec2& v) { value.localAnchorA = v.value; } \
B2Vec2 Type::GetLocalAnchorB() const { return B2Vec2(value.localAnchorB); } \
void Type::SetLocalAnchorB(const B2Vec2& v) { value.localAnchorB = v.value; }

B2DistanceJointDef::B2DistanceJointDef() : value(b2DefaultDistanceJointDef()) {}
DEFINE_JOINT_IDS(B2DistanceJointDef, b2DistanceJointDef)
DEFINE_ANCHORS(B2DistanceJointDef)
float B2DistanceJointDef::GetLength() const { return value.length; }
void B2DistanceJointDef::SetLength(float v) { value.length = v; }
bool B2DistanceJointDef::GetEnableSpring() const { return value.enableSpring; }
void B2DistanceJointDef::SetEnableSpring(bool v) { value.enableSpring = v; }
float B2DistanceJointDef::GetHertz() const { return value.hertz; }
void B2DistanceJointDef::SetHertz(float v) { value.hertz = v; }
float B2DistanceJointDef::GetDampingRatio() const { return value.dampingRatio; }
void B2DistanceJointDef::SetDampingRatio(float v) { value.dampingRatio = v; }
bool B2DistanceJointDef::GetEnableLimit() const { return value.enableLimit; }
void B2DistanceJointDef::SetEnableLimit(bool v) { value.enableLimit = v; }
float B2DistanceJointDef::GetMinLength() const { return value.minLength; }
void B2DistanceJointDef::SetMinLength(float v) { value.minLength = v; }
float B2DistanceJointDef::GetMaxLength() const { return value.maxLength; }
void B2DistanceJointDef::SetMaxLength(float v) { value.maxLength = v; }
bool B2DistanceJointDef::GetEnableMotor() const { return value.enableMotor; }
void B2DistanceJointDef::SetEnableMotor(bool v) { value.enableMotor = v; }
float B2DistanceJointDef::GetMaxMotorForce() const { return value.maxMotorForce; }
void B2DistanceJointDef::SetMaxMotorForce(float v) { value.maxMotorForce = v; }
float B2DistanceJointDef::GetMotorSpeed() const { return value.motorSpeed; }
void B2DistanceJointDef::SetMotorSpeed(float v) { value.motorSpeed = v; }
bool B2DistanceJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2DistanceJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2MotorJointDef::B2MotorJointDef() : value(b2DefaultMotorJointDef()) {}
DEFINE_JOINT_IDS(B2MotorJointDef, b2MotorJointDef)
B2Vec2 B2MotorJointDef::GetLinearOffset() const { return B2Vec2(value.linearOffset); }
void B2MotorJointDef::SetLinearOffset(const B2Vec2& v) { value.linearOffset = v.value; }
float B2MotorJointDef::GetAngularOffset() const { return value.angularOffset; }
void B2MotorJointDef::SetAngularOffset(float v) { value.angularOffset = v; }
float B2MotorJointDef::GetMaxForce() const { return value.maxForce; }
void B2MotorJointDef::SetMaxForce(float v) { value.maxForce = v; }
float B2MotorJointDef::GetMaxTorque() const { return value.maxTorque; }
void B2MotorJointDef::SetMaxTorque(float v) { value.maxTorque = v; }
float B2MotorJointDef::GetCorrectionFactor() const { return value.correctionFactor; }
void B2MotorJointDef::SetCorrectionFactor(float v) { value.correctionFactor = v; }
bool B2MotorJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2MotorJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2MouseJointDef::B2MouseJointDef() : value(b2DefaultMouseJointDef()) {}
DEFINE_JOINT_IDS(B2MouseJointDef, b2MouseJointDef)
B2Vec2 B2MouseJointDef::GetTarget() const { return B2Vec2(value.target); }
void B2MouseJointDef::SetTarget(const B2Vec2& v) { value.target = v.value; }
float B2MouseJointDef::GetHertz() const { return value.hertz; }
void B2MouseJointDef::SetHertz(float v) { value.hertz = v; }
float B2MouseJointDef::GetDampingRatio() const { return value.dampingRatio; }
void B2MouseJointDef::SetDampingRatio(float v) { value.dampingRatio = v; }
float B2MouseJointDef::GetMaxForce() const { return value.maxForce; }
void B2MouseJointDef::SetMaxForce(float v) { value.maxForce = v; }
bool B2MouseJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2MouseJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2FilterJointDef::B2FilterJointDef() : value(b2DefaultFilterJointDef()) {}
DEFINE_JOINT_IDS(B2FilterJointDef, b2FilterJointDef)

B2PrismaticJointDef::B2PrismaticJointDef() : value(b2DefaultPrismaticJointDef()) {}
DEFINE_JOINT_IDS(B2PrismaticJointDef, b2PrismaticJointDef)
DEFINE_ANCHORS(B2PrismaticJointDef)
B2Vec2 B2PrismaticJointDef::GetLocalAxisA() const { return B2Vec2(value.localAxisA); }
void B2PrismaticJointDef::SetLocalAxisA(const B2Vec2& v) { value.localAxisA = v.value; }
float B2PrismaticJointDef::GetReferenceAngle() const { return value.referenceAngle; }
void B2PrismaticJointDef::SetReferenceAngle(float v) { value.referenceAngle = v; }
float B2PrismaticJointDef::GetTargetTranslation() const { return value.targetTranslation; }
void B2PrismaticJointDef::SetTargetTranslation(float v) { value.targetTranslation = v; }
bool B2PrismaticJointDef::GetEnableSpring() const { return value.enableSpring; }
void B2PrismaticJointDef::SetEnableSpring(bool v) { value.enableSpring = v; }
float B2PrismaticJointDef::GetHertz() const { return value.hertz; }
void B2PrismaticJointDef::SetHertz(float v) { value.hertz = v; }
float B2PrismaticJointDef::GetDampingRatio() const { return value.dampingRatio; }
void B2PrismaticJointDef::SetDampingRatio(float v) { value.dampingRatio = v; }
bool B2PrismaticJointDef::GetEnableLimit() const { return value.enableLimit; }
void B2PrismaticJointDef::SetEnableLimit(bool v) { value.enableLimit = v; }
float B2PrismaticJointDef::GetLowerTranslation() const { return value.lowerTranslation; }
void B2PrismaticJointDef::SetLowerTranslation(float v) { value.lowerTranslation = v; }
float B2PrismaticJointDef::GetUpperTranslation() const { return value.upperTranslation; }
void B2PrismaticJointDef::SetUpperTranslation(float v) { value.upperTranslation = v; }
bool B2PrismaticJointDef::GetEnableMotor() const { return value.enableMotor; }
void B2PrismaticJointDef::SetEnableMotor(bool v) { value.enableMotor = v; }
float B2PrismaticJointDef::GetMaxMotorForce() const { return value.maxMotorForce; }
void B2PrismaticJointDef::SetMaxMotorForce(float v) { value.maxMotorForce = v; }
float B2PrismaticJointDef::GetMotorSpeed() const { return value.motorSpeed; }
void B2PrismaticJointDef::SetMotorSpeed(float v) { value.motorSpeed = v; }
bool B2PrismaticJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2PrismaticJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2RevoluteJointDef::B2RevoluteJointDef() : value(b2DefaultRevoluteJointDef()) {}
DEFINE_JOINT_IDS(B2RevoluteJointDef, b2RevoluteJointDef)
DEFINE_ANCHORS(B2RevoluteJointDef)
float B2RevoluteJointDef::GetReferenceAngle() const { return value.referenceAngle; }
void B2RevoluteJointDef::SetReferenceAngle(float v) { value.referenceAngle = v; }
float B2RevoluteJointDef::GetTargetAngle() const { return value.targetAngle; }
void B2RevoluteJointDef::SetTargetAngle(float v) { value.targetAngle = v; }
bool B2RevoluteJointDef::GetEnableSpring() const { return value.enableSpring; }
void B2RevoluteJointDef::SetEnableSpring(bool v) { value.enableSpring = v; }
float B2RevoluteJointDef::GetHertz() const { return value.hertz; }
void B2RevoluteJointDef::SetHertz(float v) { value.hertz = v; }
float B2RevoluteJointDef::GetDampingRatio() const { return value.dampingRatio; }
void B2RevoluteJointDef::SetDampingRatio(float v) { value.dampingRatio = v; }
bool B2RevoluteJointDef::GetEnableLimit() const { return value.enableLimit; }
void B2RevoluteJointDef::SetEnableLimit(bool v) { value.enableLimit = v; }
float B2RevoluteJointDef::GetLowerAngle() const { return value.lowerAngle; }
void B2RevoluteJointDef::SetLowerAngle(float v) { value.lowerAngle = v; }
float B2RevoluteJointDef::GetUpperAngle() const { return value.upperAngle; }
void B2RevoluteJointDef::SetUpperAngle(float v) { value.upperAngle = v; }
bool B2RevoluteJointDef::GetEnableMotor() const { return value.enableMotor; }
void B2RevoluteJointDef::SetEnableMotor(bool v) { value.enableMotor = v; }
float B2RevoluteJointDef::GetMaxMotorTorque() const { return value.maxMotorTorque; }
void B2RevoluteJointDef::SetMaxMotorTorque(float v) { value.maxMotorTorque = v; }
float B2RevoluteJointDef::GetMotorSpeed() const { return value.motorSpeed; }
void B2RevoluteJointDef::SetMotorSpeed(float v) { value.motorSpeed = v; }
float B2RevoluteJointDef::GetDrawSize() const { return value.drawSize; }
void B2RevoluteJointDef::SetDrawSize(float v) { value.drawSize = v; }
bool B2RevoluteJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2RevoluteJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2WeldJointDef::B2WeldJointDef() : value(b2DefaultWeldJointDef()) {}
DEFINE_JOINT_IDS(B2WeldJointDef, b2WeldJointDef)
DEFINE_ANCHORS(B2WeldJointDef)
float B2WeldJointDef::GetReferenceAngle() const { return value.referenceAngle; }
void B2WeldJointDef::SetReferenceAngle(float v) { value.referenceAngle = v; }
float B2WeldJointDef::GetLinearHertz() const { return value.linearHertz; }
void B2WeldJointDef::SetLinearHertz(float v) { value.linearHertz = v; }
float B2WeldJointDef::GetAngularHertz() const { return value.angularHertz; }
void B2WeldJointDef::SetAngularHertz(float v) { value.angularHertz = v; }
float B2WeldJointDef::GetLinearDampingRatio() const { return value.linearDampingRatio; }
void B2WeldJointDef::SetLinearDampingRatio(float v) { value.linearDampingRatio = v; }
float B2WeldJointDef::GetAngularDampingRatio() const { return value.angularDampingRatio; }
void B2WeldJointDef::SetAngularDampingRatio(float v) { value.angularDampingRatio = v; }
bool B2WeldJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2WeldJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

B2WheelJointDef::B2WheelJointDef() : value(b2DefaultWheelJointDef()) {}
DEFINE_JOINT_IDS(B2WheelJointDef, b2WheelJointDef)
DEFINE_ANCHORS(B2WheelJointDef)
B2Vec2 B2WheelJointDef::GetLocalAxisA() const { return B2Vec2(value.localAxisA); }
void B2WheelJointDef::SetLocalAxisA(const B2Vec2& v) { value.localAxisA = v.value; }
bool B2WheelJointDef::GetEnableSpring() const { return value.enableSpring; }
void B2WheelJointDef::SetEnableSpring(bool v) { value.enableSpring = v; }
float B2WheelJointDef::GetHertz() const { return value.hertz; }
void B2WheelJointDef::SetHertz(float v) { value.hertz = v; }
float B2WheelJointDef::GetDampingRatio() const { return value.dampingRatio; }
void B2WheelJointDef::SetDampingRatio(float v) { value.dampingRatio = v; }
bool B2WheelJointDef::GetEnableLimit() const { return value.enableLimit; }
void B2WheelJointDef::SetEnableLimit(bool v) { value.enableLimit = v; }
float B2WheelJointDef::GetLowerTranslation() const { return value.lowerTranslation; }
void B2WheelJointDef::SetLowerTranslation(float v) { value.lowerTranslation = v; }
float B2WheelJointDef::GetUpperTranslation() const { return value.upperTranslation; }
void B2WheelJointDef::SetUpperTranslation(float v) { value.upperTranslation = v; }
bool B2WheelJointDef::GetEnableMotor() const { return value.enableMotor; }
void B2WheelJointDef::SetEnableMotor(bool v) { value.enableMotor = v; }
float B2WheelJointDef::GetMaxMotorTorque() const { return value.maxMotorTorque; }
void B2WheelJointDef::SetMaxMotorTorque(float v) { value.maxMotorTorque = v; }
float B2WheelJointDef::GetMotorSpeed() const { return value.motorSpeed; }
void B2WheelJointDef::SetMotorSpeed(float v) { value.motorSpeed = v; }
bool B2WheelJointDef::GetCollideConnected() const { return value.collideConnected; }
void B2WheelJointDef::SetCollideConnected(bool v) { value.collideConnected = v; }

#undef DEFINE_JOINT_IDS
#undef DEFINE_ANCHORS

B2CastResult::B2CastResult() : value{} {}
B2CastResult::B2CastResult(b2CastOutput value) : value(value) {}
B2Vec2 B2CastResult::GetPoint() const { return B2Vec2(value.point); }
B2Vec2 B2CastResult::GetNormal() const { return B2Vec2(value.normal); }
float B2CastResult::GetFraction() const { return value.fraction; }
int B2CastResult::GetIterations() const { return value.iterations; }
bool B2CastResult::GetHit() const { return value.hit; }

B2RayResult::B2RayResult() : value{} {}
B2RayResult::B2RayResult(b2RayResult value) : value(value) {}
long long B2RayResult::GetShapeId() const { return static_cast<long long>(b2StoreShapeId(value.shapeId)); }
B2Vec2 B2RayResult::GetPoint() const { return B2Vec2(value.point); }
B2Vec2 B2RayResult::GetNormal() const { return B2Vec2(value.normal); }
float B2RayResult::GetFraction() const { return value.fraction; }
int B2RayResult::GetNodeVisits() const { return value.nodeVisits; }
int B2RayResult::GetLeafVisits() const { return value.leafVisits; }
bool B2RayResult::GetHit() const { return value.hit; }

B2ManifoldPoint::B2ManifoldPoint() : value{} {}
B2ManifoldPoint::B2ManifoldPoint(b2ManifoldPoint value) : value(value) {}
B2Vec2 B2ManifoldPoint::GetPoint() const { return B2Vec2(value.point); }
B2Vec2 B2ManifoldPoint::GetAnchorA() const { return B2Vec2(value.anchorA); }
B2Vec2 B2ManifoldPoint::GetAnchorB() const { return B2Vec2(value.anchorB); }
float B2ManifoldPoint::GetSeparation() const { return value.separation; }
float B2ManifoldPoint::GetNormalImpulse() const { return value.normalImpulse; }
float B2ManifoldPoint::GetTangentImpulse() const { return value.tangentImpulse; }
float B2ManifoldPoint::GetTotalNormalImpulse() const { return value.totalNormalImpulse; }
float B2ManifoldPoint::GetNormalVelocity() const { return value.normalVelocity; }
int B2ManifoldPoint::GetId() const { return value.id; }
bool B2ManifoldPoint::GetPersisted() const { return value.persisted; }

B2Manifold::B2Manifold() : value{} {}
B2Manifold::B2Manifold(b2Manifold value) : value(value) {}
B2Vec2 B2Manifold::GetNormal() const { return B2Vec2(value.normal); }
float B2Manifold::GetRollingImpulse() const { return value.rollingImpulse; }
int B2Manifold::GetPointCount() const { return value.pointCount; }
B2ManifoldPoint B2Manifold::GetPoint(int index) const {
    int checked = checkedIndex(index, value.pointCount);
    return checked >= 0 ? B2ManifoldPoint(value.points[checked]) : B2ManifoldPoint();
}

B2DistanceOutput B2Collision::ShapeDistance(const B2DistanceInput& input, B2SimplexCache& cache) {
    return B2DistanceOutput(b2ShapeDistance(&input.value, &cache.value, nullptr, 0));
}
B2CastResult B2Collision::ShapeCast(const B2ShapeCastPairInput& input) {
    return B2CastResult(b2ShapeCast(&input.value));
}
B2TOIOutput B2Collision::TimeOfImpact(const B2TOIInput& input) {
    return B2TOIOutput(b2TimeOfImpact(&input.value));
}
B2Transform B2Collision::GetSweepTransform(const B2Sweep& sweep, float time) {
    return B2Transform(b2GetSweepTransform(&sweep.value, time));
}
static b2RayCastInput makeRayInput(const B2Vec2& origin, const B2Vec2& translation, float maxFraction) {
    return {origin.value, translation.value, maxFraction};
}
B2CastResult B2Collision::RayCastCircle(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                        const B2Circle& circle) {
    b2RayCastInput input = makeRayInput(origin, translation, maxFraction);
    return B2CastResult(b2RayCastCircle(&input, &circle.value));
}
B2CastResult B2Collision::RayCastCapsule(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                         const B2Capsule& capsule) {
    b2RayCastInput input = makeRayInput(origin, translation, maxFraction);
    return B2CastResult(b2RayCastCapsule(&input, &capsule.value));
}
B2CastResult B2Collision::RayCastSegment(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                         const B2Segment& segment, bool oneSided) {
    b2RayCastInput input = makeRayInput(origin, translation, maxFraction);
    return B2CastResult(b2RayCastSegment(&input, &segment.value, oneSided));
}
B2CastResult B2Collision::RayCastPolygon(const B2Vec2& origin, const B2Vec2& translation, float maxFraction,
                                         const B2Polygon& polygon) {
    b2RayCastInput input = makeRayInput(origin, translation, maxFraction);
    b2Polygon p = polygon.GetHandle();
    return B2CastResult(b2RayCastPolygon(&input, &p));
}
B2Manifold B2Collision::CollideCircles(const B2Circle& a, const B2Transform& xfA,
                                       const B2Circle& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideCircles(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollideCapsuleAndCircle(const B2Capsule& a, const B2Transform& xfA,
                                                const B2Circle& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideCapsuleAndCircle(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollideSegmentAndCircle(const B2Segment& a, const B2Transform& xfA,
                                                const B2Circle& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideSegmentAndCircle(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollidePolygonAndCircle(const B2Polygon& a, const B2Transform& xfA,
                                                const B2Circle& b, const B2Transform& xfB) {
    b2Polygon pa = a.GetHandle();
    return B2Manifold(b2CollidePolygonAndCircle(&pa, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollideCapsules(const B2Capsule& a, const B2Transform& xfA,
                                        const B2Capsule& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideCapsules(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollideSegmentAndCapsule(const B2Segment& a, const B2Transform& xfA,
                                                 const B2Capsule& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideSegmentAndCapsule(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollidePolygonAndCapsule(const B2Polygon& a, const B2Transform& xfA,
                                                 const B2Capsule& b, const B2Transform& xfB) {
    b2Polygon pa = a.GetHandle();
    return B2Manifold(b2CollidePolygonAndCapsule(&pa, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollidePolygons(const B2Polygon& a, const B2Transform& xfA,
                                        const B2Polygon& b, const B2Transform& xfB) {
    b2Polygon pa = a.GetHandle();
    b2Polygon pb = b.GetHandle();
    return B2Manifold(b2CollidePolygons(&pa, xfA.value, &pb, xfB.value));
}
B2Manifold B2Collision::CollideSegmentAndPolygon(const B2Segment& a, const B2Transform& xfA,
                                                 const B2Polygon& b, const B2Transform& xfB) {
    b2Polygon pb = b.GetHandle();
    return B2Manifold(b2CollideSegmentAndPolygon(&a.value, xfA.value, &pb, xfB.value));
}
B2Manifold B2Collision::CollideChainSegmentAndCircle(const B2ChainSegment& a, const B2Transform& xfA,
                                                     const B2Circle& b, const B2Transform& xfB) {
    return B2Manifold(b2CollideChainSegmentAndCircle(&a.value, xfA.value, &b.value, xfB.value));
}
B2Manifold B2Collision::CollideChainSegmentAndCapsule(const B2ChainSegment& a, const B2Transform& xfA,
                                                      const B2Capsule& b, const B2Transform& xfB,
                                                      B2SimplexCache& cache) {
    return B2Manifold(b2CollideChainSegmentAndCapsule(&a.value, xfA.value, &b.value, xfB.value, &cache.value));
}
B2Manifold B2Collision::CollideChainSegmentAndPolygon(const B2ChainSegment& a, const B2Transform& xfA,
                                                      const B2Polygon& b, const B2Transform& xfB,
                                                      B2SimplexCache& cache) {
    b2Polygon pb = b.GetHandle();
    return B2Manifold(b2CollideChainSegmentAndPolygon(&a.value, xfA.value, &pb, xfB.value, &cache.value));
}
int B2Collision::TOIUnknown() { return b2_toiStateUnknown; }
int B2Collision::TOIFailed() { return b2_toiStateFailed; }
int B2Collision::TOIOverlapped() { return b2_toiStateOverlapped; }
int B2Collision::TOIHit() { return b2_toiStateHit; }
int B2Collision::TOISeparated() { return b2_toiStateSeparated; }

B2WorldCastHit::B2WorldCastHit() : shapeId(0), point(), normal(), fraction(0.0f) {}
B2WorldCastHit::B2WorldCastHit(long long shapeId, b2Vec2 point, b2Vec2 normal, float fraction)
    : shapeId(shapeId), point(point), normal(normal), fraction(fraction) {}
long long B2WorldCastHit::GetShapeId() const { return shapeId; }
B2Vec2 B2WorldCastHit::GetPoint() const { return point; }
B2Vec2 B2WorldCastHit::GetNormal() const { return normal; }
float B2WorldCastHit::GetFraction() const { return fraction; }

B2WorldCastResult::B2WorldCastResult() : nodeVisits(0), leafVisits(0) {}
int B2WorldCastResult::GetHitCount() const { return static_cast<int>(hits.size()); }
B2WorldCastHit B2WorldCastResult::GetHit(int index) const {
    int checked = checkedIndex(index, static_cast<int>(hits.size()));
    return checked >= 0 ? hits[checked] : B2WorldCastHit();
}
int B2WorldCastResult::GetNodeVisits() const { return nodeVisits; }
int B2WorldCastResult::GetLeafVisits() const { return leafVisits; }

B2WorldOverlapResult::B2WorldOverlapResult() : nodeVisits(0), leafVisits(0) {}
int B2WorldOverlapResult::GetShapeCount() const { return static_cast<int>(shapeIds.size()); }
long long B2WorldOverlapResult::GetShapeId(int index) const {
    int checked = checkedIndex(index, static_cast<int>(shapeIds.size()));
    return checked >= 0 ? shapeIds[checked] : 0;
}
int B2WorldOverlapResult::GetNodeVisits() const { return nodeVisits; }
int B2WorldOverlapResult::GetLeafVisits() const { return leafVisits; }

B2MoverResult::B2MoverResult() : translation(), clippedVelocity(), iterationCount(0) {}
B2Vec2 B2MoverResult::GetTranslation() const { return translation; }
B2Vec2 B2MoverResult::GetClippedVelocity() const { return clippedVelocity; }
int B2MoverResult::GetIterationCount() const { return iterationCount; }
int B2MoverResult::GetPlaneCount() const { return static_cast<int>(planes.size()); }
B2Vec2 B2MoverResult::GetPlaneNormal(int index) const {
    int checked = checkedIndex(index, static_cast<int>(planes.size()));
    return checked >= 0 ? B2Vec2(planes[checked].plane.normal) : B2Vec2();
}
float B2MoverResult::GetPlaneOffset(int index) const {
    int checked = checkedIndex(index, static_cast<int>(planes.size()));
    return checked >= 0 ? planes[checked].plane.offset : 0.0f;
}
float B2MoverResult::GetPlanePush(int index) const {
    int checked = checkedIndex(index, static_cast<int>(planes.size()));
    return checked >= 0 ? planes[checked].push : 0.0f;
}

B2BodyMoveEvent::B2BodyMoveEvent() : bodyId(0), transform(), fellAsleep(false) {}
B2BodyMoveEvent::B2BodyMoveEvent(const b2BodyMoveEvent& event)
    : bodyId(static_cast<long long>(b2StoreBodyId(event.bodyId))), transform(event.transform), fellAsleep(event.fellAsleep) {}
long long B2BodyMoveEvent::GetBodyId() const { return bodyId; }
B2Transform B2BodyMoveEvent::GetTransform() const { return transform; }
bool B2BodyMoveEvent::GetFellAsleep() const { return fellAsleep; }

B2BodyEvents::B2BodyEvents() = default;
B2BodyEvents::B2BodyEvents(const b2BodyEvents& events) {
    moveEvents.reserve(events.moveCount);
    for(int i = 0; i < events.moveCount; ++i) moveEvents.emplace_back(events.moveEvents[i]);
}
int B2BodyEvents::GetMoveCount() const { return static_cast<int>(moveEvents.size()); }
B2BodyMoveEvent B2BodyEvents::GetMoveEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(moveEvents.size()));
    return checked >= 0 ? moveEvents[checked] : B2BodyMoveEvent();
}

B2SensorBeginTouchEvent::B2SensorBeginTouchEvent() : sensorShapeId(0), visitorShapeId(0) {}
B2SensorBeginTouchEvent::B2SensorBeginTouchEvent(const b2SensorBeginTouchEvent& event)
    : sensorShapeId(static_cast<long long>(b2StoreShapeId(event.sensorShapeId))), visitorShapeId(static_cast<long long>(b2StoreShapeId(event.visitorShapeId))) {}
long long B2SensorBeginTouchEvent::GetSensorShapeId() const { return sensorShapeId; }
long long B2SensorBeginTouchEvent::GetVisitorShapeId() const { return visitorShapeId; }

B2SensorEndTouchEvent::B2SensorEndTouchEvent() : sensorShapeId(0), visitorShapeId(0) {}
B2SensorEndTouchEvent::B2SensorEndTouchEvent(const b2SensorEndTouchEvent& event)
    : sensorShapeId(static_cast<long long>(b2StoreShapeId(event.sensorShapeId))), visitorShapeId(static_cast<long long>(b2StoreShapeId(event.visitorShapeId))) {}
long long B2SensorEndTouchEvent::GetSensorShapeId() const { return sensorShapeId; }
long long B2SensorEndTouchEvent::GetVisitorShapeId() const { return visitorShapeId; }

B2SensorEvents::B2SensorEvents() = default;
B2SensorEvents::B2SensorEvents(const b2SensorEvents& events) {
    beginEvents.reserve(events.beginCount);
    for(int i = 0; i < events.beginCount; ++i) beginEvents.emplace_back(events.beginEvents[i]);
    endEvents.reserve(events.endCount);
    for(int i = 0; i < events.endCount; ++i) endEvents.emplace_back(events.endEvents[i]);
}
int B2SensorEvents::GetBeginCount() const { return static_cast<int>(beginEvents.size()); }
B2SensorBeginTouchEvent B2SensorEvents::GetBeginEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(beginEvents.size()));
    return checked >= 0 ? beginEvents[checked] : B2SensorBeginTouchEvent();
}
int B2SensorEvents::GetEndCount() const { return static_cast<int>(endEvents.size()); }
B2SensorEndTouchEvent B2SensorEvents::GetEndEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(endEvents.size()));
    return checked >= 0 ? endEvents[checked] : B2SensorEndTouchEvent();
}

B2ContactBeginTouchEvent::B2ContactBeginTouchEvent() : shapeIdA(0), shapeIdB(0), manifold() {}
B2ContactBeginTouchEvent::B2ContactBeginTouchEvent(const b2ContactBeginTouchEvent& event)
    : shapeIdA(static_cast<long long>(b2StoreShapeId(event.shapeIdA))), shapeIdB(static_cast<long long>(b2StoreShapeId(event.shapeIdB))), manifold(event.manifold) {}
long long B2ContactBeginTouchEvent::GetShapeIdA() const { return shapeIdA; }
long long B2ContactBeginTouchEvent::GetShapeIdB() const { return shapeIdB; }
B2Manifold B2ContactBeginTouchEvent::GetManifold() const { return manifold; }

B2ContactEndTouchEvent::B2ContactEndTouchEvent() : shapeIdA(0), shapeIdB(0) {}
B2ContactEndTouchEvent::B2ContactEndTouchEvent(const b2ContactEndTouchEvent& event)
    : shapeIdA(static_cast<long long>(b2StoreShapeId(event.shapeIdA))), shapeIdB(static_cast<long long>(b2StoreShapeId(event.shapeIdB))) {}
long long B2ContactEndTouchEvent::GetShapeIdA() const { return shapeIdA; }
long long B2ContactEndTouchEvent::GetShapeIdB() const { return shapeIdB; }

B2ContactHitEvent::B2ContactHitEvent() : shapeIdA(0), shapeIdB(0), point(), normal(), approachSpeed(0.0f) {}
B2ContactHitEvent::B2ContactHitEvent(const b2ContactHitEvent& event)
    : shapeIdA(static_cast<long long>(b2StoreShapeId(event.shapeIdA))), shapeIdB(static_cast<long long>(b2StoreShapeId(event.shapeIdB))), point(event.point), normal(event.normal), approachSpeed(event.approachSpeed) {}
long long B2ContactHitEvent::GetShapeIdA() const { return shapeIdA; }
long long B2ContactHitEvent::GetShapeIdB() const { return shapeIdB; }
B2Vec2 B2ContactHitEvent::GetPoint() const { return point; }
B2Vec2 B2ContactHitEvent::GetNormal() const { return normal; }
float B2ContactHitEvent::GetApproachSpeed() const { return approachSpeed; }

B2ContactEvents::B2ContactEvents() = default;
B2ContactEvents::B2ContactEvents(const b2ContactEvents& events) {
    beginEvents.reserve(events.beginCount);
    for(int i = 0; i < events.beginCount; ++i) beginEvents.emplace_back(events.beginEvents[i]);
    endEvents.reserve(events.endCount);
    for(int i = 0; i < events.endCount; ++i) endEvents.emplace_back(events.endEvents[i]);
    hitEvents.reserve(events.hitCount);
    for(int i = 0; i < events.hitCount; ++i) hitEvents.emplace_back(events.hitEvents[i]);
}
int B2ContactEvents::GetBeginCount() const { return static_cast<int>(beginEvents.size()); }
B2ContactBeginTouchEvent B2ContactEvents::GetBeginEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(beginEvents.size()));
    return checked >= 0 ? beginEvents[checked] : B2ContactBeginTouchEvent();
}
int B2ContactEvents::GetEndCount() const { return static_cast<int>(endEvents.size()); }
B2ContactEndTouchEvent B2ContactEvents::GetEndEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(endEvents.size()));
    return checked >= 0 ? endEvents[checked] : B2ContactEndTouchEvent();
}
int B2ContactEvents::GetHitCount() const { return static_cast<int>(hitEvents.size()); }
B2ContactHitEvent B2ContactEvents::GetHitEvent(int index) const {
    int checked = checkedIndex(index, static_cast<int>(hitEvents.size()));
    return checked >= 0 ? hitEvents[checked] : B2ContactHitEvent();
}

B2DebugPolygon::B2DebugPolygon() = default;
B2DebugPolygon::B2DebugPolygon(const b2Vec2* source, int count) {
    vertices.reserve(count);
    for(int i = 0; i < count; ++i) vertices.emplace_back(source[i]);
}
int B2DebugPolygon::GetVertexCount() const { return static_cast<int>(vertices.size()); }
B2Vec2 B2DebugPolygon::GetVertex(int index) const {
    int checked = checkedIndex(index, static_cast<int>(vertices.size()));
    return checked >= 0 ? vertices[checked] : B2Vec2();
}

B2Body::B2Body() : m_bodyId(b2_nullBodyId) {}
B2Body::B2Body(long long bodyId) : m_bodyId(loadBodyId(bodyId)) {}
B2Body::B2Body(b2BodyId bodyId) : m_bodyId(bodyId) {}
long long B2Body::GetId() const { return static_cast<long long>(b2StoreBodyId(m_bodyId)); }
bool B2Body::IsValid() const { return b2Body_IsValid(m_bodyId); }
void B2Body::Destroy() { if(IsValid()) b2DestroyBody(m_bodyId); m_bodyId = b2_nullBodyId; }
int B2Body::GetType() const { return static_cast<int>(b2Body_GetType(m_bodyId)); }
void B2Body::SetType(int type) { b2Body_SetType(m_bodyId, static_cast<b2BodyType>(type)); }
B2Vec2 B2Body::GetPosition() const { return B2Vec2(b2Body_GetPosition(m_bodyId)); }
B2Rot B2Body::GetRotation() const { return B2Rot(b2Body_GetRotation(m_bodyId)); }
B2Transform B2Body::GetTransform() const { return B2Transform(b2Body_GetTransform(m_bodyId)); }
void B2Body::SetTransform(const B2Vec2& p, const B2Rot& q) { b2Body_SetTransform(m_bodyId, p.value, q.value); }
void B2Body::SetTargetTransform(const B2Transform& target, float timeStep) { b2Body_SetTargetTransform(m_bodyId, target.value, timeStep); }
B2Vec2 B2Body::GetLocalPoint(const B2Vec2& point) const { return B2Vec2(b2Body_GetLocalPoint(m_bodyId, point.value)); }
B2Vec2 B2Body::GetWorldPoint(const B2Vec2& point) const { return B2Vec2(b2Body_GetWorldPoint(m_bodyId, point.value)); }
B2Vec2 B2Body::GetLocalVector(const B2Vec2& vector) const { return B2Vec2(b2Body_GetLocalVector(m_bodyId, vector.value)); }
B2Vec2 B2Body::GetWorldVector(const B2Vec2& vector) const { return B2Vec2(b2Body_GetWorldVector(m_bodyId, vector.value)); }
B2Vec2 B2Body::GetLinearVelocity() const { return B2Vec2(b2Body_GetLinearVelocity(m_bodyId)); }
void B2Body::SetLinearVelocity(const B2Vec2& v) { b2Body_SetLinearVelocity(m_bodyId, v.value); }
float B2Body::GetAngularVelocity() const { return b2Body_GetAngularVelocity(m_bodyId); }
void B2Body::SetAngularVelocity(float v) { b2Body_SetAngularVelocity(m_bodyId, v); }
void B2Body::ApplyForce(const B2Vec2& f, const B2Vec2& p, bool wake) { b2Body_ApplyForce(m_bodyId, f.value, p.value, wake); }
void B2Body::ApplyForceToCenter(const B2Vec2& f, bool wake) { b2Body_ApplyForceToCenter(m_bodyId, f.value, wake); }
void B2Body::ApplyTorque(float torque, bool wake) { b2Body_ApplyTorque(m_bodyId, torque, wake); }
void B2Body::ApplyLinearImpulse(const B2Vec2& impulse, const B2Vec2& point, bool wake) { b2Body_ApplyLinearImpulse(m_bodyId, impulse.value, point.value, wake); }
void B2Body::ApplyLinearImpulseToCenter(const B2Vec2& impulse, bool wake) { b2Body_ApplyLinearImpulseToCenter(m_bodyId, impulse.value, wake); }
void B2Body::ApplyAngularImpulse(float impulse, bool wake) { b2Body_ApplyAngularImpulse(m_bodyId, impulse, wake); }
float B2Body::GetMass() const { return b2Body_GetMass(m_bodyId); }
float B2Body::GetRotationalInertia() const { return b2Body_GetRotationalInertia(m_bodyId); }
B2Vec2 B2Body::GetLocalCenterOfMass() const { return B2Vec2(b2Body_GetLocalCenterOfMass(m_bodyId)); }
B2Vec2 B2Body::GetWorldCenterOfMass() const { return B2Vec2(b2Body_GetWorldCenterOfMass(m_bodyId)); }
B2MassData B2Body::GetMassData() const { return B2MassData(b2Body_GetMassData(m_bodyId)); }
void B2Body::SetMassData(const B2MassData& data) { b2Body_SetMassData(m_bodyId, data.value); }
void B2Body::ApplyMassFromShapes() { b2Body_ApplyMassFromShapes(m_bodyId); }
float B2Body::GetLinearDamping() const { return b2Body_GetLinearDamping(m_bodyId); }
void B2Body::SetLinearDamping(float v) { b2Body_SetLinearDamping(m_bodyId, v); }
float B2Body::GetAngularDamping() const { return b2Body_GetAngularDamping(m_bodyId); }
void B2Body::SetAngularDamping(float v) { b2Body_SetAngularDamping(m_bodyId, v); }
float B2Body::GetGravityScale() const { return b2Body_GetGravityScale(m_bodyId); }
void B2Body::SetGravityScale(float v) { b2Body_SetGravityScale(m_bodyId, v); }
bool B2Body::IsAwake() const { return b2Body_IsAwake(m_bodyId); }
void B2Body::SetAwake(bool v) { b2Body_SetAwake(m_bodyId, v); }
bool B2Body::IsSleepEnabled() const { return b2Body_IsSleepEnabled(m_bodyId); }
void B2Body::EnableSleep(bool v) { b2Body_EnableSleep(m_bodyId, v); }
bool B2Body::IsEnabled() const { return b2Body_IsEnabled(m_bodyId); }
void B2Body::Disable() { b2Body_Disable(m_bodyId); }
void B2Body::Enable() { b2Body_Enable(m_bodyId); }
bool B2Body::IsFixedRotation() const { return b2Body_IsFixedRotation(m_bodyId); }
void B2Body::SetFixedRotation(bool v) { b2Body_SetFixedRotation(m_bodyId, v); }
bool B2Body::IsBullet() const { return b2Body_IsBullet(m_bodyId); }
void B2Body::SetBullet(bool v) { b2Body_SetBullet(m_bodyId, v); }
int B2Body::GetShapeCount() const { return b2Body_GetShapeCount(m_bodyId); }
B2Shape* B2Body::GetShape(int index) const {
    int count = b2Body_GetShapeCount(m_bodyId);
    if(checkedIndex(index, count) < 0) return new B2Shape();
    std::vector<b2ShapeId> ids(count);
    int actual = b2Body_GetShapes(m_bodyId, ids.data(), count);
    return index < actual ? new B2Shape(ids[index]) : new B2Shape();
}
int B2Body::GetJointCount() const { return b2Body_GetJointCount(m_bodyId); }
B2Joint* B2Body::GetJoint(int index) const {
    int count = b2Body_GetJointCount(m_bodyId);
    if(checkedIndex(index, count) < 0) return new B2Joint();
    std::vector<b2JointId> ids(count);
    int actual = b2Body_GetJoints(m_bodyId, ids.data(), count);
    return index < actual ? new B2Joint(ids[index]) : new B2Joint();
}
B2AABB B2Body::ComputeAABB() const { return B2AABB(b2Body_ComputeAABB(m_bodyId)); }
B2Shape* B2Body::CreateCircleShape(const B2ShapeDef& def, const B2Circle& circle) { return new B2Shape(b2CreateCircleShape(m_bodyId, &def.value, &circle.value)); }
B2Shape* B2Body::CreateCapsuleShape(const B2ShapeDef& def, const B2Capsule& capsule) { return new B2Shape(b2CreateCapsuleShape(m_bodyId, &def.value, &capsule.value)); }
B2Shape* B2Body::CreateSegmentShape(const B2ShapeDef& def, const B2Segment& segment) { return new B2Shape(b2CreateSegmentShape(m_bodyId, &def.value, &segment.value)); }
B2Shape* B2Body::CreatePolygonShape(const B2ShapeDef& def, const B2Polygon& polygon) {
    b2Polygon value = polygon.GetHandle();
    return new B2Shape(b2CreatePolygonShape(m_bodyId, &def.value, &value));
}
B2Chain* B2Body::CreateChain(B2ChainDef& def) { return new B2Chain(b2CreateChain(m_bodyId, def.GetHandle())); }
b2BodyId B2Body::GetHandle() const { return m_bodyId; }

B2Shape::B2Shape() : m_shapeId(b2_nullShapeId) {}
B2Shape::B2Shape(long long shapeId) : m_shapeId(loadShapeId(shapeId)) {}
B2Shape::B2Shape(b2ShapeId shapeId) : m_shapeId(shapeId) {}
long long B2Shape::GetId() const { return static_cast<long long>(b2StoreShapeId(m_shapeId)); }
bool B2Shape::IsValid() const { return b2Shape_IsValid(m_shapeId); }
void B2Shape::Destroy(bool update) { if(IsValid()) b2DestroyShape(m_shapeId, update); m_shapeId = b2_nullShapeId; }
int B2Shape::GetType() const { return static_cast<int>(b2Shape_GetType(m_shapeId)); }
long long B2Shape::GetBodyId() const { return static_cast<long long>(b2StoreBodyId(b2Shape_GetBody(m_shapeId))); }
bool B2Shape::IsSensor() const { return b2Shape_IsSensor(m_shapeId); }
float B2Shape::GetDensity() const { return b2Shape_GetDensity(m_shapeId); }
void B2Shape::SetDensity(float v, bool update) { b2Shape_SetDensity(m_shapeId, v, update); }
float B2Shape::GetFriction() const { return b2Shape_GetFriction(m_shapeId); }
void B2Shape::SetFriction(float v) { b2Shape_SetFriction(m_shapeId, v); }
float B2Shape::GetRestitution() const { return b2Shape_GetRestitution(m_shapeId); }
void B2Shape::SetRestitution(float v) { b2Shape_SetRestitution(m_shapeId, v); }
B2SurfaceMaterial B2Shape::GetSurfaceMaterial() const { return B2SurfaceMaterial(b2Shape_GetSurfaceMaterial(m_shapeId)); }
void B2Shape::SetSurfaceMaterial(const B2SurfaceMaterial& material) { b2Shape_SetSurfaceMaterial(m_shapeId, material.value); }
B2Filter B2Shape::GetFilter() const { return B2Filter(b2Shape_GetFilter(m_shapeId)); }
void B2Shape::SetFilter(const B2Filter& filter) { b2Shape_SetFilter(m_shapeId, filter.value); }
void B2Shape::EnableSensorEvents(bool v) { b2Shape_EnableSensorEvents(m_shapeId, v); }
bool B2Shape::AreSensorEventsEnabled() const { return b2Shape_AreSensorEventsEnabled(m_shapeId); }
void B2Shape::EnableContactEvents(bool v) { b2Shape_EnableContactEvents(m_shapeId, v); }
bool B2Shape::AreContactEventsEnabled() const { return b2Shape_AreContactEventsEnabled(m_shapeId); }
void B2Shape::EnablePreSolveEvents(bool v) { b2Shape_EnablePreSolveEvents(m_shapeId, v); }
bool B2Shape::ArePreSolveEventsEnabled() const { return b2Shape_ArePreSolveEventsEnabled(m_shapeId); }
void B2Shape::EnableHitEvents(bool v) { b2Shape_EnableHitEvents(m_shapeId, v); }
bool B2Shape::AreHitEventsEnabled() const { return b2Shape_AreHitEventsEnabled(m_shapeId); }
bool B2Shape::TestPoint(const B2Vec2& point) const { return b2Shape_TestPoint(m_shapeId, point.value); }
B2CastResult B2Shape::RayCast(const B2Vec2& origin, const B2Vec2& translation, float maxFraction) const {
    b2RayCastInput input{origin.value, translation.value, maxFraction};
    return B2CastResult(b2Shape_RayCast(m_shapeId, &input));
}
B2Circle B2Shape::GetCircle() const { return B2Circle(b2Shape_GetCircle(m_shapeId)); }
void B2Shape::SetCircle(const B2Circle& circle) { b2Shape_SetCircle(m_shapeId, &circle.value); }
B2Capsule B2Shape::GetCapsule() const { return B2Capsule(b2Shape_GetCapsule(m_shapeId)); }
void B2Shape::SetCapsule(const B2Capsule& capsule) { b2Shape_SetCapsule(m_shapeId, &capsule.value); }
B2Segment B2Shape::GetSegment() const { return B2Segment(b2Shape_GetSegment(m_shapeId)); }
void B2Shape::SetSegment(const B2Segment& segment) { b2Shape_SetSegment(m_shapeId, &segment.value); }
B2Polygon* B2Shape::GetPolygon() const { return new B2Polygon(b2Shape_GetPolygon(m_shapeId)); }
void B2Shape::SetPolygon(const B2Polygon& polygon) {
    b2Polygon value = polygon.GetHandle();
    b2Shape_SetPolygon(m_shapeId, &value);
}
B2AABB B2Shape::GetAABB() const { return B2AABB(b2Shape_GetAABB(m_shapeId)); }
B2MassData B2Shape::GetMassData() const { return B2MassData(b2Shape_GetMassData(m_shapeId)); }
B2Vec2 B2Shape::GetClosestPoint(const B2Vec2& target) const { return B2Vec2(b2Shape_GetClosestPoint(m_shapeId, target.value)); }

B2Chain::B2Chain() : m_chainId(b2_nullChainId) {}
B2Chain::B2Chain(long long chainId) : m_chainId(loadChainId(chainId)) {}
B2Chain::B2Chain(b2ChainId chainId) : m_chainId(chainId) {}
long long B2Chain::GetId() const { return static_cast<long long>(b2StoreChainId(m_chainId)); }
bool B2Chain::IsValid() const { return b2Chain_IsValid(m_chainId); }
void B2Chain::Destroy() { if(IsValid()) b2DestroyChain(m_chainId); m_chainId = b2_nullChainId; }
int B2Chain::GetSegmentCount() const { return b2Chain_GetSegmentCount(m_chainId); }
B2Shape* B2Chain::GetSegment(int index) const {
    int count = b2Chain_GetSegmentCount(m_chainId);
    if(checkedIndex(index, count) < 0) return new B2Shape();
    std::vector<b2ShapeId> ids(count);
    int actual = b2Chain_GetSegments(m_chainId, ids.data(), count);
    return index < actual ? new B2Shape(ids[index]) : new B2Shape();
}
float B2Chain::GetFriction() const { return b2Chain_GetFriction(m_chainId); }
void B2Chain::SetFriction(float v) { b2Chain_SetFriction(m_chainId, v); }
float B2Chain::GetRestitution() const { return b2Chain_GetRestitution(m_chainId); }
void B2Chain::SetRestitution(float v) { b2Chain_SetRestitution(m_chainId, v); }

B2Joint::B2Joint() : m_jointId(b2_nullJointId) {}
B2Joint::B2Joint(long long jointId) : m_jointId(loadJointId(jointId)) {}
B2Joint::B2Joint(b2JointId jointId) : m_jointId(jointId) {}
long long B2Joint::GetId() const { return static_cast<long long>(b2StoreJointId(m_jointId)); }
bool B2Joint::IsValid() const { return b2Joint_IsValid(m_jointId); }
void B2Joint::Destroy() { if(IsValid()) b2DestroyJoint(m_jointId); m_jointId = b2_nullJointId; }
int B2Joint::GetType() const { return static_cast<int>(b2Joint_GetType(m_jointId)); }
long long B2Joint::GetBodyIdA() const { return static_cast<long long>(b2StoreBodyId(b2Joint_GetBodyA(m_jointId))); }
long long B2Joint::GetBodyIdB() const { return static_cast<long long>(b2StoreBodyId(b2Joint_GetBodyB(m_jointId))); }
B2Vec2 B2Joint::GetLocalAnchorA() const { return B2Vec2(b2Joint_GetLocalAnchorA(m_jointId)); }
void B2Joint::SetLocalAnchorA(const B2Vec2& v) { b2Joint_SetLocalAnchorA(m_jointId, v.value); }
B2Vec2 B2Joint::GetLocalAnchorB() const { return B2Vec2(b2Joint_GetLocalAnchorB(m_jointId)); }
void B2Joint::SetLocalAnchorB(const B2Vec2& v) { b2Joint_SetLocalAnchorB(m_jointId, v.value); }
bool B2Joint::GetCollideConnected() const { return b2Joint_GetCollideConnected(m_jointId); }
void B2Joint::SetCollideConnected(bool v) { b2Joint_SetCollideConnected(m_jointId, v); }
void B2Joint::WakeBodies() { b2Joint_WakeBodies(m_jointId); }
B2Vec2 B2Joint::GetConstraintForce() const { return B2Vec2(b2Joint_GetConstraintForce(m_jointId)); }
float B2Joint::GetConstraintTorque() const { return b2Joint_GetConstraintTorque(m_jointId); }
float B2Joint::GetLinearSeparation() const { return b2Joint_GetLinearSeparation(m_jointId); }
float B2Joint::GetAngularSeparation() const { return b2Joint_GetAngularSeparation(m_jointId); }
void B2Joint::DistanceSetLength(float v) { b2DistanceJoint_SetLength(m_jointId, v); }
float B2Joint::DistanceGetLength() const { return b2DistanceJoint_GetLength(m_jointId); }
void B2Joint::DistanceEnableSpring(bool v) { b2DistanceJoint_EnableSpring(m_jointId, v); }
void B2Joint::DistanceSetSpringHertz(float v) { b2DistanceJoint_SetSpringHertz(m_jointId, v); }
void B2Joint::DistanceSetSpringDampingRatio(float v) { b2DistanceJoint_SetSpringDampingRatio(m_jointId, v); }
void B2Joint::DistanceEnableLimit(bool v) { b2DistanceJoint_EnableLimit(m_jointId, v); }
void B2Joint::DistanceSetLengthRange(float min, float max) { b2DistanceJoint_SetLengthRange(m_jointId, min, max); }
void B2Joint::DistanceEnableMotor(bool v) { b2DistanceJoint_EnableMotor(m_jointId, v); }
void B2Joint::DistanceSetMotorSpeed(float v) { b2DistanceJoint_SetMotorSpeed(m_jointId, v); }
void B2Joint::DistanceSetMaxMotorForce(float v) { b2DistanceJoint_SetMaxMotorForce(m_jointId, v); }
void B2Joint::MotorSetLinearOffset(const B2Vec2& v) { b2MotorJoint_SetLinearOffset(m_jointId, v.value); }
void B2Joint::MotorSetAngularOffset(float v) { b2MotorJoint_SetAngularOffset(m_jointId, v); }
void B2Joint::MotorSetMaxForce(float v) { b2MotorJoint_SetMaxForce(m_jointId, v); }
void B2Joint::MotorSetMaxTorque(float v) { b2MotorJoint_SetMaxTorque(m_jointId, v); }
void B2Joint::MotorSetCorrectionFactor(float v) { b2MotorJoint_SetCorrectionFactor(m_jointId, v); }
void B2Joint::MouseSetTarget(const B2Vec2& v) { b2MouseJoint_SetTarget(m_jointId, v.value); }
void B2Joint::MouseSetSpringHertz(float v) { b2MouseJoint_SetSpringHertz(m_jointId, v); }
void B2Joint::MouseSetSpringDampingRatio(float v) { b2MouseJoint_SetSpringDampingRatio(m_jointId, v); }
void B2Joint::MouseSetMaxForce(float v) { b2MouseJoint_SetMaxForce(m_jointId, v); }
void B2Joint::PrismaticEnableSpring(bool v) { b2PrismaticJoint_EnableSpring(m_jointId, v); }
void B2Joint::PrismaticSetSpringHertz(float v) { b2PrismaticJoint_SetSpringHertz(m_jointId, v); }
void B2Joint::PrismaticSetSpringDampingRatio(float v) { b2PrismaticJoint_SetSpringDampingRatio(m_jointId, v); }
void B2Joint::PrismaticSetTargetTranslation(float v) { b2PrismaticJoint_SetTargetTranslation(m_jointId, v); }
void B2Joint::PrismaticEnableLimit(bool v) { b2PrismaticJoint_EnableLimit(m_jointId, v); }
void B2Joint::PrismaticSetLimits(float lower, float upper) { b2PrismaticJoint_SetLimits(m_jointId, lower, upper); }
void B2Joint::PrismaticEnableMotor(bool v) { b2PrismaticJoint_EnableMotor(m_jointId, v); }
void B2Joint::PrismaticSetMotorSpeed(float v) { b2PrismaticJoint_SetMotorSpeed(m_jointId, v); }
void B2Joint::PrismaticSetMaxMotorForce(float v) { b2PrismaticJoint_SetMaxMotorForce(m_jointId, v); }
float B2Joint::PrismaticGetTranslation() const { return b2PrismaticJoint_GetTranslation(m_jointId); }
void B2Joint::RevoluteEnableSpring(bool v) { b2RevoluteJoint_EnableSpring(m_jointId, v); }
void B2Joint::RevoluteSetSpringHertz(float v) { b2RevoluteJoint_SetSpringHertz(m_jointId, v); }
void B2Joint::RevoluteSetSpringDampingRatio(float v) { b2RevoluteJoint_SetSpringDampingRatio(m_jointId, v); }
void B2Joint::RevoluteSetTargetAngle(float v) { b2RevoluteJoint_SetTargetAngle(m_jointId, v); }
void B2Joint::RevoluteEnableLimit(bool v) { b2RevoluteJoint_EnableLimit(m_jointId, v); }
void B2Joint::RevoluteSetLimits(float lower, float upper) { b2RevoluteJoint_SetLimits(m_jointId, lower, upper); }
void B2Joint::RevoluteEnableMotor(bool v) { b2RevoluteJoint_EnableMotor(m_jointId, v); }
void B2Joint::RevoluteSetMotorSpeed(float v) { b2RevoluteJoint_SetMotorSpeed(m_jointId, v); }
void B2Joint::RevoluteSetMaxMotorTorque(float v) { b2RevoluteJoint_SetMaxMotorTorque(m_jointId, v); }
float B2Joint::RevoluteGetAngle() const { return b2RevoluteJoint_GetAngle(m_jointId); }
void B2Joint::WheelEnableSpring(bool v) { b2WheelJoint_EnableSpring(m_jointId, v); }
void B2Joint::WheelSetSpringHertz(float v) { b2WheelJoint_SetSpringHertz(m_jointId, v); }
void B2Joint::WheelSetSpringDampingRatio(float v) { b2WheelJoint_SetSpringDampingRatio(m_jointId, v); }
void B2Joint::WheelEnableLimit(bool v) { b2WheelJoint_EnableLimit(m_jointId, v); }
void B2Joint::WheelSetLimits(float lower, float upper) { b2WheelJoint_SetLimits(m_jointId, lower, upper); }
void B2Joint::WheelEnableMotor(bool v) { b2WheelJoint_EnableMotor(m_jointId, v); }
void B2Joint::WheelSetMotorSpeed(float v) { b2WheelJoint_SetMotorSpeed(m_jointId, v); }
void B2Joint::WheelSetMaxMotorTorque(float v) { b2WheelJoint_SetMaxMotorTorque(m_jointId, v); }
b2JointId B2Joint::GetHandle() const { return m_jointId; }

static B2DebugDrawEm* debugDraw(void* context) { return static_cast<B2DebugDrawEm*>(context); }
static void drawPolygonCallback(const b2Vec2* vertices, int count, b2HexColor color, void* context) {
    B2DebugPolygon polygon(vertices, count);
    debugDraw(context)->DrawPolygon(&polygon, static_cast<int>(color));
}
static void drawSolidPolygonCallback(b2Transform transform, const b2Vec2* vertices, int count, float radius, b2HexColor color, void* context) {
    B2DebugPolygon polygon(vertices, count);
    B2Transform wrappedTransform(transform);
    debugDraw(context)->DrawSolidPolygon(wrappedTransform, &polygon, radius, static_cast<int>(color));
}
static void drawCircleCallback(b2Vec2 center, float radius, b2HexColor color, void* context) {
    B2Vec2 wrapped(center);
    debugDraw(context)->DrawCircle(wrapped, radius, static_cast<int>(color));
}
static void drawSolidCircleCallback(b2Transform transform, float radius, b2HexColor color, void* context) {
    B2Transform wrapped(transform);
    debugDraw(context)->DrawSolidCircle(wrapped, radius, static_cast<int>(color));
}
static void drawSolidCapsuleCallback(b2Vec2 p1, b2Vec2 p2, float radius, b2HexColor color, void* context) {
    B2Vec2 wrappedP1(p1), wrappedP2(p2);
    debugDraw(context)->DrawSolidCapsule(wrappedP1, wrappedP2, radius, static_cast<int>(color));
}
static void drawSegmentCallback(b2Vec2 p1, b2Vec2 p2, b2HexColor color, void* context) {
    B2Vec2 wrappedP1(p1), wrappedP2(p2);
    debugDraw(context)->DrawSegment(wrappedP1, wrappedP2, static_cast<int>(color));
}
static void drawTransformCallback(b2Transform transform, void* context) {
    B2Transform wrapped(transform);
    debugDraw(context)->DrawTransform(wrapped);
}
static void drawPointCallback(b2Vec2 point, float size, b2HexColor color, void* context) {
    B2Vec2 wrapped(point);
    debugDraw(context)->DrawPoint(wrapped, size, static_cast<int>(color));
}

B2DebugDrawEm::B2DebugDrawEm() : m_draw(b2DefaultDebugDraw()) {
    m_draw.DrawPolygonFcn = drawPolygonCallback;
    m_draw.DrawSolidPolygonFcn = drawSolidPolygonCallback;
    m_draw.DrawCircleFcn = drawCircleCallback;
    m_draw.DrawSolidCircleFcn = drawSolidCircleCallback;
    m_draw.DrawSolidCapsuleFcn = drawSolidCapsuleCallback;
    m_draw.DrawSegmentFcn = drawSegmentCallback;
    m_draw.DrawTransformFcn = drawTransformCallback;
    m_draw.DrawPointFcn = drawPointCallback;
    m_draw.context = this;
}
B2DebugDrawEm::~B2DebugDrawEm() = default;
void B2DebugDrawEm::DrawWorld(B2World* world) { if(world != nullptr && world->IsValid()) b2World_Draw(world->GetHandle(), &m_draw); }
void B2DebugDrawEm::SetDrawingBounds(const B2AABB& v) { m_draw.drawingBounds = v.value; }
B2AABB B2DebugDrawEm::GetDrawingBounds() const { return B2AABB(m_draw.drawingBounds); }
#define DEBUG_FLAG(Name, field) \
void B2DebugDrawEm::Set##Name(bool v) { m_draw.field = v; } \
bool B2DebugDrawEm::Get##Name() const { return m_draw.field; }
DEBUG_FLAG(UseDrawingBounds, useDrawingBounds)
DEBUG_FLAG(DrawShapes, drawShapes)
DEBUG_FLAG(DrawJoints, drawJoints)
DEBUG_FLAG(DrawJointExtras, drawJointExtras)
DEBUG_FLAG(DrawBounds, drawBounds)
DEBUG_FLAG(DrawMass, drawMass)
DEBUG_FLAG(DrawBodyNames, drawBodyNames)
DEBUG_FLAG(DrawContacts, drawContacts)
DEBUG_FLAG(DrawGraphColors, drawGraphColors)
DEBUG_FLAG(DrawContactNormals, drawContactNormals)
DEBUG_FLAG(DrawContactImpulses, drawContactImpulses)
DEBUG_FLAG(DrawContactFeatures, drawContactFeatures)
DEBUG_FLAG(DrawFrictionImpulses, drawFrictionImpulses)
DEBUG_FLAG(DrawIslands, drawIslands)
#undef DEBUG_FLAG
void B2DebugDrawEm::DrawPolygon(B2DebugPolygon*, int) {}
void B2DebugDrawEm::DrawSolidPolygon(const B2Transform&, B2DebugPolygon*, float, int) {}
void B2DebugDrawEm::DrawCircle(const B2Vec2&, float, int) {}
void B2DebugDrawEm::DrawSolidCircle(const B2Transform&, float, int) {}
void B2DebugDrawEm::DrawSolidCapsule(const B2Vec2&, const B2Vec2&, float, int) {}
void B2DebugDrawEm::DrawSegment(const B2Vec2&, const B2Vec2&, int) {}
void B2DebugDrawEm::DrawTransform(const B2Transform&) {}
void B2DebugDrawEm::DrawPoint(const B2Vec2&, float, int) {}

B2World::B2World() : B2World(B2WorldDef()) {}
B2World::B2World(const B2WorldDef& def) : m_worldId(b2CreateWorld(&def.value)), m_destroyed(false) {}
B2World::~B2World() { Destroy(); }
long long B2World::GetId() const { return static_cast<long long>(b2StoreWorldId(m_worldId)); }
bool B2World::IsValid() const { return !m_destroyed && b2World_IsValid(m_worldId); }
void B2World::Destroy() {
    if(IsValid()) b2DestroyWorld(m_worldId);
    m_worldId = b2_nullWorldId;
    m_destroyed = true;
}
void B2World::Step(float timeStep, int subStepCount) { b2World_Step(m_worldId, timeStep, subStepCount); }
B2Vec2 B2World::GetGravity() const { return B2Vec2(b2World_GetGravity(m_worldId)); }
void B2World::SetGravity(const B2Vec2& gravity) { b2World_SetGravity(m_worldId, gravity.value); }
bool B2World::IsSleepingEnabled() const { return b2World_IsSleepingEnabled(m_worldId); }
void B2World::EnableSleeping(bool v) { b2World_EnableSleeping(m_worldId, v); }
bool B2World::IsWarmStartingEnabled() const { return b2World_IsWarmStartingEnabled(m_worldId); }
void B2World::EnableWarmStarting(bool v) { b2World_EnableWarmStarting(m_worldId, v); }
bool B2World::IsContinuousEnabled() const { return b2World_IsContinuousEnabled(m_worldId); }
void B2World::EnableContinuous(bool v) { b2World_EnableContinuous(m_worldId, v); }
float B2World::GetRestitutionThreshold() const { return b2World_GetRestitutionThreshold(m_worldId); }
void B2World::SetRestitutionThreshold(float v) { b2World_SetRestitutionThreshold(m_worldId, v); }
float B2World::GetHitEventThreshold() const { return b2World_GetHitEventThreshold(m_worldId); }
void B2World::SetHitEventThreshold(float v) { b2World_SetHitEventThreshold(m_worldId, v); }
float B2World::GetMaximumLinearSpeed() const { return b2World_GetMaximumLinearSpeed(m_worldId); }
void B2World::SetMaximumLinearSpeed(float v) { b2World_SetMaximumLinearSpeed(m_worldId, v); }
void B2World::SetContactTuning(float hertz, float ratio, float speed) { b2World_SetContactTuning(m_worldId, hertz, ratio, speed); }
void B2World::RebuildStaticTree() { b2World_RebuildStaticTree(m_worldId); }
int B2World::GetAwakeBodyCount() const { return b2World_GetAwakeBodyCount(m_worldId); }
B2Body* B2World::CreateBody(const B2BodyDef& def) { return new B2Body(b2CreateBody(m_worldId, &def.value)); }
B2Joint* B2World::CreateDistanceJoint(const B2DistanceJointDef& def) { return new B2Joint(b2CreateDistanceJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateMotorJoint(const B2MotorJointDef& def) { return new B2Joint(b2CreateMotorJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateMouseJoint(const B2MouseJointDef& def) { return new B2Joint(b2CreateMouseJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateFilterJoint(const B2FilterJointDef& def) { return new B2Joint(b2CreateFilterJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreatePrismaticJoint(const B2PrismaticJointDef& def) { return new B2Joint(b2CreatePrismaticJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateRevoluteJoint(const B2RevoluteJointDef& def) { return new B2Joint(b2CreateRevoluteJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateWeldJoint(const B2WeldJointDef& def) { return new B2Joint(b2CreateWeldJoint(m_worldId, &def.value)); }
B2Joint* B2World::CreateWheelJoint(const B2WheelJointDef& def) { return new B2Joint(b2CreateWheelJoint(m_worldId, &def.value)); }
B2BodyEvents* B2World::GetBodyEvents() const { return new B2BodyEvents(b2World_GetBodyEvents(m_worldId)); }
B2SensorEvents* B2World::GetSensorEvents() const { return new B2SensorEvents(b2World_GetSensorEvents(m_worldId)); }
B2ContactEvents* B2World::GetContactEvents() const { return new B2ContactEvents(b2World_GetContactEvents(m_worldId)); }
B2RayResult B2World::CastRayClosest(const B2Vec2& origin, const B2Vec2& translation, const B2QueryFilter& filter) const {
    return B2RayResult(b2World_CastRayClosest(m_worldId, origin.value, translation.value, filter.value));
}

static float worldCastCallback(b2ShapeId shapeId, b2Vec2 point, b2Vec2 normal, float fraction, void* context) {
    B2WorldCastResult* result = static_cast<B2WorldCastResult*>(context);
    result->hits.emplace_back(static_cast<long long>(b2StoreShapeId(shapeId)), point, normal, fraction);
    return 1.0f;
}

static bool worldOverlapCallback(b2ShapeId shapeId, void* context) {
    B2WorldOverlapResult* result = static_cast<B2WorldOverlapResult*>(context);
    result->shapeIds.push_back(static_cast<long long>(b2StoreShapeId(shapeId)));
    return true;
}

B2WorldCastResult* B2World::CastRay(const B2Vec2& origin, const B2Vec2& translation,
                                    const B2QueryFilter& filter) const {
    B2WorldCastResult* result = new B2WorldCastResult();
    b2TreeStats stats = b2World_CastRay(m_worldId, origin.value, translation.value, filter.value,
                                        worldCastCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}

B2WorldCastResult* B2World::CastShape(const B2ShapeProxy& proxy, const B2Vec2& translation,
                                      const B2QueryFilter& filter) const {
    B2WorldCastResult* result = new B2WorldCastResult();
    b2TreeStats stats = b2World_CastShape(m_worldId, &proxy.value, translation.value, filter.value,
                                          worldCastCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}

B2WorldOverlapResult* B2World::OverlapAABB(const B2AABB& aabb, const B2QueryFilter& filter) const {
    B2WorldOverlapResult* result = new B2WorldOverlapResult();
    b2TreeStats stats = b2World_OverlapAABB(m_worldId, aabb.value, filter.value, worldOverlapCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}

B2WorldOverlapResult* B2World::OverlapShape(const B2ShapeProxy& proxy, const B2QueryFilter& filter) const {
    B2WorldOverlapResult* result = new B2WorldOverlapResult();
    b2TreeStats stats = b2World_OverlapShape(m_worldId, &proxy.value, filter.value, worldOverlapCallback, result);
    result->nodeVisits = stats.nodeVisits;
    result->leafVisits = stats.leafVisits;
    return result;
}

float B2World::CastMover(const B2Capsule& mover, const B2Vec2& translation, const B2QueryFilter& filter) const {
    return b2World_CastMover(m_worldId, &mover.value, translation.value, filter.value);
}

struct MoverPlaneContext {
    std::vector<b2CollisionPlane>* planes;
    int capacity;
};

static bool moverPlaneCallback(b2ShapeId, const b2PlaneResult* planeResult, void* context) {
    MoverPlaneContext* planeContext = static_cast<MoverPlaneContext*>(context);
    if(planeResult->hit && static_cast<int>(planeContext->planes->size()) < planeContext->capacity) {
        planeContext->planes->push_back({planeResult->plane, FLT_MAX, 0.0f, true});
    }
    return true;
}

B2MoverResult* B2World::SolveMover(const B2Capsule& mover, const B2Vec2& translation,
                                   const B2Vec2& velocity, const B2QueryFilter& collideFilter,
                                   const B2QueryFilter& castFilter, int maxIterations) const {
    B2MoverResult* result = new B2MoverResult();
    b2Capsule current = mover.value;
    b2Vec2 start = current.center1;
    b2Vec2 target = b2Add(start, translation.value);
    int iterationLimit = std::max(1, std::min(maxIterations, 20));
    constexpr int planeCapacity = 8;

    for(int iteration = 0; iteration < iterationLimit; ++iteration) {
        result->planes.clear();
        MoverPlaneContext context{&result->planes, planeCapacity};
        b2World_CollideMover(m_worldId, &current, collideFilter.value, moverPlaneCallback, &context);

        b2Vec2 desired = b2Sub(target, current.center1);
        b2PlaneSolverResult solve = b2SolvePlanes(desired, result->planes.data(),
                                                   static_cast<int>(result->planes.size()));
        result->iterationCount += solve.iterationCount;
        float fraction = b2World_CastMover(m_worldId, &current, solve.translation, castFilter.value);
        b2Vec2 delta = b2MulSV(fraction, solve.translation);
        current.center1 = b2Add(current.center1, delta);
        current.center2 = b2Add(current.center2, delta);

        if(b2LengthSquared(delta) < 0.0001f) {
            break;
        }
    }

    result->translation.value = b2Sub(current.center1, start);
    result->clippedVelocity.value = result->planes.empty()
        ? velocity.value
        : b2ClipVector(velocity.value, result->planes.data(), static_cast<int>(result->planes.size()));
    return result;
}
b2WorldId B2World::GetHandle() const { return m_worldId; }

int B2::StaticBody() { return b2_staticBody; }
int B2::KinematicBody() { return b2_kinematicBody; }
int B2::DynamicBody() { return b2_dynamicBody; }
int B2::CircleShape() { return b2_circleShape; }
int B2::CapsuleShape() { return b2_capsuleShape; }
int B2::SegmentShape() { return b2_segmentShape; }
int B2::PolygonShape() { return b2_polygonShape; }
int B2::ChainSegmentShape() { return b2_chainSegmentShape; }
int B2::DistanceJoint() { return b2_distanceJoint; }
int B2::FilterJoint() { return b2_filterJoint; }
int B2::MotorJoint() { return b2_motorJoint; }
int B2::MouseJoint() { return b2_mouseJoint; }
int B2::PrismaticJoint() { return b2_prismaticJoint; }
int B2::RevoluteJoint() { return b2_revoluteJoint; }
int B2::WeldJoint() { return b2_weldJoint; }
int B2::WheelJoint() { return b2_wheelJoint; }
int B2::MaxPolygonVertices() { return B2_MAX_POLYGON_VERTICES; }
long long B2::DefaultCategoryBits() { return static_cast<long long>(b2DefaultFilter().categoryBits); }
long long B2::DefaultMaskBits() { return static_cast<long long>(b2DefaultFilter().maskBits); }
int B2::VersionMajor() { return b2GetVersion().major; }
int B2::VersionMinor() { return b2GetVersion().minor; }
int B2::VersionRevision() { return b2GetVersion().revision; }
void B2::SetLengthUnitsPerMeter(float units) { b2SetLengthUnitsPerMeter(units); }
float B2::GetLengthUnitsPerMeter() { return b2GetLengthUnitsPerMeter(); }

} // namespace JBox2D
