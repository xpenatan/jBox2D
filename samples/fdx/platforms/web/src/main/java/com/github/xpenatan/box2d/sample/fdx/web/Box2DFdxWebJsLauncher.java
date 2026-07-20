package com.github.xpenatan.box2d.sample.fdx.web;

import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.gl.web.WebGLProvider;
import io.github.libfdx.graphics.wgpu.WebWGPUProvider;

public final class Box2DFdxWebJsLauncher {
    private Box2DFdxWebJsLauncher() {
    }

    public static void main(String[] args) {
        boolean webgpu = Box2DFdxWebLauncherSupport.webGpuRequested(args);
        GraphicsAttachmentProvider graphics = webgpu ? new WebWGPUProvider() : new WebGLProvider();
        Box2DFdxWebLauncherSupport.start("JS", args, webgpu, graphics);
    }
}
