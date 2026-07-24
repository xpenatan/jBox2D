package com.github.xpenatan.box2d.gdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.github.xpenatan.box2d.B2Rot;
import com.github.xpenatan.box2d.B2Transform;
import com.github.xpenatan.box2d.B2Vec2;

/**
 * Allocation-free conversions between Box2D and libGDX math types.
 * <p>
 * Every method writes into a caller-owned output object and returns that same object for chaining.
 */
public final class GdxBox2DConverter {

    private GdxBox2DConverter() {
    }

    /** Converts a Box2D vector into a reusable libGDX vector. */
    public static Vector2 toGdx(B2Vec2 source, Vector2 out) {
        requireNonNull(source, "source");
        requireNonNull(out, "out");
        return out.set(source.GetX(), source.GetY());
    }

    /** Converts a libGDX vector into a reusable Box2D vector. */
    public static B2Vec2 toBox2D(Vector2 source, B2Vec2 out) {
        requireNonNull(source, "source");
        requireNonNull(out, "out");
        out.Set(source.x, source.y);
        return out;
    }

    /** Converts a rigid Box2D transform into a reusable libGDX matrix. */
    public static Matrix4 toGdx(B2Transform source, Matrix4 out) {
        requireNonNull(source, "source");
        requireNonNull(out, "out");
        B2Vec2 position = source.GetPosition();
        B2Rot rotation = source.GetRotation();
        float cosine = rotation.GetCosine();
        float sine = rotation.GetSine();
        out.idt();
        out.val[Matrix4.M00] = cosine;
        out.val[Matrix4.M01] = -sine;
        out.val[Matrix4.M10] = sine;
        out.val[Matrix4.M11] = cosine;
        out.val[Matrix4.M03] = position.GetX();
        out.val[Matrix4.M13] = position.GetY();
        return out;
    }

    /** Writes a libGDX position and rotation angle into a reusable Box2D transform. */
    public static B2Transform toBox2D(Vector2 position, float rotationRadians, B2Transform out) {
        requireNonNull(position, "position");
        requireNonNull(out, "out");
        out.GetPosition().Set(position.x, position.y);
        out.GetRotation().Set(rotationRadians);
        return out;
    }

    private static void requireNonNull(Object value, String name) {
        if(value == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
