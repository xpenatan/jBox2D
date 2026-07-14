package com.github.xpenatan.box2d.sample.shared;

/** A UI-neutral description of one sample-specific control. */
public final class Box2DSampleControl {
    public static final int TEXT = 0;
    public static final int BUTTON = 1;
    public static final int CHECKBOX = 2;
    public static final int SLIDER_FLOAT = 3;
    public static final int SLIDER_INT = 4;
    public static final int COMBO = 5;
    public static final int RADIO = 6;

    public interface Getter {
        float get();
    }

    public interface Setter {
        void set(float value);
    }

    public interface Action {
        void run();
    }

    public interface TextProvider {
        String get();
    }

    private final int type;
    private final String label;
    private final float minimum;
    private final float maximum;
    private final float step;
    private final String[] options;
    private final Getter getter;
    private final Setter setter;
    private final Action action;
    private final TextProvider textProvider;

    private Box2DSampleControl(int type, String label, float minimum, float maximum, float step, String[] options,
            Getter getter, Setter setter, Action action, TextProvider textProvider) {
        this.type = type;
        this.label = label == null ? "" : label;
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
        this.options = options == null ? new String[0] : options.clone();
        this.getter = getter;
        this.setter = setter;
        this.action = action;
        this.textProvider = textProvider;
    }

    public static Box2DSampleControl text(String text) {
        return dynamicText(new TextProvider() {
            @Override public String get() { return text; }
        });
    }

    public static Box2DSampleControl dynamicText(TextProvider provider) {
        return new Box2DSampleControl(TEXT, "", 0, 0, 0, null, null, null, null, provider);
    }

    public static Box2DSampleControl button(String label, Action action) {
        return new Box2DSampleControl(BUTTON, label, 0, 0, 0, null, null, null, action, null);
    }

    public static Box2DSampleControl checkbox(String label, Getter getter, Setter setter) {
        return new Box2DSampleControl(CHECKBOX, label, 0, 1, 1, null, getter, setter, null, null);
    }

    public static Box2DSampleControl slider(String label, float min, float max, float step, Getter getter,
            Setter setter) {
        return new Box2DSampleControl(step >= 1.0f ? SLIDER_INT : SLIDER_FLOAT, label, min, max, step, null,
                getter, setter, null, null);
    }

    public static Box2DSampleControl combo(String label, String[] options, Getter getter, Setter setter) {
        return new Box2DSampleControl(COMBO, label, 0, options.length - 1, 1, options, getter, setter, null, null);
    }

    public static Box2DSampleControl radio(String label, Getter getter, Action action) {
        return new Box2DSampleControl(RADIO, label, 0, 1, 1, null, getter, null, action, null);
    }

    public int type() { return type; }
    public String label() { return type == TEXT && textProvider != null ? textProvider.get() : label; }
    public float minimum() { return minimum; }
    public float maximum() { return maximum; }
    public float step() { return step; }
    public int optionCount() { return options.length; }
    public String option(int index) { return options[index]; }
    public float value() { return getter == null ? 0.0f : getter.get(); }
    public void setValue(float value) { if(setter != null) setter.set(value); }
    public void press() { if(action != null) action.run(); }
}
