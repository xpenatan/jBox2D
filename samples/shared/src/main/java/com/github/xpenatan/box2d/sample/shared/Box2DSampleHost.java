package com.github.xpenatan.box2d.sample.shared;

/** Platform-facing callbacks for the Java sample controller. */
public interface Box2DSampleHost {
    default void onSampleChanged(Box2DSampleEntry entry, Box2DSample sample) {
    }

    default void requestExit() {
    }
}
