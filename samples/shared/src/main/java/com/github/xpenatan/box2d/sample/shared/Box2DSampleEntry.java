package com.github.xpenatan.box2d.sample.shared;

public final class Box2DSampleEntry {
    private final String category;
    private final String name;
    private final Box2DSampleFactory factory;
    private final Box2DSampleCamera camera;

    public Box2DSampleEntry(String category, String name, Box2DSampleFactory factory, Box2DSampleCamera camera) {
        if(category == null || category.length() == 0) {
            throw new IllegalArgumentException("category cannot be empty");
        }
        if(name == null || name.length() == 0) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        if(factory == null) {
            throw new IllegalArgumentException("factory cannot be null");
        }
        this.category = category;
        this.name = name;
        this.factory = factory;
        this.camera = camera != null ? camera : new Box2DSampleCamera(0.0f, 10.0f, 12.0f);
    }

    public String category() {
        return category;
    }

    public String name() {
        return name;
    }

    public String displayName() {
        return category + " / " + name;
    }

    public Box2DSampleCamera camera() {
        return camera;
    }

    public Box2DSample create() {
        return factory.create();
    }
}
