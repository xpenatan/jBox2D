package com.github.xpenatan.box2d.sample;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.box2d.sample.shared.Box2DSample;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleCamera;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleControl;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleController;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleEntry;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleHost;
import com.github.xpenatan.box2d.sample.shared.Box2DSampleSettings;
import java.util.ArrayList;

/** Cross-platform selector and viewer for the Java ports of the Box2D 3.1.1 samples. */
public final class Box2DSampleApplication extends ApplicationAdapter {
    private static final float TOP_BAR_HEIGHT = 54f;
    private static final float SAMPLE_PANEL_WIDTH = 245f;
    private static final float OPTIONS_PANEL_WIDTH = 300f;
    private static final float CHECKBOX_TEXT_GAP = 6f;
    private static final Color BACKGROUND = new Color(0.035f, 0.045f, 0.060f, 1f);
    private static final Color PANEL = new Color(0.075f, 0.090f, 0.115f, 0.97f);
    private static final Color PANEL_LIGHT = new Color(0.115f, 0.135f, 0.165f, 1f);
    private static final Color ACCENT = new Color(0.21f, 0.72f, 0.95f, 1f);

    private final long exitAfterFrames;
    private final String screenshotPath;
    private final CameraScrollInput cameraScrollInput = new CameraScrollInput();
    private final WorldInput worldInput = new WorldInput();
    private final Vector3 unproject = new Vector3();
    private final ArrayList<TextButton> sampleButtons = new ArrayList<TextButton>();
    private final ArrayList<ControlWidget> controlWidgets = new ArrayList<ControlWidget>();

    private Stage stage;
    private Skin skin;
    private OrthographicCamera worldCamera;
    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont worldFont;
    private BitmapFont screenFont;
    private SampleDrawRenderer drawRenderer;
    private Box2DSampleController controller;
    private Box2DSample currentSample;
    private Table root;
    private Table sampleTable;
    private Table controlsTable;
    private Label loadingLabel;
    private Label titleLabel;
    private Label statsLabel;
    private TextButton pauseButton;
    private boolean controlsUpdating;
    private int highlightedSampleIndex = -1;
    private int controlsSignature = Integer.MIN_VALUE;
    private long sampleFrameCount;
    private boolean screenshotWritten;
    private float cameraCenterX;
    private float cameraCenterY;
    private float cameraZoom = 10.0f;

    public Box2DSampleApplication() {
        this(parseLongProperty("jbox2d.sample.exitAfterFrames", 0L));
    }

    public Box2DSampleApplication(long exitAfterFrames) {
        this.exitAfterFrames = Math.max(0L, exitAfterFrames);
        this.screenshotPath = System.getProperty("jbox2d.sample.screenshot", "").trim();
    }

    @Override
    public void create() {
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        worldCamera = new OrthographicCamera();
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        worldFont = new BitmapFont();
        screenFont = new BitmapFont();
        skin = createSkin();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(new InputMultiplexer(cameraScrollInput, stage, worldInput));
        buildLoadingUi();
        controller = new Box2DSampleController(new Box2DSampleHost() {
            @Override
            public void onSampleChanged(Box2DSampleEntry entry, Box2DSample sample) {
                if(drawRenderer == null) drawRenderer = new SampleDrawRenderer();
                currentSample = sample;
                highlightedSampleIndex = controller.selectedIndex();
                Box2DSampleCamera camera = entry.camera();
                setCamera(camera.centerX, camera.centerY, camera.zoom);
                if(titleLabel == null) buildApplicationUi();
                refreshDynamicControls(true);
                updateHeader();
            }
        });
        controller.create();
    }

    private void buildLoadingUi() {
        stage.clear();
        root = new Table(skin);
        root.setFillParent(true);
        loadingLabel = new Label("Loading Box2D 3.1.1 samples...", skin, "title");
        root.add(loadingLabel).expand().center();
        stage.addActor(root);
    }

    private void buildApplicationUi() {
        stage.clear();
        sampleButtons.clear();
        controlWidgets.clear();

        root = new Table(skin);
        root.setFillParent(true);
        root.top().left();
        stage.addActor(root);

        Table top = new Table(skin);
        top.setBackground(skin.newDrawable("white", PANEL));
        titleLabel = new Label("", skin, "title");
        statsLabel = new Label("", skin, "small");
        TextButton restart = new TextButton("Restart", skin);
        pauseButton = new TextButton("Pause", skin);
        TextButton singleStep = new TextButton("Step", skin);
        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.restartSample();
                refreshDynamicControls(true);
            }
        });
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Box2DSampleSettings settings = controller.settings();
                settings.setPaused(!settings.paused());
            }
        });
        singleStep.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.settings().requestSingleStep();
            }
        });
        top.add(titleLabel).left().padLeft(12f).growX();
        top.add(statsLabel).right().padRight(12f);
        top.add(restart).width(86f).height(34f).pad(5f);
        top.add(pauseButton).width(78f).height(34f).pad(5f);
        top.add(singleStep).width(68f).height(34f).pad(5f).padRight(10f);
        root.add(top).colspan(3).height(TOP_BAR_HEIGHT).growX().row();

        sampleTable = new Table(skin);
        sampleTable.top().left();
        buildSampleList();
        ScrollPane sampleScroll = new ScrollPane(sampleTable, skin);
        sampleScroll.setFadeScrollBars(false);
        sampleScroll.setScrollingDisabled(true, false);

        Table optionsTable = new Table(skin);
        optionsTable.top().left();
        buildOptions(optionsTable);
        ScrollPane optionsScroll = new ScrollPane(optionsTable, skin);
        optionsScroll.setFadeScrollBars(false);
        optionsScroll.setScrollingDisabled(true, false);

        root.add(sampleScroll).width(SAMPLE_PANEL_WIDTH).growY().fillY();
        root.add().grow();
        root.add(optionsScroll).width(OPTIONS_PANEL_WIDTH).growY().fillY();
        updateHeader();
    }

    private void buildSampleList() {
        java.util.List<Box2DSampleEntry> entries = controller.entries();
        Label heading = new Label("SAMPLES  (" + entries.size() + ")", skin, "section");
        sampleTable.add(heading).growX().left().pad(10f, 10f, 8f, 8f).row();
        String previousCategory = null;
        for(int i = 0; i < entries.size(); i++) {
            final int sampleIndex = i;
            Box2DSampleEntry entry = entries.get(i);
            String category = entry.category();
            String name = entry.name();
            if(!category.equals(previousCategory)) {
                Label categoryLabel = new Label(category.toUpperCase(), skin, "category");
                sampleTable.add(categoryLabel).growX().left().pad(10f, 10f, 3f, 8f).row();
                previousCategory = category;
            }
            final TextButton button = new TextButton(name, skin, "sample");
            button.getLabel().setAlignment(com.badlogic.gdx.utils.Align.left);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    highlightSample(sampleIndex);
                    if(getTapCount() >= 2) selectSample(sampleIndex);
                }
            });
            sampleButtons.add(button);
            sampleTable.add(button).height(30f).growX().pad(1f, 7f, 1f, 7f).row();
        }
    }

    private void buildOptions(Table table) {
        table.setBackground(skin.newDrawable("white", PANEL));
        table.defaults().left().padLeft(10f).padRight(10f);
        table.add(new Label("SIMULATION", skin, "section")).growX().padTop(10f).padBottom(6f).row();

        final Box2DSampleSettings settings = controller.settings();
        addSlider(table, "Hertz", Box2DSampleSettings.MIN_HERTZ, Box2DSampleSettings.MAX_HERTZ, 1f,
                settings.hertz(), new FloatSetter() {
            @Override
            public void set(float value) {
                settings.setHertz(value);
            }
        });
        addSlider(table, "Sub-steps", Box2DSampleSettings.MIN_SUB_STEPS, 16f, 1f,
                settings.subStepCount(), new FloatSetter() {
            @Override
            public void set(float value) {
                settings.setSubStepCount(Math.round(value));
            }
        });

        addCheckBox(table, "Sleeping", settings.sleepEnabled(), new BooleanSetter() {
            @Override public void set(boolean value) { settings.setSleepEnabled(value); }
        });
        addCheckBox(table, "Warm starting", settings.warmStartingEnabled(), new BooleanSetter() {
            @Override public void set(boolean value) { settings.setWarmStartingEnabled(value); }
        });
        addCheckBox(table, "Continuous collision", settings.continuousEnabled(), new BooleanSetter() {
            @Override public void set(boolean value) { settings.setContinuousEnabled(value); }
        });

        table.add(new Label("DEBUG DRAW", skin, "section")).growX().padTop(12f).padBottom(6f).row();
        addCheckBox(table, "Shapes", drawRenderer.GetDrawShapes(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawShapes(value); }
        });
        addCheckBox(table, "Joints", drawRenderer.GetDrawJoints(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawJoints(value); }
        });
        addCheckBox(table, "Joint extras", drawRenderer.GetDrawJointExtras(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawJointExtras(value); }
        });
        addCheckBox(table, "Bounds", drawRenderer.GetDrawBounds(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawBounds(value); }
        });
        addCheckBox(table, "Centers of mass", drawRenderer.GetDrawMass(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawMass(value); }
        });
        addCheckBox(table, "Contact points", drawRenderer.GetDrawContacts(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawContacts(value); }
        });
        addCheckBox(table, "Contact normals", drawRenderer.GetDrawContactNormals(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawContactNormals(value); }
        });
        addCheckBox(table, "Contact impulses", drawRenderer.GetDrawContactImpulses(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawContactImpulses(value); }
        });
        addCheckBox(table, "Friction impulses", drawRenderer.GetDrawFrictionImpulses(), new BooleanSetter() {
            @Override public void set(boolean value) { drawRenderer.SetDrawFrictionImpulses(value); }
        });

        table.add(new Label("SAMPLE CONTROLS", skin, "section")).growX().padTop(12f).padBottom(6f).row();
        controlsTable = new Table(skin);
        controlsTable.top().left();
        table.add(controlsTable).growX().pad(0f, 7f, 14f, 7f).row();
    }

    private void addSlider(Table parent, String name, float minimum, float maximum, float step,
                           float value, final FloatSetter setter) {
        final Label valueLabel = new Label(formatValue(value, step), skin, "small");
        Table labelRow = new Table(skin);
        labelRow.add(new Label(name, skin, "small")).left().growX();
        labelRow.add(valueLabel).right();
        parent.add(labelRow).growX().padTop(3f).row();
        final Slider slider = new Slider(minimum, maximum, step, false, skin);
        slider.setValue(value);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setter.set(slider.getValue());
                valueLabel.setText(formatValue(slider.getValue(), slider.getStepSize()));
            }
        });
        parent.add(slider).height(22f).growX().padBottom(3f).row();
    }

    private void addCheckBox(Table parent, String text, boolean checked, final BooleanSetter setter) {
        final CheckBox checkBox = new CheckBox(text, skin);
        configureCheckBox(checkBox);
        checkBox.setChecked(checked);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setter.set(checkBox.isChecked());
            }
        });
        parent.add(checkBox).height(27f).growX().row();
    }

    private static void configureCheckBox(CheckBox checkBox) {
        checkBox.left();
        checkBox.getImageCell().padRight(CHECKBOX_TEXT_GAP);
    }

    private void selectSample(int index) {
        if(controller == null || index < 0 || index >= controller.entries().size()) {
            return;
        }
        controller.selectSample(index);
    }

    private void highlightSample(int index) {
        highlightedSampleIndex = index;
        updateSampleButtonSelection();
    }

    private void updateSampleButtonSelection() {
        for(int i = 0; i < sampleButtons.size(); i++) {
            sampleButtons.get(i).setChecked(i == highlightedSampleIndex);
        }
    }

    private void refreshDynamicControls(boolean force) {
        if(currentSample == null || controlsTable == null) {
            return;
        }
        int signature = controlSignature();
        if(force || signature != controlsSignature) {
            controlsSignature = signature;
            rebuildDynamicControls();
        }
        updateDynamicControls();
    }

    private int controlSignature() {
        int signature = 17;
        java.util.List<Box2DSampleControl> controls = currentSample.controls();
        int count = controls.size();
        signature = 31 * signature + count;
        for(int i = 0; i < count; i++) {
            Box2DSampleControl control = controls.get(i);
            signature = 31 * signature + control.type();
            signature = 31 * signature + control.optionCount();
        }
        return signature;
    }

    private void rebuildDynamicControls() {
        controlsUpdating = true;
        controlsTable.clearChildren();
        controlWidgets.clear();
        final java.util.List<Box2DSampleControl> controls = currentSample.controls();
        int count = controls.size();
        if(count == 0) {
            Label empty = new Label("No sample-specific controls", skin, "muted");
            controlsTable.add(empty).growX().left().pad(4f).row();
            controlsUpdating = false;
            return;
        }

        for(int i = 0; i < count; i++) {
            final int controlIndex = i;
            final Box2DSampleControl control = controls.get(i);
            int type = control.type();
            String label = control.label();
            final ControlWidget widget = new ControlWidget(i, type);
            controlWidgets.add(widget);

            if(type == Box2DSampleControl.TEXT) {
                Label text = new Label(label, skin, "muted");
                text.setWrap(true);
                widget.label = text;
                widget.actor = text;
                controlsTable.add(text).growX().left().pad(3f).row();
            }
            else if(type == Box2DSampleControl.CHECKBOX) {
                final CheckBox checkBox = new CheckBox(label, skin);
                configureCheckBox(checkBox);
                checkBox.setChecked(control.value() >= 0.5f);
                checkBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(!controlsUpdating) control.setValue(checkBox.isChecked() ? 1f : 0f);
                    }
                });
                widget.actor = checkBox;
                controlsTable.add(checkBox).height(28f).growX().left().row();
            }
            else if(type == Box2DSampleControl.SLIDER_FLOAT || type == Box2DSampleControl.SLIDER_INT) {
                Label controlLabel = new Label(label, skin, "small");
                Label valueLabel = new Label("", skin, "small");
                Table labelRow = new Table(skin);
                labelRow.add(controlLabel).left().growX();
                labelRow.add(valueLabel).right();
                controlsTable.add(labelRow).growX().padTop(3f).row();
                float step = Math.max(0.0001f, control.step());
                final Slider slider = new Slider(control.minimum(), control.maximum(), step, false, skin);
                slider.setValue(control.value());
                slider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(!controlsUpdating) control.setValue(slider.getValue());
                    }
                });
                widget.actor = slider;
                widget.label = controlLabel;
                widget.valueLabel = valueLabel;
                controlsTable.add(slider).height(22f).growX().padBottom(3f).row();
            }
            else if(type == Box2DSampleControl.COMBO) {
                Label controlLabel = new Label(label, skin, "small");
                final SelectBox<String> selectBox = new SelectBox<String>(skin);
                Array<String> options = new Array<String>();
                for(int option = 0; option < control.optionCount(); option++) {
                    options.add(control.option(option));
                }
                selectBox.setItems(options);
                if(options.size > 0) selectBox.setSelectedIndex(MathUtils.clamp(Math.round(control.value()), 0, options.size - 1));
                selectBox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(!controlsUpdating) control.setValue(selectBox.getSelectedIndex());
                    }
                });
                widget.actor = selectBox;
                widget.label = controlLabel;
                controlsTable.add(controlLabel).growX().left().padTop(3f).row();
                controlsTable.add(selectBox).height(30f).growX().padBottom(4f).row();
            }
            else {
                final TextButton button = new TextButton(label, skin);
                button.setChecked(type == Box2DSampleControl.RADIO && control.value() >= 0.5f);
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(!controlsUpdating) control.press();
                    }
                });
                widget.actor = button;
                controlsTable.add(button).height(30f).growX().pad(2f).row();
            }
        }
        controlsUpdating = false;
    }

    @SuppressWarnings("unchecked")
    private void updateDynamicControls() {
        controlsUpdating = true;
        java.util.List<Box2DSampleControl> controls = currentSample.controls();
        if(controls.size() != controlWidgets.size()) {
            controlsUpdating = false;
            refreshDynamicControls(true);
            return;
        }
        for(int i = 0; i < controlWidgets.size(); i++) {
            ControlWidget widget = controlWidgets.get(i);
            Box2DSampleControl control = controls.get(widget.index);
            String label = control.label();
            float value = control.value();
            if(widget.type == Box2DSampleControl.TEXT) {
                ((Label)widget.actor).setText(label);
            }
            else if(widget.type == Box2DSampleControl.CHECKBOX) {
                CheckBox checkBox = (CheckBox)widget.actor;
                checkBox.setText(label);
                checkBox.setChecked(value >= 0.5f);
            }
            else if(widget.type == Box2DSampleControl.SLIDER_FLOAT || widget.type == Box2DSampleControl.SLIDER_INT) {
                Slider slider = (Slider)widget.actor;
                widget.label.setText(label);
                slider.setValue(value);
                widget.valueLabel.setText(formatValue(value,
                        widget.type == Box2DSampleControl.SLIDER_INT ? 1f : slider.getStepSize()));
            }
            else if(widget.type == Box2DSampleControl.COMBO) {
                SelectBox<String> selectBox = (SelectBox<String>)widget.actor;
                widget.label.setText(label);
                if(selectBox.getItems().size > 0) {
                    selectBox.setSelectedIndex(MathUtils.clamp(Math.round(value), 0, selectBox.getItems().size - 1));
                }
            }
            else {
                TextButton button = (TextButton)widget.actor;
                button.setText(label);
                if(widget.type == Box2DSampleControl.RADIO) button.setChecked(value >= 0.5f);
            }
        }
        controlsUpdating = false;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(controller != null) {
            try {
                controller.update(Gdx.graphics.getDeltaTime());
                if(currentSample != null) {
                    if(currentSample.tracksCameraX()) {
                        cameraCenterX = currentSample.cameraCenterX();
                        syncCamera();
                    }
                    drawRenderer.beginFrame();
                    drawRenderer.DrawWorld(currentSample.world());
                    currentSample.draw(drawRenderer);
                    drawRenderer.render(worldCamera, shapes, batch, worldFont, screenFont);
                    refreshDynamicControls(false);
                    updateHeader();
                    sampleFrameCount++;
                }
            }
            catch(Throwable throwable) {
                Gdx.app.error("jBox2D", "Java sample failed", throwable);
                if(loadingLabel != null) loadingLabel.setText("Could not run jBox2D sample\n" + throwable);
                controller.dispose();
                controller = null;
                currentSample = null;
            }
        }
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1f / 20f));
        stage.draw();

        if(!screenshotWritten && screenshotPath.length() > 0 && sampleFrameCount >= 3) {
            writeScreenshot();
        }
        if(exitAfterFrames > 0 && sampleFrameCount >= exitAfterFrames) {
            Gdx.app.exit();
        }
    }

    private void updateHeader() {
        if(controller == null || currentSample == null || titleLabel == null) return;
        int selected = controller.selectedIndex();
        Box2DSampleEntry entry = controller.selectedEntry();
        titleLabel.setText(entry.category() + "  /  " + entry.name());
        statsLabel.setText("FPS " + Gdx.graphics.getFramesPerSecond() + "   Bodies " + currentSample.bodyCount()
                + "   Shapes " + currentSample.shapeCount()
                + "   Joints " + currentSample.jointCount());
        pauseButton.setText(controller.settings().paused() ? "Resume" : "Pause");
        if(highlightedSampleIndex < 0) highlightedSampleIndex = selected;
        updateSampleButtonSelection();
    }

    private void syncCamera() {
        worldCamera.viewportHeight = cameraZoom * 2f;
        worldCamera.viewportWidth = worldCamera.viewportHeight * Gdx.graphics.getWidth() / Math.max(1f, Gdx.graphics.getHeight());
        worldCamera.position.set(cameraCenterX, cameraCenterY, 0f);
        worldCamera.update();
    }

    private void setCamera(float centerX, float centerY, float zoom) {
        cameraCenterX = centerX;
        cameraCenterY = centerY;
        cameraZoom = MathUtils.clamp(zoom, 0.02f, 1000f);
        syncCamera();
    }

    private Vector3 worldPoint(int screenX, int screenY) {
        unproject.set(screenX, screenY, 0f);
        return worldCamera.unproject(unproject);
    }

    private boolean isWorldArea(int screenX, int screenY) {
        return screenX >= SAMPLE_PANEL_WIDTH
                && screenX < Gdx.graphics.getWidth() - OPTIONS_PANEL_WIDTH
                && screenY >= TOP_BAR_HEIGHT
                && screenY < Gdx.graphics.getHeight();
    }

    private void zoomCamera(float scrollAmount) {
        float factor = (float)Math.pow(1.1, scrollAmount);
        setCamera(cameraCenterX, cameraCenterY, cameraZoom * factor);
    }

    private void writeScreenshot() {
        Pixmap source = null;
        Pixmap flipped = null;
        try {
            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();
            source = Pixmap.createFromFrameBuffer(0, 0, width, height);
            flipped = new Pixmap(width, height, source.getFormat());
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    flipped.drawPixel(x, height - y - 1, source.getPixel(x, y));
                }
            }
            FileHandle output = Gdx.files.absolute(screenshotPath);
            output.parent().mkdirs();
            PixmapIO.writePNG(output, flipped);
            screenshotWritten = true;
            Gdx.app.log("jBox2D", "Screenshot written to " + screenshotPath);
        }
        catch(Throwable throwable) {
            screenshotWritten = true;
            Gdx.app.error("jBox2D", "Could not write screenshot", throwable);
        }
        finally {
            if(source != null) source.dispose();
            if(flipped != null) flipped.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        if(stage != null) stage.getViewport().update(width, height, true);
        if(worldCamera != null) syncCamera();
    }

    @Override
    public void dispose() {
        if(controller != null) controller.dispose();
        if(drawRenderer != null) drawRenderer.dispose();
        if(stage != null) stage.dispose();
        if(skin != null) skin.dispose();
        if(shapes != null) shapes.dispose();
        if(batch != null) batch.dispose();
        if(worldFont != null) worldFont.dispose();
        if(screenFont != null) screenFont.dispose();
    }

    private Skin createSkin() {
        Skin result = new Skin();
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        Texture white = new Texture(pixel);
        pixel.dispose();
        result.add("white", white);

        BitmapFont font = new BitmapFont();
        result.add("default-font", font);
        result.add("default", new Label.LabelStyle(font, Color.WHITE));
        result.add("small", new Label.LabelStyle(font, new Color(0.84f, 0.88f, 0.93f, 1f)));
        result.add("muted", new Label.LabelStyle(font, new Color(0.64f, 0.69f, 0.76f, 1f)));
        result.add("title", new Label.LabelStyle(font, Color.WHITE));
        result.add("section", new Label.LabelStyle(font, ACCENT));
        result.add("category", new Label.LabelStyle(font, new Color(0.48f, 0.74f, 0.88f, 1f)));

        Drawable buttonUp = result.newDrawable("white", PANEL_LIGHT);
        Drawable buttonDown = result.newDrawable("white", new Color(0.16f, 0.43f, 0.57f, 1f));
        Drawable buttonChecked = result.newDrawable("white", new Color(0.12f, 0.36f, 0.49f, 1f));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(buttonUp, buttonDown, buttonChecked, font);
        result.add("default", buttonStyle);
        result.add("sample", new TextButton.TextButtonStyle(result.newDrawable("white", PANEL), buttonDown, buttonChecked, font));

        Drawable checkOff = result.newDrawable("white", new Color(0.18f, 0.21f, 0.26f, 1f));
        Drawable checkOn = result.newDrawable("white", ACCENT);
        checkOff.setMinWidth(16f);
        checkOff.setMinHeight(16f);
        checkOn.setMinWidth(16f);
        checkOn.setMinHeight(16f);
        CheckBox.CheckBoxStyle checkStyle = new CheckBox.CheckBoxStyle(checkOff, checkOn, font, Color.WHITE);
        result.add("default", checkStyle);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(
                result.newDrawable("white", new Color(0.18f, 0.21f, 0.26f, 1f)),
                result.newDrawable("white", ACCENT));
        sliderStyle.background.setMinHeight(5f);
        sliderStyle.knob.setMinWidth(12f);
        sliderStyle.knob.setMinHeight(18f);
        result.add("default-horizontal", sliderStyle);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = result.newDrawable("white", PANEL);
        scrollStyle.vScroll = result.newDrawable("white", new Color(0.10f, 0.12f, 0.15f, 1f));
        scrollStyle.vScrollKnob = result.newDrawable("white", new Color(0.31f, 0.37f, 0.44f, 1f));
        result.add("default", scrollStyle);

        List.ListStyle listStyle = new List.ListStyle(font, Color.WHITE, Color.WHITE,
                result.newDrawable("white", new Color(0.12f, 0.36f, 0.49f, 1f)));
        result.add("default", listStyle);
        SelectBox.SelectBoxStyle selectStyle = new SelectBox.SelectBoxStyle(font, Color.WHITE,
                buttonUp, scrollStyle, listStyle);
        result.add("default", selectStyle);
        return result;
    }

    private static String formatValue(float value, float step) {
        return step >= 1f ? Integer.toString(Math.round(value)) : String.format(java.util.Locale.US, "%.2f", value);
    }

    private static long parseLongProperty(String name, long fallback) {
        try {
            return Long.parseLong(System.getProperty(name, Long.toString(fallback)));
        }
        catch(NumberFormatException ignored) {
            return fallback;
        }
    }

    private static final class ControlWidget {
        final int index;
        final int type;
        Actor actor;
        Label label;
        Label valueLabel;

        ControlWidget(int index, int type) {
            this.index = index;
            this.type = type;
        }
    }

    private interface BooleanSetter {
        void set(boolean value);
    }

    private interface FloatSetter {
        void set(float value);
    }

    /** Gets first chance at wheel events so an unfocused Stage cannot swallow world zooming. */
    private final class CameraScrollInput extends InputAdapter {
        @Override
        public boolean scrolled(float amountX, float amountY) {
            if(currentSample == null || amountY == 0f || !isWorldArea(Gdx.input.getX(), Gdx.input.getY())) {
                return false;
            }
            zoomCamera(amountY);
            return true;
        }
    }

    private final class WorldInput extends InputAdapter {
        private boolean panning;
        private boolean worldDragging;
        private int lastX;
        private int lastY;

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(currentSample == null || !isWorldArea(screenX, screenY)) return false;
            lastX = screenX;
            lastY = screenY;
            panning = button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE;
            worldDragging = !panning;
            if(worldDragging) {
                Vector3 point = worldPoint(screenX, screenY);
                currentSample.mouseDown(point.x, point.y, 0, modifiers());
            }
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if(currentSample == null) return false;
            if(panning) {
                float unitsPerPixel = 2f * cameraZoom / Math.max(1, Gdx.graphics.getHeight());
                setCamera(cameraCenterX - (screenX - lastX) * unitsPerPixel,
                        cameraCenterY + (screenY - lastY) * unitsPerPixel, cameraZoom);
            }
            else if(worldDragging) {
                Vector3 point = worldPoint(screenX, screenY);
                currentSample.mouseMove(point.x, point.y);
            }
            lastX = screenX;
            lastY = screenY;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(currentSample == null) return false;
            if(worldDragging) {
                Vector3 point = worldPoint(screenX, screenY);
                currentSample.mouseUp(point.x, point.y, 0);
            }
            panning = false;
            worldDragging = false;
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if(currentSample == null || !isWorldArea(screenX, screenY)) return false;
            Vector3 point = worldPoint(screenX, screenY);
            currentSample.mouseMove(point.x, point.y);
            return false;
        }

        @Override
        public boolean keyDown(int keycode) {
            if(currentSample == null) return false;
            if(keycode == Input.Keys.HOME) {
                Box2DSampleCamera camera = controller.selectedEntry().camera();
                setCamera(camera.centerX, camera.centerY, camera.zoom);
                return true;
            }
            int key = glfwKey(keycode);
            if(key >= 0) {
                currentSample.keyDown(key);
                return true;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if(currentSample == null) return false;
            int key = glfwKey(keycode);
            if(key >= 0) {
                currentSample.keyUp(key);
                return true;
            }
            return false;
        }

        private int modifiers() {
            int value = 0;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) value |= 1;
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) value |= 2;
            if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) value |= 4;
            return value;
        }

        private int glfwKey(int keycode) {
            if(keycode >= Input.Keys.A && keycode <= Input.Keys.Z) return 65 + (keycode - Input.Keys.A);
            if(keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9) return 48 + (keycode - Input.Keys.NUM_0);
            if(keycode == Input.Keys.SPACE) return 32;
            if(keycode == Input.Keys.ENTER) return 257;
            if(keycode == Input.Keys.LEFT) return 263;
            if(keycode == Input.Keys.RIGHT) return 262;
            if(keycode == Input.Keys.DOWN) return 264;
            if(keycode == Input.Keys.UP) return 265;
            return -1;
        }
    }
}
