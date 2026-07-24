package com.github.xpenatan.box2d.gdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class GdxBox2DConverterTest {
    private static final float EPSILON = 0.00001f;

    @Test
    public void convertsVectorsBothWaysIntoReusableOutputs() {
        FakeB2Vec2 box2dSource = new FakeB2Vec2(1.25f, -2.5f);
        Vector2 gdxOut = new Vector2();

        assertSame(gdxOut, GdxBox2DConverter.toGdx(box2dSource, gdxOut));
        assertVector(1.25f, -2.5f, gdxOut);

        FakeB2Vec2 box2dOut = new FakeB2Vec2();
        assertSame(box2dOut, GdxBox2DConverter.toBox2D(new Vector2(-4.0f, 5.0f), box2dOut));
        assertVector(-4.0f, 5.0f, box2dOut);
    }

    @Test
    public void convertsRigidTransformsWithoutAllocatingOutputs() {
        FakeB2Transform box2dSource = new FakeB2Transform(
                new FakeB2Vec2(2.0f, 3.0f), new FakeB2Rot((float)(Math.PI * 0.5)));
        Matrix4 gdxOut = new Matrix4();

        assertSame(gdxOut, GdxBox2DConverter.toGdx(box2dSource, gdxOut));
        Vector3 transformed = new Vector3(1.0f, 0.0f, 0.0f).mul(gdxOut);
        assertEquals(2.0f, transformed.x, EPSILON);
        assertEquals(4.0f, transformed.y, EPSILON);
        assertEquals(0.0f, transformed.z, EPSILON);

        FakeB2Transform box2dOut = new FakeB2Transform();
        Vector2 position = new Vector2(-3.0f, 7.0f);
        float rotation = 0.75f;
        assertSame(box2dOut, GdxBox2DConverter.toBox2D(position, rotation, box2dOut));
        assertVector(position.x, position.y, box2dOut.GetPosition());
        assertEquals(rotation, box2dOut.GetRotation().GetAngle(), EPSILON);
    }

    private static void assertVector(float x, float y, Vector2 actual) {
        assertEquals(x, actual.x, EPSILON);
        assertEquals(y, actual.y, EPSILON);
    }

    private static void assertVector(float x, float y, B2Vec2 actual) {
        assertEquals(x, actual.GetX(), EPSILON);
        assertEquals(y, actual.GetY(), EPSILON);
    }

    private static final class FakeB2Vec2 extends B2Vec2 {
        private float x;
        private float y;

        FakeB2Vec2() {
            this(0.0f, 0.0f);
        }

        FakeB2Vec2(float x, float y) {
            super((byte)0, (char)0);
            Set(x, y);
        }

        @Override
        public float GetX() {
            return x;
        }

        @Override
        public float GetY() {
            return y;
        }

        @Override
        public void Set(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final class FakeB2Rot extends B2Rot {
        private float angle;

        FakeB2Rot() {
            this(0.0f);
        }

        FakeB2Rot(float angle) {
            super((byte)0, (char)0);
            Set(angle);
        }

        @Override
        public float GetCosine() {
            return (float)Math.cos(angle);
        }

        @Override
        public float GetSine() {
            return (float)Math.sin(angle);
        }

        @Override
        public float GetAngle() {
            return angle;
        }

        @Override
        public void Set(float radians) {
            angle = radians;
        }
    }

    private static final class FakeB2Transform extends B2Transform {
        private final FakeB2Vec2 position;
        private final FakeB2Rot rotation;

        FakeB2Transform() {
            this(new FakeB2Vec2(), new FakeB2Rot());
        }

        FakeB2Transform(FakeB2Vec2 position, FakeB2Rot rotation) {
            super((byte)0, (char)0);
            this.position = position;
            this.rotation = rotation;
        }

        @Override
        public B2Vec2 GetPosition() {
            return position;
        }

        @Override
        public B2Rot GetRotation() {
            return rotation;
        }
    }
}
