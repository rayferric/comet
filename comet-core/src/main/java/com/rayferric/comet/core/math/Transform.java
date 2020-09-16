package com.rayferric.comet.core.math;

import com.rayferric.comet.core.scenegraph.node.Node;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Transform {
    public Transform(Node owner) {
        this.owner = owner;
        this.matrix = new Matrix4f(1);
    }

    public Vector3f getTranslation() {
        try {
            lock.readLock().lock();
            return matrix.getTranslation();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setTranslation(float x, float y, float z) {
        try {
            lock.writeLock().lock();
            matrix.setTranslation(x, y, z);
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    public void setTranslation(Vector3f translation) {
        setTranslation(translation.getX(), translation.getY(), translation.getZ());
    }

    public void translate(Vector3f translation) {
        try {
            lock.writeLock().lock();
            matrix.setTranslation(matrix.getTranslation().add(translation));
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    public void translate(float x, float y, float z) {
        translate(new Vector3f(x, y, z));
    }

    public Quaternion getRotation() {
        try {
            lock.readLock().lock();
            return matrix.getRotation();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setRotation(Quaternion rotation) {
        try {
            lock.writeLock().lock();
            matrix.setRotation(rotation);
        } finally {
            lock.writeLock().unlock();
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
        try {
            lock.writeLock().lock();
            matrix.setRotation(rotation.mul(matrix.getRotation()));
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    public void rotate(float pitch, float yaw, float roll) {
        rotate(Quaternion.eulerAngle(pitch, yaw, roll));
    }

    public void rotate(Vector3f euler) {
        rotate(Quaternion.eulerAngle(euler));
    }

    public Vector3f getScale() {
        try {
            lock.readLock().lock();
            return matrix.getTranslation();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setScale(float x, float y, float z) {
        try {
            lock.writeLock().lock();
            matrix.setScale(x, y, z);
        } finally {
            lock.writeLock().unlock();
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
        try {
            lock.writeLock().lock();
            matrix.setScale(matrix.getScale().mul(scale));
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    public void scale(float x, float y, float z) {
        scale(new Vector3f(x, y, z));
    }

    public void scale(float scale) {
        scale(scale, scale, scale);
    }

    public Matrix4f getMatrix() {
        try {
            lock.readLock().lock();
            return new Matrix4f(matrix);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setMatrix(Matrix4f matrix) {
        try {
            lock.writeLock().lock();
            this.matrix = new Matrix4f(matrix);
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    public void applyMatrix(Matrix4f matrix) {
        try {
            lock.writeLock().lock();
            this.matrix = matrix.mul(this.matrix);
        } finally {
            lock.writeLock().unlock();
        }
        owner.invalidateGlobalTransform();
    }

    private final Node owner;
    private Matrix4f matrix;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}
