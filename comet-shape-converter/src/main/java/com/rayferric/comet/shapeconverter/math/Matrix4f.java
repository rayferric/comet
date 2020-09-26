package com.rayferric.comet.shapeconverter.math;

import java.util.Objects;

public class Matrix4f {
    public static final int BYTES = 64;
    public static final Matrix4f IDENTITY = new Matrix4f(1);

    public Matrix4f() {
        x = new Vector4f(1, 0, 0, 0);
        y = new Vector4f(0, 1, 0, 0);
        z = new Vector4f(0, 0, 1, 0);
        w = new Vector4f(0, 0, 0, 1);
    }

    public Matrix4f(float identity) {
        x = new Vector4f(identity, 0, 0, 0);
        y = new Vector4f(0, identity, 0, 0);
        z = new Vector4f(0, 0, identity, 0);
        w = new Vector4f(0, 0, 0, identity);
    }

    public Matrix4f(Vector4f x, Vector4f y, Vector4f z, Vector4f w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Matrix4f(float xx, float xy, float xz, float xw,
                    float yx, float yy, float yz, float yw,
                    float zx, float zy, float zz, float zw,
                    float wx, float wy, float wz, float ww) {
        x = new Vector4f(xx, xy, xz, xw);
        y = new Vector4f(yx, yy, yz, yw);
        z = new Vector4f(zx, zy, zz, zw);
        w = new Vector4f(wx, wy, wz, ww);
    }

    public Matrix4f(float[] array) {
        x = new Vector4f(array[0], array[1], array[2], array[3]);
        y = new Vector4f(array[4], array[5], array[6], array[7]);
        z = new Vector4f(array[8], array[9], array[10], array[11]);
        w = new Vector4f(array[12], array[13], array[14], array[15]);
    }

    public Matrix4f(Matrix4f other) {
        x = new Vector4f(other.x);
        y = new Vector4f(other.y);
        z = new Vector4f(other.z);
        w = new Vector4f(other.w);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Matrix4f other = (Matrix4f)o;
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
        return String.format("Matrix4f{x=%s, y=%s, z=%s, w=%s}", x, y, z, w);
    }

    public static Matrix4f transform(Vector3f translation, Vector3f rotation, Vector3f scale) {
        float pitch = rotation.getX();
        float yaw = rotation.getY();
        float roll = rotation.getZ();

        float sx = Mathf.sin(pitch);
        float sy = Mathf.sin(yaw);
        float sz = Mathf.sin(roll);

        float cx = Mathf.cos(pitch);
        float cy = Mathf.cos(yaw);
        float cz = Mathf.cos(roll);


        return new Matrix4f(
                new Vector4f(
                        (cy * cz + sy * sx * sz) * scale.getX(),
                        (cx * sz) * scale.getX(),
                        (cy * sx * sz - cz * sy) * scale.getX(),
                        0
                ),
                new Vector4f(
                        (cz * sy * sx - cy * sz) * scale.getY(),
                        (cx * cz) * scale.getY(),
                        (cy * cz * sx + sy * sz) * scale.getY(),
                        0
                ),
                new Vector4f(
                        (cx * sy) * scale.getZ(),
                        (-sx) * scale.getZ(),
                        (cy * cx) * scale.getZ(),
                        0
                ),
                new Vector4f(
                        translation.getX(),
                        translation.getY(),
                        translation.getZ(),
                        1
                )
        );
    }

    public static Matrix4f perspective(float fov, float ratio, float near, float far) {
        float ht = Mathf.tan(fov * 0.5F);

        Matrix4f result = new Matrix4f(0);

        result.x.setX(1 / (ratio * ht));
        result.y.setY(1 / ht);
        result.z.setZ((near + far) / (near - far));
        result.z.setW(-1);
        result.w.setZ((2 * near * far) / (near - far));

        return result;
    }

    public static Matrix4f ortho(float width, float height, float near, float far) {
        Matrix4f result = new Matrix4f(1);

        result.x.setX(2 / width);
        result.y.setY(2 / height);
        result.z.setZ(2 / (near - far));
        result.w.setZ((near + far) / (near - far));

        return result;
    }

    public float[] toArray() {
        float[] array = new float[4 * 4];

        System.arraycopy(x.toArray(), 0, array, 0, 4);
        System.arraycopy(y.toArray(), 0, array, 4, 4);
        System.arraycopy(z.toArray(), 0, array, 8, 4);
        System.arraycopy(w.toArray(), 0, array, 12, 4);

        return array;
    }

    public Matrix4f mul(Matrix4f rhs) {
        return new Matrix4f(
                x.mul(rhs.x.getX()).add(y.mul(rhs.x.getY())).add(z.mul(rhs.x.getZ())).add(w.mul(rhs.x.getW())),
                x.mul(rhs.y.getX()).add(y.mul(rhs.y.getY())).add(z.mul(rhs.y.getZ())).add(w.mul(rhs.y.getW())),
                x.mul(rhs.z.getX()).add(y.mul(rhs.z.getY())).add(z.mul(rhs.z.getZ())).add(w.mul(rhs.z.getW())),
                x.mul(rhs.w.getX()).add(y.mul(rhs.w.getY())).add(z.mul(rhs.w.getZ())).add(w.mul(rhs.w.getW()))
        );
    }

    public Vector4f mul(Vector4f rhs) {
        return new Vector4f(
                x.getX() * rhs.getX() + y.getX() * rhs.getY() + z.getX() * rhs.getZ() + w.getX() * rhs.getW(),
                x.getY() * rhs.getX() + y.getY() * rhs.getY() + z.getY() * rhs.getZ() + w.getY() * rhs.getW(),
                x.getZ() * rhs.getX() + y.getZ() * rhs.getY() + z.getZ() * rhs.getZ() + w.getZ() * rhs.getW(),
                x.getW() * rhs.getX() + y.getW() * rhs.getY() + z.getW() * rhs.getZ() + w.getW() * rhs.getW()
        );
    }

    public Vector3f mul(Vector3f rhs, float w) {
        Vector4f result = mul(new Vector4f(rhs.getX(), rhs.getY(), rhs.getZ(), w));
        return new Vector3f(result.getX(), result.getY(), result.getZ());
    }

    public Matrix4f inverse() {
        float s0 = x.getX() * y.getY() - x.getY() * y.getX();
        float s1 = x.getX() * z.getY() - x.getY() * z.getX();
        float s2 = x.getX() * w.getY() - x.getY() * w.getX();
        float s3 = y.getX() * z.getY() - y.getY() * z.getX();
        float s4 = y.getX() * w.getY() - y.getY() * w.getX();
        float s5 = z.getX() * w.getY() - z.getY() * w.getX();

        float c5 = z.getZ() * w.getW() - z.getW() * w.getZ();
        float c4 = y.getZ() * w.getW() - y.getW() * w.getZ();
        float c3 = y.getZ() * z.getW() - y.getW() * z.getZ();
        float c2 = x.getZ() * w.getW() - x.getW() * w.getZ();
        float c1 = x.getZ() * z.getW() - x.getW() * z.getZ();
        float c0 = x.getZ() * y.getW() - x.getW() * y.getZ();

        float invDet = 1 / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);

        return new Matrix4f(
                new Vector4f(
                        (y.getY() * c5 - z.getY() * c4 + w.getY() * c3),
                        (-x.getY() * c5 + z.getY() * c2 - w.getY() * c1),
                        (x.getY() * c4 - y.getY() * c2 + w.getY() * c0),
                        (-x.getY() * c3 + y.getY() * c1 - z.getY() * c0)
                ).mul(invDet),
                new Vector4f(
                        (-y.getX() * c5 + z.getX() * c4 - w.getX() * c3),
                        (x.getX() * c5 - z.getX() * c2 + w.getX() * c1),
                        (-x.getX() * c4 + y.getX() * c2 - w.getX() * c0),
                        (x.getX() * c3 - y.getX() * c1 + z.getX() * c0)
                ).mul(invDet),
                new Vector4f(
                        (y.getW() * s5 - z.getW() * s4 + w.getW() * s3),
                        (-x.getW() * s5 + z.getW() * s2 - w.getW() * s1),
                        (x.getW() * s4 - y.getW() * s2 + w.getW() * s0),
                        (-x.getW() * s3 + y.getW() * s1 - z.getW() * s0)
                ).mul(invDet),
                new Vector4f(
                        (-y.getZ() * s5 + z.getZ() * s4 - w.getZ() * s3),
                        (x.getZ() * s5 - z.getZ() * s2 + w.getZ() * s1),
                        (-x.getZ() * s4 + y.getZ() * s2 - w.getZ() * s0),
                        (x.getZ() * s3 - y.getZ() * s1 + z.getZ() * s0)
                ).mul(invDet)
        );
    }

    public Vector3f getTranslation() {
        return new Vector3f(w.getX(), w.getY(), w.getZ());
    }

    public void setTranslation(float x, float y, float z) {
        w.setX(x);
        w.setY(y);
        w.setZ(z);
    }

    public void setTranslation(Vector3f translation) {
        setTranslation(translation.getX(), translation.getY(), translation.getZ());
    }

    public Quaternion getRotation() {
        Vector4f x = this.x.normalize();
        Vector4f y = this.y.normalize();
        Vector4f z = this.z.normalize();

        float xx = x.getX();
        float yy = y.getY();
        float zz = z.getZ();

        float t;
        Quaternion q;

        if(zz < 0) {
            if(xx > yy) {
                t = 1 + xx - yy - zz;
                q = new Quaternion(y.getZ() - z.getY(), t, x.getY() + y.getX(), z.getX() + x.getZ());
            }
            else {
                t = 1 - xx + yy - zz;
                q = new Quaternion(z.getX() - x.getZ(), x.getY() + y.getX(), t, y.getZ() + z.getY());
            }
        } else {
            if(xx < -yy) {
                t = 1 - xx - yy + zz;
                q = new Quaternion(x.getY() - y.getX(), z.getX() + x.getZ(), y.getZ() + z.getY(), t);
            }
            else {
                t = 1 + xx + yy + zz;
                q = new Quaternion(t, y.getZ() - z.getY(), z.getX() - x.getZ(), x.getY() - y.getX());
            }
        }

        return q.mul(0.5F / Mathf.sqrt(t));
    }

    public void setRotation(Quaternion rotation) {
        Vector3f scale = getScale();
        Matrix4f m = rotation.toMatrix();
        x = m.x.mul(scale.getX());
        y = m.y.mul(scale.getY());
        z = m.z.mul(scale.getZ());
    }

    public Vector3f getScale() {
        return new Vector3f(x.length(), y.length(), z.length());
    }

    public void setScale(float x, float y, float z) {
        this.x = this.x.normalize().mul(x);
        this.y = this.y.normalize().mul(y);
        this.z = this.z.normalize().mul(z);
    }

    public void setScale(Vector3f scale) {
        setScale(scale.getX(), scale.getY(), scale.getZ());
    }

    public Vector4f getX() {
        return x;
    }

    public void setX(Vector4f x) {
        this.x = x;
    }

    public Vector4f getY() {
        return y;
    }

    public void setY(Vector4f y) {
        this.y = y;
    }

    public Vector4f getZ() {
        return z;
    }

    public void setZ(Vector4f z) {
        this.z = z;
    }

    public Vector4f getW() {
        return w;
    }

    public void setW(Vector4f w) {
        this.w = w;
    }

    // Those are columns:
    private Vector4f x, y, z, w;
}
