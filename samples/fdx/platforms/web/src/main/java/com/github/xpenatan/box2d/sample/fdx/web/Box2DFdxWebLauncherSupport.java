package com.github.xpenatan.box2d.sample.fdx.web;

import com.github.xpenatan.box2d.sample.fdx.Box2DFdxSampleApplication;
import io.github.libfdx.backend.web.WebApplicationBackend;
import io.github.libfdx.backend.web.WebApplicationConfig;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;

final class Box2DFdxWebLauncherSupport {
    private static final String CANVAS_ID = "libfdx-canvas";

    private Box2DFdxWebLauncherSupport() {
    }

    static void start(
            String runtimeName,
            String[] args,
            boolean webgpu,
            GraphicsAttachmentProvider graphics) {
        long exitAfterFrames = Long.parseLong(option(args, "--exit-after-frames=", "0"));
        applySampleOptions(args);

        String graphicsName = webgpu ? "WebGPU" : "WebGL";
        WebApplicationConfig config = new WebApplicationConfig()
                .title("jBox2D libfdx - " + graphicsName + " " + runtimeName)
                .size(0, 0)
                .canvasId(CANVAS_ID)
                .graphics(graphics);

        new WebApplicationBackend().start(config, new Box2DFdxSampleApplication(exitAfterFrames));
    }

    private static void applySampleOptions(String[] args) {
        setPropertyFromOption(args, "--sample=", "jbox2d.sample.sample");
        setPropertyFromOption(args, "--sample-index=", "jbox2d.sample.sampleIndex");
    }

    private static void setPropertyFromOption(String[] args, String prefix, String property) {
        String value = option(args, prefix, "");
        if(value.length() > 0) System.setProperty(property, value);
    }

    private static boolean isWebGPU(String graphics) {
        return "webgpu".equalsIgnoreCase(graphics) || "wgpu".equalsIgnoreCase(graphics);
    }

    static boolean webGpuRequested(String[] args) {
        return isWebGPU(option(args, "--graphics=", "webgl"));
    }

    private static String option(String[] args, String prefix, String fallback) {
        if(args == null) return fallback;
        for(String arg : args) {
            if(arg != null && arg.startsWith(prefix)) return arg.substring(prefix.length());
        }
        return fallback;
    }
}
