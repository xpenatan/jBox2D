package com.github.xpenatan.box2d.sample.shared;

/** Platform-neutral drawing hooks used by samples for diagnostics beyond world debug drawing. */
public interface Box2DSampleDraw {
    void segment(float x1, float y1, float x2, float y2, int color);

    void point(float x, float y, float size, int color);

    void circle(float x, float y, float radius, int color);

    void worldText(float x, float y, String text, int color);

    void screenText(float x, float y, String text, int color);
}
