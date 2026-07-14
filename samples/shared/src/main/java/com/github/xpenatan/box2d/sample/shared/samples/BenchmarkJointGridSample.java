package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Circle;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Benchmark / Joint Grid sample. */
public final class BenchmarkJointGridSample extends AbstractBox2DSample {
    public BenchmarkJointGridSample() {
        world().EnableSleeping(false);
        int size = BenchmarkSampleSupport.DEBUG_SIZE ? 10 : 100;
        B2Body[] bodies = new B2Body[size * size];
        B2ShapeDef shapeDef = BenchmarkSampleSupport.filteredShapeDef(this, 1.0f, 0.6f,
                2L, ~2L, true);
        B2Vec2 center = new B2Vec2(0.0f, 0.0f);
        B2Circle circle = new B2Circle(center, 0.4f);
        int index = 0;
        for(int column = 0; column < size; column++) {
            for(int row = 0; row < size; row++) {
                boolean anchored = column >= size / 2 - 3 && column <= size / 2 + 3 && row == 0;
                B2Body body = createBody(anchored ? B2.StaticBody() : B2.DynamicBody(),
                        column, -row, 0.0f);
                B2Shape shape = createCircleShape(body, shapeDef, circle);
                discardHandle(shape);
                if(row > 0) createJoint(bodies[index - 1], body, 0.0f, -0.5f, 0.0f, 0.5f);
                if(column > 0) createJoint(bodies[index - size], body, 0.5f, 0.0f, -0.5f, 0.0f);
                bodies[index++] = body;
            }
        }
        release(circle, center, shapeDef);
    }

    private void createJoint(B2Body bodyA, B2Body bodyB, float ax, float ay, float bx, float by) {
        B2RevoluteJointDef def = new B2RevoluteJointDef();
        B2Vec2 localA = new B2Vec2(ax, ay);
        B2Vec2 localB = new B2Vec2(bx, by);
        def.SetBodyIdA(bodyA.GetId());
        def.SetBodyIdB(bodyB.GetId());
        def.SetLocalAnchorA(localA);
        def.SetLocalAnchorB(localB);
        createRevoluteJoint(def);
        release(localB, localA, def);
    }
}
