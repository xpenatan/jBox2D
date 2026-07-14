package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Robustness / Overlap Recovery sample. */
public final class OverlapRecoverySample extends AbstractBox2DSample {
    private final List<B2Body> boxes = new ArrayList<B2Body>();
    private int baseCount = 4;
    private float overlap = 0.25f;
    private float extent = 0.5f;
    private float pushOut = 3.0f;
    private float hertz = 30.0f;
    private float dampingRatio = 10.0f;

    public OverlapRecoverySample() {
        addGroundSegment(-40.0f, 0.0f, 40.0f, 0.0f);
        createScene();
    }

    private void createScene() {
        for(B2Body body : boxes) destroyBody(body);
        boxes.clear();
        world().SetContactTuning(hertz, dampingRatio, pushOut);
        float fraction = 1.0f - overlap;
        float y = extent;
        for(int row = 0; row < baseCount; row++) {
            float x = fraction * extent * (row - baseCount);
            for(int column = row; column < baseCount; column++) {
                boxes.add(addDynamicBox(x, y, extent, extent));
                x += 2.0f * fraction * extent;
            }
            y += 2.0f * fraction * extent;
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                slider("Extent", 0.1f, 1.0f, 0.1f, () -> extent, value -> extent = value),
                slider("Base Count", 1.0f, 10.0f, 1.0f, () -> baseCount, value -> baseCount = (int)value),
                slider("Overlap", 0.0f, 1.0f, 0.01f, () -> overlap, value -> overlap = value),
                slider("Speed", 0.0f, 10.0f, 0.1f, () -> pushOut, value -> pushOut = value),
                slider("Hertz", 0.0f, 240.0f, 1.0f, () -> hertz, value -> hertz = value),
                slider("Damping Ratio", 0.0f, 20.0f, 0.1f, () -> dampingRatio,
                        value -> dampingRatio = value),
                Box2DSampleControl.button("Reset Scene", this::createScene));
    }

    private Box2DSampleControl slider(String name, float min, float max, float step,
            Box2DSampleControl.Getter getter, Box2DSampleControl.Setter setter) {
        return Box2DSampleControl.slider(name, min, max, step, getter, value -> { setter.set(value); createScene(); });
    }
}
