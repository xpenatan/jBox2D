package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.B2BodyEvents;
import com.github.xpenatan.box2d.B2BodyMoveEvent;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Java port of Box2D 3.1.1's Events / Body Move sample. */
public final class BodyMoveSample extends AbstractBox2DSample {
    private static final int MAX_COUNT = 50;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private final Map<Long, Integer> indices = new HashMap<Long, Integer>();
    private final boolean[] sleeping = new boolean[MAX_COUNT];
    private final List<float[]> movedTransforms = new ArrayList<float[]>();
    private int sleepCount;
    private int stepCount;
    private float explosionMagnitude = 10.0f;

    public BodyMoveSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addOffsetBoxShape(ground, 12.0f, 0.1f, -10.0f, -0.1f, -0.15f * PI, 0.0f, 0.1f, 0.0f, 0.0f);
        addOffsetBoxShape(ground, 12.0f, 0.1f, 10.0f, -0.1f, 0.15f * PI, 0.0f, 0.1f, 0.0f, 0.0f);
        addOffsetBoxShape(ground, 0.1f, 10.0f, 19.9f, 10.0f, 0.0f, 0.0f, 0.1f, 0.8f, 0.0f);
        addOffsetBoxShape(ground, 0.1f, 10.0f, -19.9f, 10.0f, 0.0f, 0.0f, 0.1f, 0.8f, 0.0f);
        addOffsetBoxShape(ground, 20.0f, 0.1f, 0.0f, 20.1f, 0.0f, 0.0f, 0.1f, 0.8f, 0.0f);
    }

    private void createBodies() {
        float x = -5.0f;
        for(int i = 0; i < 10 && bodies.size() < MAX_COUNT; i++, x += 1.0f) {
            int index = bodies.size();
            B2BodyDef def = new B2BodyDef();
            B2Vec2 position = vector(x, 10.0f);
            def.SetType(B2.DynamicBody());
            def.SetPosition(position);
            def.SetIsBullet(index % 12 == 0);
            B2Body body = createBody(def);
            switch(index % 4) {
                case 0:
                    addCapsuleShape(body, -0.25f, 0.0f, 0.25f, 0.0f, 0.25f, 1.0f, 0.6f, 0.0f, 0.0f);
                    break;
                case 1:
                    addCircleShape(body, 0.0f, 0.0f, 0.35f, 1.0f, 0.6f, 0.0f, 0.0f);
                    break;
                case 2:
                    addBoxShape(body, 0.35f, 0.35f, 1.0f, 0.6f, 0.0f, 0.0f);
                    break;
                default:
                    addPolygonShape(body, new float[] {
                            -0.5f, -0.3f, 0.45f, -0.45f, 0.65f, 0.1f, 0.15f, 0.65f, -0.55f, 0.4f
                    }, 0.1f, 1.0f, 0.6f, 0.0f, 0.0f);
                    break;
            }
            bodies.add(body);
            indices.put(body.GetId(), index);
            release(position, def);
        }
    }

    private void explode() {
        for(B2Body body : bodies) {
            if(!body.IsValid()) continue;
            B2Vec2 position = body.GetPosition();
            float dx = position.GetX();
            float dy = position.GetY() + 5.0f;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);
            if(distance > 0.001f && distance < 10.0f) {
                float scale = explosionMagnitude * (1.0f - distance / 10.0f) / distance;
                applyLinearImpulseToCenter(body, dx * scale, dy * scale);
            }
            release(position);
        }
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        if((stepCount & 15) == 15 && bodies.size() < MAX_COUNT) createBodies();
        stepCount++;
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        movedTransforms.clear();
        B2BodyEvents events = world().GetBodyEvents();
        for(int i = 0; i < events.GetMoveCount(); i++) {
            B2BodyMoveEvent event = events.GetMoveEvent(i);
            B2Transform transform = event.GetTransform();
            B2Vec2 position = transform.GetPosition();
            B2Rot rotation = transform.GetRotation();
            movedTransforms.add(new float[] { position.GetX(), position.GetY(), rotation.GetCosine(), rotation.GetSine() });
            Integer index = indices.get(event.GetBodyId());
            if(index != null) {
                if(event.GetFellAsleep() && !sleeping[index]) {
                    sleeping[index] = true;
                    sleepCount++;
                }
                else if(!event.GetFellAsleep() && sleeping[index]) {
                    sleeping[index] = false;
                    sleepCount--;
                }
            }
            release(rotation, position, transform, event);
        }
        release(events);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(float[] transform : movedTransforms) {
            float x = transform[0], y = transform[1], c = transform[2], s = transform[3];
            draw.segment(x, y, x + 0.4f * c, y + 0.4f * s, 0xFF0000FF);
            draw.segment(x, y, x - 0.4f * s, y + 0.4f * c, 0x00FF00FF);
        }
        draw.circle(0.0f, -5.0f, 10.0f, 0x007FFFFF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.button("Explode", this::explode),
                Box2DSampleControl.slider("Magnitude", -20.0f, 20.0f, 0.1f,
                        () -> explosionMagnitude, value -> explosionMagnitude = value),
                Box2DSampleControl.dynamicText(() -> "sleep count: " + sleepCount));
    }
}
