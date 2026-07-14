package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Construction helpers shared by the individual Java benchmark sample ports. */
final class BenchmarkSampleSupport {
    // This matches the official testbed's non-NDEBUG branch. The release scale can be requested for profiling.
    static final boolean DEBUG_SIZE = !"release".equalsIgnoreCase(
            System.getProperty("jbox2d.benchmark.size", "debug"));

    private BenchmarkSampleSupport() {
    }

    static B2Shape addBox(AbstractBox2DSample sample, B2Body body, float halfWidth, float halfHeight,
            float centerX, float centerY, float angle, float density, float friction) {
        B2Shape shape = sample.addOffsetBoxShape(body, halfWidth, halfHeight, centerX, centerY, angle,
                density, friction, 0.0f, 0.0f);
        return shape;
    }

    static void discardShape(AbstractBox2DSample sample, B2Shape shape) {
        sample.discardHandle(shape);
    }

    static void destroyAndDiscard(AbstractBox2DSample sample, B2Body body) {
        if(body != null && !body.isDisposed()) {
            sample.destroyBody(body);
            sample.discardHandle(body);
        }
    }

    static B2ShapeDef filteredShapeDef(AbstractBox2DSample sample, float density, float friction,
            long categoryBits, long maskBits, boolean updateMass) {
        B2ShapeDef shapeDef = sample.shapeDef(density, friction, 0.0f, 0.0f);
        B2Filter filter = new B2Filter();
        filter.SetCategoryBits(categoryBits);
        filter.SetMaskBits(maskBits);
        shapeDef.SetFilter(filter);
        shapeDef.SetUpdateBodyMass(updateMass);
        AbstractBox2DSample.release(filter);
        return shapeDef;
    }

    static void createTumbler(AbstractBox2DSample sample) {
        B2Body ground = sample.createStaticBody(0.0f, 0.0f, 0.0f);
        B2Body body = sample.createDynamicBody(0.0f, 10.0f, 0.0f);
        discardShape(sample, addBox(sample, body, 0.5f, 10.0f, 10.0f, 0.0f, 0.0f, 50.0f, 0.6f));
        discardShape(sample, addBox(sample, body, 0.5f, 10.0f, -10.0f, 0.0f, 0.0f, 50.0f, 0.6f));
        discardShape(sample, addBox(sample, body, 10.0f, 0.5f, 0.0f, 10.0f, 0.0f, 50.0f, 0.6f));
        discardShape(sample, addBox(sample, body, 10.0f, 0.5f, 0.0f, -10.0f, 0.0f, 50.0f, 0.6f));

        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 anchorA = new B2Vec2(0.0f, 10.0f);
        B2Vec2 anchorB = new B2Vec2(0.0f, 0.0f);
        def.SetBodyIdA(ground.GetId());
        def.SetBodyIdB(body.GetId());
        def.SetLocalAnchorA(anchorA);
        def.SetLocalAnchorB(anchorB);
        def.SetMotorSpeed(AbstractBox2DSample.radians(25.0f));
        def.SetMaxMotorTorque(1.0e8f);
        def.SetEnableMotor(true);
        sample.createRevoluteJoint(def);
        AbstractBox2DSample.release(anchorB, anchorA, def);

        int gridCount = DEBUG_SIZE ? 20 : 45;
        for(int row = 0; row < gridCount; row++) {
            float y = -0.2f * gridCount + 10.0f + 0.4f * row;
            for(int column = 0; column < gridCount; column++) {
                float x = -0.2f * gridCount + 0.4f * column;
                B2Body item = sample.createDynamicBody(x, y, 0.0f);
                B2Shape shape = sample.addBoxShape(item, 0.125f, 0.125f, 1.0f, 0.6f, 0.0f, 0.0f);
                discardShape(sample, shape);
            }
        }
    }

    static void createPyramid(AbstractBox2DSample sample, int baseCount, float extent, float centerX,
            float baseY) {
        B2Polygon box = B2Polygon.CreateBox(extent, extent);
        B2ShapeDef shapeDef = sample.shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
        for(int row = 0; row < baseCount; row++) {
            float y = (2.0f * row + 1.0f) * extent + baseY;
            for(int column = row; column < baseCount; column++) {
                float x = (row + 1.0f) * extent + 2.0f * (column - row) * extent + centerX - 0.5f;
                B2Body body = sample.createDynamicBody(x, y, 0.0f);
                B2Shape shape = sample.createPolygonShape(body, shapeDef, box);
                discardShape(sample, shape);
            }
        }
        AbstractBox2DSample.release(shapeDef, box);
    }

    static B2Body createBody(AbstractBox2DSample sample, int type, float x, float y, float angle,
            float angularVelocity, float angularDamping, boolean awake, boolean enableSleep) {
        B2BodyDef bodyDef = new B2BodyDef();
        B2Vec2 position = new B2Vec2(x, y);
        bodyDef.SetType(type);
        bodyDef.SetPosition(position);
        bodyDef.SetAngle(angle);
        bodyDef.SetAngularVelocity(angularVelocity);
        bodyDef.SetAngularDamping(angularDamping);
        bodyDef.SetIsAwake(awake);
        bodyDef.SetEnableSleep(enableSleep);
        B2Body body = sample.createBody(bodyDef);
        AbstractBox2DSample.release(position, bodyDef);
        return body;
    }

    static B2Shape addPolygon(AbstractBox2DSample sample, B2Body body, B2ShapeDef shapeDef,
            float halfWidth, float halfHeight, float centerX, float centerY, float angle) {
        B2Vec2 center = new B2Vec2(centerX, centerY);
        B2Rot rotation = new B2Rot(angle);
        B2Polygon polygon = B2Polygon.CreateOffsetBox(halfWidth, halfHeight, center, rotation);
        B2Shape shape = sample.createPolygonShape(body, shapeDef, polygon);
        AbstractBox2DSample.release(polygon, rotation, center);
        return shape;
    }
}
