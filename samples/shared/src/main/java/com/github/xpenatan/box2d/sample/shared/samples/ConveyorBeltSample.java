package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2SurfaceMaterial;

/** Java port of Box2D 3.1.1's Shapes / Conveyor Belt sample. */
public final class ConveyorBeltSample extends AbstractBox2DSample {
    public ConveyorBeltSample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        B2Body platform = createStaticBody(-5.0f, 5.0f, 0.0f);
        B2Polygon belt = B2Polygon.CreateRoundedBox(10.0f, 0.25f, 0.25f);
        B2ShapeDef def = new B2ShapeDef();
        B2SurfaceMaterial material = new B2SurfaceMaterial();
        material.SetFriction(0.8f);
        material.SetTangentSpeed(2.0f);
        def.SetMaterial(material);
        createPolygonShape(platform, def, belt);
        release(material, def, belt);
        for(int i = 0; i < 5; i++) addDynamicBox(-10.0f + 2.0f * i, 7.0f, 0.5f, 0.5f);
    }
}
