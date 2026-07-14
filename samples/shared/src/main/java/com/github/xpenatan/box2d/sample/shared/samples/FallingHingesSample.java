package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyEvents;
import com.github.xpenatan.box2d.B2RevoluteJointDef;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Java port of the Box2D 3.1.1 cross-platform determinism Falling Hinges scenario. */
public final class FallingHingesSample extends AbstractBox2DSample {
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private int stepCount;
    private int sleepStep = -1;
    private int transformHash;

    public FallingHingesSample() {
        addStaticBox(0.0f, -1.0f, 20.0f, 1.0f, 0.0f);
        int columns = 4;
        int rows = 30;
        float h = 0.25f;
        float radius = 0.1f * h;
        float offset = 0.4f * h;
        float dx = 10.0f * h;
        float xRoot = -0.5f * dx * (columns - 1.0f);
        for(int column = 0; column < columns; column++) {
            float x = xRoot + column * dx;
            B2Body previous = null;
            for(int row = 0; row < rows; row++) {
                B2Body body = createDynamicBody(x + offset * row, h + 2.0f * h * row, 0.1f * row - 1.0f);
                addRoundedBoxShape(body, h - radius, h - radius, radius, 1.0f, 0.3f, 0.0f, 0.0f);
                bodies.add(body);
                if((row & 1) == 0) previous = body;
                else {
                    B2RevoluteJointDef def = new B2RevoluteJointDef();
                    def.SetBodyIdA(previous.GetId());
                    def.SetBodyIdB(body.GetId());
                    B2Vec2 anchorA = vector(h, h);
                    B2Vec2 anchorB = vector(offset, -h);
                    def.SetLocalAnchorA(anchorA);
                    def.SetLocalAnchorB(anchorB);
                    def.SetEnableLimit(true);
                    def.SetLowerAngle(-0.1f * PI);
                    def.SetUpperAngle(0.2f * PI);
                    def.SetEnableSpring(true);
                    def.SetHertz(0.5f);
                    def.SetDampingRatio(0.5f);
                    def.SetDrawSize(0.1f);
                    createRevoluteJoint(def);
                    release(anchorB, anchorA, def);
                    previous = null;
                }
            }
        }
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        if(transformHash == 0) {
            B2BodyEvents events = world().GetBodyEvents();
            if(events.GetMoveCount() == 0 && world().GetAwakeBodyCount() == 0) {
                int hash = 0x811C9DC5;
                for(B2Body body : bodies) {
                    B2Transform transform = body.GetTransform();
                    B2Vec2 position = transform.GetPosition();
                    B2Rot rotation = transform.GetRotation();
                    hash = fnv(hash, Float.floatToIntBits(position.GetX()));
                    hash = fnv(hash, Float.floatToIntBits(position.GetY()));
                    hash = fnv(hash, Float.floatToIntBits(rotation.GetCosine()));
                    hash = fnv(hash, Float.floatToIntBits(rotation.GetSine()));
                    release(rotation, position, transform);
                }
                transformHash = hash == 0 ? 1 : hash;
                sleepStep = stepCount;
            }
            release(events);
        }
        stepCount++;
    }

    private static int fnv(int hash, int value) {
        for(int shift = 0; shift < 32; shift += 8) hash = (hash ^ ((value >>> shift) & 0xFF)) * 0x01000193;
        return hash;
    }

    @Override
    public List<Box2DSampleControl> controls() {
        String hash = String.format(java.util.Locale.US, "sleep step = %d, hash = 0x%08x", sleepStep, transformHash);
        return Collections.singletonList(Box2DSampleControl.text(hash));
    }
}
