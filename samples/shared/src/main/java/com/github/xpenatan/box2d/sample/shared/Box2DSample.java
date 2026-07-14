package com.github.xpenatan.box2d.sample.shared;

import com.github.xpenatan.box2d.B2World;
import java.util.Collections;
import java.util.List;

/** A single Java implementation of an official Box2D sample. */
public interface Box2DSample {
    void step(float deltaSeconds, Box2DSampleSettings settings);

    B2World world();

    void dispose();

    default void keyDown(int key) {
    }

    default void keyUp(int key) {
    }

    default void mouseDown(float x, float y, int button, int modifiers) {
    }

    default void mouseMove(float x, float y) {
    }

    default void mouseUp(float x, float y, int button) {
    }

    default void draw(Box2DSampleDraw draw) {
    }

    /** Mirrors samples such as Character / Mover that optionally track the subject horizontally. */
    default boolean tracksCameraX() {
        return false;
    }

    default float cameraCenterX() {
        return 0.0f;
    }

    default List<Box2DSampleControl> controls() {
        return Collections.emptyList();
    }

    default int bodyCount() {
        return 0;
    }

    default int shapeCount() {
        return 0;
    }

    default int jointCount() {
        return 0;
    }
}
