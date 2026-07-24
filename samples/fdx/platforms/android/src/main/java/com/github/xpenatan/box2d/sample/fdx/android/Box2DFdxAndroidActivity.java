package com.github.xpenatan.box2d.sample.fdx.android;

import android.content.Intent;
import com.github.xpenatan.box2d.sample.fdx.Box2DFdxSampleApplication;
import io.github.libfdx.application.ApplicationListener;
import io.github.libfdx.backend.android.AndroidApplicationActivity;
import io.github.libfdx.backend.android.AndroidApplicationConfig;
import io.github.libfdx.backend.android.AndroidGlesProvider;
import io.github.libfdx.backend.android.AndroidVulkanProvider;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.wgpu.WGPUProvider;

public class Box2DFdxAndroidActivity extends AndroidApplicationActivity {
    @Override
    protected AndroidApplicationConfig createApplicationConfig() {
        applySampleOptions();
        return new AndroidApplicationConfig()
                .title("jBox2D libfdx - " + graphicsDisplayName())
                .size(1280, 720)
                .vSync(true)
                .foregroundFps(60)
                .graphics(graphicsProvider());
    }

    @Override
    protected ApplicationListener createApplicationListener() {
        return new Box2DFdxSampleApplication(exitAfterFrames());
    }

    protected String graphicsName() {
        return "wgpu";
    }

    protected String graphicsDisplayName() {
        if("gles".equalsIgnoreCase(graphicsName())) return "OpenGL ES";
        if("vulkan".equalsIgnoreCase(graphicsName()) || "vk".equalsIgnoreCase(graphicsName())) {
            return "Vulkan JNI";
        }
        return "WGPU JNI";
    }

    private GraphicsAttachmentProvider graphicsProvider() {
        if("gles".equalsIgnoreCase(graphicsName())) return new AndroidGlesProvider();
        if("vulkan".equalsIgnoreCase(graphicsName()) || "vk".equalsIgnoreCase(graphicsName())) {
            return new AndroidVulkanProvider();
        }
        return new WGPUProvider();
    }

    private void applySampleOptions() {
        Intent intent = getIntent();
        if(intent == null) return;
        setSystemPropertyFromExtra(intent, "jbox2d.sample.sample");
        setSystemPropertyFromExtra(intent, "jbox2d.sample.sampleIndex");
    }

    private void setSystemPropertyFromExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        if(value != null && value.trim().length() > 0) System.setProperty(key, value.trim());
    }

    private long exitAfterFrames() {
        Intent intent = getIntent();
        String value = intent != null ? intent.getStringExtra("jbox2d.sample.exitAfterFrames") : null;
        if(value == null || value.trim().isEmpty()) return 0L;
        return Long.parseLong(value.trim());
    }
}
