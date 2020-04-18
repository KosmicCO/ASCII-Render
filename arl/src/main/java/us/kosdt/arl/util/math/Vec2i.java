package us.kosdt.arl.util.math;

import org.joml.Vector2d;
import org.joml.Vector2i;

public class Vec2i {

    public final int x, y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i add(int a) {
        return new Vec2i(x + a, y + a);
    }

    public Vec2i add(Vec2i other) {
        return new Vec2i(x + other.x, y + other.y);
    }

    public Vec2i clamp(int lower, int upper) {
        return new Vec2i(MathUtils.clamp(x, lower, upper), MathUtils.clamp(y, lower, upper));
    }

    public Vec2i clamp(Vec2i lower, Vec2i upper) {
        return new Vec2i(MathUtils.clamp(x, lower.x, upper.x), MathUtils.clamp(y, lower.y, upper.y));
    }

    public boolean contains(Vec2i other){
        Vec2i adjusted = new Vec2i(x > 0 ? other.x : -other.x, y > 0 ? other.y : -other.y);
        Vec2i positiveCompare = new Vec2i(Math.abs(x), Math.abs(y));
        if(adjusted.x < 0 || adjusted.y < 0 || positiveCompare.x < adjusted.x || positiveCompare.y < adjusted.y){
            return false;
        }
        return true;
    }

    public boolean containsExclusive(Vec2i other){
        Vec2i adjusted = new Vec2i(x > 0 ? other.x : -other.x, y > 0 ? other.y : -other.y);
        Vec2i positiveCompare = new Vec2i(Math.abs(x), Math.abs(y));
        if(adjusted.x < 0 || adjusted.y < 0 || positiveCompare.x <= adjusted.x || positiveCompare.y <= adjusted.y){
            return false;
        }
        return true;
    }

    public Vec2i div(int a) {
        return new Vec2i(x / a, y / a);
    }

    public Vec2i div(Vec2i other) {
        return new Vec2i(x / other.x, y / other.y);
    }

    public double dot(Vec2i other) {
        return x * other.x + y * other.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vec2i other = (Vec2i) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Integer.hashCode(this.x);
        hash = 13 * hash + Integer.hashCode(this.y);
        return hash;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public Vec2i lerp(Vec2i other, Vec2i amt) {
        return mul(new Vec2i(1, 1).sub(amt)).add(other.mul(amt));
    }

    public Vec2i mul(int a) {
        return new Vec2i(x * a, y * a);
    }

    public Vec2i mul(Vec2i other) {
        return new Vec2i(x * other.x, y * other.y);
    }

    public Vec2i setX(int x) {
        return new Vec2i(x, y);
    }

    public Vec2i setY(int y) {
        return new Vec2i(x, y);
    }

    public Vec2i sub(int a) {
        return new Vec2i(x - a, y - a);
    }

    public Vec2i sub(Vec2i other) {
        return new Vec2i(x - other.x, y - other.y);
    }

    public Vector2i toJOML() {
        return new Vector2i(x, y);
    }

    public Vec2d toVec2d() {
        return new Vec2d(x, y);
    }

    @Override
    public String toString() {
        return "Vec2i{" + "x=" + x + ", y=" + y + '}';
    }
}
