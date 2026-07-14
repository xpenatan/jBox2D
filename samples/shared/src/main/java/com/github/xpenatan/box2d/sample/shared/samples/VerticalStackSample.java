package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2BodyDef;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Stacking / Vertical Stack CCD sample. */
public final class VerticalStackSample extends AbstractBox2DSample {
    private static final int MAX_ROWS = 15;
    private static final int MAX_COLUMNS = 10;
    private static final int MAX_BULLETS = 8;
    private final List<B2Body> stackBodies = new ArrayList<B2Body>();
    private final List<B2Body> bullets = new ArrayList<B2Body>();
    private int shapeType = 1;
    private int rowCount = 12;
    private int columnCount = 1;
    private int bulletCount = 1;
    private int bulletType;

    public VerticalStackSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        addSegmentShape(ground, 10.0f, 0.0f, 10.0f, 20.0f, 0.0f, 0.6f, 0.0f);
        addSegmentShape(ground, -30.0f, 0.0f, 30.0f, 0.0f, 0.0f, 0.6f, 0.0f);
        createStacks();
    }

    private void createStacks() {
        for(B2Body body : stackBodies) destroyBody(body);
        stackBodies.clear();
        float offset = shapeType == 0 ? 0.0f : 0.01f;
        for(int column = 0; column < columnCount; column++) {
            float x = 8.0f - 3.0f * column;
            for(int row = 0; row < rowCount; row++) {
                float shift = (row & 1) == 0 ? -offset : offset;
                B2Body body = createDynamicBody(x + shift, 0.5f + row, 0.0f);
                if(shapeType == 0) addCircleShape(body, 0.0f, 0.0f, 0.5f, 1.0f, 0.3f, 0.0f, 0.0f);
                else addRoundedBoxShape(body, 0.45f, 0.45f, 0.05f, 1.0f, 0.3f, 0.0f, 0.0f);
                stackBodies.add(body);
            }
        }
    }

    private void destroyBullets() {
        for(B2Body bullet : bullets) destroyBody(bullet);
        bullets.clear();
    }

    private void fireBullets() {
        destroyBullets();
        for(int i = 0; i < bulletCount; i++) {
            B2BodyDef def = new B2BodyDef();
            def.SetType(com.github.xpenatan.box2d.B2.DynamicBody());
            com.github.xpenatan.box2d.B2Vec2 position = vector(-26.7f - i, 6.0f);
            com.github.xpenatan.box2d.B2Vec2 velocity = vector(randomFloat(200.0f, 300.0f), 0.0f);
            def.SetPosition(position);
            def.SetLinearVelocity(velocity);
            def.SetIsBullet(true);
            B2Body bullet = createBody(def);
            if(bulletType == 0) addCircleShape(bullet, 0.0f, 0.0f, 0.25f, 4.0f, 0.6f, 0.0f, 0.0f);
            else addBoxShape(bullet, 0.25f, 0.25f, 4.0f, 0.6f, 0.0f, 0.0f);
            bullets.add(bullet);
            release(velocity, position, def);
        }
    }

    private void destroyOneBodyPerColumn() {
        for(int column = 0; column < columnCount; column++) {
            int start = column * rowCount;
            for(int row = 0; row < rowCount && start + row < stackBodies.size(); row++) {
                B2Body body = stackBodies.get(start + row);
                if(body.IsValid()) {
                    destroyBody(body);
                    break;
                }
            }
        }
    }

    private void rebuild() {
        destroyBullets();
        createStacks();
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Shape", new String[] { "Circle", "Box" }, () -> shapeType,
                        value -> { shapeType = (int)value; rebuild(); }),
                Box2DSampleControl.slider("Rows", 1, MAX_ROWS, 1, () -> rowCount,
                        value -> { rowCount = (int)value; rebuild(); }),
                Box2DSampleControl.slider("Columns", 1, MAX_COLUMNS, 1, () -> columnCount,
                        value -> { columnCount = (int)value; rebuild(); }),
                Box2DSampleControl.slider("Bullets", 1, MAX_BULLETS, 1, () -> bulletCount,
                        value -> bulletCount = (int)value),
                Box2DSampleControl.combo("Bullet Shape", new String[] { "Circle", "Box" }, () -> bulletType,
                        value -> bulletType = (int)value),
                Box2DSampleControl.button("Fire Bullets", this::fireBullets),
                Box2DSampleControl.button("Destroy Body", this::destroyOneBodyPerColumn),
                Box2DSampleControl.button("Reset Stack", this::rebuild));
    }
}
