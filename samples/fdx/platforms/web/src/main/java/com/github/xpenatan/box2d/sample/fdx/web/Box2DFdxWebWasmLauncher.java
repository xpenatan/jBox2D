package com.github.xpenatan.box2d.sample.fdx.web;

import io.github.libfdx.graphics.gl.web.WebGLProvider;

public final class Box2DFdxWebWasmLauncher {
    private Box2DFdxWebWasmLauncher() {
    }

    public static void main(String[] args) {
        Box2DFdxWebLauncherSupport.start("Wasm", args, false, new WebGLProvider());
    }
}
