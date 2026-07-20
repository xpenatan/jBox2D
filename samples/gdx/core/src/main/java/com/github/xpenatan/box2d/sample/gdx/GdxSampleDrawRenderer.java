package com.github.xpenatan.box2d.sample.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.github.xpenatan.box2d.B2DebugPolygon;
import com.github.xpenatan.box2d.B2DebugDrawEm;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleDraw;
import java.util.ArrayList;

/** Collects Box2D debug-draw callbacks and renders them with libGDX. */
public final class GdxSampleDrawRenderer extends B2DebugDrawEm implements Box2DSampleDraw {
    private static final int CIRCLE_SEGMENTS = 24;

    private final ArrayList<PolygonCommand> polygons = new ArrayList<PolygonCommand>();
    private final ArrayList<CircleCommand> circles = new ArrayList<CircleCommand>();
    private final ArrayList<CapsuleCommand> capsules = new ArrayList<CapsuleCommand>();
    private final ArrayList<SegmentCommand> segments = new ArrayList<SegmentCommand>();
    private final ArrayList<PointCommand> points = new ArrayList<PointCommand>();
    private final ArrayList<TextCommand> worldText = new ArrayList<TextCommand>();
    private final ArrayList<TextCommand> screenText = new ArrayList<TextCommand>();
    private final Matrix4 screenProjection = new Matrix4();
    private final Color color = new Color();

    private int polygonCount;
    private int circleCount;
    private int capsuleCount;
    private int segmentCount;
    private int pointCount;
    private int worldTextCount;
    private int screenTextCount;

    public GdxSampleDrawRenderer() {
        SetDrawShapes(true);
        SetDrawJoints(true);
    }

    public void beginFrame() {
        polygonCount = 0;
        circleCount = 0;
        capsuleCount = 0;
        segmentCount = 0;
        pointCount = 0;
        worldTextCount = 0;
        screenTextCount = 0;
    }

    @Override
    protected void DrawPolygon(B2DebugPolygon polygon, int hexColor) {
        PolygonCommand command = polygon();
        command.solid = false;
        command.color = hexColor;
        copyPolygon(command, polygon, 0f, 0f, 1f, 0f);
    }

    @Override
    protected void DrawSolidPolygon(B2Transform transform, B2DebugPolygon polygon, float radius, int hexColor) {
        B2Vec2 position = transform.GetPosition();
        B2Rot rotation = transform.GetRotation();
        PolygonCommand command = polygon();
        command.solid = true;
        command.color = hexColor;
        command.radius = radius;
        copyPolygon(command, polygon, position.GetX(), position.GetY(), rotation.GetCosine(), rotation.GetSine());
    }

    @Override
    protected void DrawCircle(B2Vec2 center, float radius, int hexColor) {
        CircleCommand command = circle();
        command.x = center.GetX();
        command.y = center.GetY();
        command.radius = radius;
        command.axisX = 0f;
        command.axisY = 0f;
        command.color = hexColor;
        command.solid = false;
    }

    @Override
    protected void DrawSolidCircle(B2Transform transform, float radius, int hexColor) {
        B2Vec2 position = transform.GetPosition();
        B2Rot rotation = transform.GetRotation();
        CircleCommand command = circle();
        command.x = position.GetX();
        command.y = position.GetY();
        command.radius = radius;
        command.axisX = rotation.GetCosine();
        command.axisY = rotation.GetSine();
        command.color = hexColor;
        command.solid = true;
    }

    @Override
    protected void DrawSolidCapsule(B2Vec2 p1, B2Vec2 p2, float radius, int hexColor) {
        CapsuleCommand command = capsule();
        command.x1 = p1.GetX();
        command.y1 = p1.GetY();
        command.x2 = p2.GetX();
        command.y2 = p2.GetY();
        command.radius = radius;
        command.color = hexColor;
    }

    @Override
    protected void DrawSegment(B2Vec2 p1, B2Vec2 p2, int hexColor) {
        addSegment(p1.GetX(), p1.GetY(), p2.GetX(), p2.GetY(), hexColor);
    }

    @Override
    protected void DrawTransform(B2Transform transform) {
        B2Vec2 position = transform.GetPosition();
        B2Rot rotation = transform.GetRotation();
        float x = position.GetX();
        float y = position.GetY();
        float c = rotation.GetCosine();
        float s = rotation.GetSine();
        addSegment(x, y, x + 0.4f * c, y + 0.4f * s, 0xFF3030);
        addSegment(x, y, x - 0.4f * s, y + 0.4f * c, 0x30FF30);
    }

    @Override
    protected void DrawPoint(B2Vec2 point, float size, int hexColor) {
        PointCommand command = point();
        command.x = point.GetX();
        command.y = point.GetY();
        command.size = size;
        command.color = hexColor;
    }

    @Override
    public void worldText(float x, float y, String text, int hexColor) {
        TextCommand command = worldText();
        command.x = x;
        command.y = y;
        command.text = text == null ? "" : text;
        command.color = hexColor;
    }

    @Override
    public void screenText(float x, float y, String text, int hexColor) {
        TextCommand command = screenText();
        command.x = x;
        command.y = y;
        command.text = text == null ? "" : text;
        command.color = hexColor;
    }

    @Override
    public void segment(float x1, float y1, float x2, float y2, int hexColor) {
        addSegment(x1, y1, x2, y2, hexColor);
    }

    @Override
    public void point(float x, float y, float size, int hexColor) {
        PointCommand command = point();
        command.x = x;
        command.y = y;
        command.size = size;
        command.color = hexColor;
    }

    @Override
    public void circle(float x, float y, float radius, int hexColor) {
        CircleCommand command = circle();
        command.x = x;
        command.y = y;
        command.radius = radius;
        command.axisX = 0.0f;
        command.axisY = 0.0f;
        command.color = hexColor;
        command.solid = false;
    }

    void render(OrthographicCamera camera, ShapeRenderer shapes, SpriteBatch batch,
                BitmapFont worldFont, BitmapFont screenFont) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(camera.combined);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < polygonCount; i++) {
            PolygonCommand command = polygons.get(i);
            if(command.solid && command.count >= 3) {
                setColor(shapes, command.color, 0.58f);
                for(int vertex = 1; vertex < command.count - 1; vertex++) {
                    shapes.triangle(command.vertices[0], command.vertices[1],
                            command.vertices[vertex * 2], command.vertices[vertex * 2 + 1],
                            command.vertices[(vertex + 1) * 2], command.vertices[(vertex + 1) * 2 + 1]);
                }
            }
        }
        for(int i = 0; i < circleCount; i++) {
            CircleCommand command = circles.get(i);
            if(command.solid) {
                setColor(shapes, command.color, 0.58f);
                shapes.circle(command.x, command.y, command.radius, CIRCLE_SEGMENTS);
            }
        }
        for(int i = 0; i < capsuleCount; i++) {
            drawCapsuleFill(shapes, capsules.get(i));
        }
        float worldPerPixel = camera.viewportHeight / Math.max(1, Gdx.graphics.getHeight());
        for(int i = 0; i < pointCount; i++) {
            PointCommand command = points.get(i);
            setColor(shapes, command.color, 1f);
            shapes.circle(command.x, command.y, Math.max(1.5f, command.size * 0.5f) * worldPerPixel, 12);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        for(int i = 0; i < polygonCount; i++) {
            PolygonCommand command = polygons.get(i);
            setColor(shapes, command.color, 1f);
            for(int vertex = 0; vertex < command.count; vertex++) {
                int next = (vertex + 1) % command.count;
                shapes.line(command.vertices[vertex * 2], command.vertices[vertex * 2 + 1],
                        command.vertices[next * 2], command.vertices[next * 2 + 1]);
            }
        }
        for(int i = 0; i < circleCount; i++) {
            CircleCommand command = circles.get(i);
            setColor(shapes, command.color, 1f);
            shapes.circle(command.x, command.y, command.radius, CIRCLE_SEGMENTS);
            if(command.solid) {
                shapes.line(command.x, command.y,
                        command.x + command.radius * command.axisX,
                        command.y + command.radius * command.axisY);
            }
        }
        for(int i = 0; i < capsuleCount; i++) {
            drawCapsuleOutline(shapes, capsules.get(i));
        }
        for(int i = 0; i < segmentCount; i++) {
            SegmentCommand command = segments.get(i);
            setColor(shapes, command.color, 1f);
            shapes.line(command.x1, command.y1, command.x2, command.y2);
        }
        shapes.end();

        if(worldTextCount > 0) {
            batch.setProjectionMatrix(camera.combined);
            worldFont.getData().setScale(worldPerPixel);
            batch.begin();
            for(int i = 0; i < worldTextCount; i++) {
                TextCommand command = worldText.get(i);
                setColor(worldFont, command.color, 1f);
                worldFont.draw(batch, command.text, command.x, command.y);
            }
            batch.end();
        }

        if(screenTextCount > 0) {
            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();
            screenProjection.setToOrtho2D(0f, 0f, width, height);
            batch.setProjectionMatrix(screenProjection);
            screenFont.getData().setScale(1f);
            batch.begin();
            for(int i = 0; i < screenTextCount; i++) {
                TextCommand command = screenText.get(i);
                setColor(screenFont, command.color, 1f);
                screenFont.draw(batch, command.text, command.x, height - command.y);
            }
            batch.end();
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void copyPolygon(PolygonCommand command, B2DebugPolygon polygon,
                             float px, float py, float cosine, float sine) {
        command.count = Math.min(polygon.GetVertexCount(), command.vertices.length / 2);
        for(int i = 0; i < command.count; i++) {
            B2Vec2 vertex = polygon.GetVertex(i);
            float x = vertex.GetX();
            float y = vertex.GetY();
            command.vertices[i * 2] = px + cosine * x - sine * y;
            command.vertices[i * 2 + 1] = py + sine * x + cosine * y;
        }
    }

    private void drawCapsuleFill(ShapeRenderer shapes, CapsuleCommand command) {
        float dx = command.x2 - command.x1;
        float dy = command.y2 - command.y1;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        setColor(shapes, command.color, 0.58f);
        if(length < 0.0001f) {
            shapes.circle(command.x1, command.y1, command.radius, CIRCLE_SEGMENTS);
            return;
        }
        float nx = -dy * command.radius / length;
        float ny = dx * command.radius / length;
        shapes.triangle(command.x1 + nx, command.y1 + ny, command.x1 - nx, command.y1 - ny,
                command.x2 - nx, command.y2 - ny);
        shapes.triangle(command.x1 + nx, command.y1 + ny, command.x2 - nx, command.y2 - ny,
                command.x2 + nx, command.y2 + ny);
        shapes.circle(command.x1, command.y1, command.radius, CIRCLE_SEGMENTS);
        shapes.circle(command.x2, command.y2, command.radius, CIRCLE_SEGMENTS);
    }

    private void drawCapsuleOutline(ShapeRenderer shapes, CapsuleCommand command) {
        float dx = command.x2 - command.x1;
        float dy = command.y2 - command.y1;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        setColor(shapes, command.color, 1f);
        if(length < 0.0001f) {
            shapes.circle(command.x1, command.y1, command.radius, CIRCLE_SEGMENTS);
            return;
        }
        float nx = -dy * command.radius / length;
        float ny = dx * command.radius / length;
        shapes.line(command.x1 + nx, command.y1 + ny, command.x2 + nx, command.y2 + ny);
        shapes.line(command.x1 - nx, command.y1 - ny, command.x2 - nx, command.y2 - ny);
        shapes.circle(command.x1, command.y1, command.radius, CIRCLE_SEGMENTS);
        shapes.circle(command.x2, command.y2, command.radius, CIRCLE_SEGMENTS);
    }

    private void addSegment(float x1, float y1, float x2, float y2, int hexColor) {
        SegmentCommand command = segment();
        command.x1 = x1;
        command.y1 = y1;
        command.x2 = x2;
        command.y2 = y2;
        command.color = hexColor;
    }

    private void setColor(ShapeRenderer renderer, int hex, float alpha) {
        decodeColor(hex, alpha);
        renderer.setColor(color);
    }

    private void setColor(BitmapFont font, int hex, float alpha) {
        decodeColor(hex, alpha);
        font.setColor(color);
    }

    private void decodeColor(int hex, float alpha) {
        color.set(((hex >>> 16) & 0xFF) / 255f, ((hex >>> 8) & 0xFF) / 255f,
                (hex & 0xFF) / 255f, alpha);
    }

    private PolygonCommand polygon() {
        if(polygonCount == polygons.size()) polygons.add(new PolygonCommand());
        return polygons.get(polygonCount++);
    }

    private CircleCommand circle() {
        if(circleCount == circles.size()) circles.add(new CircleCommand());
        return circles.get(circleCount++);
    }

    private CapsuleCommand capsule() {
        if(capsuleCount == capsules.size()) capsules.add(new CapsuleCommand());
        return capsules.get(capsuleCount++);
    }

    private SegmentCommand segment() {
        if(segmentCount == segments.size()) segments.add(new SegmentCommand());
        return segments.get(segmentCount++);
    }

    private PointCommand point() {
        if(pointCount == points.size()) points.add(new PointCommand());
        return points.get(pointCount++);
    }

    private TextCommand worldText() {
        if(worldTextCount == worldText.size()) worldText.add(new TextCommand());
        return worldText.get(worldTextCount++);
    }

    private TextCommand screenText() {
        if(screenTextCount == screenText.size()) screenText.add(new TextCommand());
        return screenText.get(screenTextCount++);
    }

    private static final class PolygonCommand {
        final float[] vertices = new float[32];
        int count;
        int color;
        float radius;
        boolean solid;
    }

    private static final class CircleCommand {
        float x;
        float y;
        float radius;
        float axisX;
        float axisY;
        int color;
        boolean solid;
    }

    private static final class CapsuleCommand {
        float x1;
        float y1;
        float x2;
        float y2;
        float radius;
        int color;
    }

    private static final class SegmentCommand {
        float x1;
        float y1;
        float x2;
        float y2;
        int color;
    }

    private static final class PointCommand {
        float x;
        float y;
        float size;
        int color;
    }

    private static final class TextCommand {
        float x;
        float y;
        String text;
        int color;
    }
}
