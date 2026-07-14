package com.github.xpenatan.box2d;

import com.github.xpenatan.jParser.api.NativeObject;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/** End-to-end check that the generated JNI surface can drive Box2D 3.1.1. */
public class Box2DJniSmokeTest {

    @Test
    public void createStepAndDestroyWorld() throws Exception {
        loadBox2D();

        B2Vec2 gravity = new B2Vec2(0f, -10f);
        B2WorldDef worldDef = new B2WorldDef();
        B2BodyDef groundDef = new B2BodyDef();
        B2ChainDef chainDef = new B2ChainDef();
        B2Vec2 chainGhost1 = new B2Vec2(-3f, 0f);
        B2Vec2 chainStart = new B2Vec2(-2f, 0f);
        B2Vec2 chainEnd = new B2Vec2(2f, 0f);
        B2Vec2 chainGhost2 = new B2Vec2(3f, 0f);
        B2DistanceJointDef jointDef = new B2DistanceJointDef();
        B2BodyDef bodyDef = new B2BodyDef();
        B2Vec2 startPosition = new B2Vec2(0f, 5f);
        B2ShapeDef shapeDef = new B2ShapeDef();
        B2Polygon box = B2Polygon.CreateBox(0.5f, 0.5f);
        B2World world = null;
        B2Body ground = null;
        B2Chain chain = null;
        B2Body body = null;
        B2Body bodyAlias = null;
        B2Shape shape = null;
        B2Joint joint = null;
        B2DebugDrawEm debugDraw = null;

        try {
            assertEquals(3, B2.VersionMajor());
            assertEquals(1, B2.VersionMinor());
            assertEquals(1, B2.VersionRevision());

            worldDef.SetGravity(gravity);
            world = new B2World(worldDef);
            assertTrue(world.IsValid());

            ground = world.CreateBody(groundDef);
            assertEquals(1, chainDef.GetMaterialCount());
            chainDef.AddPoint(chainGhost1);
            chainDef.AddPoint(chainStart);
            chainDef.AddPoint(chainEnd);
            chainDef.AddPoint(chainGhost2);
            chain = ground.CreateChain(chainDef);
            assertTrue(chain.IsValid());
            assertEquals(1, chain.GetSegmentCount());

            bodyDef.SetType(B2.DynamicBody());
            bodyDef.SetPosition(startPosition);
            body = world.CreateBody(bodyDef);
            assertTrue(body.IsValid());
            bodyAlias = new B2Body(body.GetId());
            assertTrue("stored Box2D handle should round-trip", bodyAlias.IsValid());

            shapeDef.SetDensity(1f);
            shapeDef.SetEnableContactEvents(true);
            shape = body.CreatePolygonShape(shapeDef, box);
            assertTrue(shape.IsValid());
            assertEquals(1, body.GetShapeCount());

            final int[] drawCalls = {0};
            debugDraw = new B2DebugDrawEm() {
                @Override
                protected void DrawSolidPolygon(B2Transform transform, B2DebugPolygon polygon, float radius, int color) {
                    drawCalls[0]++;
                }
            };
            debugDraw.SetDrawShapes(true);
            debugDraw.DrawWorld(world);
            assertTrue("debug draw callback should reach Java", drawCalls[0] > 0);

            float beforeY = body.GetPosition().GetY();
            boolean sawContactBegin = false;
            for(int i = 0; i < 180; i++) {
                world.Step(1f / 60f, 4);
                B2ContactEvents contactEvents = world.GetContactEvents();
                try {
                    sawContactBegin |= contactEvents.GetBeginCount() > 0;
                }
                finally {
                    dispose(contactEvents);
                }
            }
            float afterY = body.GetPosition().GetY();
            assertTrue("dynamic body should move under gravity", afterY < beforeY);
            assertTrue("chain collision should emit a buffered contact event", sawContactBegin);

            jointDef.SetBodyIdA(ground.GetId());
            jointDef.SetBodyIdB(body.GetId());
            joint = world.CreateDistanceJoint(jointDef);
            assertTrue(joint.IsValid());
            assertEquals(B2.DistanceJoint(), joint.GetType());
        }
        finally {
            if(world != null && world.IsValid()) {
                world.Destroy();
            }
            dispose(debugDraw, joint, shape, bodyAlias, body, chain, ground, world, box, shapeDef, startPosition, bodyDef,
                    jointDef,
                    chainGhost2, chainEnd, chainStart, chainGhost1, chainDef, groundDef, worldDef, gravity);
        }
    }

    @Test
    public void pointerReturningFactoriesUseIndependentOwnedWrappers() throws Exception {
        loadBox2D();

        B2WorldDef worldDef = new B2WorldDef();
        B2BodyDef bodyDef = new B2BodyDef();
        B2World world = new B2World(worldDef);
        B2Body first = null;
        B2Body second = null;

        try {
            first = world.CreateBody(bodyDef);
            second = world.CreateBody(bodyDef);
            assertNotSame("CreateBody must not recycle one Java temporary", first, second);
            assertTrue("body IDs must remain independent", first.GetId() != second.GetId());
            assertTrue(first.native_hasOwnership());
            assertTrue(second.native_hasOwnership());
            assertTrue(first.IsValid());
            assertTrue(second.IsValid());
        }
        finally {
            if(world.IsValid()) world.Destroy();
            dispose(second, first, world, bodyDef, worldDef);
        }
    }

    private static void loadBox2D() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<Throwable>();

        JBox2DLoader.init(new JParserLibraryLoaderListener() {
            @Override
            public void onLoad(boolean success, Throwable throwable) {
                if(!success) {
                    error.set(throwable != null ? throwable : new RuntimeException("jBox2D JNI loader returned false"));
                }
                latch.countDown();
            }
        });

        assertTrue("jBox2D JNI loader did not finish", latch.await(10, TimeUnit.SECONDS));
        if(error.get() != null) {
            throw new AssertionError("jBox2D JNI loader failed", error.get());
        }
    }

    private static void dispose(NativeObject... objects) {
        for(NativeObject object : objects) {
            if(object != null && object.native_hasOwnership() && !object.isDisposed()) {
                object.dispose();
            }
        }
    }
}
