package com.github.xpenatan.box2d.sample.shared;

public final class Box2DSampleSettings {
    public static final int MIN_SUB_STEPS = 1;
    public static final int MAX_SUB_STEPS = 50;
    public static final float MIN_HERTZ = 5.0f;
    public static final float MAX_HERTZ = 240.0f;

    private int subStepCount = 4;
    private float hertz = 60.0f;
    private boolean sleepEnabled = true;
    private boolean warmStartingEnabled = true;
    private boolean continuousEnabled = true;
    private boolean paused;
    private boolean singleStep;

    public int subStepCount() {
        return subStepCount;
    }

    public void setSubStepCount(int value) {
        subStepCount = Math.max(MIN_SUB_STEPS, Math.min(value, MAX_SUB_STEPS));
    }

    public float hertz() {
        return hertz;
    }

    public void setHertz(float value) {
        hertz = Math.max(MIN_HERTZ, Math.min(value, MAX_HERTZ));
    }

    public boolean sleepEnabled() {
        return sleepEnabled;
    }

    public void setSleepEnabled(boolean value) {
        sleepEnabled = value;
    }

    public boolean warmStartingEnabled() {
        return warmStartingEnabled;
    }

    public void setWarmStartingEnabled(boolean value) {
        warmStartingEnabled = value;
    }

    public boolean continuousEnabled() {
        return continuousEnabled;
    }

    public void setContinuousEnabled(boolean value) {
        continuousEnabled = value;
    }

    public boolean paused() {
        return paused;
    }

    public void setPaused(boolean value) {
        paused = value;
    }

    public void requestSingleStep() {
        singleStep = true;
    }

    public boolean consumeSingleStep() {
        boolean value = singleStep;
        singleStep = false;
        return value;
    }
}
