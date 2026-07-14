package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Filter;
import com.github.xpenatan.box2d.B2Polygon;
import com.github.xpenatan.box2d.B2Shape;
import com.github.xpenatan.box2d.B2ShapeDef;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.List;

/** Java port of Box2D 3.1.1's Shapes / Filter sample. */
public final class ShapeFilterSample extends AbstractBox2DSample {
    private static final long GROUND = 1;
    private static final long TEAM1 = 2;
    private static final long TEAM2 = 4;
    private static final long TEAM3 = 8;
    private final B2Body[] players = new B2Body[3];
    private final B2Shape[] shapes = new B2Shape[3];

    public ShapeFilterSample() {
        addGroundSegment(-20.0f, 0.0f, 20.0f, 0.0f);
        long[] categories = { TEAM1, TEAM2, TEAM3 };
        long[] masks = { GROUND | TEAM2 | TEAM3, GROUND | TEAM1 | TEAM3, GROUND | TEAM1 | TEAM2 };
        B2Polygon box = B2Polygon.CreateBox(2.0f, 1.0f);
        for(int i = 0; i < 3; i++) {
            players[i] = createDynamicBody(0.0f, 2.0f + 3.0f * i, 0.0f);
            B2ShapeDef def = shapeDef(1.0f, 0.6f, 0.0f, 0.0f);
            B2Filter filter = new B2Filter();
            filter.SetCategoryBits(categories[i]);
            filter.SetMaskBits(masks[i]);
            def.SetFilter(filter);
            shapes[i] = createPolygonShape(players[i], def, box);
            release(filter, def);
        }
        release(box);
    }

    private boolean collides(int player, long team) {
        B2Filter filter = shapes[player].GetFilter();
        boolean value = (filter.GetMaskBits() & team) != 0;
        release(filter);
        return value;
    }

    private void setCollision(int player, long team, boolean enabled) {
        B2Filter filter = shapes[player].GetFilter();
        long mask = filter.GetMaskBits();
        filter.SetMaskBits(enabled ? mask | team : mask & ~team);
        shapes[player].SetFilter(filter);
        release(filter);
    }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(int i = 0; i < players.length; i++) {
            B2Vec2 p = players[i].GetPosition();
            draw.worldText(p.GetX() - 0.5f, p.GetY(), "player " + (i + 1), 0xFFFFFFFF);
            release(p);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        ArrayList<Box2DSampleControl> controls = new ArrayList<Box2DSampleControl>();
        controls.add(Box2DSampleControl.text("Player 1 Collides With"));
        controls.add(check("Team 2 (P1)", 0, TEAM2));
        controls.add(check("Team 3 (P1)", 0, TEAM3));
        controls.add(Box2DSampleControl.text("Player 2 Collides With"));
        controls.add(check("Team 1 (P2)", 1, TEAM1));
        controls.add(check("Team 3 (P2)", 1, TEAM3));
        controls.add(Box2DSampleControl.text("Player 3 Collides With"));
        controls.add(check("Team 1 (P3)", 2, TEAM1));
        controls.add(check("Team 2 (P3)", 2, TEAM2));
        return controls;
    }

    private Box2DSampleControl check(String label, int player, long team) {
        return Box2DSampleControl.checkbox(label, () -> collides(player, team) ? 1 : 0,
                value -> setCollision(player, team, value != 0));
    }
}
