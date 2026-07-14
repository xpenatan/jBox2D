package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Restitution sample. */
public final class RestitutionSample extends AbstractBox2DSample {
    private static final int COUNT = 40;
    private final List<B2Body> bodies = new ArrayList<B2Body>();
    private int shapeType;

    public RestitutionSample() {
        addGroundSegment(-COUNT, 0.0f, COUNT, 0.0f);
        createBodies();
    }

    private void createBodies() {
        for(B2Body body : bodies) destroyBody(body);
        bodies.clear();
        float x = -(COUNT - 1.0f);
        float delta = 1.0f / (COUNT - 1.0f);
        for(int i = 0; i < COUNT; i++) {
            float restitution = i * delta;
            B2Body body;
            if(shapeType == 0) body = addDynamicCircle(x, 40.0f, 0.5f, 1.0f, 0.6f, restitution, 0.0f);
            else body = addDynamicBox(x, 40.0f, 0.5f, 0.5f, 0.0f, 1.0f, 0.6f, restitution, 0.0f);
            bodies.add(body);
            x += 2.0f;
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Shape", new String[] { "Circle", "Box" }, () -> shapeType,
                        value -> { shapeType = (int)value; createBodies(); }),
                Box2DSampleControl.button("Reset", this::createBodies));
    }
}
