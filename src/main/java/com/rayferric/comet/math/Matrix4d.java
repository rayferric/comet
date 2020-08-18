package com.rayferric.comet.math;

import java.util.Objects;

public class Matrix4d {
    public Matrix4d() {
        x = new Vector4d(1, 0, 0, 0);
        y = new Vector4d(0, 1, 0, 0);
        z = new Vector4d(0, 0, 1, 0);
        w = new Vector4d(0, 0, 0, 1);
    }

    public Matrix4d(double identity) {
        x = new Vector4d(identity, 0, 0, 0);
        y = new Vector4d(0, identity, 0, 0);
        z = new Vector4d(0, 0, identity, 0);
        w = new Vector4d(0, 0, 0, identity);
    }

    public Matrix4d(Vector4d x, Vector4d y, Vector4d z, Vector4d w) {
        setX(x);
        setY(y);
        setZ(z);
        setW(w);
    }

    public Matrix4d(Matrix4d other) {
        setX(other.x);
        setY(other.y);
        setZ(other.z);
        setW(other.w);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Matrix4d other = (Matrix4d)o;
        return Objects.equals(x, other.x) &&
                Objects.equals(y, other.y) &&
                Objects.equals(z, other.z) &&
                Objects.equals(w, other.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return String.format("Matrix4d{x=%s, y=%s, z=%s, w=%s}", x, y, z, w);
    }

    public Matrix4d mul(Matrix4d rhs) {
        return new Matrix4d(
                x.mul(rhs.x.getX()).add(y.mul(rhs.x.getY())).add(z.mul(rhs.x.getZ())).add(w.mul(rhs.x.getW())),
                x.mul(rhs.y.getX()).add(y.mul(rhs.y.getY())).add(z.mul(rhs.y.getZ())).add(w.mul(rhs.y.getW())),
                x.mul(rhs.z.getX()).add(y.mul(rhs.z.getY())).add(z.mul(rhs.z.getZ())).add(w.mul(rhs.z.getW())),
                x.mul(rhs.w.getX()).add(y.mul(rhs.w.getY())).add(z.mul(rhs.w.getZ())).add(w.mul(rhs.w.getW()))
        );
    }

    public Vector4d mul(Vector4d rhs) {
        return new Vector4d(
                x.getX() * rhs.getX() + y.getX() * rhs.getY() + z.getX() * rhs.getZ() + w.getX() * rhs.getW(),
                x.getY() * rhs.getX() + y.getY() * rhs.getY() + z.getY() * rhs.getZ() + w.getY() * rhs.getW(),
                x.getZ() * rhs.getX() + y.getZ() * rhs.getY() + z.getZ() * rhs.getZ() + w.getZ() * rhs.getW(),
                x.getW() * rhs.getX() + y.getW() * rhs.getY() + z.getW() * rhs.getZ() + w.getW() * rhs.getW()
        );
    }

    public static Matrix4d transform(Vector3d translation, Vector3d rotation, Vector3d scale) {
        double rx = Math.toRadians(rotation.getX());
        double ry = Math.toRadians(rotation.getY());
        double rz = Math.toRadians(rotation.getZ());

        double sx = Math.sin(rx);
        double sy = Math.sin(ry);
        double sz = Math.sin(rz);

        double cx = Math.cos(rx);
        double cy = Math.cos(ry);
        double cz = Math.cos(rz);


        return new Matrix4d(
                new Vector4d(
                        (cy * cz + sy * sx * sz) * scale.getX(),
                        (cx * sz) * scale.getX(),
                        (cy * sx * sz - cz * sy) * scale.getX(),
                        0
                ),
                new Vector4d(
                        (cz * sy * sx - cy * sz) * scale.getY(),
                        (cx * cz) * scale.getY(),
                        (cy * cz * sx + sy * sz) * scale.getY(),
                        0
                ),
                new Vector4d(
                        (cx * sy) * scale.getZ(),
                        (-sx) * scale.getZ(),
                        (cy * cx) * scale.getZ(),
                        0
                ),
                new Vector4d(
                        translation.getX(),
                        translation.getY(),
                        translation.getZ(),
                        1
                )
        );
    }

    public static Matrix4d perspective(double fov, double aspect, double near, double far) {
        double ht = Math.tan(Math.toRadians(fov) / 2);

        Matrix4d result = new Matrix4d(0);

        result.x.setX(1 / (aspect * ht));
        result.y.setY(1 / ht);
        result.z.setZ((near + far) / (near - far));
        result.z.setW(-1);
        result.w.setZ((2 * near * far) / (near - far));

        return result;
    }

    public static Matrix4d ortho(double width, double height, double near, double far) {
        Matrix4d result = new Matrix4d(1);

        result.x.setX(2 / width);
        result.y.setY(2 / height);
        result.z.setZ(2 / (near - far));
        result.w.setZ((near + far) / (near - far));

        return result;
    }

    public Matrix4d inverse() {
        double s0 = x.getX() * y.getY() - x.getY() * y.getX();
        double s1 = x.getX() * z.getY() - x.getY() * z.getX();
        double s2 = x.getX() * w.getY() - x.getY() * w.getX();
        double s3 = y.getX() * z.getY() - y.getY() * z.getX();
        double s4 = y.getX() * w.getY() - y.getY() * w.getX();
        double s5 = z.getX() * w.getY() - z.getY() * w.getX();

        double c5 = z.getZ() * w.getW() - z.getW() * w.getZ();
        double c4 = y.getZ() * w.getW() - y.getW() * w.getZ();
        double c3 = y.getZ() * z.getW() - y.getW() * z.getZ();
        double c2 = x.getZ() * w.getW() - x.getW() * w.getZ();
        double c1 = x.getZ() * z.getW() - x.getW() * z.getZ();
        double c0 = x.getZ() * y.getW() - x.getW() * y.getZ();

        double invDet = 1 / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

        return new Matrix4d(
                new Vector4d(
                        (y.getY() * c5 - z.getY() * c4 + w.getY() * c3),
                        (-x.getY() * c5 + z.getY() * c2 - w.getY() * c1),
                        (x.getY() * c4 - y.getY() * c2 + w.getY() * c0),
                        (-x.getY() * c3 + y.getY() * c1 - z.getY() * c0)
                ).mul(invDet),
                new Vector4d(
                        (-y.getX() * c5 + z.getX() * c4 - w.getX() * c3),
                        (x.getX() * c5 - z.getX() * c2 + w.getX() * c1),
                        (-x.getX() * c4 + y.getX() * c2 - w.getX() * c0),
                        (x.getX() * c3 - y.getX() * c1 + z.getX() * c0)
                ).mul(invDet),
                new Vector4d(
                        (y.getW() * s5 - z.getW() * s4 + w.getW() * s3),
                        (-x.getW() * s5 + z.getW() * s2 - w.getW() * s1),
                        (x.getW() * s4 - y.getW() * s2 + w.getW() * s0),
                        (-x.getW() * s3 + y.getW() * s1 - z.getW() * s0)
                ).mul(invDet),
                new Vector4d(
                        (-y.getZ() * s5 + z.getZ() * s4 - w.getZ() * s3),
                        (x.getZ() * s5 - z.getZ() * s2 + w.getZ() * s1),
                        (-x.getZ() * s4 + y.getZ() * s2 - w.getZ() * s0),
                        (x.getZ() * s3 - y.getZ() * s1 + z.getZ() * s0)
                ).mul(invDet)
        );
    }

    public Vector4d getX() {
        return new Vector4d(x);
    }

    public void setX(Vector4d x) {
        this.x = new Vector4d(x);
    }

    public Vector4d getY() {
        return new Vector4d(y);
    }

    public void setY(Vector4d y) {
        this.y = new Vector4d(y);
    }

    public Vector4d getZ() {
        return new Vector4d(z);
    }

    public void setZ(Vector4d z) {
        this.z = new Vector4d(z);
    }

    public Vector4d getW() {
        return new Vector4d(w);
    }

    public void setW(Vector4d w) {
        this.w = new Vector4d(w);
    }

    // Those are columns
    private Vector4d x, y, z, w;
}
