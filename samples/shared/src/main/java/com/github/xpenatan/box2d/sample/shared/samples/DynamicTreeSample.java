package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2;
import com.github.xpenatan.box2d.B2AABB;
import com.github.xpenatan.box2d.B2DynamicTree;
import com.github.xpenatan.box2d.B2TreeResult;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java port of Box2D 3.1.1's Collision / Dynamic Tree sample. */
public final class DynamicTreeSample extends AbstractBox2DSample {
    private static final class Proxy {
        float x, y, width, height;
        float fatLowerX, fatLowerY, fatUpperX, fatUpperY;
        int id;
        int rayStamp = -1;
        int queryStamp = -1;
    }

    private final ArrayList<Proxy> proxies = new ArrayList<Proxy>();
    private B2DynamicTree tree;
    private int rows = 100;
    private int columns = 100;
    private float fill = 0.25f;
    private float grid = 1.0f;
    private float ratio = 5.0f;
    private float moveFraction = 0.05f;
    private float moveDelta = 0.1f;
    private int updateType;
    private int timeStamp;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean rayDrag;
    private boolean queryDrag;
    private int nodeVisits;
    private int leafVisits;
    private int rebuilt;

    public DynamicTreeSample() {
        super(1, 0.0f, 0.0f);
        buildTree();
    }

    private void buildTree() {
        if(tree != null) release(tree);
        tree = own(new B2DynamicTree());
        proxies.clear();
        float y = -4.0f;
        for(int row = 0; row < rows; row++, y += grid) {
            float x = -40.0f;
            for(int column = 0; column < columns; column++, x += grid) {
                if(randomFloat(0.0f, 1.0f) > fill) continue;
                Proxy proxy = new Proxy();
                proxy.x = x; proxy.y = y;
                float aspect = randomFloat(1.0f, ratio);
                float base = randomFloat(0.1f, 0.5f);
                if(randomFloat(-1.0f, 1.0f) > 0.0f) { proxy.width = aspect * base; proxy.height = base; }
                else { proxy.width = base; proxy.height = aspect * base; }
                setFatBounds(proxy);
                B2AABB aabb = aabb(proxy.fatLowerX, proxy.fatLowerY, proxy.fatUpperX, proxy.fatUpperY);
                proxy.id = tree.CreateProxy(aabb, B2.DefaultCategoryBits(), proxies.size());
                release(aabb);
                proxies.add(proxy);
            }
        }
        timeStamp = 0;
    }

    private static B2AABB aabb(float lx, float ly, float ux, float uy) {
        B2Vec2 lower = new B2Vec2(lx, ly);
        B2Vec2 upper = new B2Vec2(ux, uy);
        B2AABB result = new B2AABB(lower, upper);
        release(upper, lower);
        return result;
    }

    private static void setFatBounds(Proxy proxy) {
        proxy.fatLowerX = proxy.x - 0.1f;
        proxy.fatLowerY = proxy.y - 0.1f;
        proxy.fatUpperX = proxy.x + proxy.width + 0.1f;
        proxy.fatUpperY = proxy.y + proxy.height + 0.1f;
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        nodeVisits = leafVisits = 0;
        if(queryDrag) {
            B2AABB query = aabb(Math.min(startX, endX), Math.min(startY, endY),
                    Math.max(startX, endX), Math.max(startY, endY));
            B2TreeResult result = tree.Query(query, B2.DefaultMaskBits());
            mark(result, false);
            nodeVisits = result.GetNodeVisits(); leafVisits = result.GetLeafVisits();
            release(result, query);
        }
        if(rayDrag) {
            B2Vec2 origin = new B2Vec2(startX, startY);
            B2Vec2 translation = new B2Vec2(endX - startX, endY - startY);
            B2TreeResult result = tree.RayCast(origin, translation, 1.0f, B2.DefaultMaskBits());
            mark(result, true);
            nodeVisits = result.GetNodeVisits(); leafVisits = result.GetLeafVisits();
            release(result, translation, origin);
        }

        boolean moved = false;
        for(int i = 0; i < proxies.size(); i++) {
            Proxy proxy = proxies.get(i);
            if(randomFloat(0.0f, 1.0f) >= moveFraction) continue;
            proxy.x += moveDelta * randomFloat(-1.0f, 1.0f);
            proxy.y += moveDelta * randomFloat(-1.0f, 1.0f);
            float lowerX = proxy.x;
            float lowerY = proxy.y;
            float upperX = proxy.x + proxy.width;
            float upperY = proxy.y + proxy.height;
            if(lowerX >= proxy.fatLowerX && lowerY >= proxy.fatLowerY
                    && upperX <= proxy.fatUpperX && upperY <= proxy.fatUpperY) continue;
            setFatBounds(proxy);
            B2AABB fat = aabb(proxy.fatLowerX, proxy.fatLowerY, proxy.fatUpperX, proxy.fatUpperY);
            if(updateType == 0) tree.MoveProxy(proxy.id, fat);
            else tree.EnlargeProxy(proxy.id, fat);
            release(fat);
            moved = true;
        }
        rebuilt = updateType == 0 || !moved ? 0 : tree.Rebuild(updateType == 1);
        tree.Validate();
        timeStamp++;
    }

    private void mark(B2TreeResult result, boolean ray) {
        for(int i = 0; i < result.GetCount(); i++) {
            int index = (int)result.GetUserData(i);
            if(index >= 0 && index < proxies.size()) {
                if(ray) proxies.get(index).rayStamp = timeStamp;
                else proxies.get(index).queryStamp = timeStamp;
            }
        }
    }

    @Override public void mouseDown(float x, float y, int button, int modifiers) {
        if(button != 0) return;
        startX = endX = x; startY = endY = y;
        if((modifiers & 1) != 0) queryDrag = true;
        else rayDrag = true;
    }
    @Override public void mouseMove(float x, float y) { endX = x; endY = y; }
    @Override public void mouseUp(float x, float y, int button) { rayDrag = queryDrag = false; }

    @Override
    public void draw(Box2DSampleDraw draw) {
        for(Proxy proxy : proxies) {
            boolean hit = proxy.queryStamp == timeStamp - 1 || proxy.rayStamp == timeStamp - 1;
            CollisionSampleSupport.drawAABB(draw, proxy.x, proxy.y, proxy.x + proxy.width,
                    proxy.y + proxy.height, hit ? 0x00FF00FF : 0x0000FFFF);
        }
        if(queryDrag) CollisionSampleSupport.drawAABB(draw, Math.min(startX, endX), Math.min(startY, endY),
                Math.max(startX, endX), Math.max(startY, endY), 0xFFFFFFFF);
        if(rayDrag) {
            draw.segment(startX, startY, endX, endY, 0xFFFFFFFF);
            draw.point(startX, startY, 5.0f, 0x00FF00FF);
            draw.point(endX, endY, 5.0f, 0xFF0000FF);
        }
    }

    @Override
    public List<Box2DSampleControl> controls() {
        return Arrays.asList(
                Box2DSampleControl.slider("rows", 0, 1000, 1, () -> rows, v -> { rows = (int)v; buildTree(); }),
                Box2DSampleControl.slider("columns", 0, 1000, 1, () -> columns, v -> { columns = (int)v; buildTree(); }),
                Box2DSampleControl.slider("fill", 0, 1, 0.01f, () -> fill, v -> { fill = v; buildTree(); }),
                Box2DSampleControl.slider("grid", 0.5f, 2, 0.01f, () -> grid, v -> { grid = v; buildTree(); }),
                Box2DSampleControl.slider("ratio", 1, 10, 0.01f, () -> ratio, v -> { ratio = v; buildTree(); }),
                Box2DSampleControl.slider("move", 0, 1, 0.01f, () -> moveFraction, v -> moveFraction = v),
                Box2DSampleControl.slider("delta", 0, 1, 0.01f, () -> moveDelta, v -> moveDelta = v),
                Box2DSampleControl.combo("update", new String[]{"Incremental", "Full Rebuild", "Partial Rebuild"},
                        () -> updateType, v -> updateType = (int)v),
                Box2DSampleControl.button("Rebuild", this::buildTree),
                Box2DSampleControl.dynamicText(() -> String.format(
                        "proxies = %d, height = %d, area ratio = %.1f, rebuilt = %d, visits = %d/%d",
                        proxies.size(), tree.GetHeight(), tree.GetAreaRatio(), rebuilt, nodeVisits, leafVisits)),
                Box2DSampleControl.text("mouse 1: ray cast; shift + mouse 1: query"));
    }
}
