package com.github.xpenatan.box2d.sample.fdx.desktop;

import com.github.xpenatan.box2d.sample.fdx.Box2DFdxSampleApplication;
import io.github.libfdx.backend.desktop.DesktopApplicationBackend;
import io.github.libfdx.backend.desktop.DesktopApplicationConfig;
import io.github.libfdx.backend.desktop.DesktopOpenGLProvider;
import io.github.libfdx.backend.desktop.DesktopVulkanProvider;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.wgpu.WGPUProvider;

public final class Box2DFdxDesktopLauncher {
    private Box2DFdxDesktopLauncher() {
    }

    public static void main(String[] args) {
        String graphics = option(args, "--graphics=", System.getProperty("jbox2d.sample.graphics", "gl"));
        String label = option(args, "--graphics-label=", System.getProperty("jbox2d.sample.graphicsLabel"));
        boolean visible = Boolean.parseBoolean(option(args, "--visible=",
                System.getProperty("jbox2d.sample.visible", "true")));
        long exitAfterFrames = Long.parseLong(option(args, "--exit-after-frames=",
                System.getProperty("jbox2d.sample.exitAfterFrames", "0")));

        DesktopApplicationConfig config = new DesktopApplicationConfig()
                .title("jBox2D libfdx - " + graphicsDisplayName(graphics, label) + " C")
                .size(1280, 720)
                .visible(visible)
                .vSync(true)
                .foregroundFps(60)
                .graphics(graphicsProvider(graphics));

        new DesktopApplicationBackend().start(config, new Box2DFdxSampleApplication(exitAfterFrames));
    }

    private static GraphicsAttachmentProvider graphicsProvider(String graphics) {
        if("vulkan".equalsIgnoreCase(graphics) || "vk".equalsIgnoreCase(graphics)) {
            return new DesktopVulkanProvider();
        }
        if("wgpu".equalsIgnoreCase(graphics) || "webgpu".equalsIgnoreCase(graphics)) {
            return new WGPUProvider();
        }
        return new DesktopOpenGLProvider();
    }

    private static String graphicsDisplayName(String graphics, String configured) {
        if(configured != null && configured.trim().length() > 0) return configured.trim();
        if("vulkan".equalsIgnoreCase(graphics) || "vk".equalsIgnoreCase(graphics)) return "Vulkan";
        if("wgpu".equalsIgnoreCase(graphics) || "webgpu".equalsIgnoreCase(graphics)) return "WGPU";
        return "OpenGL";
    }

    private static String option(String[] args, String prefix, String fallback) {
        if(args == null) return fallback;
        for(String arg : args) {
            if(arg != null && arg.startsWith(prefix)) return arg.substring(prefix.length());
        }
        return fallback;
    }
}
