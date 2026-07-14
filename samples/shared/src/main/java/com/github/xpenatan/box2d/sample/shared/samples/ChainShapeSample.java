package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Chain;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Chain Shape sample. */
public final class ChainShapeSample extends AbstractBox2DSample {
    private static final float[] LOOP = {
            -56.885498f, 12.8985004f, -56.885498f, 16.2057495f, 56.885498f, 16.2057495f,
            56.885498f, -16.2057514f, 51.5935059f, -16.2057514f, 43.6559982f, -10.9139996f,
            35.7184982f, -10.9139996f, 27.7809982f, -10.9139996f, 21.1664963f, -14.2212505f,
            11.9059982f, -16.2057514f, 0.0f, -16.2057514f, -10.5835037f, -14.8827496f,
            -17.1980019f, -13.5597477f, -21.1665001f, -12.2370014f, -25.1355019f, -9.5909977f,
            -31.75f, -3.63799858f, -38.3644981f, 6.2840004f, -42.3334999f, 9.59125137f,
            -47.625f, 11.5755005f, -56.885498f, 12.8985004f
    };
    private final B2Chain chain;
    private B2Body launchedBody;
    private B2Shape launchedShape;
    private int shapeType;
    private float restitution;
    private float friction = 0.2f;

    public ChainShapeSample() {
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        chain = addChain(ground, LOOP, true, friction);
        launch();
    }

    private void launch() {
        destroyBody(launchedBody);
        launchedBody = createDynamicBody(-55.0f, 13.5f, 0.0f);
        if(shapeType == 0) launchedShape = addCircleShape(launchedBody, 0.0f, 0.0f, 0.5f,
                1.0f, friction, restitution, 0.0f);
        else if(shapeType == 1) launchedShape = addCapsuleShape(launchedBody, -0.5f, 0.0f, 0.5f, 0.0f, 0.25f,
                1.0f, friction, restitution, 0.0f);
        else launchedShape = addBoxShape(launchedBody, 0.5f, 0.5f, 1.0f, friction, restitution, 0.0f);
    }

    private void setFriction(float value) {
        friction = value;
        chain.SetFriction(value);
        if(launchedShape != null && launchedShape.IsValid()) launchedShape.SetFriction(value);
    }

    private void setRestitution(float value) {
        restitution = value;
        if(launchedShape != null && launchedShape.IsValid()) launchedShape.SetRestitution(value);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        draw.segment(0.0f, 0.0f, 0.5f, 0.0f, 0xFF0000FF);
        draw.segment(0.0f, 0.0f, 0.0f, 0.5f, 0x00FF00FF);
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.combo("Shape", new String[] { "Circle", "Capsule", "Box" },
                        () -> shapeType, value -> { shapeType = (int)value; launch(); }),
                Box2DSampleControl.slider("Friction", 0.0f, 1.0f, 0.01f, () -> friction, this::setFriction),
                Box2DSampleControl.slider("Restitution", 0.0f, 2.0f, 0.1f,
                        () -> restitution, this::setRestitution),
                Box2DSampleControl.button("Launch", this::launch));
    }
}
