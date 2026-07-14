package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Stacking / Single Box sample. */
public final class SingleBoxSample extends AbstractBox2DSample {
    private final B2Body box;
    private float x;
    private float y = 1.0f;

    public SingleBoxSample() {
        addGroundSegment(-66.0f, 0.0f, 66.0f, 0.0f);
        box = addDynamicBox(0.0f, 1.0f, 1.0f, 1.0f);
        setLinearVelocity(box, 5.0f, 0.0f);
    }

    @Override
    protected void afterStep(float deltaSeconds) {
        B2Vec2 position = box.GetPosition();
        x = position.GetX();
        y = position.GetY();
        release(position);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.dynamicText(new Box2DSampleControl.TextProvider() {
            @Override public String get() { return String.format("(x, y) = (%.2f, %.2f)", x, y); }
        }));
    }
}
