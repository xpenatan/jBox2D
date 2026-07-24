package com.github.xpenatan.box2d.sample.gdx.desktop;

import com.github.xpenatan.box2d.sample.gdx.Box2DGdxSampleApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;

public final class Box2DGdxDesktopLauncher {
    private Box2DGdxDesktopLauncher() {
    }

    public static void main(String[] args) {
        applyOptions(args);
        long exitAfterFrames = Long.parseLong(System.getProperty("jbox2d.sample.exitAfterFrames", "0"));

        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("jBox2D Samples - TeaVM C");
        config.setWindowedMode(1280, 720);
        config.useVsync(false);
        config.setForegroundFPS(0);
        System.setProperty("os.name", "Windows");

        new GLFWApplication(new Box2DGdxSampleApplication(exitAfterFrames), config);
    }

    private static void applyOptions(String[] args) {
        if(args == null) {
            return;
        }
        for(String arg : args) {
            setOption(arg, "--sample=", "jbox2d.sample.sample");
            setOption(arg, "--sample-index=", "jbox2d.sample.sampleIndex");
            setOption(arg, "--exit-after-frames=", "jbox2d.sample.exitAfterFrames");
            setOption(arg, "--screenshot=", "jbox2d.sample.screenshot");
        }
    }

    private static void setOption(String arg, String prefix, String property) {
        if(arg != null && arg.startsWith(prefix)) {
            System.setProperty(property, arg.substring(prefix.length()));
        }
    }
}
