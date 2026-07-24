package com.github.xpenatan.box2d.sample.gdx.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.box2d.sample.gdx.Box2DGdxSampleApplication;

public final class Box2DGdxDesktopLauncher {
    private Box2DGdxDesktopLauncher() {
    }

    public static void main(String[] args) {
        applyOptions(args);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("jBox2D 3.1.1 Samples - FFM");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new Box2DGdxSampleApplication(), config);
    }

    private static void applyOptions(String[] args) {
        if(args == null) return;
        for(String arg : args) {
            setOption(arg, "--sample=", "jbox2d.sample.sample");
            setOption(arg, "--sample-index=", "jbox2d.sample.sampleIndex");
            setOption(arg, "--exit-after-frames=", "jbox2d.sample.exitAfterFrames");
            setOption(arg, "--screenshot=", "jbox2d.sample.screenshot");
        }
    }

    private static void setOption(String arg, String prefix, String property) {
        if(arg != null && arg.startsWith(prefix)) System.setProperty(property, arg.substring(prefix.length()));
    }
}
