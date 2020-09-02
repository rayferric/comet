package com.rayferric.comet.math;

import com.rayferric.comet.scenegraph.node.Node;

public class Transform {
    public Transform(Node owner) {
        this.owner = owner;
        this.matrix = new Matrix4f(1);
    }

    public Vector3f getTranslation() {
        synchronized(matrixLock) {
            Vector4f w = matrix.getW();
            return new Vector3f(w.getX(), w.getY(), w.getZ());
        }
    }

    public void setTranslation(float x, float y, float z) {
        synchronized(matrixLock) {
            matrix.setW(new Vector4f(x, y, z, 1));
        }

        owner.invalidateGlobalTransform();
    }

    public void setTranslation(Vector3f translation) {
        setTranslation(translation.getX(), translation.getY(), translation.getZ());
    }

    public void translate(Vector3f translation) {
        setTranslation(getTranslation().add(translation));
    }

    public void translate(float x, float y, float z) {
        translate(new Vector3f(x, y, z));
    }

    public Quaternion getRotation() {
        Vector4f x, y, z;
        synchronized(matrixLock) {
            x = matrix.getX().normalize();
            y = matrix.getY().normalize();
            z = matrix.getZ().normalize();
        }

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

        synchronized(matrixLock) {
            matrix.setX(m.getX().mul(scale.getX()));
            matrix.setY(m.getY().mul(scale.getY()));
            matrix.setZ(m.getZ().mul(scale.getZ()));
        }

        owner.invalidateGlobalTransform();
    }

    public void setRotation(float pitch, float yaw, float roll) {
        setRotation(Quaternion.eulerAngle(pitch, yaw, roll));
    }

    public void setRotation(Vector3f euler) {
        setRotation(Quaternion.eulerAngle(euler));
    }

    public void rotate(Quaternion rotation) {
        setRotation(getRotation().mul(rotation));
    }

    public void rotate(float pitch, float yaw, float roll) {
        rotate(Quaternion.eulerAngle(pitch, yaw, roll));
    }

    public void rotate(Vector3f euler) {
        rotate(Quaternion.eulerAngle(euler));
    }

    public Vector3f getScale() {
        synchronized(matrixLock) {
            return new Vector3f(matrix.getX().length(), matrix.getY().length(), matrix.getZ().length());
        }
    }

    public void setScale(float x, float y, float z) {
        synchronized(matrixLock) {
            matrix.setX(matrix.getX().normalize().mul(x));
            matrix.setY(matrix.getY().normalize().mul(y));
            matrix.setZ(matrix.getZ().normalize().mul(z));
        }

        owner.invalidateGlobalTransform();
    }

    public void setScale(Vector3f scale) {
        setScale(scale.getX(), scale.getY(), scale.getZ());
    }

    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }

    public void scale(Vector3f scale) {
        setScale(getScale().mul(scale));
    }

    public void scale(float x, float y, float z) {
        scale(new Vector3f(x, y, z));
    }

    public void scale(float scale) {
        scale(scale, scale, scale);
    }

    public Matrix4f getMatrix() {
        synchronized(matrixLock) {
            return new Matrix4f(matrix);
        }
    }

    public void setMatrix(Matrix4f matrix) {
        synchronized(matrixLock) {
            this.matrix = new Matrix4f(matrix);
        }
        owner.invalidateGlobalTransform();
    }

    private final Node owner;
    private Matrix4f matrix;
    private final Object matrixLock = new Object();
}
