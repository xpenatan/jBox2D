package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2Vec2;

/** Java port of Box2D 3.1.1's Stacking / Confined sample. */
public final class ConfinedSample extends AbstractBox2DSample {
    public ConfinedSample() {
        super(4, 0.0f, -10.0f);
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addCapsuleShape(ground, -10.5f, 0.0f, 10.5f, 0.0f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        addCapsuleShape(ground, -10.5f, 0.0f, -10.5f, 20.5f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        addCapsuleShape(ground, 10.5f, 0.0f, 10.5f, 20.5f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);
        addCapsuleShape(ground, -10.5f, 20.5f, 10.5f, 20.5f, 0.5f, 0.0f, 0.6f, 0.0f, 0.0f);

        final int gridCount = 25;
        for(int column = 0; column < gridCount; column++) {
            for(int row = 0; row < gridCount; row++) {
                float x = -8.75f + column * 18.0f / gridCount;
                float y = 1.5f + row * 18.0f / gridCount;
                B2BodyDef def = new B2BodyDef();
                B2Vec2 position = vector(x, y);
                def.SetType(com.github.xpenatan.box2d.B2.DynamicBody());
                def.SetPosition(position);
                def.SetGravityScale(0.0f);
                B2Body body = createBody(def);
                addCircleShape(body, 0.0f, 0.0f, 0.5f, 1.0f, 0.6f, 0.0f, 0.0f);
                release(position, def);
            }
        }
    }
}
