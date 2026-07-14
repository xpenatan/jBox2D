package com.github.xpenatan.box2d.sample.shared.samples;

/** Java port of Box2D 3.1.1's Robustness / HighMassRatio2 sample. */
public final class HighMassRatio2Sample extends AbstractBox2DSample {
    public HighMassRatio2Sample() {
        addStaticBox(0.0f, -1.0f, 50.0f, 1.0f, 0.0f);
        addDynamicBox(-9.0f, 0.5f, 0.5f, 0.5f);
        addDynamicBox(9.0f, 0.5f, 0.5f, 0.5f);
        addDynamicBox(0.0f, 26.0f, 10.0f, 10.0f);
    }
}
