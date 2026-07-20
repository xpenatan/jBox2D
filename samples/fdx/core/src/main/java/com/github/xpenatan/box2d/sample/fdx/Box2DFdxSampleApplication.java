package com.github.xpenatan.box2d.sample.fdx;

import com.github.xpenatan.box2d.sample.shared.Box2DSample;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleCamera;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleController;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleEntry;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleHost;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleSettings;
import io.github.libfdx.Fdx;
import io.github.libfdx.application.Application;
import io.github.libfdx.application.ApplicationAdapter;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.core.Logger;
import io.github.libfdx.display.Display;
import io.github.libfdx.graphics.GraphicsContext;
import io.github.libfdx.graphics.camera.Camera;
import io.github.libfdx.graphics.camera.CameraProjection;
import io.github.libfdx.graphics.camera.controller.CameraController2D;
import io.github.libfdx.input.Input;
import io.github.libfdx.input.InputAdapter;
import io.github.libfdx.input.Key;
import io.github.libfdx.input.KeyEvent;
import io.github.libfdx.input.MouseButton;
import io.github.libfdx.input.PointerEvent;
import io.github.libfdx.input.TouchEvent;
import io.github.libfdx.input.TouchPoint;
import io.github.libfdx.ui.Ui;
import io.github.libfdx.ui.UiBooleanState;
import io.github.libfdx.ui.UiFloatState;
import io.github.libfdx.ui.UiRoot;
import io.github.libfdx.ui.UiScope;
import io.github.libfdx.ui.UiState;
import io.github.libfdx.ui.UiToolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** libfdx frontend for the shared jBox2D sample catalog. */
public final class Box2DFdxSampleApplication extends ApplicationAdapter implements Box2DSampleHost {
    private static final int SELECTOR_HIT_WIDTH = 328;
    private static final float FPS_UPDATE_INTERVAL = 0.25f;

    private final Box2DSampleController controller = new Box2DSampleController(this);
    private final ArrayList<ControlBinding> controlBindings = new ArrayList<ControlBinding>();
    private final UiState<String> activeSampleName = Ui.state("Loading");
    private final UiState<String> statsText = Ui.state("FPS 0");
    private final UiState<String> diagnosticsText = Ui.state("");
    private final UiFloatState subStepState = Ui.state(4.0f);
    private final UiFloatState hertzState = Ui.state(60.0f);
    private final UiBooleanState sleepEnabledState = Ui.state(true);
    private final UiBooleanState warmStartingEnabledState = Ui.state(true);
    private final UiBooleanState continuousEnabledState = Ui.state(true);
    private final UiBooleanState pausedState = Ui.state(false);
    private final long exitAfterFrames;

    private Application application;
    private Display display;
    private Logger logger;
    private GraphicsContext graphics;
    private Input input;
    private Camera camera;
    private CameraController2D cameraController;
    private InputAdapter worldInput;
    private FdxSampleDrawRenderer drawRenderer;
    private UiRoot uiRoot;
    private boolean preserveCameraOnSampleChange;
    private boolean worldDragging;
    private float fpsElapsed;
    private int fpsFrames;
    private boolean fpsHasValue;
    private long renderedFrames;

    public Box2DFdxSampleApplication() {
        this(0L);
    }

    public Box2DFdxSampleApplication(long exitAfterFrames) {
        this.exitAfterFrames = Math.max(0L, exitAfterFrames);
    }

    @Override
    public void create(Fdx fdx) {
        application = fdx.app();
        display = fdx.displays().main();
        logger = fdx.logger();
        graphics = fdx.graphics().main();
        input = fdx.input();
        camera = new Camera()
                .projection(CameraProjection.ORTHOGRAPHIC)
                .nearFar(0.1f, 10.0f)
                .position(0.0f, 0.0f, 1.0f)
                .direction(0.0f, 0.0f, -1.0f)
                .up(0.0f, 1.0f, 0.0f);
        configureCamera(controller.selectedEntry().camera());
        cameraController = new CameraController2D(input, camera)
                .pointerRegion((x, y) -> x > SELECTOR_HIT_WIDTH)
                .touchEnabled(false)
                .zoomRange(0.00005f, 8.0f);
        uiRoot = new UiToolkit(fdx.files()).root(display, graphics).input(input);
        uiRoot.setContent(this::buildUi);
        worldInput = new WorldInput();
        input.addProcessor(worldInput);
        activeSampleName.set(controller.selectedEntry().displayName());
        controller.create();
    }

    @Override
    public void resize(int width, int height) {
        if(uiRoot != null) uiRoot.resize(width, height);
        if(camera != null) camera.viewport(Math.max(1, width), Math.max(1, height)).update();
    }

    @Override
    public void render() {
        float deltaSeconds = Math.min(application.deltaTime(), 1.0f / 20.0f);
        applySimulationSettings();
        syncSampleControls();
        cameraController.update(deltaSeconds);
        camera.viewport(framebufferWidth(), framebufferHeight()).update();
        graphics.clear(0.04f, 0.045f, 0.06f, 1.0f);

        try {
            controller.update(deltaSeconds);
            Box2DSample sample = controller.sample();
            if(sample != null) {
                if(sample.tracksCameraX()) {
                    camera.position(sample.cameraCenterX(), camera.position().y(), camera.position().z()).update();
                }
                drawRenderer.beginFrame();
                drawRenderer.DrawWorld(sample.world());
                sample.draw(drawRenderer);
                drawRenderer.render(camera);
                diagnosticsText.set(drawRenderer.diagnostics());
                updateStats(sample, deltaSeconds);
            }
        }
        catch(RuntimeException exception) {
            throw new FdxException("jBox2D libfdx sample failed: " + controller.selectedEntry().displayName(),
                    exception);
        }

        if(uiRoot != null) {
            uiRoot.update(application.deltaTime());
            uiRoot.render();
        }
        renderedFrames++;
        if(exitAfterFrames > 0 && renderedFrames >= exitAfterFrames) application.requestExit();
    }

    @Override
    public void onSampleChanged(Box2DSampleEntry entry, Box2DSample sample) {
        if(drawRenderer == null) drawRenderer = new FdxSampleDrawRenderer(graphics);
        worldDragging = false;
        if(!preserveCameraOnSampleChange) configureCamera(entry.camera());
        rebuildControlBindings(sample.controls());
        activeSampleName.set(entry.displayName());
        diagnosticsText.set("");
        if(uiRoot != null) uiRoot.requestCompose();
        if(logger != null) logger.info("Selected jBox2D sample: " + entry.displayName());
    }

    @Override
    public void requestExit() {
        application.requestExit();
    }

    @Override
    public void dispose() {
        if(input != null && worldInput != null) input.removeProcessor(worldInput);
        worldInput = null;
        if(cameraController != null) {
            cameraController.dispose();
            cameraController = null;
        }
        if(uiRoot != null) {
            uiRoot.dispose();
            uiRoot = null;
        }
        if(drawRenderer != null) {
            drawRenderer.dispose();
            drawRenderer = null;
        }
        controller.dispose();
        if(logger != null) logger.info("jBox2D libfdx sample disposed after " + renderedFrames + " frames");
    }

    private void buildUi(UiScope ui) {
        ui.row(Ui.modifier().fill().padding(12.0f).gap(12.0f), page -> {
            page.panel(Ui.modifier().width(304.0f).fillHeight().padding(10.0f).gap(6.0f), panel -> {
                panel.text("jBox2D libfdx Samples");
                panel.text(activeSampleName.get());
                panel.text(statsText.get());
                panel.scrollView(Ui.modifier().fillWidth().fillHeight(), controls -> {
                    controls.scrollView(Ui.modifier().fillWidth().height(220.0f), list -> {
                        String previousCategory = "";
                        for(int i = 0; i < controller.entries().size(); i++) {
                            Box2DSampleEntry entry = controller.entries().get(i);
                            if(!entry.category().equals(previousCategory)) {
                                previousCategory = entry.category();
                                list.text(previousCategory);
                            }
                            final int sampleIndex = i;
                            String marker = sampleIndex == controller.selectedIndex() ? "> " : "";
                            list.button(marker + entry.name(), Ui.modifier().fillWidth().height(30.0f),
                                    () -> selectSample(sampleIndex));
                        }
                    });
                    controls.button("Restart Sample", Ui.modifier().fillWidth().height(32.0f), this::restartSample);
                    controls.button("Reset Camera", Ui.modifier().fillWidth().height(32.0f), this::resetCamera);
                    controls.button(pausedState.get() ? "Resume" : "Pause",
                            Ui.modifier().fillWidth().height(32.0f), pausedState::toggle);
                    controls.button("Single Step", Ui.modifier().fillWidth().height(32.0f),
                            controller.settings()::requestSingleStep);

                    controls.text("Simulation");
                    controls.text("Sub-steps: " + Math.round(subStepState.get()));
                    controls.slider(Ui.modifier().fillWidth().height(24.0f), subStepState,
                            Box2DSampleSettings.MIN_SUB_STEPS, Box2DSampleSettings.MAX_SUB_STEPS);
                    controls.text("Hertz: " + Math.round(hertzState.get()));
                    controls.slider(Ui.modifier().fillWidth().height(24.0f), hertzState,
                            Box2DSampleSettings.MIN_HERTZ, Box2DSampleSettings.MAX_HERTZ);
                    controls.checkbox("Sleep", Ui.modifier().fillWidth().height(28.0f), sleepEnabledState);
                    controls.checkbox("Warm Starting", Ui.modifier().fillWidth().height(28.0f),
                            warmStartingEnabledState);
                    controls.checkbox("Continuous", Ui.modifier().fillWidth().height(28.0f),
                            continuousEnabledState);

                    buildSampleControls(controls);
                    if(diagnosticsText.get().length() > 0) {
                        controls.text("Diagnostics");
                        controls.text(diagnosticsText.get());
                    }
                });
            });
            page.spacer(Ui.modifier().fill());
        });
    }

    private void buildSampleControls(UiScope controls) {
        if(controlBindings.isEmpty()) return;
        controls.text("Sample Controls");
        for(int i = 0; i < controlBindings.size(); i++) {
            ControlBinding binding = controlBindings.get(i);
            Box2DSampleControl control = binding.control;
            if(control.type() == Box2DSampleControl.TEXT) {
                controls.text(binding.textState.get());
            }
            else if(control.type() == Box2DSampleControl.BUTTON) {
                controls.button(control.label(), Ui.modifier().fillWidth().height(30.0f), control::press);
            }
            else if(control.type() == Box2DSampleControl.CHECKBOX) {
                controls.checkbox(control.label(), Ui.modifier().fillWidth().height(28.0f), binding.booleanState);
            }
            else if(control.type() == Box2DSampleControl.SLIDER_FLOAT
                    || control.type() == Box2DSampleControl.SLIDER_INT) {
                controls.text(control.label() + ": " + formatValue(binding.floatState.get(), control.step()));
                controls.slider(Ui.modifier().fillWidth().height(24.0f), binding.floatState,
                        control.minimum(), control.maximum());
            }
            else if(control.type() == Box2DSampleControl.COMBO) {
                controls.text(control.label());
                for(int optionIndex = 0; optionIndex < control.optionCount(); optionIndex++) {
                    final int selectedIndex = optionIndex;
                    String marker = Math.round(control.value()) == optionIndex ? "> " : "";
                    controls.button(marker + control.option(optionIndex),
                            Ui.modifier().fillWidth().height(28.0f),
                            () -> setControlValue(binding, selectedIndex));
                }
            }
            else if(control.type() == Box2DSampleControl.RADIO) {
                String marker = control.value() >= 0.5f ? "> " : "";
                controls.button(marker + control.label(), Ui.modifier().fillWidth().height(28.0f),
                        () -> pressControl(binding));
            }
        }
    }

    private void selectSample(int index) {
        worldDragging = false;
        controller.selectSample(index);
        if(uiRoot != null) uiRoot.requestCompose();
    }

    private void restartSample() {
        worldDragging = false;
        preserveCameraOnSampleChange = true;
        try {
            controller.restartSample();
        }
        finally {
            preserveCameraOnSampleChange = false;
        }
    }

    private void resetCamera() {
        configureCamera(controller.selectedEntry().camera());
    }

    private void configureCamera(Box2DSampleCamera sampleCamera) {
        if(camera == null) return;
        float worldPerPixel = 2.0f * sampleCamera.zoom / Math.max(1, framebufferHeight());
        camera.position(sampleCamera.centerX, sampleCamera.centerY, 1.0f)
                .zoom(worldPerPixel)
                .viewport(framebufferWidth(), framebufferHeight())
                .update();
    }

    private void applySimulationSettings() {
        Box2DSampleSettings settings = controller.settings();
        settings.setSubStepCount(Math.round(subStepState.get()));
        settings.setHertz(hertzState.get());
        settings.setSleepEnabled(sleepEnabledState.get());
        settings.setWarmStartingEnabled(warmStartingEnabledState.get());
        settings.setContinuousEnabled(continuousEnabledState.get());
        settings.setPaused(pausedState.get());
    }

    private void rebuildControlBindings(List<Box2DSampleControl> controls) {
        controlBindings.clear();
        for(int i = 0; i < controls.size(); i++) controlBindings.add(new ControlBinding(controls.get(i)));
    }

    private void syncSampleControls() {
        for(int i = 0; i < controlBindings.size(); i++) {
            ControlBinding binding = controlBindings.get(i);
            Box2DSampleControl control = binding.control;
            if(control.type() == Box2DSampleControl.TEXT) {
                binding.textState.set(control.label());
            }
            else if(control.type() == Box2DSampleControl.CHECKBOX) {
                float uiValue = binding.booleanState.get() ? 1.0f : 0.0f;
                syncValue(binding, uiValue);
                boolean current = control.value() >= 0.5f;
                if(binding.booleanState.get() != current) binding.booleanState.set(current);
                binding.lastUiValue = binding.booleanState.get() ? 1.0f : 0.0f;
                binding.lastControlValue = control.value();
            }
            else if(control.type() == Box2DSampleControl.SLIDER_FLOAT
                    || control.type() == Box2DSampleControl.SLIDER_INT) {
                syncValue(binding, binding.floatState.get());
                float current = control.value();
                if(!same(binding.floatState.get(), current)) binding.floatState.set(current);
                binding.lastUiValue = binding.floatState.get();
                binding.lastControlValue = current;
            }
        }
    }

    private void syncValue(ControlBinding binding, float uiValue) {
        float controlValue = binding.control.value();
        if(!same(uiValue, binding.lastUiValue)) binding.control.setValue(uiValue);
        else if(!same(controlValue, binding.lastControlValue)) {
            if(binding.booleanState != null) binding.booleanState.set(controlValue >= 0.5f);
            if(binding.floatState != null) binding.floatState.set(controlValue);
        }
    }

    private void setControlValue(ControlBinding binding, float value) {
        binding.control.setValue(value);
        binding.lastControlValue = binding.control.value();
        binding.lastUiValue = binding.lastControlValue;
        if(uiRoot != null) uiRoot.requestCompose();
    }

    private void pressControl(ControlBinding binding) {
        binding.control.press();
        binding.lastControlValue = binding.control.value();
        if(uiRoot != null) uiRoot.requestCompose();
    }

    private void updateStats(Box2DSample sample, float deltaSeconds) {
        fpsElapsed += Math.max(0.0f, deltaSeconds);
        fpsFrames++;
        if(fpsElapsed <= 0.000001f || (fpsHasValue && fpsElapsed < FPS_UPDATE_INTERVAL)) return;
        int fps = Math.round(fpsFrames / fpsElapsed);
        statsText.set("FPS " + fps + "   Bodies " + sample.bodyCount() + "   Shapes " + sample.shapeCount()
                + "   Joints " + sample.jointCount());
        fpsHasValue = true;
        if(fpsElapsed >= FPS_UPDATE_INTERVAL) {
            fpsElapsed = 0.0f;
            fpsFrames = 0;
        }
    }

    private boolean isWorldArea(int x, int y) {
        return x > SELECTOR_HIT_WIDTH && x < framebufferWidth() && y >= 0 && y < framebufferHeight();
    }

    private float worldX(int screenX) {
        return camera.position().x() + (screenX - framebufferWidth() * 0.5f) * camera.zoom();
    }

    private float worldY(int screenY) {
        return camera.position().y() + (framebufferHeight() * 0.5f - screenY) * camera.zoom();
    }

    private int modifiers() {
        int value = 0;
        if(input.isKeyPressed(Key.SHIFT_LEFT) || input.isKeyPressed(Key.SHIFT_RIGHT)) value |= 1;
        if(input.isKeyPressed(Key.CONTROL_LEFT) || input.isKeyPressed(Key.CONTROL_RIGHT)) value |= 2;
        if(input.isKeyPressed(Key.ALT_LEFT) || input.isKeyPressed(Key.ALT_RIGHT)) value |= 4;
        return value;
    }

    private int glfwKey(Key key) {
        if(key.ordinal() >= Key.A.ordinal() && key.ordinal() <= Key.Z.ordinal()) {
            return 65 + key.ordinal() - Key.A.ordinal();
        }
        if(key.ordinal() >= Key.NUM_0.ordinal() && key.ordinal() <= Key.NUM_9.ordinal()) {
            return 48 + key.ordinal() - Key.NUM_0.ordinal();
        }
        if(key == Key.SPACE) return 32;
        if(key == Key.ENTER) return 257;
        if(key == Key.LEFT) return 263;
        if(key == Key.RIGHT) return 262;
        if(key == Key.DOWN) return 264;
        if(key == Key.UP) return 265;
        return -1;
    }

    private int framebufferWidth() {
        if(display == null) return 1280;
        int width = display.framebufferWidth() > 0 ? display.framebufferWidth() : display.width();
        return width > 0 ? width : 1280;
    }

    private int framebufferHeight() {
        if(display == null) return 720;
        int height = display.framebufferHeight() > 0 ? display.framebufferHeight() : display.height();
        return height > 0 ? height : 720;
    }

    private static boolean same(float first, float second) {
        return first == second || (Float.isNaN(first) && Float.isNaN(second));
    }

    private static String formatValue(float value, float step) {
        return step >= 1.0f ? Integer.toString(Math.round(value)) : String.format(Locale.US, "%.2f", value);
    }

    private final class WorldInput extends InputAdapter {
        @Override
        public boolean pointerDown(PointerEvent event) {
            if(event.button() != MouseButton.LEFT || !isWorldArea(event.x(), event.y()) || !controller.isReady()) {
                return false;
            }
            worldDragging = true;
            controller.sample().mouseDown(worldX(event.x()), worldY(event.y()), 0, modifiers());
            return true;
        }

        @Override
        public boolean pointerMoved(PointerEvent event) {
            if(!controller.isReady() || (!worldDragging && !isWorldArea(event.x(), event.y()))) return false;
            controller.sample().mouseMove(worldX(event.x()), worldY(event.y()));
            return worldDragging;
        }

        @Override
        public boolean pointerUp(PointerEvent event) {
            if(event.button() != MouseButton.LEFT || !worldDragging || !controller.isReady()) return false;
            controller.sample().mouseUp(worldX(event.x()), worldY(event.y()), 0);
            worldDragging = false;
            return true;
        }

        @Override
        public boolean touchDown(TouchEvent event) {
            TouchPoint point = event.point();
            if(point == null || !isWorldArea(point.x(), point.y()) || !controller.isReady()) return false;
            worldDragging = true;
            controller.sample().mouseDown(worldX(point.x()), worldY(point.y()), 0, modifiers());
            return true;
        }

        @Override
        public boolean touchMoved(TouchEvent event) {
            TouchPoint point = event.point();
            if(point == null || !worldDragging || !controller.isReady()) return false;
            controller.sample().mouseMove(worldX(point.x()), worldY(point.y()));
            return true;
        }

        @Override
        public boolean touchUp(TouchEvent event) {
            TouchPoint point = event.point();
            if(point == null || !worldDragging || !controller.isReady()) return false;
            controller.sample().mouseUp(worldX(point.x()), worldY(point.y()), 0);
            worldDragging = false;
            return true;
        }

        @Override
        public boolean keyDown(KeyEvent event) {
            if(!controller.isReady()) return false;
            if(event.key() == Key.HOME) {
                resetCamera();
                return true;
            }
            int key = glfwKey(event.key());
            if(key < 0) return false;
            controller.sample().keyDown(key);
            return true;
        }

        @Override
        public boolean keyUp(KeyEvent event) {
            if(!controller.isReady()) return false;
            int key = glfwKey(event.key());
            if(key < 0) return false;
            controller.sample().keyUp(key);
            return true;
        }
    }

    private static final class ControlBinding {
        final Box2DSampleControl control;
        final UiState<String> textState;
        final UiBooleanState booleanState;
        final UiFloatState floatState;
        float lastUiValue;
        float lastControlValue;

        ControlBinding(Box2DSampleControl control) {
            this.control = control;
            float value = control.value();
            textState = control.type() == Box2DSampleControl.TEXT ? Ui.state(control.label()) : null;
            booleanState = control.type() == Box2DSampleControl.CHECKBOX ? Ui.state(value >= 0.5f) : null;
            floatState = control.type() == Box2DSampleControl.SLIDER_FLOAT
                    || control.type() == Box2DSampleControl.SLIDER_INT ? Ui.state(value) : null;
            lastUiValue = value;
            lastControlValue = value;
        }
    }
}
