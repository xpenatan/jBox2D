package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2SurfaceMaterial;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.Collections;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Custom Filter parity rule. */
public final class CustomFilterSample extends AbstractBox2DSample {
    private final B2Body[] bodies = new B2Body[10];

    public CustomFilterSample() {
        world().EnableParityCustomFilter(true);
        addGroundSegment(-40.0f, 0.0f, 40.0f, 0.0f);
        B2Polygon box = B2Polygon.CreateSquare(1.0f);
        for(int i = 0; i < bodies.length; i++) {
            bodies[i] = createDynamicBody(-10.0f + 2.0f * i, 5.0f, 0.0f);
            B2ShapeDef def = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
            B2SurfaceMaterial material = def.GetMaterial();
            material.SetUserMaterialId(i + 1);
            def.SetMaterial(material);
            createPolygonShape(bodies[i], def, box);
            release(material, def);
        }
        release(box);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(int i = 0; i < bodies.length; i++) {
            B2Vec2 p = bodies[i].GetPosition();
            draw.worldText(p.GetX(), p.GetY(), Integer.toString(i), 0xFFFFFFFF);
            release(p);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Collections.singletonList(Box2DSampleControl.text(
                "Custom filter disables collision between odd and even shapes"));
    }
}
