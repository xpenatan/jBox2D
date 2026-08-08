package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Joints / Soft Body sample. */
public final class SoftBodySample extends AbstractBox2DSample {
    public SoftBodySample() {
        addGroundSegment(-20, 0, 20, 0);
        new DonutAssembly(this, 0, 10, 2, 0, false);
    }
}
