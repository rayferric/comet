package com.rayferric.comet.math;

import java.util.Objects;

public class Quaternion {
    public Quaternion() {
        w = 1;
        v = new Vector3f(0);
    }

    public Quaternion(float w, Vector3f v) {
        this.w = w;
        this.v = v;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.v = new Vector3f(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Quaternion other = (Quaternion)o;
        return w == other.w && Objects.equals(v, other.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(w, v);
    }

    @Override
    public String toString() {
        return String.format("Quaternion{w=%s, v=%s}", w, v);
    }

    public static Quaternion axisAngle(Vector3f axis, float angle) {
        float halfAngle = angle * 0.5F;

        return new Quaternion(
                Mathf.cos(halfAngle),
                axis.normalize().mul(Mathf.sin(halfAngle))
        );
    }

    /**
     * Euler YXZ, applied in order: roll (Z), pitch (X), yaw (Y).
     *
     * @param pitch X axis (2)
     * @param yaw   Y axis (3)
     * @param roll  Z axis (1)
     *
     * @return equivalent quaternion rotation
     */
    public static Quaternion eulerAngle(float pitch, float yaw, float roll) {
        float cp = Mathf.cos(pitch * 0.5F);
        float sp = Mathf.sin(pitch * 0.5F);
        float cy = Mathf.cos(yaw * 0.5F);
        float sy = Mathf.sin(yaw * 0.5F);
        float cr = Mathf.cos(roll * 0.5F);
        float sr = Mathf.sin(roll * 0.5F);

        return new Quaternion(
                cr * cp * cy + sr * sp * sy,
                cr * sp * cy + sr * cp * sy,
                cr * cp * sy - sr * sp * cy,
                sr * cp * cy - cr * sp * sy
        );
    }

    public static Quaternion eulerAngle(Vector3f euler) {
        return eulerAngle(euler.getX(), euler.getY(), euler.getZ());
    }

    public Quaternion mul(float rhs) {
        return new Quaternion(w * rhs, v.mul(rhs));
    }

    public Quaternion mul(Quaternion rhs) {
//        The precision loss of this method is too high.
//        return new Quaternion(
//                w * rhs.w + v.dot(rhs.v),
//                v.mul(rhs.w).add(rhs.v.mul(w)).add(v.cross(rhs.v))
//        );

        float x = v.getX();
        float y = v.getY();
        float z = v.getZ();

        float rhsW = rhs.w;
        float rhsX = rhs.v.getX();
        float rhsY = rhs.v.getY();
        float rhsZ = rhs.v.getZ();

        return new Quaternion(
                w * rhsW - x * rhsX - y * rhsY - z * rhsZ,
                w * rhsX + x * rhsW + y * rhsZ - z * rhsY,
                w * rhsY - x * rhsZ + y * rhsW + z * rhsX,
                w * rhsZ + x * rhsY - y * rhsX + z * rhsW
        );
    }

    public Vector3f mul(Vector3f rhs) {
        Vector3f c = v.cross(rhs);
        return rhs.add(c.mul(2 * w)).add(v.cross(c).mul(2));
    }

    public Quaternion inverse() {
        return new Quaternion(w, -v.getX(), -v.getY(), -v.getZ());
    }

    // Euler YXZ, applied in order: roll (Z), pitch (X), yaw (Y).

    /**
     * Euler YXZ, applied in order: roll (Z), pitch (X), yaw (Y).
     *
     * @return equivalent YXZ euler rotation
     */
    public Vector3f toEuler() {
        float x = v.getX();
        float y = v.getY();
        float z = v.getZ();

        float yy = x * x;

        float sp = 2 * (w * x - y * z);
        sp = Mathf.clamp(sp, -1, 1);
        float pitch = Mathf.asin(sp);

        float syCp = 2 * (w * y + z * x);
        float cyCp = 1 - 2 * (yy + y * y);
        float yaw = Mathf.atan2(syCp, cyCp);

        float srCp = 2 * (w * z + x * y);
        float crCp = 1 - 2 * (z * z + yy);
        float roll = Mathf.atan2(srCp, crCp);

        return new Vector3f(pitch, yaw, roll);
    }

    public Matrix4f toMatrix() {
        float x = v.getX();
        float y = v.getY();
        float z = v.getZ();

        return new Matrix4f(
                new Vector4f(
                        1 - 2 * (y * y + z * z),
                        2 * (x * y + z * w),
                        2 * (x * z - y * w),
                        0
                ),
                new Vector4f(
                        2 * (x * y - z * w),
                        1 - 2 * (x * x + z * z),
                        2 * (y * z + x * w),
                        0
                ),
                new Vector4f(
                        2 * (x * z + y * w),
                        2 * (y * z - x * w),
                        1 - 2 * (x * x + y * y),
                        0
                ),
                new Vector4f(
                        0,
                        0,
                        0,
                        1
                )
        );
    }

    private final float w;
    private final Vector3f v;
}
