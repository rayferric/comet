package com.rayferric.spatialwalker;

import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Quaternion;
import com.rayferric.comet.math.Transform;
import com.rayferric.comet.math.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBGLSPIRV.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Test {
    public static void main(String[] args) {
        Vector3f euler = new Vector3f(0, 5, 0);

        printMatrix(Matrix4f.transform(new Vector3f(1), euler, new Vector3f(1)));

        Transform transform = new Transform();
        //transform.translate(1, 1, 1);
        Quaternion q = Quaternion.fromEuler(euler);
        Quaternion q2 = new Quaternion();

        for(int i = 0; i < 100; i++) {
            System.out.println(q2.toEuler());
            q2 = q2.mul(q);
        }

        //printMatrix(transform.getMatrix());
    }

    private static void printMatrix(Matrix4f matrix) {
        System.out.println(matrix.getX());
        System.out.println(matrix.getY());
        System.out.println(matrix.getZ());
        System.out.println(matrix.getW());
        System.out.println();
    }
}
