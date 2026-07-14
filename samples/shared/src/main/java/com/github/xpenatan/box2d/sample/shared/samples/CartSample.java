package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Robustness / Cart high-gravity, high-mass-ratio sample. */
public final class CartSample extends AbstractBox2DSample {
    private B2Body chassis;
    private B2Body wheel1;
    private B2Body wheel2;
    private float contactHertz = 30.0f;
    private float contactDampingRatio = 10.0f;
    private float contactSpeed = 3.0f;
    private float jointHertz = 60.0f;
    private float jointDampingRatio = 1.0f;

    public CartSample() {
        addStaticBox(0.0f, -1.0f, 20.0f, 1.0f, 0.0f);
        setGravity(0.0f, -22.0f);
        createScene();
    }

    private void createScene() {
        destroyBody(chassis);
        destroyBody(wheel1);
        destroyBody(wheel2);
        world().SetContactTuning(contactHertz, contactDampingRatio, contactSpeed);
        float y = 2.0f;
        chassis = createDynamicBody(0.0f, y, 0.0f);
        addOffsetBoxShape(chassis, 0.5f, 0.25f, 0.0f, 0.25f, 0.0f,
                100.0f, 0.6f, 0.0f, 0.0f);
        wheel1 = addDynamicCircle(-0.4f, y - 0.15f, 0.1f, 10.0f, 0.6f, 0.0f, 0.02f);
        wheel2 = addDynamicCircle(0.4f, y - 0.15f, 0.1f, 10.0f, 0.6f, 0.0f, 0.02f);
        addRevoluteJoint(chassis, wheel1, -0.4f, y - 0.15f, false);
        addRevoluteJoint(chassis, wheel2, 0.4f, y - 0.15f, false);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.text("Contact"));
        controls.add(tuning("Hertz (contact)", 0, 240, 1, () -> contactHertz, v -> contactHertz = v));
        controls.add(tuning("Damping Ratio (contact)", 0, 1000, 1,
                () -> contactDampingRatio, v -> contactDampingRatio = v));
        controls.add(tuning("Speed", 0, 5, 0.1f, () -> contactSpeed, v -> contactSpeed = v));
        controls.add(Box2DSampleControl.text("Joint"));
        controls.add(tuning("Hertz (joint)", 0, 240, 1, () -> jointHertz, v -> jointHertz = v));
        controls.add(tuning("Damping Ratio (joint)", 0, 1000, 1,
                () -> jointDampingRatio, v -> jointDampingRatio = v));
        controls.add(Box2DSampleControl.button("Reset Scene", this::createScene));
        return controls;
    }

    private Box2DSampleControl tuning(String label, float min, float max, float step,
            Box2DSampleControl.Getter getter, Box2DSampleControl.Setter setter) {
        return Box2DSampleControl.slider(label, min, max, step, getter, value -> { setter.set(value); createScene(); });
    }
}
