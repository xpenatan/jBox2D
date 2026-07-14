package com.github.xpenatan.box2d.sample.shared;

import com.github.xpenatan.box2d.JBox2DLoader;
import com.github.xpenatan.box2d.sample.shared.samples.Box2DSampleRegistry;
import java.util.List;

/** Owns runtime loading, selection, restart, and fixed-step simulation for the Java sample browser. */
public final class Box2DSampleController {
    private static final float MAX_ACCUMULATED_FRAME_TIME = 0.25f;
    private static final int MAX_STEPS_PER_FRAME = 8;

    private final Box2DSampleHost host;
    private final List<Box2DSampleEntry> entries;
    private final Box2DSampleSettings settings = new Box2DSampleSettings();
    private Box2DSample sample;
    private volatile boolean runtimeLoaded;
    private volatile Throwable runtimeError;
    private boolean loadStarted;
    private int selectedIndex;
    private float accumulator;

    public Box2DSampleController(Box2DSampleHost host) {
        if(host == null) throw new IllegalArgumentException("host cannot be null");
        this.host = host;
        entries = Box2DSampleRegistry.entries();
        selectedIndex = resolveInitialSampleIndex();
    }

    public void create() {
        if(loadStarted) return;
        loadStarted = true;
        JBox2DLoader.init((success, throwable) -> {
            if(success) runtimeLoaded = true;
            else runtimeError = throwable != null ? throwable : new IllegalStateException("jBox2D runtime load failed");
        });
    }

    public void update(float deltaSeconds) {
        ensureSampleReady();
        if(sample == null) return;
        boolean singleStep = settings.consumeSingleStep();
        if(settings.paused() && !singleStep) return;

        float step = 1.0f / settings.hertz();
        if(singleStep) {
            sample.step(step, settings);
            return;
        }
        accumulator += Math.max(0.0f, Math.min(deltaSeconds, MAX_ACCUMULATED_FRAME_TIME));
        int count = 0;
        while(accumulator >= step && count < MAX_STEPS_PER_FRAME) {
            sample.step(step, settings);
            accumulator -= step;
            count++;
        }
        if(count == MAX_STEPS_PER_FRAME && accumulator >= step) accumulator = step;
    }

    public void selectSample(int index) {
        int next = Math.max(0, Math.min(index, entries.size() - 1));
        if(next == selectedIndex && sample != null) return;
        selectedIndex = next;
        if(runtimeLoaded) createSelectedSample();
    }

    public void restartSample() {
        if(runtimeLoaded) createSelectedSample();
    }

    public List<Box2DSampleEntry> entries() { return entries; }
    public Box2DSampleSettings settings() { return settings; }
    public int selectedIndex() { return selectedIndex; }
    public Box2DSampleEntry selectedEntry() { return entries.get(selectedIndex); }
    public Box2DSample sample() { return sample; }
    public boolean isReady() { return sample != null; }

    public void dispose() {
        disposeSample();
    }

    private void ensureSampleReady() {
        if(sample != null) return;
        if(runtimeError != null) throw new IllegalStateException("Failed to load the jBox2D runtime", runtimeError);
        if(runtimeLoaded) createSelectedSample();
    }

    private void createSelectedSample() {
        disposeSample();
        accumulator = 0.0f;
        Box2DSampleEntry entry = selectedEntry();
        sample = entry.create();
        host.onSampleChanged(entry, sample);
    }

    private void disposeSample() {
        if(sample != null) {
            sample.dispose();
            sample = null;
        }
    }

    private int resolveInitialSampleIndex() {
        String index = System.getProperty("jbox2d.sample.sampleIndex");
        if(index != null) {
            try { return Math.max(0, Math.min(Integer.parseInt(index.trim()), entries.size() - 1)); }
            catch(NumberFormatException ignored) { }
        }
        String requested = normalize(System.getProperty("jbox2d.sample.sample"));
        if(requested.length() == 0) return 0;
        for(int i = 0; i < entries.size(); i++) {
            Box2DSampleEntry entry = entries.get(i);
            if(normalize(entry.name()).equals(requested) || normalize(entry.displayName()).equals(requested)) return i;
        }
        return 0;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase().replace(" ", "").replace("_", "")
                .replace("-", "").replace('\\', '/').trim();
    }
}
