package com.snapgames.framework.physic.math;

import java.util.List;
import java.util.Objects;

/**
 * {@link Vector2d} class is to define, manage and operate 2D Vector.
 * <p>
 * usage :
 *
 * <pre>
 * // create a vector
 * Vector2D v1 = new Vector2D(0.0, 0.0);
 * // apply some common operation
 * Vector2D v2 = v1.multiply(12.0).ceil(0.001).maximize(2000.0);
 * // normalize the resulting vector
 * double norm = v2.normalize();
 * // compute length of the vector
 * double l = v2.length();
 * // define distance between 2 vector
 * double distance = v1.distance(v2);
 * // get the opposite vector
 * Vector2D v3 = v2.negate();
 * // compute dot product between v1 and v2.
 * double d = v1.dot(v2);
 * </pre>
 *
 * @author Frédéric Delorme
 * @since 1.0.3
 */
public class Vector2d {
    public double x, y;

    public Vector2d() {
        x = 0.0f;
        y = 0.0f;
    }

    /**
     * @param x
     * @param y
     */
    public Vector2d(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Vector2d add(Vector2d v) {
        return new Vector2d(x + v.x, y + v.y);
    }

    public Vector2d substract(Vector2d v1) {
        return new Vector2d(x - v1.x, y - v1.y);
    }

    public Vector2d multiply(double f) {
        return new Vector2d(x * f, y * f);
    }

    public double dot(Vector2d v1) {

        return v1.x * y + v1.y * x;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double distance(Vector2d v1) {
        return substract(v1).length();
    }

    public Vector2d divide(double f) {
        return new Vector2d(x / f, y / f);
    }

    public Vector2d normalize() {
        return divide(length());
    }

    public Vector2d negate() {
        return new Vector2d(-x, -y);
    }

    public double angle(Vector2d v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return Math.acos(vDot);

    }

    public Vector2d addAll(List<Vector2d> forces) {
        Vector2d sum = new Vector2d();
        for (Vector2d f : forces) {
            sum = sum.add(f);
        }
        return sum;
    }

    public String toString() {
        return String.format("{x:%04.2f,y:%04.2f}", x, y);
    }

    public Vector2d maximize(double maxValue) {
        if (Math.abs(x) > maxValue) {
            x = Math.signum(x) * maxValue;
        }
        if (Math.abs(y) > maxValue) {
            y = Math.signum(y) * maxValue;
        }
        return this;
    }

    public Vector2d maximize(double maxX, double maxY) {
        if (Math.abs(x) > maxX) {
            x = Math.signum(x) * maxX;
        }
        if (Math.abs(y) > maxY) {
            y = Math.signum(y) * maxY;
        }
        return this;
    }

    public Vector2d ceil(double ceilThreshod) {
        x = Math.copySign((Math.abs(x) < ceilThreshod ? 0 : x), x);
        y = Math.copySign((Math.abs(x) < ceilThreshod ? 0 : y), y);
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        Vector2d vo = (Vector2d) o;
        return Objects.equals(x, vo.x) && Objects.equals(y, vo.y);
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
