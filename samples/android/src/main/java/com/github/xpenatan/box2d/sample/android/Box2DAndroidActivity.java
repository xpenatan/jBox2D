package com.github.xpenatan.box2d.sample.android;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.xpenatan.box2d.sample.Box2DSampleApplication;

public final class Box2DAndroidActivity extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySampleOptions();
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true;
        config.useWakelock = true;
        config.useGL30 = true;
        initialize(new Box2DSampleApplication(exitAfterFrames()), config);
    }

    private void applySampleOptions() {
        Intent intent = getIntent();
        if(intent == null) return;
        copyExtra(intent, "jbox2d.sample.sample");
        copyExtra(intent, "jbox2d.sample.sampleIndex");
    }

    private void copyExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        if(value != null && value.trim().length() > 0) System.setProperty(key, value.trim());
    }

    private long exitAfterFrames() {
        Intent intent = getIntent();
        String value = intent == null ? null : intent.getStringExtra("jbox2d.sample.exitAfterFrames");
        if(value == null || value.trim().length() == 0) return 0L;
        return Long.parseLong(value.trim());
    }
}
