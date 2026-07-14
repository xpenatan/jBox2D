package com.github.xpenatan.box2d.sample.shared.samples;

import com.github.xpenatan.box2d.B2Body;
import com.github.xpenatan.box2d.B2Shape;

/** Java port of Box2D 3.1.1's Benchmark / Rain sample. */
public final class BenchmarkRainSample extends AbstractBox2DSample {
    private final int rowCount = BenchmarkSampleSupport.DEBUG_SIZE ? 3 : 5;
    private final int columnCapacity = BenchmarkSampleSupport.DEBUG_SIZE ? 10 : 40;
    private final int groupSize = BenchmarkSampleSupport.DEBUG_SIZE ? 2 : 5;
    private final HumanRagdoll[][][] groups;
    private final float gridSize = 0.5f;
    private final int gridCount = BenchmarkSampleSupport.DEBUG_SIZE ? 200 : 500;
    private int columnCount;
    private int columnIndex;
    private int stepCount;

    public BenchmarkRainSample() {
        groups = new HumanRagdoll[rowCount][columnCapacity][groupSize];
        B2Body ground = createStaticBody(0.0f, 0.0f, 0.0f);
        for(int row = 0; row < rowCount; row++) {
            float y = 45.0f * row;
            for(int column = 0; column <= gridCount; column++) {
                float x = -0.5f * gridCount * gridSize + column * gridSize;
                B2Shape shape = addOffsetBoxShape(ground, 0.25f, 0.25f, x, y, 0.0f,
                        0.0f, 0.6f, 0.0f, 0.0f);
                discardHandle(shape);
            }
        }
    }

    @Override
    protected void beforeStep(float deltaSeconds) {
        int delay = BenchmarkSampleSupport.DEBUG_SIZE ? 0x1F : 0x7;
        if((stepCount & delay) == 0) {
            if(columnCount < columnCapacity) {
                for(int row = 0; row < rowCount; row++) createGroup(row, columnCount);
                columnCount++;
            }
            else {
                for(int row = 0; row < rowCount; row++) {
                    destroyGroup(row, columnIndex);
                    createGroup(row, columnIndex);
                }
                columnIndex = (columnIndex + 1) % columnCapacity;
            }
        }
        stepCount++;
    }

    private void createGroup(int row, int column) {
        float span = gridCount * gridSize;
        float groupDistance = span / columnCapacity;
        float x = -0.5f * span + groupDistance * (column + 0.5f);
        float y = 40.0f + 45.0f * row;
        for(int i = 0; i < groupSize; i++) {
            groups[row][column][i] = new HumanRagdoll(this, x, y, 1.0f, 0.05f, 5.0f, 0.5f);
            x += 0.5f;
        }
    }

    private void destroyGroup(int row, int column) {
        for(int i = 0; i < groupSize; i++) {
            HumanRagdoll human = groups[row][column][i];
            if(human != null) {
                human.destroy();
                groups[row][column][i] = null;
            }
        }
    }
}
